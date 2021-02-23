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
public class OBE_LeftButton extends OBEmulationBase {

    public OBE_LeftButton() {
        super(ESolution.S_LEFT_EMULATION);
    }

    @Override
    T_Action getPressAction() {
        return new T_Action( EAction.HID_MOUSE, Model.MOUSE_PRESS);
    }

    @Override
    T_Action getReleaseAction() {
        return new T_Action( EAction.HID_MOUSE, Model.MOUSE_RELEASE);
    }
}
