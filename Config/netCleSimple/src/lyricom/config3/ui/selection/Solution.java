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

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

/**
 *
 * @author Andrew
 */
public class Solution extends JPanel implements MouseListener {

    private final ESolution type;
    private final Device parent;
    private Color stdBackground;
    private Description desc = null;
    
    public Solution(Device parent, ESolution type) {
        this.type = type;
        this.parent = parent;
        setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        setBorder(new EmptyBorder(3, 15, 3, 5));
        setAlignmentX(LEFT_ALIGNMENT);
        
        JLabel l = new JLabel(type.getText());
        l.setFont(FormatCtl.SOLUTION);
        add(l);
        
        addMouseListener(this);
    }

    @Override
    public void mouseClicked(MouseEvent me) {
    }

    @Override
    public void mousePressed(MouseEvent me) {
    }

    @Override
    public void mouseReleased(MouseEvent me) {
        if (desc != null) {
            desc.dispose();
        }
        parent.selectionMade(type);
    }

    @Override
    public void mouseEntered(MouseEvent me) {
        if (desc == null) {
            String descID = type.getShortDescription();
    //        String fullDescription = SelectionResource.getStr(descID);
            desc = new Description(null, descID);
            desc.toFront();
            desc.repaint();
        }
        stdBackground = getBackground();
        setBackground(FormatCtl.HighlightColor);
    }

    @Override
    public void mouseExited(MouseEvent me) {
        if (desc != null) {
            desc.dispose();
            desc = null;
        }
        setBackground(stdBackground);
    }
}
