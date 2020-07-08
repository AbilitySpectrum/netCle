/* * * * * * * * * * * * * * * * * * * * * * * * * * * * 
    Copyright (C) 2019 Andrew Hodgson

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
package lyricom.netCleConfig.ui;

import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.zip.DataFormatException;
import static javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import lyricom.netCleConfig.comms.Connection;
import lyricom.netCleConfig.comms.Serial;
import lyricom.netCleConfig.model.ExportFilter;
import lyricom.netCleConfig.model.IOError;
import lyricom.netCleConfig.model.ImportFilter;
import lyricom.netCleConfig.model.InStream;
import lyricom.netCleConfig.model.MRes;
import lyricom.netCleConfig.model.Model;
import lyricom.netCleConfig.model.OutStream;
import lyricom.netCleConfig.model.SensorGroup;
import lyricom.netCleConfig.model.TmpImport;
import lyricom.netCleConfig.model.Triggers;
import lyricom.netCleConfig.model.TriggerCallback;

/**
 * Defines the main frame of the application, its control buttons
 * and its tabs.
 * Holds a list of SensorGroupPanel items.
 * @author Andrew
 */
public class MainFrame extends JFrame implements TriggerCallback {
    
    public static MainFrame TheFrame;
    private static final ResourceBundle RES = ResourceBundle.getBundle("strings");

    private final List<SensorGroupPanel> sensorGroups = new ArrayList<>();
    
    public MainFrame() {
        setTitle(RES.getString("PROGRAM_NAME"));
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (inSyncCheck()) {
                    TheFrame.dispose();
                    System.exit(0);
                }
            }
        });

        TheFrame = this;
        
        setLayout(new BorderLayout());

        JComponent tc = triggerCount();
        add(buttonPanel(tc), BorderLayout.WEST); 

        add(tabbedPanes(), BorderLayout.CENTER);
        pack();
 
        // Center on screen
        Dimension dim = new Dimension(1100,600);
        setSize(dim);
        Point center = ScreenInfo.getCenter();
        setLocation(center.x-dim.width/2, center.y-dim.height/2);

        setVisible(true);
        
        // Get triggers after frame is set up.
        // This will happen on the initial connection only.
        // Not on a reconnection - since that does not rebuild the main frame.
        Connection.getInstance().writeByte(Model.CMD_GET_TRIGGERS);
    }
    
    private static final int BTN_SPACING = 10;
    
    private int MAX_TRIGGERS;
    private JLabel triggerCntLbl;
    private int triggerCnt;
    private JComponent triggerCount() {
        // In the future this will be set based on the
        // version number of the netCle board
        // ( from Connection.getInstance.getVersionID(); )
        MAX_TRIGGERS = 40;
        JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 5));
        triggerCntLbl = new JLabel("");
        p.add(triggerCntLbl);
        JLabel l = new JLabel(" " + RES.getString("TRIGGER_COUNT"));
        p.add(l);
        Triggers t = Triggers.getInstance();
        t.addCallback(this);
        return p;
    }
    
    // Don't put up a too-many-triggers message if user is deleting triggers.
    boolean suppressWarning = false;
    @Override
    public void newTriggerCount(int count) {            
        suppressWarning = count < triggerCnt;
        triggerCnt = count;
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                triggerCntLbl.setText( Integer.toString(triggerCnt) );
                if (triggerCnt > MAX_TRIGGERS) {
                    triggerCntLbl.setForeground(Color.red);
                    if (!suppressWarning) {
                        triggerWarning();
                    }
                } else {
                   triggerCntLbl.setForeground(Color.black); 
                }                       
            }
        });
     }

    // Multiple trigger changes can come in quickly, especially if a solution
    // is selected.  This code ensures that the too-many-triggers warning
    // will only be displayed once for a burst of changes.
    private volatile long lastWarningTime = 0;
    private void triggerWarning() {
        if (System.currentTimeMillis() > (lastWarningTime + 5000)) {
            lastWarningTime = System.currentTimeMillis();
            JOptionPane.showMessageDialog(MainFrame.TheFrame, 
                String.format(RES.getString("TOO_MANY_TRIGGERS"), triggerCnt, MAX_TRIGGERS),
                RES.getString("TOO_MANY_TRIGGERS_TITLE"),
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private JButton getBtn;
    private JButton saveBtn;
    private JButton clearAllBtn;
    private JButton runBtn;
    private JButton idleBtn;
    private JButton importBtn;
    private JButton exportBtn;
    private JButton aboutBtn;
    private JButton exitBtn;
//    private JButton testBtn;
    
    private JComponent buttonPanel(JComponent tc) {
        JPanel p = new JPanel();
        Border boarder = new LineBorder(Color.BLACK, 2);
        Border margin = new EmptyBorder(0, 10, 10, 10);
        p.setBorder(new CompoundBorder(boarder, margin));
        
        Box vb = Box.createVerticalBox();
        vb.add(tc);
        vb.add(Box.createVerticalStrut(15));
        
        JButton[] buttons = createButtons();
        
        // Make all buttons the same size
        int height = 0;
        int maxWidth = 0;
        Dimension dim = new Dimension();
        for(JButton b: buttons) {
            dim = b.getPreferredSize();
            height = dim.height;
            if (dim.width > maxWidth) {
                maxWidth = dim.width;
            }
        }
        dim.height = height;
        dim.width = maxWidth;
        for(JButton b: buttons) {
            b.setPreferredSize(dim);
            b.setMaximumSize(dim);
        }

        // Display buttons
        vb.add(getBtn);
        vb.add(Box.createVerticalStrut(BTN_SPACING));
        vb.add(saveBtn);
        vb.add(Box.createVerticalStrut(BTN_SPACING));
        vb.add(clearAllBtn);
        vb.add(Box.createVerticalStrut(BTN_SPACING));
        vb.add(new JSeparator(JSeparator.HORIZONTAL));
        vb.add(Box.createVerticalStrut(BTN_SPACING));
        vb.add(runBtn);
        runBtn.setMnemonic(KeyEvent.VK_R);
        vb.add(Box.createVerticalStrut(BTN_SPACING));
        vb.add(idleBtn);
        idleBtn.setMnemonic(KeyEvent.VK_I);
        vb.add(Box.createVerticalStrut(BTN_SPACING));
        vb.add(new JSeparator(JSeparator.HORIZONTAL));
        vb.add(Box.createVerticalStrut(BTN_SPACING));
        vb.add(importBtn);
        vb.add(Box.createVerticalStrut(BTN_SPACING));
        vb.add(exportBtn);
        vb.add(Box.createVerticalStrut(BTN_SPACING));
        vb.add(new JSeparator(JSeparator.HORIZONTAL));
        vb.add(Box.createVerticalStrut(BTN_SPACING));
        vb.add(aboutBtn);
        vb.add(Box.createVerticalStrut(BTN_SPACING));
        vb.add(exitBtn);        
                     
        p.add(vb);
        return p;
    }
    
    private JButton[] createButtons() {
        getBtn = newBtn(RES.getString("BTN_GET"));
        saveBtn = newBtn(RES.getString("BTN_SAVE"));
        clearAllBtn = newBtn(RES.getString("BTN_CLEAR_ALL"));
        runBtn = newBtn(RES.getString("BTN_RUN"));
        idleBtn = newBtn(RES.getString("BTN_IDLE"));
        importBtn = newBtn(RES.getString("BTN_IMPORT"));
        exportBtn = newBtn(RES.getString("BTN_EXPORT"));
        aboutBtn = newBtn(RES.getString("BTN_ABOUT"));
        exitBtn = newBtn(RES.getString("BTN_EXIT"));
        
        getBtn.setToolTipText(RES.getString("BTN_GET_TTT"));
        saveBtn.setToolTipText(RES.getString("BTN_SAVE_TTT"));
        clearAllBtn.setToolTipText(RES.getString("BTN_CLEAR_ALL_TTT"));
        runBtn.setToolTipText(RES.getString("BTN_RUN_TTT"));
        idleBtn.setToolTipText(RES.getString("BTN_IDLE_TTT"));
        importBtn.setToolTipText(RES.getString("BTN_IMPORT_TTT"));
        exportBtn.setToolTipText(RES.getString("BTN_EXPORT_TTT"));
        exitBtn.setToolTipText(RES.getString("BTN_EXIT_TTT"));
        
        getBtn.addActionListener(e -> Connection.getInstance().writeByte(Model.CMD_GET_TRIGGERS));
        saveBtn.addActionListener(e -> doSave());
        clearAllBtn.addActionListener(e -> doClearAll());
        runBtn.addActionListener (e -> {
            if (inSyncCheck()) {
                Connection.getInstance().writeByte(Model.CMD_RUN);
            }
        });
        idleBtn.addActionListener(e -> Connection.getInstance().writeByte(Model.CMD_VERSION));
        importBtn.addActionListener(e -> doImport());
        exportBtn.addActionListener(e -> doExport());
        aboutBtn.addActionListener(e -> {
            new AboutDlg();
        });
        exitBtn.addActionListener(e -> {
            if (inSyncCheck()) {
                System.exit(0);
            }
        });
        
//        JButton testBtn = newBtn("Test");
//        testBtn.addActionListener(e -> displayTriggers());

        JButton[] buttons =  {
            getBtn,
            saveBtn,
            clearAllBtn,
            runBtn,
            idleBtn,
            importBtn,
            exportBtn,
            aboutBtn,
//            testBtn,
            exitBtn
        };
        
        return buttons;
    }
    
    private JButton newBtn(String title) {
        JButton b = new JButton(title);
        b.setAlignmentX(0.5F);
        
        return b;
    }
    
    private boolean doSave() {
        OutStream os;
        
        if (triggerCnt > MAX_TRIGGERS) {
            JOptionPane.showMessageDialog(MainFrame.TheFrame, 
                String.format(RES.getString("TOO_MANY_TRIGGERS"), triggerCnt, MAX_TRIGGERS),
                RES.getString("TOO_MANY_TRIGGERS_TITLE"),
                JOptionPane.ERROR_MESSAGE);
            return false;
        }
        try {
            os = Triggers.getInstance().getAllTriggerData();
        } catch (DataFormatException ex) {
            JOptionPane.showMessageDialog(MainFrame.TheFrame, 
                RES.getString("INTERNAL_ERROR"),
                RES.getString("DATA_ERROR_TITLE"),
                JOptionPane.ERROR_MESSAGE);
            return false;
        }
        Connection.getInstance().writeList(os.getBuffer());
        Triggers.DATA_IN_SYNC = true;
        return true;
    }
    
    private void doClearAll() {
        int result = JOptionPane.showConfirmDialog(MainFrame.this,
            RES.getString("CLEAR_ALL_TRIGGERS_TEXT"),
            RES.getString("CLEAR_ALL_TRIGGERS_TITLE"),
            JOptionPane.YES_NO_OPTION); 
        if (result == JOptionPane.NO_OPTION) {
            return;
        }
        
        Triggers.getInstance().deleteAll();
        SensorPanel.reloadTriggers();    
        Triggers.DATA_IN_SYNC = true;
    }
    
    private void doImport() {
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
                FileInputStream fis = new FileInputStream(input);
                List<Byte> bytes = new ArrayList<>();
                int val;
                do {
                    val = fis.read();
                    if (val != -1) {
                        bytes.add((byte)val);
                    }
                } while (val != Model.END_OF_BLOCK && val != -1);
                
                InStream is = new InStream(bytes);
                TmpImport tmp = Triggers.getInstance().readTriggers(is);
                ImportDlg dlg = new ImportDlg(this, tmp);
                ImportFilter filter = dlg.getFilter();
                if (filter == null) return;
                Triggers.getInstance().loadTriggers(tmp, filter);
                SensorPanel.reloadTriggers();      
                               
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(MainFrame.this,
                    RES.getString("IMPORT_FAILED_TEXT"),
                    RES.getString("IMPORT_FAILED_TITLE"),
                    JOptionPane.ERROR_MESSAGE);
            
            } catch(IOError e) {
                JOptionPane.showMessageDialog(MainFrame.TheFrame, 
                    RES.getString("DATA_ERROR_TEXT") + "\n" + e.getMessage(),
                    RES.getString("DATA_ERROR_TITLE"),
                    JOptionPane.ERROR_MESSAGE);
            }
        }        
    }
    
    private void doExport() {
        ExportDlg dlg = new ExportDlg(this);
        ExportFilter filter = dlg.getFilter();
        if (filter == null) return;        
        
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
            // Force the extension to be .txt unless an extension was already 
            // provided.
            String fileName = output.getAbsolutePath();           
            if (! fileName.toLowerCase().endsWith(".txt")) {
                fileName += ".txt";
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
                OutStream os;
                try {
                    os = Triggers.getInstance().getTriggerData(filter);
                } catch (DataFormatException ex) {
                    JOptionPane.showMessageDialog(MainFrame.TheFrame, 
                        RES.getString("INTERNAL_ERROR"),
                        RES.getString("DATA_ERROR_TITLE"),
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
                        try {
                    FileOutputStream fos = new FileOutputStream(output);
                    for(Byte b: os.getBuffer()) {
                        fos.write(b);
                    }
                    fos.close();
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(MainFrame.this, 
                        RES.getString("IO_ERROR_TEXT"),
                        RES.getString("IO_ERROR_TITLE"),
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        }        
    }
    
    private void displayTriggers() {
        OutStream os;
        try {
            os = Triggers.getInstance().getAllTriggerData();
        } catch (DataFormatException ex) {
            JOptionPane.showMessageDialog(MainFrame.TheFrame, 
                RES.getString("INTERNAL_ERROR"),
                RES.getString("DATA_ERROR_TITLE"),
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        byte[] bytes = Utils.listToArray(os.getBuffer());
        String s = new String(bytes);
        System.out.println(s);
    }
    
    
    private JComponent tabbedPanes() {
        JTabbedPane pane = new JTabbedPane();
        
        ImageIcon icon = Utils.getIcon(Utils.ICON_EMPTY);
        int tabNumber = 0;
        for(SensorGroup g: Model.getSensorGroups()) {
            PaneStatusCntrl psc = new PaneStatusCntrl(pane, tabNumber);
            SensorGroupPanel p = new SensorGroupPanel(g, psc);
            sensorGroups.add(p);
            pane.addTab(g.getName(), icon, p);
            tabNumber++;
        }
        
        JPanel p = new MouseSpeedPanel();
        pane.add(MRes.getStr("MOUSE_SPEED"), p);
        
        p = new TVSelectionPanel();
        pane.add(MRes.getStr("TV_SELECTION"), p);
                 
        return pane;
    }
    
    public void showGroupPanel(SensorGroup g) {
        for(SensorGroupPanel p: sensorGroups) {
            if (p.getGroup() == g) {
                p.makeVisible();
            }
        }
    }
        
    // If true is returned continue with the operation.
    // If false is returned cancel the operation.
    private boolean inSyncCheck() {
        if (Triggers.DATA_IN_SYNC == false) {
            int opt = JOptionPane.showConfirmDialog(this, 
                    RES.getString("UNSAVED_TEXT"),
                    RES.getString("UNSAVED_TITLE"),
                    JOptionPane.YES_NO_CANCEL_OPTION
            );
            if (opt == JOptionPane.YES_OPTION) {
                return doSave();
            } else if (opt == JOptionPane.NO_OPTION) {
                return true;
            } else {
                return false;  // must be cancel
            }
        }
        return true;
    }

}
