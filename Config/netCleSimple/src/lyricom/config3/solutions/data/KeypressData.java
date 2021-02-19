package lyricom.config3.solutions.data;

import java.io.PrintStream;
import javax.swing.JComboBox;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import lyricom.config3.model.EAction;
import lyricom.config3.model.ESensor;
import lyricom.config3.model.T_Action;
import lyricom.config3.model.T_Signal;
import lyricom.config3.solutions.EOneButtonSimple;
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
public class KeypressData extends SolutionsDataBase {
    
    final private JComboBox port;
    final private JComboBox keyStroke;
    final private JComboBox modifier;
    private ESubPort subPort = ESubPort.SubPortA;
    
    public KeypressData() {
        super(ESolutionType.SOL_KEYPRESS);
        sensorCount = 1;
        
        port = comboSelection(EPort.class);
        keyStroke = new JComboBox();
        for(EKeyCode code: EKeyCode.values()) {
            if(!code.isModifier()) {
                keyStroke.addItem(code);
            }
        }
        modifier = new JComboBox();
        for(EKeyCode code: EKeyCode.values()) {
            if( code.isModifier()) {
                modifier.addItem(code);
            }
        }        
    }
    
    @Override
    public void setSubPort(ESubPort sp) {
        subPort = sp;
    }
    
    @Override
    public void compile() {
        EPort portItem = (EPort)port.getSelectedItem();
        ESensor sensor = subPort.getSensor(portItem);
        int mod = ((EKeyCode) modifier.getSelectedItem()).getWiredCode();
        int key = ((EKeyCode) keyStroke.getSelectedItem()).getWiredCode();
        
        T_Action action = new T_Action(EAction.HID_KEYBOARD, 0xfd000000 + (mod << 8) + key);
        
        makeTrigger(sensor, 1, T_Signal.BTN_PRESS, 0, action, 1);         
    }

    @Override
    public void printDescription(PrintStream out) {
        out.println(getType().toString());
        out.println(String.format("   Port: %s", port.getSelectedItem().toString()));
        out.println(String.format("   Modifier:   %s", modifier.getSelectedItem().toString()));
        out.println(String.format("   Key Stroke: %s", keyStroke.getSelectedItem().toString()));
    }

    public JComboBox getPortCombo() {
        return port;
    }

    public JComboBox getKeyStroke() {
        return keyStroke;
    }

    public JComboBox getModifier() {
        return modifier;
    }
    
    @Override
    public EPort getPortUsed() {
        return (EPort) port.getSelectedItem();
    }
    
    // ---------------------------------------------
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
    
    @XmlElement(name = "Keystroke")
    String getXKeystroke() { 
        EKeyCode code = (EKeyCode) keyStroke.getSelectedItem();
        return code.name();
    }
    
    void setXKeystroke(String name) {
        EKeyCode code = EKeyCode.valueOf(name);
        keyStroke.setSelectedItem(code);
    }

    @XmlElement(name = "Modifier")
    String getXModifier() { 
        EKeyCode code = (EKeyCode) modifier.getSelectedItem();
        return code.name();
    }
    
    void setXModifier(String name) {
        EKeyCode code = EKeyCode.valueOf(name);
        modifier.setSelectedItem(code);
    }
}
