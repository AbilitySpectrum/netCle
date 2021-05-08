package lyricom.config3.solutions.data;

import java.io.PrintStream;
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
public class ThreeFuncBtnData extends PortOnlyBase
{
    public ThreeFuncBtnData() {
        super(ESolution.S_THREE_FUNC_BUTTON);
        sensorCount = 1;
    }
    
    @Override
    public void compile() {
        EPort portItem = (EPort)port.getSelectedItem();
        ESensor sensor = subPort.getSensor(portItem);
        
        T_Action mouseLClick = T_Action.MOUSE_LCLICK;
        T_Action mouseRClick = T_Action.MOUSE_RCLICK;
        T_Action mouseLPress = T_Action.MOUSE_PRESS;
        T_Action nothing = T_Action.NONE;
        T_Action buzz    = T_Action.createBuzzerAction(200, 100);
        T_Action hibuzz    = T_Action.createBuzzerAction(400, 100);
        
        makeTrigger(sensor, 1, T_Signal.BTN_PRESS,     0, nothing,     2);
        makeTrigger(sensor, 2, T_Signal.BTN_RELEASE,   0, mouseLClick, 1);
        makeTrigger(sensor, 2, T_Signal.BTN_PRESS,   500, hibuzz,      3);
        makeTrigger(sensor, 3, T_Signal.BTN_RELEASE,   0, mouseRClick, 1);
        makeTrigger(sensor, 3, T_Signal.BTN_PRESS,   500, hibuzz,      4);
        makeTrigger(sensor, 4, T_Signal.BTN_RELEASE,   0, mouseLPress, 1);
    }

    @Override
    public void printDescription(PrintStream out) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }     
}
