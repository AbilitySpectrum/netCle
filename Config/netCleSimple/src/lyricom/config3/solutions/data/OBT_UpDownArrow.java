package lyricom.config3.solutions.data;

import lyricom.config3.model.EKeyCode;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import lyricom.config3.model.ESensor;
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
public class OBT_UpDownArrow extends OBToggleBase {

    public OBT_UpDownArrow() {
        super(ESolution.S_UP_DOWN_ARROW_TOGGLE);
    }

    @Override
    T_Action getAction1() {
        return T_Action.createKeyboardAction(EKeyCode.ACT_KEY_UP_ARROW);
    }

    @Override
    T_Action getAction2() {
        return T_Action.createKeyboardAction(EKeyCode.ACT_KEY_DOWN_ARROW);
    }

    @Override
    public void compile() {
        EPort portItem = (EPort)port.getSelectedItem();
        ESensor sensor = subPort.getSensor(portItem);
                
        T_Action action1 = getAction1();
        T_Action action2 = getAction2();
        T_Action nothing = T_Action.NONE;
        T_Action buzz    = T_Action.createBuzzerAction(200, 100);
        T_Action hibuzz    = T_Action.createBuzzerAction(800, 100);
        
        makeTrigger(sensor, 1, T_Signal.BTN_PRESS,     0, nothing,   2);
        makeTriggerWRepeat(sensor, 2, T_Signal.BTN_PRESS,     0, action1,   2);
        makeTrigger(sensor, 2, T_Signal.BTN_RELEASE, 800, buzz,      3);
        makeTrigger(sensor, 3, T_Signal.BTN_RELEASE, 3000, hibuzz,   1);
        makeTrigger(sensor, 3, T_Signal.BTN_PRESS,     0, nothing,   4);
        makeTriggerWRepeat(sensor, 4, T_Signal.BTN_PRESS,     0, action2,   4);
        makeTrigger(sensor, 4, T_Signal.BTN_RELEASE, 800, buzz,      1);
    }
 }
