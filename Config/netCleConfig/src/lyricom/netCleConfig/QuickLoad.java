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
/*
 * MiniMain - a minimal program that simply imports a config file and saves
 * it to the hub.
*/

package lyricom.netCleConfig;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.zip.DataFormatException;
import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import lyricom.netCleConfig.comms.Connection;
import lyricom.netCleConfig.comms.Serial;
import lyricom.netCleConfig.comms.Shortcut;
import lyricom.netCleConfig.model.IOError;
import lyricom.netCleConfig.model.ImportFilter;
import lyricom.netCleConfig.model.InStream;
import lyricom.netCleConfig.model.Model;
import lyricom.netCleConfig.model.OutStream;
import lyricom.netCleConfig.model.TmpImport;
import lyricom.netCleConfig.model.Triggers;
import lyricom.netCleConfig.ui.ScreenInfo;
import lyricom.netCleConfig.ui.TriggerPanel;
import lyricom.netCleConfig.ui.Utils;

/**
 *
 * @author Andrew
 */
public class QuickLoad extends JFrame implements ActionListener, Shortcut, KeyListener {
    private static final ResourceBundle RES = ResourceBundle.getBundle("strings");
    // Action Commands
    private static final String COPY  = "Copy";
    private static final String PASTE = "Paste";
    private static final String CLOSE = "Close";
    private static final String IDLE  = "Idle";
    private static final String RUN   = "Run";
    
    private static Connection conn;
    
    public static void main(String[] args) {
        conn = Connection.getInstance();
        conn.establishConnection();
        
        Model.initModel(conn.getVersionID());
        
        SwingUtilities.invokeLater(() -> {
            new QuickLoad();
        });
    }
    
    private JTextArea textArea;
    
    public QuickLoad() {
        super();
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setTitle(RES.getString("QUICKLOAD_TITLE"));
        setLayout(new BorderLayout());
        
        // Get the connection to send us the config when sent from netCle
        conn.registerShortcut(this);
        
        textArea = new JTextArea();
        textArea.setText("To send:\n"
                + "click Copy, then paste into an e-mail.\n\n"
                + "To receive:\n"
                + "copy e-mail containing configuration\n"
                + "and then Paste here.\n\n"
                + "You can also drag and drop text or config files\n"
                + "to this window.");
        textArea.setFont(Utils.TITLE_FONT);
        textArea.setForeground(new Color(0x606050));
        textArea.setEditable(false);
        textArea.setBorder(new CompoundBorder(
                new LineBorder(Color.BLACK, 2),
                new EmptyBorder(15,15,15,15))
        );
        textArea.setDragEnabled(true);
        textArea.setTransferHandler(new DandDImport(this));
        textArea.setFocusable(true);
        textArea.addKeyListener(this);
        
        add(textArea, BorderLayout.CENTER);
        
        add(buttons(), BorderLayout.SOUTH);
                
        createMenus();
        MouseListener pListener = new PopupListener();
        textArea.addMouseListener(pListener);
        
        pack();
        // Center on screen
        Dimension dim = getPreferredSize();
        if (dim.width < 400) dim.width = 400;
        setSize(dim);
        Point center = ScreenInfo.getCenter();
        setLocation(center.x-dim.width/2, center.y-dim.height/2);
        
        setVisible(true);
    }
    
    private JPanel buttons() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton btn = new JButton("Copy");
        btn.addActionListener(this);
        btn.setActionCommand(COPY);
        p.add(btn);
 
        btn = new JButton("Paste");
        btn.addActionListener(this);
        btn.setActionCommand(PASTE);
        p.add(btn);
        
        btn = new JButton("Close");
        btn.addActionListener(this);
        btn.setActionCommand(CLOSE);
        p.add(btn);

        return p;
    }
    
    private JPopupMenu popup;
    private JMenuItem copyMI;
    private JMenuItem pasteMI;
    private JMenuItem idleMI;
    private JMenuItem runMI;
    
    private void createMenus() {
        popup = new JPopupMenu();
        copyMI = new JMenuItem("Copy configuration");
        pasteMI = new JMenuItem("Paste configuration");
        idleMI = new JMenuItem("Idle Mode");
        runMI = new JMenuItem("Run Mode");
        
        popup.add(copyMI);
        popup.add(pasteMI);
        popup.add(idleMI);
        popup.add(runMI);
        
        copyMI.addActionListener(this);
        copyMI.setActionCommand(COPY);
        pasteMI.addActionListener(this);
        pasteMI.setActionCommand(PASTE);
        idleMI.addActionListener(this);
        idleMI.setActionCommand(IDLE);
        runMI.addActionListener(this);
        runMI.setActionCommand(RUN);

        copyMI.setEnabled(true);
        pasteMI.setEnabled(true);
        idleMI.setEnabled(true);
        runMI.setEnabled(true);
    }
    
    public void setText(String s) {
        textArea.setText(s);
    }
    
    // Button actions
    @Override
    public void actionPerformed(ActionEvent ae) {
        String ac = ae.getActionCommand();
        if (ac == COPY) {
           Serial.getInstance().writeByte(Model.CMD_GET_TRIGGERS);
           // ... data will arrive at configDataForExport() below.
           
        } else if (ac == PASTE) {
            doPaste();
            
        } else if (ac == CLOSE) {
            System.exit(0);
            
        } else if (ac == IDLE) {
            Serial.getInstance().writeByte(Model.CMD_VERSION);
            
        } else if (ac == RUN) {
            Serial.getInstance().writeByte(Model.CMD_RUN);
        }
    }

    // ------------------------------------
    // Support for COPY
    // ------------------------------------
    // Connection sends configuration data received from
    // netCle to this routine.
    @Override
    public void configDataForExport(List<Byte> data) {
        String buf = formatForExport(data);
        StringSelection selection = new StringSelection(buf);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(selection, selection);
        textArea.setText("Configuration copied to clipboard.");
    }
    
    // Functions to support export
    private String formatForExport(List<Byte> data) {
        StringBuffer buf = new StringBuffer();
        int count = 0;
        boolean firstLine = true;
        boolean lastLine = false;
        
        for(Byte b: data) {
            char c = (char) b.byteValue();
            buf.append(c);
            count++;
            if (firstLine) {
                if (count == 4) {
                    buf.append('\n');
                    firstLine = false;
                    count = 0;
                }
            } else if (!lastLine) {
                if (count == 1 && c == 'Y') {
                    lastLine = true;
                }
                if (count == 23) {
                    buf.append('\n');
                    count = 0;
                }
            }
        }
        return buf.toString();
    }
    
    // --------------------------------------
    // Support for PASTE
    // --------------------------------------
    // Paste ...
    private void doPaste() {
        // Get clipboard data
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        try {
            String pastedData = (String) clipboard.getData(DataFlavor.stringFlavor);
            StringReader stream = new StringReader(pastedData);
            loadConfigurationData(stream);
                                
        } catch (UnsupportedFlavorException ex) {
            textArea.setText("Sorry, there is no suitable data\non the clipboard.");

        } catch (IOException ex) {
            textArea.setText("Unable to access clipboard data.");  
 
        }
    }
    
    // Drag and drop file ...
    public void loadFromFile(File f) throws FileNotFoundException {
        FileInputStream fis = new FileInputStream(f);
        InputStreamReader inStream = new InputStreamReader(fis);
        loadConfigurationData(inStream);
    }
    
    // Drag and drop string ...
    public void loadFromString(String s) {
        StringReader inStream = new StringReader(s);
        loadConfigurationData(inStream);
    }
    
    // loadConfiguration handles config for paste and drop actions.
    private void loadConfigurationData(Reader stream) {
        try {
            String configData = findConfigData(stream);
            doImport(configData);
            doSave();
            Serial.getInstance().writeByte(Model.CMD_RUN);
            setText("New configuration saved to netClé");
            
        } catch (IOError ex) {
            setText("Error in configuration data:\n" + ex.getMessage());
        } catch (DataFormatException | IOException ex) {
            setText("Unexpected error.\n" + ex.getMessage());
        }
    }
    // Input data could be a whole e-mail.
    // Find the section of the string which contains the configuration data.
    private static String findConfigData(Reader stream) throws IOException, IOError {
        StringBuilder buf = new StringBuilder();
        int nextChr;
        
        while((nextChr = stream.read()) != -1) {
            if (nextChr == 'T') {
                nextChr = stream.read();
                if (nextChr == '1' || nextChr == '`') {
                    // Found T1 or T` - looks like start of config
                    buf.append('T');
                    buf.append((char)nextChr);
                    do {
                        nextChr = stream.read();
                        if (nextChr != -1) {
                            buf.append((char)nextChr);
                        }
                    } while (nextChr != Model.END_OF_BLOCK && nextChr != -1);
                    if (nextChr == -1) {
                        throw new IOError("The pasted text does not contain\n"
                            + "any configuration data.");
                    } else {
                        return buf.toString();
                    }
                }
            }
        }
        
        throw new IOError("The pasted text does not contain\n"
                            + "any configuration data.");
    }
    
    // doImport
    // gets a string that is a clean config-data string.
    // Loads the triggers - doing error checking.
    private static void doImport(String s) throws IOError {
        byte[] bytes = s.getBytes();
        InStream is = new InStream(bytes);
        TmpImport tmp = Triggers.getInstance().readTriggers(is);
        ImportFilter filter = new ImportFilter();
        filter.setOverwrite(true);
        Triggers.getInstance().loadTriggers(tmp, filter);   
    }
    
    // Sends loaded triggers to netCle.
    private static void doSave() throws DataFormatException {
        OutStream os;
        
        os = Triggers.getInstance().getAllTriggerData();
        Serial.getInstance().writeList(os.getBuffer());
        Triggers.DATA_IN_SYNC = true;
    }

    boolean controlDown = false;

    // -------------------------------
    // Keyboard support    
    @Override
    public void keyTyped(KeyEvent ke) {
    }

    @Override
    public void keyPressed(KeyEvent ke) {
        if (ke.isControlDown()) {
            if (ke.getKeyCode() == KeyEvent.VK_C) {
                Serial.getInstance().writeByte(Model.CMD_GET_TRIGGERS);

            }
            if (ke.getKeyCode() == KeyEvent.VK_V) {
                doPaste();
            }
            if (ke.getKeyCode() == KeyEvent.VK_I) {
                Serial.getInstance().writeByte(Model.CMD_VERSION);
            }
            if (ke.getKeyCode() == KeyEvent.VK_R) {
                Serial.getInstance().writeByte(Model.CMD_RUN);
            }
        }
     }

    @Override
    public void keyReleased(KeyEvent ke) {
    }
    // ------------------------------------------

    class PopupListener extends MouseAdapter {
        @Override
        public void mousePressed(MouseEvent e) {
            maybeShowPopup(e);
        }
        
        @Override
        public void mouseReleased(MouseEvent e) {
            maybeShowPopup(e);
        }
        
        private void maybeShowPopup(MouseEvent e) {
            if (e.isPopupTrigger()) {
                popup.show(e.getComponent(), e.getX(), e.getY());
            }
        }
    }

    // -------------------------------------------
    // UNUSED AT THIS TIME
    // -------------------------------------------
    /*
    
/*    - UNUSED.  Code to open a file browser to find a config file.
    private boolean doImport() {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(null);
        if (result == JFileChooser.APPROVE_OPTION) {
            File input = fileChooser.getSelectedFile();
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(input);
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
                ImportFilter filter = new ImportFilter();
                filter.setOverwrite(true);
                Triggers.getInstance().loadTriggers(tmp, filter);
                return true;
                               
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(null,
                    RES.getString("IMPORT_FAILED_TEXT"),
                    RES.getString("IMPORT_FAILED_TITLE"),
                    JOptionPane.ERROR_MESSAGE);
                return false;
            
            } catch(IOError e) {
                JOptionPane.showMessageDialog(null, 
                    RES.getString("DATA_ERROR_TEXT") + "\n" + e.getMessage(),
                    RES.getString("DATA_ERROR_TITLE"),
                    JOptionPane.ERROR_MESSAGE);
                return false;
            } finally {
                if (fis != null) {
                    try {
                        fis.close();
                    } catch (IOException ex) {                        
                    }
                }
            }
        }     
        return false;
    }
*/
}
