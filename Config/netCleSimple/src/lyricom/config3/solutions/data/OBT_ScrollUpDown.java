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
public class OBT_ScrollUpDown extends OBToggleBase {

    public OBT_ScrollUpDown() {
        super(ESolution.S_SCROLL_UP_DOWN_TOGGLE);
    }

    @Override
    T_Action getAction1() {
        return T_Action.MOUSE_WHEEL_DOWN;
    }

    @Override
    T_Action getAction2() {
        return T_Action.MOUSE_WHEEL_UP;
    }
}
