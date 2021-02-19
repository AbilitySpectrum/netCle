package lyricom.config3.solutions.data;

import java.io.PrintStream;
import javax.swing.JComboBox;
import lyricom.config3.solutions.EPort;
import lyricom.config3.solutions.ESolutionType;
import lyricom.config3.solutions.SolutionsDataBase;

/**
 *
 * @author Andrew
 */
public class KeyboardData extends SolutionsDataBase {

    final private JComboBox port;
    final private JComboBox keyStroke;

    public KeyboardData() {
        super(ESolutionType.SOL_KEYBOARD );
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
}
