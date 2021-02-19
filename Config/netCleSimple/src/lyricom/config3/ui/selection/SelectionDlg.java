package lyricom.config3.ui.selection;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import lyricom.config3.ui.ScreenInfo;
import lyricom.config3.ui.Utils;

/**
 *
 * @author Andrew
 */
public class SelectionDlg extends JDialog implements ActionListener {
    private static final String CANCEL = "Cancel";
    
    private List<Activity> activities = new ArrayList<>();
    private boolean cancelled = false;
    private ESolution selection;
    
    public SelectionDlg(JFrame parent) {
        super(parent, false);
        
        setTitle("Solutions");
        setLayout(new BorderLayout());

        add(question(), BorderLayout.NORTH);

        Box box = Box.createVerticalBox();
        for(EActivity a: EActivity.values()) {
            Activity acc = new Activity(this, a);
            activities.add(acc);
            box.add(acc);
        }
        
        add(box, BorderLayout.CENTER);
        
        add(buttons(), BorderLayout.SOUTH);
        pack();
        // Center on screen
        Dimension dim = getPreferredSize();
        Point center = ScreenInfo.getCenter();
        setLocation(center.x-dim.width/2, center.y-dim.height/2);
        
        setVisible(true);
    }
    
    private JPanel question() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel l = new JLabel("Select an Activity");
        l.setFont(Utils.TITLE_FONT);
        p.setBorder(new EmptyBorder(5, 10, 10, 10));
        p.add(l);
        return p;
    }
    
    // Called by Activity when it is clicked.
    // Shows devices for the clicked activity.
    // Hide them for all others.
    void mouseClicked(EActivity act) {
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
    
    void clean() {
        for(Activity a: activities) {
            a.clean();
            this.dispose();
        }
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

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getActionCommand() == CANCEL) {
            cancelled = true;
            clean();
        }
    }
    
    public boolean wasCancelled() {
        return cancelled;
    };
    
    public ESolution getSelection() {
        return selection;
    }
    
    void selectionMade(ESolution type) {
        System.out.println("Selection: " + type.getText());
        selection = type;
        clean();
    }

}
