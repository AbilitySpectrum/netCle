package lyricom.config3.solutions.ui;

import javax.swing.Box;
import lyricom.config3.solutions.SolutionsUIBase;
import lyricom.config3.solutions.data.PortAndAudioBase;

/**
 *
 * @author Andrew
 */
public class PortAndAudioUI extends SolutionsUIBase {
    PortAndAudioBase data;
    
    public PortAndAudioUI(PortAndAudioBase data) {
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
        
        vb = Box.createVerticalBox();
        vb.add(data.getAudioFeedback());
        optionsArea.add(vb);
    }
}
