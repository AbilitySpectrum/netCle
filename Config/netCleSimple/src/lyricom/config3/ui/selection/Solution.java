package lyricom.config3.ui.selection;

import java.awt.FlowLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.*;

/**
 *
 * @author Andrew
 */
public class Solution extends JPanel implements MouseListener {

    private final ESolution type;
    private final Device parent;
    private Description desc = null;
    
    public Solution(Device parent, ESolution type) {
        this.type = type;
        this.parent = parent;
        setLayout(new FlowLayout(FlowLayout.LEFT));
        
        JLabel l = new JLabel(type.getText());
        add(l);
        
        addMouseListener(this);
    }

    @Override
    public void mouseClicked(MouseEvent me) {
    }

    @Override
    public void mousePressed(MouseEvent me) {
    }

    @Override
    public void mouseReleased(MouseEvent me) {
        if (desc != null) {
            desc.dispose();
        }
        parent.selectionMade(type);
    }

    @Override
    public void mouseEntered(MouseEvent me) {
        if (desc == null) {
            String descID = type.getDesc();
    //        String fullDescription = SelectionResource.getStr(descID);
            desc = new Description(null, descID);
        }
    }

    @Override
    public void mouseExited(MouseEvent me) {
        if (desc != null) {
            desc.dispose();
            desc = null;
        }
    }
}
