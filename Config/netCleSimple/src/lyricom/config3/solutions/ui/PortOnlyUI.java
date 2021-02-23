package lyricom.config3.solutions.ui;

import javax.swing.Box;
import lyricom.config3.solutions.SolutionsUIBase;
import lyricom.config3.solutions.data.PortOnlyBase;

/**
 *
 * @author Andrew
 */
public class PortOnlyUI extends SolutionsUIBase {
    PortOnlyBase data;
    
    public PortOnlyUI(PortOnlyBase data) {
        super(data);
        this.data = data;
                
        Box vb = Box.createVerticalBox();
        String portPrompt;
        if (data.getSensorCount() == 1) {
            portPrompt = RES.getString("Q_ONE_BTN_PORT_LOCATION");
        } else {
            portPrompt = RES.getString("Q_TWO_BTN_PORT_LOCATION");            
        }
        vb.add( labelledItem(portPrompt, data.getPortCombo()));
        setupArea.add(vb); 
    }
}
