package lyricom.netCleConfig.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Point;
import java.util.List;
import java.util.ResourceBundle;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import lyricom.netCleConfig.model.Model;
import lyricom.netCleConfig.model.Sensor;
import lyricom.netCleConfig.model.Triggers;
/* * * * * * * * * * * * * * * * * * * * * * * * * * * * 
    Copyright (C) 2020 Andrew Hodgson

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
/**
 *
 * @author Andrew
 */
public class MoveTriggersDlg extends JDialog {
    private static final ResourceBundle RES = ResourceBundle.getBundle("strings");
    public final static int DO_CANCEL = 0;
    public final static int DO_MOVE = 1;
    public final static int DO_SWAP = 2;
    private int action = DO_CANCEL;  // default.
    private Sensor target;
    
    public MoveTriggersDlg(Sensor from) {
        super(MainFrame.TheFrame, true);
        setTitle(RES.getString("MT_TITLE"));
        
        List<Sensor> group = Model.getSensorMoveGroup(from.getMoveGroup());
        
        String title = RES.getString("MT_HEADING");
        JLabel top = new JLabel(String.format(title, from.toString()));
        top.setBorder(new EmptyBorder(5,0,15,0));
        
        JPanel p = new JPanel(new BorderLayout());
        add(p);
        p.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        p.add(top, BorderLayout.NORTH);
        
        Box v = Box.createVerticalBox();
        final MoveTriggersDlg thisDlg = this;
        Triggers triggers = Triggers.getInstance();
        
        for(Sensor s: group) {
            if (s != from) {
                final Sensor theSensor = s;
                boolean inUse = triggers.isSensorUsed(s);
                
                JButton btn = new JButton(s.toString());
                btn.addActionListener( e-> {
                    if (Triggers.getInstance().isSensorUsed(theSensor)) {
                        InUseDlg dlg = new InUseDlg(theSensor.toString());
                        action = dlg.getAction();
                    } else {
                        action = DO_MOVE;                    
                    }
                    target = theSensor;
                    thisDlg.dispose();
                });
                JPanel pp = new JPanel(new FlowLayout(FlowLayout.LEFT));
                pp.add(btn);
                if (inUse) {
                    pp.add(new JLabel("  " + RES.getString("MT_IN_USE")));
                }
                v.add(pp);
             }
        }
        p.add(v, BorderLayout.CENTER);
        
        p.add(cancelBtn(), BorderLayout.SOUTH);
        
        pack();
        
        Dimension dim = getPreferredSize();
        Point center = ScreenInfo.getCenter();
        setLocation(center.x-dim.width/2, center.y-dim.height/2);
        
        setVisible(true);
    }
    
    public int getAction() {
        return action;
    }
    
    public Sensor getTarget() {
        return target;
    }
    
    private JPanel cancelBtn() {
        JButton b = new JButton(RES.getString("BTN_CANCEL"));
        final JDialog dlg = this;
        b.addActionListener(e -> {
            action = DO_CANCEL;
            dlg.dispose();
        });
        JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER));
        p.add(b);
        return p;
    }
    
    private class InUseDlg extends JDialog {
        private int action;
        
        private InUseDlg(String targetName) {
            super(MainFrame.TheFrame, true);
            setTitle(RES.getString("MTSUB_TITLE"));
            
            String ques1 = RES.getString("MTSUB_HEADING1");
            
            JPanel p = new JPanel();
            p.setBorder(new EmptyBorder(10, 10, 10, 10));
            add(p);
            
            Box v = Box.createVerticalBox();
            p.add(v);
            
            v.add(newLabel(String.format(ques1, targetName)));
            v.add(newLabel(RES.getString("MTSUB_HEADING2")));
            v.add(Box.createVerticalStrut(10));
            
            final InUseDlg thisDlg = this;
            
            JButton overwrite = newBtn(RES.getString("MT_OVERWRITE"));
            overwrite.addActionListener( e-> {
                action = DO_MOVE;
                thisDlg.dispose();
            });
            v.add(overwrite);
            v.add(Box.createVerticalStrut(6));
            
            JButton swap = newBtn(RES.getString("MT_SWAP"));
            swap.addActionListener( e-> {
                action = DO_SWAP;
                thisDlg.dispose();
            });
            v.add(swap);
            v.add(Box.createVerticalStrut(6));
            
            JButton cancel = newBtn(RES.getString("BTN_CANCEL"));
            cancel.addActionListener( e-> {
                action = DO_CANCEL;
                thisDlg.dispose();
            });
            v.add(cancel);
            
            pack();

            Dimension dim = getPreferredSize();
            Point center = ScreenInfo.getCenter();
            setLocation(center.x-dim.width/2, center.y-dim.height/2);

            setVisible(true);                       
        }
        
        private JLabel newLabel(String text) {
            JLabel l = new JLabel(text);
            l.setAlignmentX(0.5F);
            return l;
        }
        
        private JButton newBtn(String label) {
            JButton btn = new JButton(label);
            btn.setAlignmentX(0.5F);
            return btn;
        }
        
        int getAction() {
            return action;
        }
    }
}
