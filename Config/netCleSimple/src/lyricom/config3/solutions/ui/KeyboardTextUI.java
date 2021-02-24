package lyricom.config3.solutions.ui;

import javax.swing.Box;
import lyricom.config3.solutions.SolutionsUIBase;
import lyricom.config3.solutions.data.KeyboardTextData;

/**
 *
 * @author Andrew
 */
public class KeyboardTextUI extends SolutionsUIBase {
    
    private final KeyboardTextData data;
    
    public KeyboardTextUI(KeyboardTextData d) {
        super(d);
        this.data = d;
        
        Box vb = Box.createVerticalBox();
        vb.add( labelledItem(RES.getString("Q_ONE_BTN_PORT_LOCATION"), data.getPortCombo()));
        vb.add( labelledItem(RES.getString("KT_TEXT"), data.getTextField()));
        setupArea.add(vb);
        
        vb = Box.createVerticalBox();
        vb.add( data.getEndWithReturn());
        optionsArea.add(vb);
  }
}
