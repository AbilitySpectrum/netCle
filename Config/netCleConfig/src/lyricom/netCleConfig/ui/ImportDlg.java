/* * * * * * * * * * * * * * * * * * * * * * * * * * * * 
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
    along with this netClé Arduino software.  
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
import java.util.Set;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import lyricom.netCleConfig.model.ImportFilter;
import lyricom.netCleConfig.model.Sensor;
import lyricom.netCleConfig.model.TmpImport;
import lyricom.netCleConfig.model.Triggers;

/**
 *
 * @author Andrew
 */
public class ImportDlg extends JDialog {
    private JDialog thisDlg;
    private ImportFilter filter = null;
    
    public ImportDlg(JFrame parent, TmpImport tmp) {
        super(parent, true);
        setLayout(new BorderLayout());
        thisDlg = this;

        Box theBox = Box.createVerticalBox();
        theBox.setBorder(new EmptyBorder(10,10,10,10));
        theBox.add(Box.createVerticalStrut(5));
        
        JLabel title = new JLabel("Import Options");
        title.setFont(Utils.TITLE_FONT);
        title.setAlignmentX(CENTER_ALIGNMENT);
        theBox.add(title);
        theBox.add(Box.createVerticalStrut(5));
        
        JTextArea ta = new JTextArea(
                "How do you wish to do the import?"
        );
        ta.setEditable(false);
        ta.setBackground(theBox.getBackground());
        ta.setFont(Utils.STD_FONT);
        ta.setAlignmentX(CENTER_ALIGNMENT);
        theBox.add(ta);
        theBox.add(Box.createVerticalStrut(5));
        
        theBox.add(everythingBtn());
        theBox.add(Box.createVerticalStrut(5));
        
        theBox.add(selection(tmp));
        
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
    
    JComponent everythingBtn() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton btn = new JButton("Overwrite Everything");
        btn.addActionListener(e -> {
            filter = new ImportFilter();
            filter.setOverwrite(true);
            thisDlg.dispose();
        });
        
        p.add(btn);
        p.setAlignmentX(CENTER_ALIGNMENT);
        return p;
    }
    
    private class CheckSensor {
        final JCheckBox box;
        final Sensor sensor;
        
        CheckSensor(JCheckBox b, Sensor s) {
            box = b;
            sensor = s;
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
        
        for(Sensor s: sensors) {
            JCheckBox cb = new JCheckBox(s.getName());
            cb.setAlignmentY(LEFT_ALIGNMENT);
            checkList.add(new CheckSensor(cb, s));
            box.add(cb);
        }
        
        if (tmp.getMouseSpeeds() != null) {
            mouseSpeedBox = new JCheckBox("Mouse Speed");
            mouseSpeedBox.setAlignmentY(LEFT_ALIGNMENT);
            box.add(mouseSpeedBox);
        }
        
        box.add(selectedItemsBtn(tmp));
        return box;
    }
    
    JComponent selectedItemsBtn(TmpImport tmp) {        
        JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton btn = new JButton("Merge Selected Items");
        btn.addActionListener(e -> {
            // Remove the items the user does not want to import.
            filter = new ImportFilter();
            for(CheckSensor cs: checkList) {
                if (!cs.box.isSelected()) {
                    tmp.deleteTriggerSet(cs.sensor);
                }
            }
            if (mouseSpeedBox != null && !mouseSpeedBox.isSelected()) {
                tmp.eraseMouseSpeeds();
            }
            
            mergeCheck(tmp);
            thisDlg.dispose();
        });
        
        p.add(btn);
        p.setAlignmentX(LEFT_ALIGNMENT);
        return p;
    }

    private void mergeCheck(TmpImport tmp) {
        Set<Sensor> mergeItems = tmp.getUsedSensors();
        Set<Sensor> existingItems = Triggers.getInstance().getUsedSensors();
        Set<Sensor> conflicts = new HashSet<>(mergeItems);
        
        conflicts.retainAll(existingItems);
        
        for(Sensor s: conflicts) {
            mergeConflictDlg dlg = new mergeConflictDlg(this, s);
            if (dlg.getOption() == MergeOption.USE_CONFIG) {
                tmp.deleteTriggerSet(s);
            } else {
                filter.addToDeleteList(s);
            }
        }
    }
    
    private enum MergeOption {USE_CONFIG, USE_IMPORT};
    
    private class mergeConflictDlg extends JDialog {
        MergeOption option;
        
        mergeConflictDlg(JDialog parent, Sensor s) {
            super(parent, true);
            Box theBox = Box.createVerticalBox();
            theBox.setBorder(new EmptyBorder(10,10,10,10));
            theBox.add(Box.createVerticalStrut(5));
            
            JTextArea ta = new JTextArea(
               s.getName() + " is defined in both the import data\n"
                       + "and the current configuration.\n"
                       + "How do you wish to resolve this?"
            ); 
            ta.setEditable(false);
            ta.setBackground(theBox.getBackground());
            ta.setFont(Utils.STD_FONT);
            ta.setAlignmentX(CENTER_ALIGNMENT);
            theBox.add(ta);
            theBox.add(Box.createVerticalStrut(5));
                
            theBox.add(useConfigBtn());
            theBox.add(Box.createVerticalStrut(5));
            theBox.add(useImportData());
            
            add(theBox);
            
            pack();

            // Center on screen
            Dimension dim = getPreferredSize();
            Point center = ScreenInfo.getCenter();
            setLocation(center.x-dim.width/2, center.y-dim.height/2);

            setVisible(true);
        }
        
        MergeOption getOption() { return option; }
        JComponent useConfigBtn() {
            JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER));
            JButton btn = new JButton("Use the existing configuration");
            btn.addActionListener(e -> {
                option = MergeOption.USE_CONFIG;
                thisDlg.dispose();
            });

            p.add(btn);
            p.setAlignmentX(CENTER_ALIGNMENT);
            return p;
        }
        
        JComponent useImportData() {
            JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER));
            JButton btn = new JButton("Use the imported configuration");
            btn.addActionListener(e -> {
                option = MergeOption.USE_IMPORT;
                thisDlg.dispose();
            });

            p.add(btn);
            p.setAlignmentX(CENTER_ALIGNMENT);
            return p;
        }
        
    }
}
