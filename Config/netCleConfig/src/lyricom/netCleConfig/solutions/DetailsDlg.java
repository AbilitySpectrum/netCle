package lyricom.netCleConfig.solutions;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import lyricom.netCleConfig.ui.MainFrame;
import lyricom.netCleConfig.ui.ScreenInfo;

/**
 *
 * @author Andrew
 */
public class DetailsDlg extends JDialog {

    static public void showDlg(String message) {
        SwingUtilities.invokeLater(() -> {new DetailsDlg(message);});
    }
    
    public DetailsDlg(String message) {
        super(MainFrame.TheFrame, true);
        setTitle("Option Description");
        
        setLayout(new BorderLayout());
        JEditorPane pane = new JEditorPane();
        pane.setContentType("text/html");        
        pane.setText(message);
        pane.setEditable(false);
        pane.setBorder(new EmptyBorder(10,10,10,10));
        add(pane, BorderLayout.CENTER);
        add(OKButton(), BorderLayout.SOUTH);
        
        pack();
        // Center on screen
        Dimension dim = getPreferredSize();
        Point center = ScreenInfo.getCenter();
        setLocation(center.x-dim.width/2, center.y-dim.height/2 - 100);
        setVisible(true);
    }
    
    private JComponent OKButton() {
        JPanel p = new JPanel();
        JButton btn = new JButton("OK");
        final DetailsDlg dlg = this;
        btn.addActionListener(e -> {
            dlg.dispose();
        });
        p.add(btn);
        return p;
    }
}
