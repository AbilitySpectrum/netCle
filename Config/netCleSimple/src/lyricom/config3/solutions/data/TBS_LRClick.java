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
public class TBS_LRClick extends TBSimpleBase {

    public TBS_LRClick() {
        super(ESolution.S_LEFT_RIGHT_CLICK);
    }

    @Override
    T_Action getAction1() {
        return new T_Action(EAction.HID_MOUSE, Model.MOUSE_CLICK);
    }

    @Override
    T_Action getAction2() {
        return new T_Action(EAction.HID_MOUSE, Model.MOUSE_RIGHT_CLICK);
    }
}
