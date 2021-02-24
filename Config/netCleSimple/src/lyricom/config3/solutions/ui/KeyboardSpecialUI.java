package lyricom.config3.solutions.ui;

import javax.swing.Box;
import lyricom.config3.solutions.SolutionsUIBase;
import lyricom.config3.solutions.data.KeyboardSpecialData;

/**
 *
 * @author Andrew
 */
public class KeyboardSpecialUI extends SolutionsUIBase {

    private final KeyboardSpecialData data;
    
    public KeyboardSpecialUI(KeyboardSpecialData d) {
        super(d);
        this.data = d;
                
        Box vb = Box.createVerticalBox();
        vb.add( labelledItem(RES.getString("Q_ONE_BTN_PORT_LOCATION"), data.getPortCombo()));
        vb.add( labelledItem(RES.getString("KP_KEYPRESS"), data.getKeyStroke()));
        setupArea.add(vb);
    }

}
