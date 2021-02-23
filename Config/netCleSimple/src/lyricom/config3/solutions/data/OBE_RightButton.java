package lyricom.config3.solutions.data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import lyricom.config3.model.EAction;
import lyricom.config3.model.Model;
import lyricom.config3.model.T_Action;
import lyricom.config3.ui.selection.ESolution;

/**
 *
 * @author Andrew
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class OBE_RightButton extends OBEmulationBase {

    public OBE_RightButton() {
        super(ESolution.S_RIGHT_EMULATION);
    }

    @Override
    T_Action getPressAction() {
        return new T_Action( EAction.HID_MOUSE, Model.MOUSE_RIGHT_PRESS);
    }

    @Override
    T_Action getReleaseAction() {
        return new T_Action( EAction.HID_MOUSE, Model.MOUSE_RIGHT_RELEASE);
    }
}