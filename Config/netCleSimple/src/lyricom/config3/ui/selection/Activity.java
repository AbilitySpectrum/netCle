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
package lyricom.config3.ui.selection;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;

/**
 *
 * @author Andrew
 */
public class Activity extends JPanel implements MouseListener {
    private static final List<Device> devices = new ArrayList<>();
    static void closeAllSolutionsWindows() {
        for(Device d: devices) {
            d.hideSolutions();
        }
    }
    static void clearDevices() {
        devices.clear();
    }
    
    private Color savedBackground;
    private final EActivity type;
    private final SelectionDlg parent;

    private final JPanel deviceContainer;
    
    
    Activity(SelectionDlg p, EActivity type) {
        parent = p;
        this.type = type;
        
        setLayout(new BorderLayout());
        JLabel label = new JLabel(type.getText());
        label.setFont(FormatCtl.ACTIVITY);
        label.setBorder(new EmptyBorder(4, 20, 6, 20));
        add(label, BorderLayout.NORTH);
        
        deviceContainer = deviceBox();
        add(deviceContainer, BorderLayout.CENTER);
        deviceContainer.setVisible(false);
        
        addMouseListener(this);
    }
    
    private JPanel deviceBox() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        CompoundBorder b1 = new CompoundBorder(
                    new EmptyBorder(0, 35, 10, 5),
                    new MatteBorder(0, 2, 0, 0, Color.BLACK)
        );
        p.setBorder(b1);
        Box bx = Box.createVerticalBox();
        JLabel title = new JLabel(SelectionResource.getStr("DEVICE_PROMPT"));
        title.setFont(FormatCtl.DEVICE_PROMPT);
        title.setBorder(new EmptyBorder(3, 5, 3, 3));
        title.setAlignmentX(LEFT_ALIGNMENT);
        bx.add(title);
        for(EDevice d: EDevice.values()) {
            if (d.getActivity() == type) {
                Device dev = new Device(this, parent, d);
                devices.add(dev);
                bx.add(dev);
            }
        }
        p.add(bx);
        
        return p;
    }

    EActivity getType() {
        return type;
    }
    
    void selectionMade(ESolution type) {
        parent.selectionMade(type);
    }

    @Override
    public void mouseClicked(MouseEvent me) {
    }

    @Override
    public void mousePressed(MouseEvent me) {
    }

    @Override
    public void mouseReleased(MouseEvent me) {
        parent.activityMouseClicked(type);
    }

    @Override
    public void mouseEntered(MouseEvent me) {
        savedBackground = getBackground();
        setBackground(FormatCtl.HighlightColor);
    }

    @Override
    public void mouseExited(MouseEvent me) {
        setBackground(savedBackground);
   }
        
    
    void showDeviceBox() {
        deviceContainer.setVisible(true);
    }
    
    void hideDeviceBox() {
        deviceContainer.setVisible(false);
    }
    
    void clean() {
         for(Device d: devices) {
             d.clean();
         }
    }
    
    void deviceMouseClicked(MouseEvent me, EDevice dev) {
         for(Device d: devices) {
             if (d.getType() == dev) {
                 d.showSolutions(me);
             } else {
                 d.hideSolutions();
             }
         }
         repaint();
    }
}
