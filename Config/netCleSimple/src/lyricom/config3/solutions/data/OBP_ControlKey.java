package lyricom.config3.solutions.data;

import lyricom.config3.model.EKeyCode;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import lyricom.config3.model.EAction;
import lyricom.config3.model.T_Action;
import lyricom.config3.ui.selection.ESolution;

/**
 *
 * @author Andrew
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class OBP_ControlKey extends OBPressReleaseBase {

    public OBP_ControlKey() {
        super(ESolution.S_KEYBOARD_CONTROL);
    }

    @Override
    T_Action getPressAction() {
        return T_Action.createKeyPressAction(EKeyCode.ACT_LCONTROL);
    }

    @Override
    T_Action getReleaseAction() {
        return T_Action.createKeyReleaseAction(EKeyCode.ACT_LCONTROL);
    }   
}
