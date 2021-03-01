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

import java.awt.Dimension;
import java.awt.Point;
import java.util.ResourceBundle;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

/**
 *
 * @author Andrew
 */
public class AboutDlg extends JDialog {
    private static final ResourceBundle RES = ResourceBundle.getBundle("strings");
    private AboutDlg thisDlg;
    
    public AboutDlg() {
        super(MainFrame.getInstance(), true);
        thisDlg = this;
        String CONFIG_VERSION = "1.04b5";
        Box b = Box.createVerticalBox();
        
        JLabel top = new JLabel(RES.getString("PRODUCT_NAME")); 
        top.setAlignmentX(0.5f);
        top.setFont(Utils.TITLE_FONT);
        
        JLabel configVersion = new JLabel(RES.getString("CONFIG_VERSION")
            + " " + CONFIG_VERSION);
        configVersion.setAlignmentX(0.5f);
        configVersion.setFont(Utils.STATE_FONT);
        
        JLabel copyRightNotice = new JLabel(RES.getString("COPYRIGHT_NOTICE"));
        copyRightNotice.setAlignmentX(0.5f);
        copyRightNotice.setFont(Utils.STD_FONT);
        
        JLabel licenseNotice = new JLabel(RES.getString("LICENSE_NOTICE"));
        licenseNotice.setAlignmentX(0.5f);
        licenseNotice.setFont(Utils.STD_FONT);
        
        b.add(top);
        b.add(Box.createVerticalStrut(10));
        b.add(configVersion);
        b.add(Box.createVerticalStrut(20));
        b.add(copyRightNotice);
        b.add(Box.createVerticalStrut(8));
        b.add(licenseNotice);
        b.add(Box.createVerticalStrut(10));
        b.add(doneBtn());
        
        b.setBorder(new EmptyBorder(15, 15, 15, 15));
        add(b);

        // Center on screen
//        Dimension dim = new Dimension(1100,600);
//        setSize(dim);
        Dimension dim = getPreferredSize();
        Point center = ScreenInfo.getCenter();
        setLocation(center.x-dim.width/2, center.y-dim.height/2);
        
        pack();
        setVisible(true);
    }
    
    private JComponent doneBtn() {
        JPanel p = new JPanel();
        p.setAlignmentX(0.5f);
        JButton done = new JButton(RES.getString("BTN_DONE")); 
        done.addActionListener(e -> {
            thisDlg.dispose();
        });        
        p.add(done);
        return p;
    }
}
