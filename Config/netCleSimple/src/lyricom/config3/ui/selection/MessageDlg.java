package lyricom.config3.ui.selection;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.MouseInfo;
import java.awt.Point;
import java.util.ResourceBundle;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import lyricom.config3.ui.ScreenInfo;

/**
 * A simple dialog.
 * It puts up an html message and an OK button.
 * @author Andrew
 */
public class MessageDlg extends JDialog {
    private static final ResourceBundle RES = ResourceBundle.getBundle("strings");
    
    public MessageDlg(JFrame parent, String title, String msgID) {
        super(parent, true);
        setTitle(RES.getString(title));
        //setUndecorated(true);
        rootPane.setBorder(new LineBorder(Color.BLACK, 2));
        setLayout(new BorderLayout());
        
        add(showText(msgID), BorderLayout.CENTER);
        add(okBtn(), BorderLayout.SOUTH);
        
        pack();
        // Center on screen
        Dimension dim = getPreferredSize();
        Point center = ScreenInfo.getCenter();
        setLocation(center.x-dim.width/2, center.y-dim.height/2);
        
        setVisible(true);        
        
    }
    
    private JComponent showText(String msgID) {
        JEditorPane pane = new JEditorPane();
        pane.setContentType("text/html");
        pane.setText(RES.getString(msgID));
        pane.setEditable(false);
        pane.setBorder(new EmptyBorder(10,10,10,10));
        return pane;
    }
    
    private JPanel okBtn() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton btn = new JButton(RES.getString("BTN_OK"));
        btn.addActionListener((e) -> closeDlg());
        p.add(btn);
        return p;
    }
    
    private void closeDlg() {
        dispose();
    }

}
