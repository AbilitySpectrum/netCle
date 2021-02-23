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
public class TBS_ScrollUpDown extends TBSimpleBase {

    public TBS_ScrollUpDown() {
        super(ESolution.S_SCROLL_UP_DOWN);
    }

    @Override
    T_Action getAction1() {
        return new T_Action(EAction.HID_MOUSE, Model.MOUSE_WHEEL_UP);
    }

    @Override
    T_Action getAction2() {
        return new T_Action(EAction.HID_MOUSE, Model.MOUSE_WHEEL_DOWN);
    }
}
