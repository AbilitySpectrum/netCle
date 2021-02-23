package lyricom.config3.solutions.data;

import java.io.PrintStream;
import javax.swing.JComboBox;
import javax.xml.bind.annotation.XmlElement;
import lyricom.config3.model.ESensor;
import lyricom.config3.model.T_Action;
import lyricom.config3.model.T_Signal;
import lyricom.config3.solutions.EPort;
import lyricom.config3.solutions.ESubPort;
import lyricom.config3.solutions.SolutionsDataBase;
import lyricom.config3.ui.selection.ESolution;

/**
 *
 * @author Andrew
 */
public abstract class OBEmulationBase extends PortOnlyBase {
    
    OBEmulationBase(ESolution type) {
        super(type);
        sensorCount = 1;       
    }
    
    abstract T_Action getPressAction();
    abstract T_Action getReleaseAction();
   
    @Override
    public void compile() {
        EPort portItem = (EPort)port.getSelectedItem();
        ESensor sensor = subPort.getSensor(portItem);
        T_Action press = getPressAction();
        T_Action release = getReleaseAction();
       
         
        makeTrigger(sensor, 1, T_Signal.BTN_PRESS, 0, press, 2); 
        makeTrigger(sensor, 2, T_Signal.BTN_RELEASE, 0, release, 1);         
    }

    @Override
    public void printDescription(PrintStream out) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
