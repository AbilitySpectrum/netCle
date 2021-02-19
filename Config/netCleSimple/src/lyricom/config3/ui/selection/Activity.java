package lyricom.config3.ui.selection;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

/**
 *
 * @author Andrew
 */
public class Activity extends JPanel implements MouseListener {
    
    private Color savedBackground;
    private final EActivity type;
    private final SelectionDlg parent;

    private final JPanel deviceContainer;
    private final List<Device> devices = new ArrayList<>();
    
    
    public Activity(SelectionDlg p, EActivity type) {
        parent = p;
        this.type = type;
        
        setLayout(new BorderLayout());
        JLabel label = new JLabel(type.getText());
        add(label, BorderLayout.NORTH);
        label.setBorder(new EmptyBorder(10, 10, 3, 20));
        
        deviceContainer = deviceBox();
        add(deviceContainer, BorderLayout.CENTER);
        deviceContainer.setVisible(false);
        
        addMouseListener(this);
    }
    
    EActivity getType() {
        return type;
    }
    
    void selectionMade(ESolution type) {
        parent.selectionMade(type);
    }

    @Override
    public void mouseClicked(MouseEvent me) {
    }

    @Override
    public void mousePressed(MouseEvent me) {
    }

    @Override
    public void mouseReleased(MouseEvent me) {
        parent.mouseClicked(type);
    }

    @Override
    public void mouseEntered(MouseEvent me) {
        savedBackground = getBackground();
        setBackground(Color.lightGray);
    }

    @Override
    public void mouseExited(MouseEvent me) {
        setBackground(savedBackground);
   }
        
    private JPanel deviceBox() {
        JPanel p = new JPanel();
        CompoundBorder b1 = new CompoundBorder(
                    new EmptyBorder(0, 25, 10, 5),
                    new LineBorder(Color.BLACK, 1)
        );
        p.setBorder(b1);
        Box bx = Box.createVerticalBox();
        for(EDevice d: EDevice.values()) {
            if (d.getActivity() == type) {
                Device dev = new Device(this, d);
                devices.add(dev);
                bx.add(dev);
            }
        }
        p.add(bx);
        
        return p;
    }
    
    void showDeviceBox() {
        deviceContainer.setVisible(true);
    }
    
    void hideDeviceBox() {
        deviceContainer.setVisible(false);
    }
    
    void clean() {
         for(Device d: devices) {
             d.clean();
         }
    }
    
    void deviceMouseClicked(MouseEvent me, EDevice dev) {
         for(Device d: devices) {
             if (d.getType() == dev) {
                 d.showSolutions(me);
             } else {
                 d.hideSolutions();
             }
         }
         repaint();
    }
}
