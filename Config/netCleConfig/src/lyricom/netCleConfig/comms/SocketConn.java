package lyricom.netCleConfig.comms;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import lyricom.netCleConfig.model.Model;

/**
 *
 * @author Andrew
 */
public class SocketConn extends Connection implements Runnable {
    
    private final String address;
    private final int port;
   
    private Socket sock;
    private OutputStream output;
    private InputStream input;

    SocketConn(String a, int p) {
        address = a;
        port = p;
    }

    @Override
    boolean doConnection(boolean unused) {
        closing = false;
        try {
            sock = new Socket(address, port);
            output = sock.getOutputStream();
            input = sock.getInputStream();
            connected = true;
            startDispatchThread();
            return true;
        } catch (IOException ex) {
            return false;
        }
    }

    @Override
    public int writeData(byte[] buffer) {
        try {
            output.write(buffer);
            return buffer.length;
        } catch (IOException ex) {
            // raise error??
            return 0;
        }
    }

    @Override
    public void connectionLost() {
        close();
        connected = false;
        // Reconnect in a new thread??

        confirm(RES.getString("CMT_LOST"), RES.getString("CM_RECONNECT"));    
        
        establishConnection();  
    }
    
    @Override
    public void close() {
        try {
            sock.close();
            closing = true;
            if (readThread != null) readThread.interrupt();
            sock = null;
        } catch (IOException ex) {
            // dont care
        }
        connected = false;
    }
    
    Thread readThread = null;
    boolean closing = false;
    
    public void startDispatchThread() {
        readThread = new Thread(this);
        readThread.start();
    }

    @Override
    public void run() {
        while( !closing) {
            List<Byte> bytes = readData();
            if (bytes != null && bytes.size() > 0) {
                dispatchData(bytes);
            }
        } 
//        System.out.println("Read thread exit");
        readThread = null;
    }
    
    private List<Byte> readData() {
        List<Byte> blist = new ArrayList<>();
        boolean done = false;
        
        while (!done && !closing) {    
            int databyte = 0;
            try {
                databyte = input.read();
                if (databyte == -1) {
            //        System.out.println("Read returned -1");
                    connectionLost();
                }
            } catch (IOException ex) {
            //    System.out.println("Read IOException: " + ex.getMessage());
                connectionLost();
            }
            blist.add((byte) databyte);
            if (databyte == Model.END_OF_BLOCK) {
                done = true;
            }            
        }
        return blist;
    }
}
