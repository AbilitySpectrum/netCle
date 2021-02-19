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
package lyricom.config3.solutions;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.ResourceBundle;
import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import lyricom.config3.ui.MainFrame;
import lyricom.config3.ui.Utils;

/**
 *
 * @author Andrew
 */
public abstract class SolutionsUIBase extends JPanel {
    protected static final ResourceBundle RES = ResourceBundle.getBundle("strings");
    protected JPanel descriptionArea;
    protected JEditorPane descriptionText;
    protected JPanel setupArea;
    protected JPanel optionsArea;
    
    private SolutionsDataBase generalData;
    
    public SolutionsUIBase(SolutionsDataBase data) {
        generalData = data;
        setLayout(new BorderLayout());
        add(deleteBtn(), BorderLayout.EAST);
        
        Box vb = Box.createVerticalBox();
        add(vb, BorderLayout.CENTER);
        
        descriptionArea = annotatedPanel(RES.getString("PN_DESCRIPTION"));
//        descriptionArea.setLayout(new GridBagLayout());
        vb.add(descriptionArea);
        
        descriptionText = new JEditorPane();
        descriptionText.setContentType("text/html");   
//        descriptionText = new JTextArea(1,80);
        descriptionText.setBackground(descriptionArea.getBackground());
        descriptionText.setFont(Utils.STD_FONT);
        descriptionText.setEditable(false);
//        descriptionText.setLineWrap(true);
//        descriptionText.setWrapStyleWord(true);
        descriptionArea.add(descriptionText);
        
        setupArea = annotatedPanel(RES.getString("PN_SETTINGS"));
        vb.add(setupArea);
        
        optionsArea = annotatedPanel(RES.getString("PN_OPTIONS"));
        vb.add(optionsArea);
        
//        vb.add(Box.createVerticalGlue());
        
    }
    
    // Call which pass through to the data layer.
    public String getTypeName() {
        return generalData.getType().toString();
    }
        
    // Delete button for all UIs
    JPanel deleteBtn() {
        JPanel p = new JPanel();
        JButton b = new JButton(Utils.getIcon(Utils.ICON_DELETE));
        b.addActionListener(e-> {
            String name = getTypeName();
            int val = JOptionPane.showConfirmDialog(MainFrame.getInstance(), 
                    String.format(RES.getString("SUI_DELETE_CONFIRM_MSG"),name),
                    RES.getString("SUI_DELETE_CONFIRM_TITLE"),
                    JOptionPane.YES_NO_OPTION);
            SolutionsDataList.getInstance().remove(generalData);
            if (val == JOptionPane.YES_OPTION) {
                MainFrame.getInstance().deleteSolution(this);
            }
        });
        b.setPreferredSize(new Dimension(24,24));
        b.setFont(new Font("Monospaced", Font.PLAIN, 14));
        b.setBorder(new EmptyBorder(0,0,0,0));
           
        p.add(b);
        return p;
    }

    
    private JPanel annotatedPanel(String title) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
        p.setAlignmentX(LEFT_ALIGNMENT);
        
        p.setBorder(
            new CompoundBorder(
                new EmptyBorder(5,5,5,5),
                new TitledBorder(title)));
        return p;
    }
    
    protected final JPanel labelledItem(String label, JComponent item) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
        p.setAlignmentX(LEFT_ALIGNMENT);
        JLabel l = new JLabel(label);
        l.setFont(Utils.STD_BOLD_FONT);
        p.add(l);
        p.add(item);
        return p;
    }
    
    protected final  JPanel labelledItem(JComponent item, String label) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
        p.setAlignmentX(LEFT_ALIGNMENT);
        JLabel l = new JLabel(label);
        l.setFont(Utils.STD_BOLD_FONT);
        p.add(item);
        p.add(l);
        return p;
    }
}
