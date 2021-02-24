package lyricom.config3.solutions.data;

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
        return new T_Action(EAction.HID_KEYPRESS, 0xff000000 + EKeyCode.ACT_LCONTROL.getCode());
    }

    @Override
    T_Action getReleaseAction() {
        return new T_Action(EAction.HID_KEYRELEASE, 0xfe000000 + EKeyCode.ACT_LCONTROL.getCode());
    }   
}
