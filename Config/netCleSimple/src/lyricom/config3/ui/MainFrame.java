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
package lyricom.config3.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.zip.DataFormatException;
import javax.swing.*;
import static javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import lyricom.config3.AppProperties;
import lyricom.config3.comms.Connection;
import lyricom.config3.comms.Serial;
import lyricom.config3.model.Model;
import lyricom.config3.model.OutStream;
import lyricom.config3.model.Triggers;
import lyricom.config3.solutions.ESolutionType;
import lyricom.config3.solutions.SolutionsDataBase;
import lyricom.config3.solutions.SolutionsDataList;
import lyricom.config3.solutions.SolutionsUIBase;
import lyricom.config3.solutions.XMLSolutionsList;
import lyricom.config3.solutions.ui.CursorSpeedDlg;
import lyricom.config3.ui.selection.ESolution;
import lyricom.config3.ui.selection.SelectionDlg;

/**
 *
 * @author Andrew
 */
public class MainFrame extends JFrame {
    
    private static MainFrame instance = null;
    public static MainFrame getInstance() {
          if (instance == null) {
            instance = new MainFrame();
            instance.XMLLoadOnStartup();
        }
        return instance;
    }
    
    private boolean BLUETOOTH = false;
    JMenuItem wired;
    JMenuItem bluetooth;

    public boolean getBluetooth() {
        return BLUETOOTH;
    }
    
    public void setBluetooth(boolean val) {
        BLUETOOTH = val;
        if (BLUETOOTH) {
            bluetooth.setSelected(true);
        } else {
            wired.setSelected(true);
        }
    }
    
    private final List<JMenuItem> BT_INCAPABLE_MENU_ITEMS = new ArrayList<>();

    private static final ResourceBundle RES = ResourceBundle.getBundle("strings");
    private JTabbedPane pane = new JTabbedPane(); 
    
    private MainFrame() {
        setTitle(RES.getString("PROGRAM_NAME"));
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
//                if (inSyncCheck()) {
                    MainFrame.getInstance().dispose();
                    System.exit(0);
//                }
            }
        });
                
        setLayout(new BorderLayout());
        add(pane, BorderLayout.CENTER);
        add(buttons(), BorderLayout.WEST);
 
        setJMenuBar(menus());
        
        // Center on screen
        Dimension dim = new Dimension(1100,600);
        setSize(dim);
        Point center = ScreenInfo.getCenter();
        setLocation(center.x-dim.width/2, center.y-dim.height/2);

        
        setVisible(true);
    }
    
    JMenuBar menus() {
        JMenuBar bar = new JMenuBar();
        JMenu fileMenu = new JMenu(RES.getString("M_FILE"));
        bar.add(fileMenu);
        JMenuItem item = new JMenuItem(RES.getString("M_DOWNLOAD"));
        item.addActionListener((e) -> downloadBtnAction() );
        fileMenu.add(item);
        
        item = new JMenuItem(RES.getString("M_SAVE"));
        item.addActionListener((e) -> saveBtnAction() );
        fileMenu.add(item);
        item = new JMenuItem(RES.getString("M_LOAD"));
        item.addActionListener((e) -> loadBtnAction() );
        fileMenu.add(item);
        
        item = new JMenuItem(RES.getString("M_ABOUT"));
        item.addActionListener((e) -> new AboutDlg());
        fileMenu.add(item);
        
        item = new JMenuItem(RES.getString("M_EXIT"));
        item.addActionListener((e) -> System.exit(0) );
        fileMenu.add(item);
        
        JMenu addMenu = new JMenu("Add Solution");
        bar.add(addMenu);
        
        JMenu oneBtnMenu = new JMenu(RES.getString("M_ONE_BUTTON"));
        addMenu.add(oneBtnMenu);
        addSolutionMenu(oneBtnMenu, ESolutionType.SOL_ONE_BUTTON_SIMPLE);
        addSolutionMenu(oneBtnMenu, ESolutionType.SOL_ONE_BUTTON_TOGGLE);
        addSolutionMenu(oneBtnMenu, ESolutionType.SOL_ONE_BUTTON_MOUSE_CLICKS);
        addSolutionMenu(oneBtnMenu, ESolutionType.SOL_ONE_BUTTON_MOUSE);
        addSolutionMenu(oneBtnMenu, ESolutionType.SOL_KEYPRESS);
        addSolutionMenu(oneBtnMenu, ESolutionType.SOL_KEYBOARD);
        
        JMenu twoBtnMenu = new JMenu(RES.getString("M_TWO_BUTTONS"));
        addMenu.add(twoBtnMenu);
        addSolutionMenu(twoBtnMenu, ESolutionType.SOL_TWO_BUTTON_SIMPLE);
        addSolutionMenu(twoBtnMenu, ESolutionType.SOL_TWO_BUTTON_CURSOR_CONTROL);

        JMenu joystickMenu = new JMenu(RES.getString("M_JOYSTICK"));
        addMenu.add(joystickMenu);
        addSolutionMenu(joystickMenu, ESolutionType.SOL_JOYSTICK_1);
        addSolutionMenu(joystickMenu, ESolutionType.SOL_JOYSTICK_2);
        
        JMenu gyroMenu = new JMenu(RES.getString("M_GYRO"));
        addMenu.add(gyroMenu);
        addSolutionMenu(gyroMenu, ESolutionType.SOL_GYRO_MOUSE);
        
        JMenu connectionType = new JMenu(RES.getString("M_CONNECTION_TYPE"));
        bar.add(connectionType);
        wired = new JRadioButtonMenuItem(RES.getString("M_WIRED"));
        bluetooth = new JRadioButtonMenuItem(RES.getString("M_BLUETOOTH"));
        ButtonGroup group = new ButtonGroup();
        group.add(wired);
        group.add(bluetooth);
        connectionType.add(wired);
        connectionType.add(bluetooth);
        wired.setSelected(true);
        wired.addActionListener((e) -> setWired());
        bluetooth.addActionListener((e) -> setBluetooth());
        
        return bar;
    }
    
    // Called during menu creation - once for each solution type.
    // Creates the menu item and links to solution panel creation.
    // As a side effect - creates a list of BT incapable solutions.
    private void addSolutionMenu(JMenu parent, ESolutionType type) {
        JMenuItem menu = new JMenuItem(type.toString());
        menu.addActionListener((a) -> addSolutionPane(type));
        if (!type.worksOverBT()) {
            BT_INCAPABLE_MENU_ITEMS.add(menu); 
        }
        parent.add(menu);
    }
    
    JPanel buttons() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
        Border boarder = new LineBorder(Color.BLACK, 2);
        Border margin = new EmptyBorder(0, 10, 10, 10);
        p.setBorder(new CompoundBorder(boarder, margin));
        
        JButton addBtn = new JButton(Utils.getIcon(Utils.ICON_ADD));
        JButton uploadBtn = new JButton(Utils.getIcon(Utils.ICON_UPLOAD));
        JButton downloadBtn = new JButton(Utils.getIcon(Utils.ICON_DOWNLOAD));
        JButton cursorSpeedBtn = new JButton(Utils.getIcon(Utils.ICON_CURSOR_SPEED));
        JButton saveBtn = new JButton(Utils.getIcon(Utils.ICON_SAVE_FILE));
        
        Dimension d = new Dimension(32 ,32);
        addBtn.setPreferredSize(d);
        uploadBtn.setPreferredSize(d);
        downloadBtn.setPreferredSize(d);
        cursorSpeedBtn.setPreferredSize(d);
        saveBtn.setPreferredSize(d);

        addBtn.setToolTipText(RES.getString("TT_ADD"));        
        uploadBtn.setToolTipText(RES.getString("TT_UPLOAD"));      
        downloadBtn.setToolTipText(RES.getString("TT_DOWNLOAD")); 
        cursorSpeedBtn.setToolTipText(RES.getString("TT_CURSOR_SPEED")); 
        saveBtn.setToolTipText(RES.getString("TT_SAVE_FILE"));
        
        Box vb = Box.createVerticalBox();
        
        vb.add(Box.createVerticalStrut(30));
        vb.add(addBtn);
//        vb.add(Box.createVerticalStrut(20));
//        vb.add(uploadBtn);
        vb.add(Box.createVerticalStrut(20));
        vb.add(downloadBtn);
        vb.add(Box.createVerticalStrut(20));
        vb.add(cursorSpeedBtn);
        vb.add(Box.createVerticalStrut(20));
        vb.add(saveBtn);
        
        addBtn.addActionListener((ae) -> addBtnAction());
        uploadBtn.addActionListener((ae) -> uploadBtnAction());
        downloadBtn.addActionListener((ae) -> downloadBtnAction());
        cursorSpeedBtn.addActionListener((ae) -> cursorSpeedBtnAction());
        saveBtn.addActionListener((ae) -> saveBtnAction());
        p.add(vb);
        return p;
    }
    
    private void addBtnAction() {
        new SelectionDlg(this);
        // The dialog calls addSolutionPane when a selection is made.
/*        SolutionSelectionDlg dlg = new SolutionSelectionDlg(this);
        ESolutionType type = dlg.getSelection();
        if (type != null) {
            addSolutionPane(type);
        }    
*/
    }
    
    public void addSolutionPane(ESolution type) {
        JPanel pp = type.createSolution(null);
        if (pp == null) return;
        pane.addTab(type.toString(), pp);
        pane.setSelectedComponent(pp); 
        repaint();       
    }
    
    private void addSolutionPane(ESolutionType type) {
        JPanel pp = type.createSolution(null);
        pane.addTab(type.toString(), pp);
        pane.setSelectedComponent(pp); 
        repaint();       
    }
    
    private void uploadBtnAction() {
        
    }
    
    private void downloadBtnAction() {
        SolutionsDataList solList = SolutionsDataList.getInstance();
        Triggers triggers = Triggers.getInstance();
        
        // Check for over-use of a port.
        if (solList.portUsageCheck() != true) {
            return;
        }
        
        // Create the triggers
        triggers.deleteAll();
        solList.compile();
        
        // Check for excessive complexity
        if (triggers.length() > 40) {
            JOptionPane.showMessageDialog(MainFrame.getInstance(), 
                RES.getString("SC_TOO_MANY_TRIGGERS"),
                RES.getString("SC_TOO_MANY_TRIGGERS_TITLE"),
                JOptionPane.ERROR_MESSAGE);  
            return;
        }

        // Everything looks good.  Let get connected.
        Connection conn = Connection.getInstance();
        if (!conn.isConnected()) {
            if (!conn.establishConnection()) {
                return;
            }
        }
        OutStream os;
        try {
            os = triggers.getTriggerData();
        } catch (DataFormatException ex) {
            JOptionPane.showMessageDialog(MainFrame.getInstance(), 
                RES.getString("CMT_INTERNAL_ERROR"),
                RES.getString("CMT_DATA_ERROR"),
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        Serial.getInstance().writeList(os.getBuffer());
        Serial.getInstance().writeByte(Model.CMD_RUN);
        
        XMLSaveAfterDownload();
        
        JOptionPane.showMessageDialog(MainFrame.getInstance(),
                RES.getString("DOWNLOAD_COMPLETE"),
                RES.getString("DOWNLOAD_COMPLETE"),
                JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void cursorSpeedBtnAction() {
        new CursorSpeedDlg();
    }
    
    private void saveBtnAction() {
        JFileChooser fileChooser = new JFileChooser();
        AppProperties props = AppProperties.getInstance();
        String lastDir = props.getProperty("last.import");
        if (lastDir != null) {
            fileChooser.setCurrentDirectory(new File(lastDir));
        }
        
        int result = fileChooser.showSaveDialog(MainFrame.this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File output = fileChooser.getSelectedFile();
            props.setProperty("last.import", output.getParent().toString());
            // Force the extension to be .xml unless an extension was already 
            // provided.
            String fileName = output.getAbsolutePath();           
            if (! fileName.toLowerCase().endsWith(".xml")) {
                fileName += ".xml";
                output = new File(fileName);
            }
            boolean writeIt = false;
            if (output.exists()) {
                result = JOptionPane.showConfirmDialog(MainFrame.this,
                    RES.getString("FILE_EXISTS_TEXT"),
                    RES.getString("FILE_EXISTS_TITLE"),
                    JOptionPane.YES_NO_OPTION);
                if (result == JOptionPane.YES_OPTION) {
                    writeIt = true;
                }
            } else {    // File does not exist
                writeIt = true;
            }
            if (writeIt) {
                try {
                    XMLWrite(output);
                } catch (FileNotFoundException | JAXBException e) {
                    JOptionPane.showMessageDialog(MainFrame.this, 
                        RES.getString("IO_ERROR_TEXT"),
                        RES.getString("IO_ERROR_TITLE"),
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        }            
    }
    
    private void loadBtnAction() {
        JFileChooser fileChooser = new JFileChooser();
        AppProperties props = AppProperties.getInstance();
        String lastDir = props.getProperty("last.import");
        if (lastDir != null) {
            fileChooser.setCurrentDirectory(new File(lastDir));
        }
        
        int result = fileChooser.showOpenDialog(MainFrame.this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File input = fileChooser.getSelectedFile();
            props.setProperty("last.import", input.getParent().toString());
            try {
                XMLRead(input);
                              
            } catch (JAXBException ex) {
                JOptionPane.showMessageDialog(MainFrame.this,
                    RES.getString("IMPORT_FAILED_TEXT"),
                    RES.getString("IMPORT_FAILED_TITLE"),
                    JOptionPane.ERROR_MESSAGE);
            
            } 
        }         
    }
    
    public void deleteSolution(SolutionsUIBase sol) {
        pane.remove(sol);
    }
   
    private void setWired() {
        BLUETOOTH = false;   
        for(JMenuItem mi: BT_INCAPABLE_MENU_ITEMS) {
            mi.setEnabled(true);
        }
    }
    
    private void setBluetooth() {
        SolutionsDataList sdl = SolutionsDataList.getInstance();
        ESolutionType nonBT = sdl.bluetoothCheck();
        if (nonBT != null) {
            wired.setSelected(true);
            JOptionPane.showMessageDialog(MainFrame.this, 
                 String.format( RES.getString("SC_BLUETOOTH_ERROR"),
                       nonBT.toString()),
                 RES.getString("SC_BLUETOOTH_ERROR_TITLE"),
                 JOptionPane.ERROR_MESSAGE);
            return;
        }
        BLUETOOTH = true;
        for(JMenuItem mi: BT_INCAPABLE_MENU_ITEMS) {
            mi.setEnabled(false);
        }
    }
    
    // ---------------------------------------
    // XML Support
    private void XMLSaveAfterDownload() {
        try {
            AppProperties props = AppProperties.getInstance();
            XMLWrite(props.getLastDownloadLocation());
        } catch (FileNotFoundException | JAXBException f) {
            // Fail quietly
        }
    }
    
    private void XMLLoadOnStartup() {
        AppProperties props = AppProperties.getInstance();
        File target = props.getLastDownloadLocation();
        if (target.exists() && target.canRead()) {
            try {
                XMLRead(target);
            } catch (JAXBException e) {
                // Fail quietly
            }
        }
    }
    
    private JAXBContext contextObj = null;
    private JAXBContext getContextObj() throws JAXBException {
        if (contextObj == null) {
            contextObj = JAXBContext.newInstance(XMLSolutionsList.class); 
        }
        return contextObj;
    }
    
    private void XMLWrite(File target) throws JAXBException, FileNotFoundException {
        JAXBContext jaxbContext = getContextObj();

        Marshaller marshallerObj = jaxbContext.createMarshaller();  
        marshallerObj.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true); 

        XMLSolutionsList list = SolutionsDataList.getInstance().getXMLRoot();

        marshallerObj.marshal(list, new FileOutputStream(target)); 
    }
    
    private void XMLRead(File target) throws JAXBException {
        JAXBContext jaxbContext = getContextObj();

        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();    
        XMLSolutionsList list=(XMLSolutionsList) jaxbUnmarshaller.unmarshal(target);    
        SolutionsDataList.getInstance().updateFromXML(list);

        pane.removeAll();
        List<SolutionsDataBase> newList = SolutionsDataList.getInstance().getList();
        for(SolutionsDataBase sdb: newList) {
            ESolutionType type = sdb.getType();                
            JPanel pp = type.createSolution(sdb);
            pane.addTab(type.toString(), pp);
        }
        repaint();
    }
        
}
