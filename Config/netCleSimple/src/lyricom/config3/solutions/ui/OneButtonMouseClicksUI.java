package lyricom.config3.solutions.ui;

import javax.swing.Box;
import lyricom.config3.solutions.SolutionsUIBase;
import lyricom.config3.solutions.data.OneButtonMouseClicksData;

/**
 *
 * @author Andrew
 */
public class OneButtonMouseClicksUI extends SolutionsUIBase {
    private final OneButtonMouseClicksData data;
    
    public OneButtonMouseClicksUI(OneButtonMouseClicksData data) {
        super(data);
        this.data = data;
        
        descriptionText.setText(RES.getString("OBMC_DESCRIPTION"));
        
        Box vb = Box.createVerticalBox();
        vb.add( labelledItem(RES.getString("Q_ONE_BTN_PORT_LOCATION"), data.getPortCombo()));
        setupArea.add(vb);
        
        vb = Box.createVerticalBox();
        optionsArea.add(vb);
    }
}
