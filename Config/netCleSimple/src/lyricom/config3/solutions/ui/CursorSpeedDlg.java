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
package lyricom.config3.solutions.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ResourceBundle;
import javax.swing.*;
import lyricom.config3.solutions.Slider;
import lyricom.config3.solutions.data.CursorSpeedData;
import lyricom.config3.ui.MainFrame;
import lyricom.config3.ui.ScreenInfo;
import lyricom.config3.ui.Utils;

/**
 *
 * @author Andrew
 */
public class CursorSpeedDlg extends JDialog implements ActionListener {
    private static final ResourceBundle RES = ResourceBundle.getBundle("strings");
    private static final String CLOSE = "Close";
    private final CursorSpeedData data;
    JLabel firstLabel, secondLabel, thirdLabel;
    
    public CursorSpeedDlg() {
        super(MainFrame.getInstance(), true);
        data = CursorSpeedData.getInstance();
        makeLabels();
        
        setLayout(new BorderLayout());
        add(title(), BorderLayout.NORTH);
        
        Box vb = Box.createVerticalBox();
        vb.add(Box.createVerticalStrut(10));
        vb.add(labelledSlider(firstLabel, data.getFirstSpeed()));
        vb.add(Box.createVerticalStrut(10));
        vb.add(labelledTimer(RES.getString("CS_AFTER"), data.getFirstInterval()));
        vb.add(Box.createVerticalStrut(10));
        vb.add(labelledSlider(secondLabel, data.getSecondSpeed()));
        vb.add(Box.createVerticalStrut(10));
        vb.add(labelledTimer(RES.getString("CS_AFTER_ADDITIONAL"), data.getSecondInterval()));
        vb.add(Box.createVerticalStrut(10));
        vb.add(labelledSlider(thirdLabel, data.getThirdSpeed()));
        vb.add(Box.createVerticalStrut(10));
        
        add(vb, BorderLayout.CENTER);
        
        add(closeBtn(), BorderLayout.SOUTH);
        
        pack();
        
        Dimension dim = getPreferredSize();
        Point center = ScreenInfo.getCenter();
        setLocation(center.x-dim.width/2, center.y-dim.height/2);
        
        setVisible(true);                
        
    }
    
    private JPanel title() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel l = new JLabel(RES.getString("CS_TITLE"));
        l.setFont(Utils.STD_BOLD_FONT);
        p.add(l);
        return p;
    }
    
    // Create labels that all have the same width
    private void makeLabels() {
        firstLabel = new JLabel(RES.getString("CS_START"));
        secondLabel = new JLabel(RES.getString("CS_CHANGE_TO"));
        thirdLabel = new JLabel(RES.getString("CS_CHANGE_TO"));
        
        Dimension max = firstLabel.getPreferredSize();
        Dimension dim = secondLabel.getPreferredSize();
        if (dim.width > max.width) max = dim;
        
        firstLabel.setPreferredSize(max);
        secondLabel.setPreferredSize(max);
        thirdLabel.setPreferredSize(max);
        
    }
    
    private JPanel labelledTimer(String str, JComponent component) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel l = new JLabel(str);
        p.add(l);
        p.add(component);
        l = new JLabel(RES.getString("CS_MSEC"));
        p.add(l);
        return p;
    }
    
    private JPanel labelledSlider(JLabel l, JComponent slider) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
        p.add(l);
        p.add(slider);
        return p;
    }   
    
    private JPanel closeBtn() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton b = new JButton(RES.getString("BTN_CLOSE"));
        b.setActionCommand(CLOSE);
        b.addActionListener(this);
        p.add(b);
        return p;
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getActionCommand() == CLOSE) {
            dispose();
        }
    }
}
