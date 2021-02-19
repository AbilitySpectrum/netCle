package lyricom.config3.solutions.ui;

import javax.swing.Box;
import lyricom.config3.solutions.SolutionsUIBase;
import lyricom.config3.solutions.data.ThreeBtnMouseData;

/**
 *
 * @author Andrew
 */
public class ThreeBtnMouseUI extends SolutionsUIBase {
    private final ThreeBtnMouseData data;
    
    public ThreeBtnMouseUI(ThreeBtnMouseData data) {
        super(data);
        this.data = data;
        
        descriptionText.setText(RES.getString("TTTM_DESCRIPTION"));
        
        Box vb = Box.createVerticalBox();
        vb.add( labelledItem(RES.getString("TTTM_DOUBLE"), data.getDblButton()));
        vb.add( labelledItem(RES.getString("TTTM_SINGLE"), data.getSingleButton()));
        setupArea.add(vb);
        
        vb = Box.createVerticalBox();
        optionsArea.add(vb);
    }
}
