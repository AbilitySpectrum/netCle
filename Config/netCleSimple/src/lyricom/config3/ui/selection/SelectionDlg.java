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
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import lyricom.config3.ui.MainFrame;
import lyricom.config3.ui.ScreenInfo;

/**
 *
 * @author Andrew
 */
public class SelectionDlg extends JDialog implements ActionListener {
    private static final String CANCEL = "Cancel";
    
    private static List<Activity> activities = new ArrayList<>();
    
    private final MainFrame mainFrame;
    
    public SelectionDlg(MainFrame parent) {
        super(parent, false);
        mainFrame = parent;
        
        // Initialize arrays - probably an unnecessary precaution.
        activities.clear();
        Activity.clearDevices();
        
        setTitle(SelectionResource.getStr("SELECTION_TITLE"));
        
        setLayout(new BorderLayout());

        add(question(), BorderLayout.NORTH);        
        add(activities(), BorderLayout.CENTER);        
        add(buttons(), BorderLayout.SOUTH);
        
        pack();
        // Center on screen 20 pixels up from center.
        Dimension dim = getPreferredSize();
        Point center = ScreenInfo.getCenter();
        setLocation(center.x-dim.width/2, center.y-dim.height/2 - 20);
        
        setVisible(true);
    }
    
    // UI Components
    private JPanel question() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        JLabel l = new JLabel(SelectionResource.getStr("ACTIVITY_PROMPT"));
        l.setFont(FormatCtl.ACTIVITY_PROMPT);
        p.setBorder(new EmptyBorder(5, 10, 5, 10));
        p.add(l);
        return p;
    }
    
    private JComponent activities() {
        Box box = Box.createVerticalBox();
        for(EActivity a: EActivity.values()) {
            Activity acc = new Activity(this, a);
            activities.add(acc);
            box.add(acc);
        }
        return box;        
    }
    
    private JPanel buttons() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER));
        p.add(cancelBtn());
        return p;
    }
    
    private JButton cancelBtn() {
        JButton b = new JButton("Cancel");
        b.addActionListener(this);
        b.setActionCommand(CANCEL);
        return b;
    }
    
    // Callbacks.
    
    // Called by Activity when it is clicked.
    // Shows devices for the clicked activity.
    // Hide them for all others.
    void activityMouseClicked(EActivity act) {
        Activity.closeAllSolutionsWindows();
        for(Activity a: activities) {
            if (a.getType() == act) {
                a.showDeviceBox();
            } else {
                a.hideDeviceBox();
            }
        }
        pack();
        repaint();
    }
    
    // Called when a selection is made.
    // When a selection is made we call MainFrame to add a solution pane.
    // This is not a modal dialog.  It can't be because it spawns other
    // dialogs.  As a result, the main-line does not hang when this
    // dialog is created - so the usual:
    //              createDialog (and hang)
    //              askDialogWhatHappened
    // does not work.
    void selectionMade(ESolution type) {
        clean();
        if (type != null) {
            mainFrame.addSolutionPane(type);
        }
    }

    void clean() {
        for(Activity a: activities) {
            a.clean();
        }
        // Empty static lists.
        activities.clear();
        Activity.clearDevices(); 
        this.dispose();
    }   

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getActionCommand() == CANCEL) {
            // Cancel button was pressed.
            clean();
        }
    }


}
