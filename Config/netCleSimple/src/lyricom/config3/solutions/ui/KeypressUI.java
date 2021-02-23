package lyricom.config3.solutions.ui;

import javax.swing.Box;
import lyricom.config3.solutions.SolutionsUIBase;
import lyricom.config3.solutions.data.KeypressData;

/**
 *
 * @author Andrew
 */
public class KeypressUI extends SolutionsUIBase {

    private final KeypressData data;
    
    public KeypressUI(KeypressData data) {
        super(data);
        this.data = data;
                
        Box vb = Box.createVerticalBox();
        vb.add( labelledItem(RES.getString("KP_PORT_SELECTION"), data.getPortCombo()));
        vb.add( labelledItem(RES.getString("KP_MODIFIER"), data.getModifier()));
        vb.add( labelledItem(RES.getString("KP_KEYPRESS"), data.getKeyStroke()));
        setupArea.add(vb);
    }

}
