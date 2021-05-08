package lyricom.config3.solutions;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.ResourceBundle;
import javax.swing.JOptionPane;
import lyricom.config3.solutions.data.CursorSpeedData;
import lyricom.config3.ui.MainFrame;
import lyricom.config3.ui.selection.ESolution;

/**
 *
 * @author Andrew
 */
public class SolutionsDataList {
    private static final ResourceBundle RES = ResourceBundle.getBundle("strings");
    private static SolutionsDataList instance = null;
    
    public static SolutionsDataList getInstance() {
        if (instance == null) {
            instance = new SolutionsDataList();
        }
        return instance;
    }
    
    private List<SolutionsDataBase> theList = new ArrayList<>();
    
    private SolutionsDataList() {}
    
    // XML Support --------------------------
    // Put data in a non-singleton class.
    public XMLSolutionsList getXMLRoot() {
        XMLSolutionsList list = new XMLSolutionsList();
        list.setList(theList);
        return list;
    }
    
    public void updateFromXML(XMLSolutionsList list) {
        theList = list.getList();
    }
    // -------------------------------
    
    public void add(SolutionsDataBase sol) {
        theList.add(sol);
    }
    
    public void remove(SolutionsDataBase sol) {
        theList.remove(sol);
    }
    
    public void removeAll() {
        theList.clear();
    }
    
    public void compile() {
        for(SolutionsDataBase sol: theList) {
            sol.compile();
        }
    }
    
    public List<SolutionsDataBase> getList() {
        return theList;
    }
    
    public void printDescription(PrintStream out ) {
        for(SolutionsDataBase sol: theList) {
            sol.printDescription(out);
        }    
        CursorSpeedData.getInstance().printDescription(out);
    }
    
    // * * * * * * * * * * * * * * * * * * * * * *
    // SANITY CHECK
    // See if any input port is over used.
    //
    private class PortUsage {
        EPort thePort;
        int usageCount;
        SolutionsDataBase firstUser;
        SolutionsDataBase secondUser;
    };
    
    EnumMap<EPort, PortUsage> portMap = new EnumMap<>(EPort.class);
    List<EPort> portsWithTwoSingles = new ArrayList<>();
    
    // Return true if the configuration is Ok and can be loaded.
    // Otherwise return false.
    public boolean portUsageCheck() {
        portMap.clear();
        portsWithTwoSingles.clear();
                
        for(SolutionsDataBase sdb: SolutionsDataList.getInstance().getList()) {
            EPort p1 = sdb.getPortUsed();
            if (p1 != null) {
                boolean ok = checkOne(sdb, p1);
                if (!ok) return false;
            } 
            
            EPort p2 = sdb.getPortBUsed();
            if (p2 != null) {
                boolean ok = checkOne(sdb, p2);
                if (!ok) return false;                
            }
        }   
        // If we get this far without an error put out info messages about ports
        // with two singles.
        for(EPort port: portsWithTwoSingles) {
            PortUsage pu = portMap.get(port);
            JOptionPane.showMessageDialog(MainFrame.getInstance(), 
                String.format(RES.getString("SC_TWO_SINGLES_ON_ONE_PORT"), 
                        pu.firstUser.getType().toString(),
                        pu.secondUser.getType().toString(),
                        port.getPortNum()), 
                RES.getString("SC_TWO_SINGLES_ON_ONE_PORT_TITLE"), 
                JOptionPane.INFORMATION_MESSAGE);           
        }
        
        // Make sure single-user ports are assigned to subPort A
        for(EPort port: portMap.keySet()) {
            PortUsage pu = portMap.get(port);
            if (pu.usageCount == 1) {
                pu.firstUser.setSubPort(ESubPort.SubPortA);
            }
        }
        
        return true;
    }
    
    private boolean checkOne(SolutionsDataBase sdb, EPort port) {
        PortUsage pu = portMap.get(port);
        if (pu == null) {
            // First detected use of this port.
            pu = new PortUsage();
            pu.thePort = port;
            pu.usageCount = sdb.getSensorCount();
            pu.firstUser = sdb;
            pu.secondUser = null;
            portMap.put(port, pu);
        } else {
            if ((pu.usageCount + sdb.getSensorCount()) > 2) {
                // Too many users of this port.
                if (pu.secondUser == null) {
                    // There are two users
                    JOptionPane.showMessageDialog(MainFrame.getInstance(), 
                        String.format(RES.getString("SC_PORT_OVER_USE"), 
                                pu.firstUser.getType().toString(),
                                sdb.getType().toString(),
                                port.getPortNum()), 
                        RES.getString("SC_PORT_OVER_USE_TITLE"), 
                        JOptionPane.ERROR_MESSAGE);
                    return false;
                } else {
                    // There are three single-button users.
                     JOptionPane.showMessageDialog(MainFrame.getInstance(), 
                        String.format(RES.getString("SC_PORT_OVER_USE_SINGLES"), 
                                port.getPortNum(),
                                pu.firstUser.getType().toString(),
                                pu.secondUser.getType().toString(),
                                sdb.getType().toString()), 
                        RES.getString("SC_PORT_OVER_USE_TITLE"), 
                        JOptionPane.ERROR_MESSAGE);
                    return false;
               }
                
            } else {
                // Must be two single-button users.
                pu.usageCount = pu.usageCount + sdb.getSensorCount();
                pu.secondUser = sdb;
                
                // Assign the two buttons to sensors A and B
                pu.firstUser.setSubPort(ESubPort.SubPortA);
                pu.secondUser.setSubPort(ESubPort.SubPortB);                      

                portsWithTwoSingles.add(port);
            }
        }
        return true;
    }   
    
    // * * * * * * * * * * * * * * * * * * 
    // Bluetooth check
    // If a bluetooth incompatable solution is being used
    // return its ENum type.
    // TODO - add bluetooth check back in.
    public ESolution bluetoothCheck() {
        for(SolutionsDataBase sdb: theList) {
            if (!sdb.getType().worksOverBluetooth()) {
                return sdb.getType();
            }
        } 
        return null;  // OK - no bluetooth incompatabilities.
    }
}
