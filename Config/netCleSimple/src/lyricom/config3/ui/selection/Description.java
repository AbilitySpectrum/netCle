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
import java.awt.MouseInfo;
import java.awt.Point;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

/**
 *
 * @author Andrew
 */
public class Description extends JDialog {

    public Description(JFrame parent, String txt) {
        super(parent, false);
        setUndecorated(true);
        rootPane.setBorder(new LineBorder(Color.BLACK, 2));
        setLayout(new BorderLayout());
        
        add(showText(txt), BorderLayout.CENTER);
//        add(closeBtn(), BorderLayout.SOUTH);
        
        pack();
        Point loc = MouseInfo.getPointerInfo().getLocation();
        loc.x += 100;
        setLocation(loc);
        
        setVisible(true);
    }
    
    private JComponent showText(String txt) {
        JEditorPane pane = new JEditorPane();
        pane.setContentType("text/html");
        pane.setText(txt);
        pane.setEditable(false);
        pane.setBorder(new EmptyBorder(10,10,10,10));
        return pane;
    }
    
}
    
