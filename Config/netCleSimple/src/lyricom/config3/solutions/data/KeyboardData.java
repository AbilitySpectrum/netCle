package lyricom.config3.solutions.data;

import java.io.PrintStream;
import javax.swing.JComboBox;
import javax.xml.bind.annotation.XmlElement;
import lyricom.config3.solutions.EPort;
import lyricom.config3.solutions.SolutionsDataBase;
import lyricom.config3.ui.selection.ESolution;

/**
 *
 * @author Andrew
 */
public class KeyboardData extends SolutionsDataBase {

    final private JComboBox port;
    final private JComboBox keyStroke;
    
    public KeyboardData() {
        super(ESolution.S_KEYBOARD_TEXT);
        sensorCount = 1;
        
        port = comboSelection(EPort.class);
        keyStroke = new JComboBox();
        for(EKeyCode code: EKeyCode.values()) {
            if(code.isSpecial()) {
                keyStroke.addItem(code);
            }
        }
    }

    @Override
    public void compile() {
    }

    @Override
    public void printDescription(PrintStream out) {
    }

    public JComboBox getPortCombo() {
        return port;
    }

    public JComboBox getKeyStroke() {
        return keyStroke;
    }

    // XML Support - incomplete.
    

}
