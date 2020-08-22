package lyricom.netCleConfig.comms;

import java.nio.charset.Charset;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import javax.swing.JOptionPane;
import lyricom.netCleConfig.model.IOError;
import lyricom.netCleConfig.model.InStream;
import lyricom.netCleConfig.model.Model;
import lyricom.netCleConfig.model.Triggers;
import lyricom.netCleConfig.ui.MainFrame;
import lyricom.netCleConfig.ui.SensorPanel;
import lyricom.netCleConfig.ui.Utils;

/**
 *
 * @author Andrew
 */
public abstract class Connection implements SerialCallback {
    protected static final ResourceBundle RES = ResourceBundle.getBundle("strings");

    private static Connection instance = null;
    
    public static Connection getInstance(String url, int port) {
        if (url == null) {
            instance = new SerialConn();
        } else {
            instance = new SocketConn(url, port);
        }
        return instance;
    }
    
    public static Connection getInstance() {
        return instance;
    }
    
    private Semaphore versionSemaphore; 
    private String versionString;
    private int versionID;
    protected boolean connected = false;
    private Shortcut shortcut = null;

    Connection() {}
       
    public String getVersionString() {
        return versionString;
    }
    
    public int getVersionID() {
        return versionID;
    }
   
    // This gives Quickload a quick way to get configuration data.
    public void registerShortcut(Shortcut sc) {
        shortcut = sc;
    }
    
    // Establish the connection and get version number.
    // Establish the connection or die!
    public void establishConnection() {
        boolean connectionSuccess = false;
        boolean autoSelect = true;  // Automatically select the 'Leonardo' device.
        while (!connectionSuccess) {
            if (doConnection(autoSelect)) {
                try {
                    Thread.sleep(1000); // Wait for ATMega to reboot.
                    versionSemaphore = new Semaphore(1);
                    versionSemaphore.acquire();
                    writeByte(Model.CMD_VERSION);
                    if (versionSemaphore.tryAcquire(1, 2000, TimeUnit.MILLISECONDS)) {
                        // We got a version number - was it a good one?
                        System.out.println(versionID);
                        if (versionID != 0) {
                            connectionSuccess = true;
                        } else {
                            JOptionPane.showMessageDialog(null, 
                                    String.format(RES.getString("CM_INVALID_VERSION"), versionString),
                                    RES.getString("CMT_ERROR"),
                                    JOptionPane.ERROR_MESSAGE);                            
                        }
                    }
                    if (!connectionSuccess) {
                        confirm(RES.getString("CMT_ERROR"),
                            RES.getString("CM_NOT_SENSACT")
                        );                                           
                    }
                } catch (InterruptedException ex) {
                    confirm(RES.getString("CMT_ERROR"),
                            RES.getString("CM_NOT_SENSACT")
                    );                    
                }
                if (!connectionSuccess) {
                    autoSelect = false;  // Automatically selected port did not work,
                                         // so let use select from a list.
                    close();  // Did not get version #.  Close this port and try another.
                }
            }            
        }
    }
    
    // dispatchData - Takes a block of newly received data and
    // sends it to the right place.
    @Override
    public void dispatchData(List<Byte> bytes) {
        if (bytes.isEmpty()) {
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
            
            InStream in = new InStream(bytes);
            try {
                Model.updateSensorValues(in);
            } catch (IOError e) {
                System.out.println(RES.getString("CM_UNKNOWN") + ' ' + e.getMessage());
            }
            
        // Process Trigger Data
        } else if (bytes.get(0).equals(Model.START_OF_TRIGGERS)) {
            if (!connected) return;
            if (shortcut != null) {
                // Running QuickLoad.  Just pass the raw data to the handler.
                shortcut.configDataForExport(bytes);
            }
            InStream input = new InStream(bytes);
            try {
                Triggers.getInstance().loadDataFromDevice(input);
                SensorPanel.reloadTriggers();    
                Triggers.DATA_IN_SYNC = true;
            } catch(IOError e) {
                JOptionPane.showMessageDialog(MainFrame.TheFrame, 
                        RES.getString("CM_DATA_ERROR") + e.getMessage(),
                        RES.getString("CMT_DATA_ERROR"),
                        JOptionPane.ERROR_MESSAGE);
            }
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
        
    /*
     * Ask a yes/no question.  If the answer is no - exit.
     */
    protected void confirm(String title, String message) {
        int val;
   
        do {
            val = JOptionPane.showConfirmDialog(
                null, message, title, JOptionPane.YES_NO_OPTION);
        } while(val == -1);  // -1 indicates message was not displayed.
                             // due to thread interruption
        if (val == JOptionPane.NO_OPTION) {
            System.exit(0);
        }            
    }
    
    
    public int writeList(List<Byte> bytes) {
        return writeData(Utils.listToArray(bytes));
    }
    
    public int writeByte(Byte val) {
        byte[] buffer = new byte[1];
        buffer[0] = (byte) val;
        return writeData(buffer);
    }
    
    // --------------------
    // Abstract functions
    // --------------------
    // Open the connection & Start the read thread.
    abstract boolean doConnection(boolean autoSelect);

    // Write data
    abstract public int writeData(byte[] buffer);
    
    // Close the connection
    abstract public void close();
}
