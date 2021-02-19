package lyricom.config3.solutions.ui;

import javax.swing.Box;
import lyricom.config3.solutions.SolutionsUIBase;
import lyricom.config3.solutions.data.TwoButtonCursorControlData;

/**
 *
 * @author Andrew
 */
public class TwoButtonCursorControlUI extends SolutionsUIBase {
    private final TwoButtonCursorControlData data;
    
    public TwoButtonCursorControlUI(TwoButtonCursorControlData data) {
        super(data);
        this.data = data;
        
        descriptionText.setText(RES.getString("TBCC_DESCRIPTION"));
        
        Box vb = Box.createVerticalBox();
        vb.add( labelledItem(RES.getString("Q_TWO_BTN_PORT_LOCATION"), data.getPortCombo()));
        setupArea.add(vb);
        
        vb = Box.createVerticalBox();
        optionsArea.add(vb);
    }
}
