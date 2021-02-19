package lyricom.config3.solutions.ui;

import javax.swing.Box;
import lyricom.config3.solutions.SolutionsUIBase;
import lyricom.config3.solutions.data.KeyboardData;

/**
 *
 * @author Andrew
 */
public class KeyboardUI extends SolutionsUIBase {
    
    private final KeyboardData data;
    
    public KeyboardUI(KeyboardData data) {
        super(data);
        this.data = data;
        
        Box vb = Box.createVerticalBox();
        vb.add( labelledItem(RES.getString("KP_PORT_SELECTION"), data.getPortCombo()));
        vb.add( labelledItem(RES.getString("KP_KEYPRESS"), data.getKeyStroke()));
        setupArea.add(vb);
   }
}
