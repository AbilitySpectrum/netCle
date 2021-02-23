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
public class TBS_LREmulation extends TBSimpleBase {
    
    public TBS_LREmulation() {
        super(ESolution.S_LEFT_RIGHT_EMULATION);
    }

    @Override
    T_Action getAction1() {
        return null;
    }

    @Override
    T_Action getAction2() {
        return null;
    }
    
    @Override
    public void compile() {
        int portNum = ((EPort) port.getSelectedItem()).getPortNum();
        ESensor sensorA = ESensor.getSensorA(portNum);
        ESensor sensorB = ESensor.getSensorB(portNum);
        
        T_Action buzz    = new T_Action(EAction.BUZZER, (200 << 16) + 100);
        T_Action leftPress = new T_Action(EAction.HID_MOUSE, Model.MOUSE_PRESS);
        T_Action rightPress = new T_Action(EAction.HID_MOUSE, Model.MOUSE_RIGHT_PRESS);
        T_Action leftRelease = new T_Action(EAction.HID_MOUSE, Model.MOUSE_RELEASE);
        T_Action rightRelease = new T_Action(EAction.HID_MOUSE, Model.MOUSE_RIGHT_RELEASE);

        makeTrigger(sensorA, 1, T_Signal.BTN_PRESS, 0, leftPress, 2);
        if (audioFeedback.isSelected()) {
            makeTrigger(sensorA, 2, T_Signal.BTN_PRESS, 0, buzz, 2);                
        }
        makeTrigger(sensorA, 2, T_Signal.BTN_RELEASE, 0, leftRelease, 1);
        makeTrigger(sensorB, 1, T_Signal.BTN_PRESS, 0, rightPress, 2);
        if (audioFeedback.isSelected()) {
            makeTrigger(sensorB, 2, T_Signal.BTN_PRESS, 0, buzz, 2);                                
        }
        makeTrigger(sensorB, 2, T_Signal.BTN_RELEASE, 0, rightRelease, 1);        
    }
}
