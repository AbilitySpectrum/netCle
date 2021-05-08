package lyricom.config3.solutions.data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import lyricom.config3.model.EAction;
import lyricom.config3.model.ESensor;
import lyricom.config3.model.Model;
import lyricom.config3.model.T_Action;
import lyricom.config3.model.T_Signal;
import lyricom.config3.solutions.EPort;
import lyricom.config3.ui.selection.ESolution;

/**
 *
 * @author Andrew
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class OBP_LPressRelease extends OBPressReleaseBase {

    public OBP_LPressRelease() {
        super(ESolution.S_LEFT_PRESS_RELEASE_TOGGLE);
    }

    @Override
    T_Action getPressAction() {
        return T_Action.MOUSE_PRESS;
    }

    @Override
    T_Action getReleaseAction() {
        return T_Action.MOUSE_RELEASE;
    }
    

}
