package lyricom.netCleConfig.comms;

import com.fazecast.jSerialComm.SerialPort;
import javax.swing.JOptionPane;
import static lyricom.netCleConfig.comms.Connection.RES;
import lyricom.netCleConfig.ui.PortSelectionDlg;

/**
 *
 * @author Andrew
 */
public class SerialConn extends Connection {
    
    Serial serial;
    SerialConn() {
        serial = Serial.getInstance();
        serial.setCallback(this);
    }

    @Override
    boolean doConnection(boolean autoSelect) {
        SerialPort port = doPortSelection(autoSelect);

        if (serial.open_port(port)) {
            connected = true;
            serial.startDispatchThread();
            return true;
        } else {
            confirm(RES.getString("CMT_FAILURE"),
                RES.getString("CM_FAIL_RETRY")
            );
            return false;
        }
    }

    private SerialPort doPortSelection(boolean autoSelect) {
        SerialPort[] ports = serial.get_list();
        
        while (ports.length == 0) {
            confirm(RES.getString("CMT_ERROR"), RES.getString("CM_NO_PORTS"));
            ports = serial.get_list();
        }
        
        if (autoSelect) {
            for(SerialPort p: ports) {
                if (p.getDescriptivePortName().contains("Leonardo")) {
                    return p;
                }
            }
        }
        
        PortSelectionDlg dlg = new PortSelectionDlg(ports);
        if (dlg.wasCancelled()) {
            System.exit(0);
        } 
        
        return dlg.getPort();
    }

    @Override
    public void connectionLost() {
        close();
        connected = false;

        confirm(RES.getString("CMT_LOST"), RES.getString("CM_RECONNECT"));        

        JOptionPane.showMessageDialog(null, 
                RES.getString("CM_INSTRUCT"), RES.getString("CMT_RECONNECT"),
                JOptionPane.INFORMATION_MESSAGE);
        
        establishConnection();  
    }
    
    @Override
    public int writeData(byte[] buffer) {
        return serial.writeData(buffer);
    }

    @Override
    public void close() {
        serial.close();
        connected = false;
    }

}
