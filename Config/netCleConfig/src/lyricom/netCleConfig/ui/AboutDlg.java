package lyricom.netCleConfig.ui;

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
import lyricom.netCleConfig.comms.Connection;

/**
 *
 * @author Andrew
 */
public class AboutDlg extends JDialog {
    public static final String CONFIG_VERSION = "1.0";
    private static final ResourceBundle RES = ResourceBundle.getBundle("strings");
    private AboutDlg thisDlg;
    
    public AboutDlg() {
        super(MainFrame.TheFrame, true);
        thisDlg = this;
        
        Box b = Box.createVerticalBox();
        
        JLabel top = new JLabel(RES.getString("PRODUCT_NAME")); 
        top.setAlignmentX(0.5f);
        top.setFont(Utils.TITLE_FONT);
        
        JLabel configVersion = new JLabel(RES.getString("CONFIG_VERSION")
            + " " + CONFIG_VERSION);
        configVersion.setAlignmentX(0.5f);
        configVersion.setFont(Utils.STATE_FONT);
        
        JLabel arduinoVersion = new JLabel(RES.getString("ARDUINO_VERSION")
            + " " + Connection.getInstance().getVersionString());
        arduinoVersion.setAlignmentX(0.5f);
        arduinoVersion.setFont(Utils.STATE_FONT);
        
        b.add(top);
        b.add(Box.createVerticalStrut(10));
        b.add(configVersion);
        b.add(Box.createVerticalStrut(8));
        b.add(arduinoVersion);
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
