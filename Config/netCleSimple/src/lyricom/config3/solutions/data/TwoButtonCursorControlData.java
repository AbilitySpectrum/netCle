package lyricom.config3.solutions.data;

import java.io.PrintStream;
import javax.swing.JComboBox;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
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
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class TwoButtonCursorControlData extends SolutionsDataBase {

    private final JComboBox port;
    
    public TwoButtonCursorControlData() {
        super(ESolutionType.SOL_TWO_BUTTON_CURSOR_CONTROL);
        sensorCount = 2;
        
        port = comboSelection(EPort.class);
    }
    
    @Override
    public void compile() {
        int portNum = ((EPort) port.getSelectedItem()).getPortNum();
        ESensor sensorA = ESensor.getSensorA(portNum);
        ESensor sensorB = ESensor.getSensorB(portNum);
        
        T_Action mouseup = new T_Action(EAction.HID_MOUSE, Model.MOUSE_UP);
        T_Action mousedown = new T_Action(EAction.HID_MOUSE, Model.MOUSE_DOWN);
        T_Action mouseleft = new T_Action(EAction.HID_MOUSE, Model.MOUSE_LEFT);
        T_Action mouseright = new T_Action(EAction.HID_MOUSE, Model.MOUSE_RIGHT);
        T_Action nothing = new T_Action(EAction.NONE, 0);
        T_Action buzz    = new T_Action(EAction.BUZZER, (200 << 16) + 100);
        
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
    }
    
    @Override
    public void printDescription(PrintStream out) {
        out.println(getType().toString());
        out.println(String.format("   Port: %s", port.getSelectedItem().toString()));
    }

    public JComboBox getPortCombo() {
        return port;
    }
    
    @Override
    public EPort getPortUsed() {
        return (EPort) port.getSelectedItem();
    }

    // -----------------------------------------
    // XML Support
    @XmlElement(name = "Port")
    String getXPort() {
        EPort p = (EPort) port.getSelectedItem();
        return p.name();
    }
    
    void setXPort(String name) {
        EPort p = EPort.valueOf(name);
        port.setSelectedItem(p);
    }
}
