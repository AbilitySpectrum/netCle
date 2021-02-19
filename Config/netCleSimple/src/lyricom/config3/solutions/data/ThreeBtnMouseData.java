package lyricom.config3.solutions.data;

import java.io.PrintStream;
import javax.swing.JComboBox;
import lyricom.config3.model.EAction;
import lyricom.config3.model.ESensor;
import lyricom.config3.model.Model;
import lyricom.config3.model.T_Action;
import lyricom.config3.model.T_Signal;
import lyricom.config3.solutions.EPort;
import lyricom.config3.solutions.ESolutionType;
import lyricom.config3.solutions.SolutionsDataBase;

/**
 *
 * @author Andrew
 */
public class ThreeBtnMouseData extends SolutionsDataBase {

    private final JComboBox dblButton;
    private final JComboBox singleButton;
    
    public ThreeBtnMouseData() {
        super(null);
        
        dblButton = comboSelection(EPort.class);
        singleButton = comboSelection(EPort.class);
    }
    
    @Override
    public void compile() {
        int portNum = ((EPort) dblButton.getSelectedItem()).getPortNum();
        ESensor sensorA = ESensor.getSensorA(portNum);
        ESensor sensorB = ESensor.getSensorB(portNum);
        portNum = ((EPort) singleButton.getSelectedItem()).getPortNum();
        ESensor single = ESensor.getSensorA(portNum);
        
        T_Action mouseup = new T_Action(EAction.HID_MOUSE, Model.MOUSE_UP);
        T_Action mousedown = new T_Action(EAction.HID_MOUSE, Model.MOUSE_DOWN);
        T_Action mouseleft = new T_Action(EAction.HID_MOUSE, Model.MOUSE_LEFT);
        T_Action mouseright = new T_Action(EAction.HID_MOUSE, Model.MOUSE_RIGHT);
        T_Action mouseLClick = new T_Action(EAction.HID_MOUSE, Model.MOUSE_CLICK);
        T_Action mouseRClick = new T_Action(EAction.HID_MOUSE, Model.MOUSE_RIGHT_CLICK);
        T_Action mouseLPress = new T_Action(EAction.HID_MOUSE, Model.MOUSE_PRESS);
        T_Action nothing = new T_Action(EAction.NONE, 0);
        T_Action buzz    = new T_Action(EAction.BUZZER, (200 << 16) + 100);
        T_Action hibuzz    = new T_Action(EAction.BUZZER, (400 << 16) + 100);
        
        makeTrigger(sensorA, 1, T_Signal.BTN_PRESS,     0, nothing,   2);
        makeTrigger(sensorA, 2, T_Signal.BTN_PRESS,     0, mouseup,   2);
        makeTrigger(sensorA, 2, T_Signal.BTN_RELEASE, 500, buzz,      3);
        makeTrigger(sensorA, 3, T_Signal.BTN_RELEASE, 3000, nothing,  1);
        makeTrigger(sensorA, 3, T_Signal.BTN_PRESS,     0, nothing,   4);
        makeTrigger(sensorA, 4, T_Signal.BTN_PRESS,     0, mousedown, 4);
        makeTrigger(sensorA, 4, T_Signal.BTN_RELEASE, 500, buzz,      1);
        
        makeTrigger(sensorB, 1, T_Signal.BTN_PRESS,     0, nothing,   2);
        makeTrigger(sensorB, 2, T_Signal.BTN_PRESS,     0, mouseleft, 2);
        makeTrigger(sensorB, 2, T_Signal.BTN_RELEASE, 500, buzz,      3);
        makeTrigger(sensorB, 3, T_Signal.BTN_RELEASE, 3000, nothing,  1);
        makeTrigger(sensorB, 3, T_Signal.BTN_PRESS,     0, nothing,   4);
        makeTrigger(sensorB, 4, T_Signal.BTN_PRESS,     0, mouseright,4);
        makeTrigger(sensorB, 4, T_Signal.BTN_RELEASE, 500, buzz,      1);
        
        makeTrigger(single, 1, T_Signal.BTN_PRESS,     0, nothing,     2);
        makeTrigger(single, 2, T_Signal.BTN_RELEASE,   0, mouseLClick, 1);
        makeTrigger(single, 2, T_Signal.BTN_PRESS,   500, hibuzz,        3);
        makeTrigger(single, 3, T_Signal.BTN_RELEASE,   0, mouseRClick, 1);
        makeTrigger(single, 3, T_Signal.BTN_PRESS,   500, hibuzz,        4);
        makeTrigger(single, 4, T_Signal.BTN_RELEASE,   0, mouseLPress, 1);
        
    }
    
    @Override
    public void printDescription(PrintStream out) {
        out.println(getType().toString());
        out.println(String.format("   Two Button Port: %s", dblButton.getSelectedItem().toString()));
        out.println(String.format("   One Button Port: %s", singleButton.getSelectedItem().toString()));
    }

    public JComboBox getDblButton() {
        return dblButton;
    }

    public JComboBox getSingleButton() {
        return singleButton;
    }

}
