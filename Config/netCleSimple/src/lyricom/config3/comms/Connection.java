/* * * * * * * * * * * * * * * * * * * * * * * * * * * * 
    Copyright (C) 2021 Andrew Hodgson

    This file is part of the netClé Configuration software.

    netClé Configuration software is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    netClé Configuration software is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this netClé configuration software.  
    If not, see <https://www.gnu.org/licenses/>.   
 * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package lyricom.config3.comms;

import com.fazecast.jSerialComm.SerialPort;
import java.nio.charset.Charset;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import lyricom.config3.model.InStream;
import lyricom.config3.model.Model;

/**
 * The Connection singleton class manages the creation of
 * the serial connection and any required reconnection.
 * 
 * If connection cannot be established this class will call
 * System.exit directly.
 * 
 * @author Andrew
 */
public class Connection implements SerialCallback {
    private static final ResourceBundle RES = ResourceBundle.getBundle("strings");
    private static Connection instance = null;
    
    public static Connection getInstance() {
        if (instance == null) {
            instance = new Connection();
        }
        return instance;
    }
    
    private final Serial serial;
    private Semaphore versionSemaphore; 
    private String versionString = null;
    private int versionID = 0;
    private boolean connected = false;
    private SensorDataCallback sensorDataCallback = null;
    private TriggerDataCallback triggerDataCallback = null;
    private String expectedPortName = "Leonardo";  // Starting guess
    
    private Connection() {
        serial = Serial.getInstance();
    }
    
    public void setSensorDataCallback(SensorDataCallback cb) {
        sensorDataCallback = cb;
    }
    public void setTriggerDataCallback(TriggerDataCallback cb) {
        triggerDataCallback = cb;
    }
    
    public boolean isConnected() {
        return connected;
    }
     
    public boolean establishConnection() {
        if (connected) {
            return true; // already connected.
        }
        
        serial.setCallback(this);  // Needed before connection to catch Version.
        if (doConnection(true)) {
            connected = true;
            return true;
        }
        
        JOptionPane.showMessageDialog(null, RES.getString("CM_INITIAL_REQUEST"),
                RES.getString("CMT_INITIAL_REQUEST"),
                JOptionPane.INFORMATION_MESSAGE);
        
        if (doConnection(false)) {
            connected = true;
        } else {
            connected = false;
        }
        return connected;
    }
    
    public String getVersionString() {
        return versionString;
    }
    
    public int getVersionID() {
        return versionID;
    }
    
    /*
     * Attempt to create a connection - handling dialog with the user
     * as required.  Return false if no connection was made 
     * (ie. if user responded "no" to one of the many "try again?" prompts.
    */
    private boolean doConnection(boolean tryIt) {
        boolean connected;
        boolean connectionSuccess = false;
        boolean userWantsToContinue;
        
        while( ! connectionSuccess) {
            SerialPort port = doPortSelection(tryIt);
            if (port == null) {
                return false;
            }
            
            if (serial.open_port(port)) {
                connected = true;
                serial.startDispatchThread();
            } else {
                connected = false;
                if (tryIt) return false;
                
                userWantsToContinue = confirm(RES.getString("CMT_FAILURE"),
                    RES.getString("CM_FAIL_RETRY")
                );
                if (!userWantsToContinue) return false;
            }
            
            if (connected) {
                try {
                    Thread.sleep(1000); // Wait for ATMega to reboot.
                    versionSemaphore = new Semaphore(1);
                    versionSemaphore.acquire();
                    serial.writeByte(Model.CMD_VERSION);
                    if (versionSemaphore.tryAcquire(1, 2000, TimeUnit.MILLISECONDS)) {
                        connectionSuccess = true;
                    } else {
                        if (tryIt) {
                            userWantsToContinue = false;
                        } else {
                            userWantsToContinue = confirm(RES.getString("CMT_ERROR"),
                                RES.getString("CM_NOT_SENSACT")
                            ); 
                        }
                        if (!userWantsToContinue) {
                            serial.close();
                            return false;
                        }                      
                    }
                } catch (InterruptedException ex) {
                    if (tryIt) {
                        userWantsToContinue = false;
                    } else {
                        userWantsToContinue = confirm(RES.getString("CMT_ERROR"),
                            RES.getString("CM_NOT_SENSACT")
                        ); 
                    }
                    if (!userWantsToContinue) {
                        serial.close();
                        return false;
                    }
                }
                if (!connectionSuccess) {
                    serial.close();  // Did not get version #.  Close this port and try another.
                }
            }
            
        }
        expectedPortName = serial.getPortName();
        return true;    
    }
    
    private SerialPort doPortSelection(boolean auto) {
        SerialPort[] ports = serial.get_list();
        
        while (ports.length == 0) {
            if (auto) return null;
            boolean userWantsToContinue =
                    confirm(RES.getString("CMT_ERROR"), RES.getString("CM_NO_PORTS"));
            if (!userWantsToContinue) return null;
            auto = false;
            ports = serial.get_list();
        }
        
        if (auto) {
            for(SerialPort p: ports) {
                if (p.getDescriptivePortName().contains(expectedPortName)) {
                    return p;
                }
            }
            return null;
        } else {       
            PortSelectionDlg dlg = new PortSelectionDlg(ports);
            if (dlg.wasCancelled()) {
                return null;
            } 

            return dlg.getPort();
        }
    }
    
    /*
     * Ask a yes/no question.  If the answer is no return false.
     */
    private boolean confirm(String title, String message) {
        int val = JOptionPane.showConfirmDialog(
            null, message, title, JOptionPane.YES_NO_OPTION);
        if (val == JOptionPane.NO_OPTION) {
            return false;
        }  
        return true;
    }

    /*
     * SerialCallback methods.
     *   dispatchData - get a data packet and figures out what to do with it.
     *   connectionLost - called when the connection is lost.
    */
    @Override
    public void dispatchData(List<Byte> bytes) {
        if (bytes.size() == 0) {
            return;
        }
        // Process Version Number
        if (bytes.get(0).equals(Model.CMD_VERSION)) {
            byte[] sub = new byte[bytes.size() - 2];
            for(int i=1; i < (bytes.size()-1); i++) {
                sub[i-1] = bytes.get(i);
            }
            versionString = new String(sub, Charset.defaultCharset());
            
            versionID = extractVersionID(sub);
           
            if (versionSemaphore != null) {
                versionSemaphore.release();
            }
                    
        // Process Sensor Reporting Data.
        } else if (bytes.get(0).equals(Model.START_OF_DATA)) {
            if (!connected) return;
            
            if (sensorDataCallback != null) {
                InStream in = new InStream(bytes);
                try {
                    sensorDataCallback.newSensorData(in);
                } catch (IOError e) {
                    System.out.println(RES.getString("CM_UNKNOWN") + ' ' + e.getMessage());
                }                
            }
/*            
            InStream in = new InStream(bytes);
            try {
                Model.updateSensorValues(in);
            } catch (IOError e) {
                System.out.println(RES.getString("CM_UNKNOWN") + ' ' + e.getMessage());
            }
*/

        // Process Trigger Data
        } else if (bytes.get(0).equals(Model.START_OF_TRIGGERS)) {
            if (!connected) return;
            
            if (triggerDataCallback != null) {
                triggerDataCallback.newTriggerData(bytes);
            }
            /*
            InStream input = new InStream(bytes);
            try {
                Triggers.getInstance().loadDataFromDevice(input);
            } catch(IOError e) {
                JOptionPane.showMessageDialog(null, 
                        RES.getString("CM_DATA_ERROR") + e.getMessage(),
                        RES.getString("CMT_DATA_ERROR"),
                        JOptionPane.ERROR_MESSAGE);
            } */
        }
    }

    // Version number will be in the format: [M]M.mm[bnnn]
    // MM is a one or two digit major version number.
    // mm is the minor version number (always 2 digits!)
    // b is the lette 'b' - only for a beta release.
    // nnn is the beta-build number.
    // The following code needs to extract the major and minor
    // version numbers and ignore the 'b' and beta-build numbers.
    // Then the internal version number will be set to:
    // major-number * 100 + minor-number.
    // If the version number format is invalid return 0.
    // Higher levels of software will report it and
    // mark the connection as "not a netCle device".
    //
    // There aught to be a cleaner way to do this, but I didn't
    // figure it out yet.
    private int extractVersionID(byte[] sub) {
        int majorNumber;
        int minorNumber;
        int minorIndex;
        
        if (sub.length < 4) return 0;
        if (sub[0] >= (byte) '1' && sub[0] <= (byte) '9') {
            majorNumber = sub[0] - (byte) '0';
        } else {
            return 0;
        }
        
        if (sub[1] >= (byte) '0' && sub[1] <= (byte) '9') {
            majorNumber = majorNumber * 10 + sub[1] - (byte) '0';
            minorIndex = 3;
            if (sub[2] != (byte) '.') return 0;
            if (sub.length < 5) return 0;
            
        } else if (sub[1] == (byte) '.') {
            minorIndex = 2;
            
        } else {
            return 0;
        }
        
        if (! (sub[minorIndex] >= (byte) '0' && sub[minorIndex] <= (byte) '9') ) {
            return 0;
        }
        if (! (sub[minorIndex+1] >= (byte) '0' && sub[minorIndex+1] <= (byte) '9') ) {
            return 0;
        }
        
        minorNumber = (sub[minorIndex] - (byte) '0') * 10 + sub[minorIndex+1] - (byte) '0';
        
        return majorNumber * 100 + minorNumber;
    }

    @Override
    public void connectionLost() {
        serial.close();
        connected = false;
    /*    // Reconnect in a new thread??
        confirm(RES.getString("CMT_LOST"), RES.getString("CM_RECONNECT"));
        JOptionPane.showMessageDialog(null, 
                RES.getString("CM_INSTRUCT"), RES.getString("CMT_RECONNECT"),
                JOptionPane.INFORMATION_MESSAGE);
        
        SwingUtilities.invokeLater(() -> {
            establishConnection();
        });  */
    }

}
