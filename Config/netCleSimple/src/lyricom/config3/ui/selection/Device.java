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
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

/**
 *
 * @author Andrew
 */
public class Device extends JPanel implements MouseListener {
    private static final List<Solution> solutions = new ArrayList<>();

    private Color savedBackground;
    private final EDevice type;
    private final Activity parent;
    private final JDialog parentFrame;

    private JDialog solutionsContainer;
    
    public Device(Activity parent, JDialog parentFrame, EDevice devID) {
        type = devID;
        this.parent = parent;
        this.parentFrame = parentFrame;
        
        setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        
        setBorder(new EmptyBorder(3, 10, 3, 3));
        setAlignmentX(LEFT_ALIGNMENT);
        JLabel device = new JLabel(type.getText());
        device.setFont(FormatCtl.DEVICE);
        add(device);
        
        getSolutionsContainer();   
        
        addMouseListener(this);
    }
    
    EDevice getType() {
        return type;
    }
    
    void selectionMade(ESolution type) {
        parent.selectionMade(type);
    }
    
    final JDialog getSolutionsContainer() {
        if (solutionsContainer != null) {
            return solutionsContainer;
        }
        solutionsContainer = new JDialog(parentFrame, false);
        solutionsContainer.setUndecorated(true);
        Box vb = Box.createVerticalBox();
        JLabel lp = new JLabel(SelectionResource.getStr("SOLUTIONS_PROMPT"));
        lp.setFont(FormatCtl.SOLUTIONS_PROMPT);
        lp.setAlignmentX(LEFT_ALIGNMENT);
        lp.setBorder(new EmptyBorder(3, 5, 3, 5));
        vb.add(lp);
        vb.setBorder(new LineBorder(Color.black, 1));
        for(ESolution sol: ESolution.values()) {
            if (sol.getDevice() == type) {
                Solution s = new Solution(this, sol);
                vb.add(s);
                solutions.add(s);
            }
        }
        solutionsContainer.add(vb);
        solutionsContainer.pack();
        solutionsContainer.setVisible(false);
        
        return solutionsContainer;
    }

    
    void showSolutions(MouseEvent me) {
        Point p = me.getLocationOnScreen();
        p.x += 25;
        solutionsContainer.setLocation(p);
        solutionsContainer.setVisible(true);
        solutionsContainer.requestFocus();
    }
    
    void hideSolutions() {
        solutionsContainer.setVisible(false);
    }
    
    void clean() {
        if (solutionsContainer != null) {
            solutionsContainer.dispose();
            solutionsContainer = null;
        }
    }

    @Override
    public void mouseClicked(MouseEvent me) {
    }

    @Override
    public void mousePressed(MouseEvent me) {
    }

    @Override
    public void mouseReleased(MouseEvent me) {
        parent.deviceMouseClicked(me, type);
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
}
