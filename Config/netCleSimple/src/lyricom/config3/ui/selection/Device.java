package lyricom.config3.ui.selection;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.border.LineBorder;

/**
 *
 * @author Andrew
 */
public class Device extends JPanel implements MouseListener {

    private Color savedBackground;
    private final EDevice type;
    private final Activity parent;

    private final JFrame solutionsContainer;
    private final List<Solution> solutions = new ArrayList<>();
    
    public Device(Activity parent, EDevice devID) {
        type = devID;
        this.parent = parent;
        
        setLayout(new FlowLayout(FlowLayout.LEFT));
        
        JLabel l = new JLabel(type.getText());
        add(l);
        
        solutionsContainer = new JFrame();
        solutionsContainer.setUndecorated(true);
        Box vb = Box.createVerticalBox();
        vb.setBorder(new LineBorder(Color.black, 1));
        for(ESolution sol: ESolution.values()) {
            if (sol.getDevice() == type) {
                Solution s = new Solution(this, sol);
                vb.add(s);
                solutions.add(s);
            }
        }
        solutionsContainer.add(vb);
        solutionsContainer.pack();
        solutionsContainer.setVisible(false);
        
        addMouseListener(this);
    }
    
    EDevice getType() {
        return type;
    }
    
    void selectionMade(ESolution type) {
        parent.selectionMade(type);
    }

    
    void showSolutions(MouseEvent me) {
        parent.deviceMouseClicked(me, type);
    }
    
    void hideSolutions() {
        solutionsContainer.setVisible(false);
    }
    
    void clean() {
        solutionsContainer.dispose();
    }

    @Override
    public void mouseClicked(MouseEvent me) {
    }

    @Override
    public void mousePressed(MouseEvent me) {
    }

    @Override
    public void mouseReleased(MouseEvent me) {
        Point p = me.getLocationOnScreen();
        p.x += 20;
        solutionsContainer.setLocation(p);
        solutionsContainer.setVisible(true);
        solutionsContainer.requestFocus();
        repaint();
    }

    @Override
    public void mouseEntered(MouseEvent me) {
        savedBackground = getBackground();
        setBackground(Color.LIGHT_GRAY);
    }

    @Override
    public void mouseExited(MouseEvent me) {
        setBackground(savedBackground);
    }
}
