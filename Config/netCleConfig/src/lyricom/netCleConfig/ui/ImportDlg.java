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
import static java.awt.Component.CENTER_ALIGNMENT;
import static java.awt.Component.LEFT_ALIGNMENT;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Point;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import lyricom.netCleConfig.model.ImportFilter;
import lyricom.netCleConfig.model.Model;
import lyricom.netCleConfig.model.Sensor;
import lyricom.netCleConfig.model.TmpImport;
import lyricom.netCleConfig.model.Trigger;
import lyricom.netCleConfig.model.Triggers;

/**
 *
 * @author Andrew
 */
public class ImportDlg extends JDialog {
    private static final ResourceBundle RES = ResourceBundle.getBundle("strings");
    private JDialog thisDlg;
    private ImportFilter filter = null;
    
    public ImportDlg(JFrame parent, TmpImport tmp) {
        super(parent, true);
        setLayout(new BorderLayout());
        thisDlg = this;

        Box theBox = Box.createVerticalBox();
        theBox.setBorder(new EmptyBorder(10,25,10,25));
        theBox.add(Box.createVerticalStrut(5));
        
        JLabel title = new JLabel(RES.getString("IMPORT_TITLE"));
        title.setFont(Utils.TITLE_FONT);
        title.setAlignmentX(CENTER_ALIGNMENT);
        theBox.add(title);
        theBox.add(Box.createVerticalStrut(5));
        
        JPanel ta = getTextArea(
                RES.getString("IMPORT_MAIN_QUESTION"),
                theBox.getBackground()
        );
        theBox.add(ta);
        theBox.add(Box.createVerticalStrut(5));
        
        theBox.add(everythingBtn());
        theBox.add(Box.createVerticalStrut(3));     
        
        ta = getTextArea(RES.getString("IMPORT_OR"), theBox.getBackground());
        theBox.add(ta);
        theBox.add(Box.createVerticalStrut(5));  
        
        theBox.add(selection(tmp));
        theBox.add(Box.createVerticalStrut(5));       
        theBox.add(cancelBtn());
        
        add(theBox, BorderLayout.CENTER);
        pack();
        
        // Center on screen
        Dimension dim = getPreferredSize();
        Point center = ScreenInfo.getCenter();
        setLocation(center.x-dim.width/2, center.y-dim.height/2);
        
        setVisible(true);
    }
    
    ImportFilter getFilter() {
        return filter;
    }
    
    JPanel getTextArea(String txt, Color backgrnd) {
        JTextArea ta = new JTextArea(txt);

        ta.setEditable(false);
        ta.setBackground(backgrnd);
        ta.setFont(Utils.STD_FONT);
        JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER));
        p.setAlignmentX(CENTER_ALIGNMENT);
        p.add(ta);
        return p;
    }
    
    JComponent everythingBtn() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton btn = new JButton(RES.getString("IMPORT_EVERYTHING"));
        btn.addActionListener(e -> {
            filter = new ImportFilter();
            filter.setOverwrite(true);
            thisDlg.dispose();
        });
        
        p.add(btn);
        p.setAlignmentX(CENTER_ALIGNMENT);
        return p;
    }
    
    JComponent cancelBtn() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton btn = new JButton(RES.getString("BTN_CANCEL"));
        btn.addActionListener(e -> {
            filter = null;
            thisDlg.dispose();
        });
        
        p.add(btn);
        p.setAlignmentX(CENTER_ALIGNMENT);
        return p;
    }
    
    private class CheckSensor {
        final JCheckBox box;
        final Sensor sensor;
        final JComboBox combo;
        
        CheckSensor(JCheckBox b, Sensor s, JComboBox c) {
            box = b;
            sensor = s;
            combo = c;
        }
    };
    
    List<CheckSensor> checkList = new ArrayList<>();
    JCheckBox mouseSpeedBox;
    
    JComponent selection(TmpImport tmp) {
        Set<Sensor> sensors;
        
        sensors = tmp.getUsedSensors();
        
        Box box = Box.createVerticalBox();
        box.setBorder(new LineBorder(Color.BLACK));
        box.setAlignmentX(CENTER_ALIGNMENT);
        
        // Set up the boxes.
        // A box for the checklist and combolist boxes.
        Box listBox = Box.createHorizontalBox();
        listBox.setAlignmentX(CENTER_ALIGNMENT);
        // A box for the check list.
        Box checkListBox = Box.createVerticalBox();
        checkListBox.setAlignmentY(TOP_ALIGNMENT);
        // A box for the combos
        Box comboListBox = Box.createVerticalBox();
        comboListBox.setAlignmentY(TOP_ALIGNMENT);
        listBox.add(checkListBox);
        // Spacing between checklist and combolist boxes.
        listBox.add(Box.createHorizontalStrut(20));
        listBox.add(comboListBox);
        box.add(listBox);
        
        // Get checkbox and combo sizes
        // and compute padding needed for alinement.
        JCheckBox sizeTestCb = new JCheckBox("foo");
        JComboBox sizeTestCombo = new JComboBox();
        sizeTestCombo.addItem("foo");
        Dimension cbdim = sizeTestCb.getPreferredSize();
        Dimension combodim = sizeTestCombo.getPreferredSize();
        int cbpadding, comboPadding, nullComboHeight;
        int SPACING = 4;
        if (cbdim.height < combodim.height) {
            cbpadding = combodim.height - cbdim.height + SPACING;
            comboPadding = SPACING;
            nullComboHeight = combodim.height + SPACING;
        } else {
            cbpadding = SPACING;
            comboPadding = cbdim.height - combodim.height + SPACING;
            nullComboHeight = cbdim.height + SPACING;
        }
        
        JLabel lbl = new JLabel("  " + RES.getString("IMPORT_FROM"));
        lbl.setAlignmentX(LEFT_ALIGNMENT);
        checkListBox.add(lbl);
        checkListBox.add(Box.createVerticalStrut(4));
        
        lbl = new JLabel("  " + RES.getString("IMPORT_TO"));
        lbl.setAlignmentX(LEFT_ALIGNMENT);
        comboListBox.add(lbl);
        comboListBox.add(Box.createVerticalStrut(4));
        
        for(Sensor s: sensors) {
            JCheckBox cb = new JCheckBox(s.getName());
            JComboBox combo = null;
            cb.setAlignmentY(LEFT_ALIGNMENT);
            checkListBox.add(cb);
            if (cbpadding != 0) {
                checkListBox.add(Box.createVerticalStrut(cbpadding));
            }
            if (s.getMoveGroup() == 0) {
                comboListBox.add(Box.createVerticalStrut(nullComboHeight));
            } else {
                combo = new JComboBox();
                combo.setAlignmentX(LEFT_ALIGNMENT);
                comboListBox.add(combo);
                List<Sensor> moveGroup = Model.getSensorMoveGroup(s.getMoveGroup());
                for(Sensor mg: moveGroup) {
                    combo.addItem(mg);
                }
                combo.setSelectedItem(s);
                if (comboPadding != 0) {
                    comboListBox.add(Box.createVerticalStrut(comboPadding));
                }
            }
            checkList.add(new CheckSensor(cb, s, combo));
        }
        
        if (tmp.getMouseSpeeds() != null) {
            mouseSpeedBox = new JCheckBox(RES.getString("EX_MOUSE_SPEED"));
            mouseSpeedBox.setAlignmentY(LEFT_ALIGNMENT);
            checkListBox.add(mouseSpeedBox);
            comboListBox.add(Box.createVerticalStrut(nullComboHeight));
        }
        
        box.add(selectedItemsBtn(tmp));
        return box;
    }
    
    JComponent selectedItemsBtn(TmpImport tmp) {        
        JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton btn = new JButton(RES.getString("IMPORT_MERGE"));
        btn.addActionListener(e -> {
            if (! inputToItemsAreUnique()) {
                return;
            }
            if (! overwriteCheck()) {
                return;
            }
            // OK.  We have all required information.
            
            // Remove the items the user does not want to import.
            for(CheckSensor cs: checkList) {
                if (!cs.box.isSelected()) {
                    tmp.deleteTriggerSet(cs.sensor);
                }
            }
            if (mouseSpeedBox != null && !mouseSpeedBox.isSelected()) {
                tmp.eraseMouseSpeeds();
            }
            
            // Do target sensor mapping
            for(Trigger t: tmp.getList()) {   // for each trigger ...
                for(CheckSensor cs: checkList) { // find the matching input ...
                    if(cs.sensor == t.getSensor()) { 
                                   // and set sensor to the combo box selection.
                        t.assignSensor((Sensor) cs.combo.getSelectedItem());
                        break;
                    }
                }
            }
            
            // Set the filter to delete all targets
            // Note: This will addToDeleteList even when there is
            // nothing to delete - who cares!?  This is a wart of
            // the previous design.
            filter = new ImportFilter();
            for(Sensor s: tmp.getUsedSensors()) {
                filter.addToDeleteList(s);
            }
            
            thisDlg.dispose();
        });
        
        p.add(btn);
        p.setAlignmentX(CENTER_ALIGNMENT);
        return p;
    }
    
    private boolean inputToItemsAreUnique() {
        Set<Sensor> targets = new HashSet<>();
        
        for(CheckSensor cs: checkList) {
            if (cs.box.isSelected()) {
                Sensor target = (Sensor) cs.combo.getSelectedItem();
                if (targets.contains(target)) {
                    JOptionPane.showMessageDialog(this, 
                        String.format(RES.getString("TARGET_NOT_UNIQUE"), target.toString()),
                        "Import Error", JOptionPane.ERROR_MESSAGE
                    );
                    return false;
                }
                targets.add((Sensor)cs.combo.getSelectedItem());
            }
        }
        return true;
    }

    private boolean overwriteCheck() {
        Set<Sensor> existingItems = Triggers.getInstance().getUsedSensors();
        
        for(CheckSensor cs: checkList) {
            if (cs.box.isSelected()) {
                Sensor target = (Sensor) cs.combo.getSelectedItem();
                if (existingItems.contains(target)) {
                    mergeConflictDlg dlg = new mergeConflictDlg(this, target);
                    if (dlg.getOption() == MergeOption.MODIFY_IMPORT) {
                        return false;
                    }
                }
            }
        }
        return true;
    }
    
    private enum MergeOption {OVERWRITE, MODIFY_IMPORT};
    
    private class mergeConflictDlg extends JDialog {
        MergeOption option = null;
        mergeConflictDlg mergeDlg;
        
        mergeConflictDlg(JDialog parent, Sensor s) {
            super(parent, true);
            mergeDlg = this;
            Box theBox = Box.createVerticalBox();
            theBox.setBorder(new EmptyBorder(10,10,10,10));
            theBox.add(Box.createVerticalStrut(5));
            
            JTextArea ta = new JTextArea(
                    String.format(RES.getString("IMPORT_CONFLICT"), s.getName())
            ); 
            ta.setEditable(false);
            ta.setBackground(theBox.getBackground());
            ta.setFont(Utils.STD_FONT);
            ta.setAlignmentX(CENTER_ALIGNMENT);
            theBox.add(ta);
            theBox.add(Box.createVerticalStrut(5));
                
            theBox.add(overwriteButton());
            theBox.add(Box.createVerticalStrut(5));
            theBox.add(modifyButton());
            
            add(theBox);
            
            pack();

            // Center on screen
            Dimension dim = getPreferredSize();
            Point center = ScreenInfo.getCenter();
            setLocation(center.x-dim.width/2, center.y-dim.height/2);

            setVisible(true);
        }
        
        MergeOption getOption() { return option; }
        
        JComponent overwriteButton() {
            JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER));
            JButton btn = new JButton(RES.getString("IMPORT_OVERWRITE"));
            btn.addActionListener(e -> {
                option = MergeOption.OVERWRITE;
                mergeDlg.dispose();
            });

            p.add(btn);
            p.setAlignmentX(CENTER_ALIGNMENT);
            return p;
        }
        
        JComponent modifyButton() {
            JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER));
            JButton btn = new JButton(RES.getString("IMPORT_MODIFY"));
            btn.addActionListener(e -> {
                option = MergeOption.MODIFY_IMPORT;
                mergeDlg.dispose();
            });

            p.add(btn);
            p.setAlignmentX(CENTER_ALIGNMENT);
            return p;
        }
    }
}
