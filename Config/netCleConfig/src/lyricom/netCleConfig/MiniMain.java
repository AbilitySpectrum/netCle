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
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.DataFormatException;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import lyricom.netCleConfig.comms.Connection;
import lyricom.netCleConfig.comms.Serial;
import lyricom.netCleConfig.model.IOError;
import lyricom.netCleConfig.model.ImportFilter;
import lyricom.netCleConfig.model.InStream;
import lyricom.netCleConfig.model.Model;
import lyricom.netCleConfig.model.OutStream;
import lyricom.netCleConfig.model.TmpImport;
import lyricom.netCleConfig.model.Triggers;
import lyricom.netCleConfig.ui.MainFrame;
import lyricom.netCleConfig.ui.ScreenInfo;
import lyricom.netCleConfig.ui.Utils;

/**
 *
 * @author Andrew
 */
public class MiniMain extends JFrame implements ActionListener {
    private static final ResourceBundle RES = ResourceBundle.getBundle("strings");
    
    public static void main(String[] args) {
         
        Connection conn = Connection.getInstance();
        conn.establishConnection();
        
        Model.initModel(conn.getVersionID());
//        SolutionRegister.init();
        
        SwingUtilities.invokeLater(() -> {
            new MiniMain();
        });
    }
    
    public MiniMain() {
        super();
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setTitle("netCle Configuration Loader");
        setLayout(new BorderLayout());
        
        JTextArea lbl = new JTextArea();
        lbl.setText("Click on OK, and then select the\nconfiguration file you want to load.");
        lbl.setFont(Utils.TITLE_FONT);
        lbl.setEditable(false);
        lbl.setBorder(new EmptyBorder(15,15,15,15));
        
        add(lbl, BorderLayout.CENTER);
        
        JButton btn = new JButton("OK");
        btn.addActionListener(this);
        JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER));
        p.add(btn);
        add(p, BorderLayout.SOUTH);
                
        pack();
        // Center on screen
        Dimension dim = getPreferredSize();
        System.out.println(dim.width);
        if (dim.width < 400) dim.width = 400;
        setSize(dim);
        Point center = ScreenInfo.getCenter();
        setLocation(center.x-dim.width/2, center.y-dim.height/2);
        
        setVisible(true);
    }
    
    @Override
    public void actionPerformed(ActionEvent ae) {
        if (doImport()) {
            doSave();
            Serial.getInstance().writeByte(Model.CMD_RUN);
            System.exit(0);
        }
    }
    
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

    private boolean doSave() {
        OutStream os;
        
        try {
            os = Triggers.getInstance().getAllTriggerData();
        } catch (DataFormatException ex) {
            JOptionPane.showMessageDialog(null, 
                RES.getString("INTERNAL_ERROR"),
                RES.getString("DATA_ERROR_TITLE"),
                JOptionPane.ERROR_MESSAGE);
            return false;
        }
        Serial.getInstance().writeList(os.getBuffer());
        Triggers.DATA_IN_SYNC = true;
        return true;
    }
}
