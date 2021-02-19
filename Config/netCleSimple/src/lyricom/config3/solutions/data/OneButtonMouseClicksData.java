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
import lyricom.config3.solutions.ESubPort;
import lyricom.config3.solutions.SolutionsDataBase;

/**
 *
 * @author Andrew
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class OneButtonMouseClicksData extends SolutionsDataBase {

    private final JComboBox port;
    private ESubPort subPort = ESubPort.SubPortA;
    
    public OneButtonMouseClicksData() {
        super(ESolutionType.SOL_ONE_BUTTON_MOUSE_CLICKS);
        sensorCount = 1;
        
        port = comboSelection(EPort.class);
    }
    
    @Override
    public void setSubPort(ESubPort sp) {
        subPort = sp;
    }
    
    @Override
    public void compile() {
        EPort portItem = (EPort)port.getSelectedItem();
        ESensor sensor = subPort.getSensor(portItem);
        
        T_Action mouseLClick = new T_Action(EAction.HID_MOUSE, Model.MOUSE_CLICK);
        T_Action mouseRClick = new T_Action(EAction.HID_MOUSE, Model.MOUSE_RIGHT_CLICK);
        T_Action mouseLPress = new T_Action(EAction.HID_MOUSE, Model.MOUSE_PRESS);
        T_Action nothing = new T_Action(EAction.NONE, 0);
        T_Action buzz    = new T_Action(EAction.BUZZER, (200 << 16) + 100);
        T_Action hibuzz    = new T_Action(EAction.BUZZER, (400 << 16) + 100);  
        
        makeTrigger(sensor, 1, T_Signal.BTN_PRESS,     0, nothing,     2);
        makeTrigger(sensor, 2, T_Signal.BTN_RELEASE,   0, mouseLClick, 1);
        makeTrigger(sensor, 2, T_Signal.BTN_PRESS,   500, hibuzz,        3);
        makeTrigger(sensor, 3, T_Signal.BTN_RELEASE,   0, mouseRClick, 1);
        makeTrigger(sensor, 3, T_Signal.BTN_PRESS,   500, hibuzz,        4);
        makeTrigger(sensor, 4, T_Signal.BTN_RELEASE,   0, mouseLPress, 1);
        
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
