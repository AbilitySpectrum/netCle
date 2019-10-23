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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Point;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import lyricom.netCleConfig.model.ExportFilter;
import lyricom.netCleConfig.model.Sensor;
import lyricom.netCleConfig.model.Triggers;

/**
 *
 * @author Andrew
 */
public class ExportDlg extends JDialog {
    private static final ResourceBundle RES = ResourceBundle.getBundle("strings");
    private JDialog thisDlg;
    private ExportFilter filter = null;
    
    public ExportDlg(JFrame parent) {
        super(parent, true);
        setLayout(new BorderLayout());
        thisDlg = this;
        
        Box theBox = Box.createVerticalBox();
        theBox.setBorder(new EmptyBorder(10,10,10,10));
        theBox.add(Box.createVerticalStrut(5));
        
        JLabel title = new JLabel(RES.getString("EXPORT_TITLE"));
        title.setFont(Utils.TITLE_FONT);
        title.setAlignmentX(CENTER_ALIGNMENT);
        theBox.add(title);
        theBox.add(Box.createVerticalStrut(5));
        
        JTextArea ta = new JTextArea(
                RES.getString("EXPORT_MAIN_QUESTION")
        );
        ta.setEditable(false);
        ta.setBackground(theBox.getBackground());
        ta.setFont(Utils.STD_FONT);
        ta.setAlignmentX(CENTER_ALIGNMENT);
        theBox.add(ta);
        theBox.add(Box.createVerticalStrut(5));
        
        theBox.add(everythingBtn());
        theBox.add(Box.createVerticalStrut(5));
        
        theBox.add(selection());
        
        add(theBox, BorderLayout.CENTER);
        
        pack();
        
        // Center on screen
        Dimension dim = getPreferredSize();
        Point center = ScreenInfo.getCenter();
        setLocation(center.x-dim.width/2, center.y-dim.height/2);
        
        setVisible(true);
    }
    
    ExportFilter getFilter() {
        return filter;
    }
    
    JComponent everythingBtn() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton btn = new JButton(RES.getString("EXPORT_EVERYTHING"));
        btn.addActionListener(e -> {
            filter = new ExportFilter();
            thisDlg.dispose();
        });
        
        p.add(btn);
        p.setAlignmentX(CENTER_ALIGNMENT);
        return p;
    }
    
    private class CheckSensor {
        final JCheckBox box;
        final Sensor sensor;
        
        CheckSensor(JCheckBox b, Sensor s) {
            box = b;
            sensor = s;
        }
    };
    
    List<CheckSensor> checkList = new ArrayList<>();
    JCheckBox mouseSpeedBox;
    
    JComponent selection() {
        Set<Sensor> sensors;
        
        sensors = Triggers.getInstance().getUsedSensors();
        
        Box box = Box.createVerticalBox();
        box.setBorder(new LineBorder(Color.BLACK));
        box.setAlignmentX(CENTER_ALIGNMENT);
        
        for(Sensor s: sensors) {
            JCheckBox cb = new JCheckBox(s.getName());
            cb.setAlignmentY(LEFT_ALIGNMENT);
            checkList.add(new CheckSensor(cb, s));
            box.add(cb);
        }
        
        mouseSpeedBox = new JCheckBox(RES.getString("EX_MOUSE_SPEED"));
        mouseSpeedBox.setAlignmentY(LEFT_ALIGNMENT);
        box.add(mouseSpeedBox);
        
        box.add(selectedItemsBtn());
        return box;
    }
    
    JComponent selectedItemsBtn() {
        Set<Sensor> sensors = new HashSet<>();
        
        JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton btn = new JButton(RES.getString("EXPORT_SELECTED"));
        btn.addActionListener(e -> {
            for(CheckSensor cs: checkList) {
                if (cs.box.isSelected()) {
                    sensors.add(cs.sensor);
                }
            }
            filter = new ExportFilter(sensors);
            filter.setExportMouseSpeed(mouseSpeedBox.isSelected());
            thisDlg.dispose();
        });
        
        p.add(btn);
        p.setAlignmentX(LEFT_ALIGNMENT);
        return p;
    }
 }
