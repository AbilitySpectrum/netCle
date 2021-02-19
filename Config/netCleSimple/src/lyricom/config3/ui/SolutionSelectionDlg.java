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
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import lyricom.config3.solutions.ESolutionType;

/**
 *
 * @author Andrew
 */
public class SolutionSelectionDlg extends JDialog {
    private static final ResourceBundle RES = ResourceBundle.getBundle("strings");

    private JList list;
    private ESolutionType selection = null;
    
    public SolutionSelectionDlg(JFrame parent) {
        super(parent, true);
        
        setLayout(new BorderLayout());
        
        List<ESolutionType> types = new ArrayList<>();
        for(ESolutionType t: ESolutionType.values()) {
            if (!MainFrame.getInstance().getBluetooth() || t.worksOverBT()) {
                types.add(t);
            }
        }
        list = new JList(ESolutionType.values()); 
        list.setListData(types.toArray()); 
       
        list.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        list.setLayoutOrientation(JList.VERTICAL);
        list.setBorder(
                new CompoundBorder(
                        new LineBorder(Color.GRAY, 2),
                        new EmptyBorder(5,20,10,20)
                )
        );
        list.setVisibleRowCount(-1);  
        
        add(heading(), BorderLayout.NORTH);
        add(list, BorderLayout.CENTER);
        add(buttons(), BorderLayout.SOUTH);
        
        pack();
        // Center on screen
        Dimension dim = getPreferredSize();
        Point center = ScreenInfo.getCenter();
        setLocation(center.x-dim.width/2, center.y-dim.height/2);
        setVisible(true);
    }
    
    ESolutionType getSelection() {
        return selection;
    }
    
    JPanel heading() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel l = new JLabel(RES.getString("SSD_PROMPT"));
        l.setFont(Utils.TITLE_FONT);
        p.add(l);
        return p;
    }
    
    JPanel buttons() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER));
        p.add(OKBtn());
        p.add(CancelBtn());
        return p;
    }
    
    JButton OKBtn() {
        JButton b = new JButton(RES.getString("BTN_OK"));
        b.addActionListener((ae) -> {
            selection = (ESolutionType) list.getSelectedValue();
            this.dispose();
        });
        return b;
    }
    
    JButton CancelBtn() {
        JButton b = new JButton(RES.getString("BTN_CANCEL"));
        b.addActionListener((ae) -> {
            selection = null;
            this.dispose();
        });
        return b;
    }
}
