package lyricom.config3.solutions.data;

import java.io.PrintStream;
import lyricom.config3.model.EAction;
import lyricom.config3.model.ESensor;
import lyricom.config3.model.T_Action;
import lyricom.config3.model.T_Signal;
import lyricom.config3.solutions.EPort;
import lyricom.config3.ui.selection.ESolution;

/**
 *
 * @author Andrew
 */
public abstract class OBPressReleaseBase extends PortAndAudioBase {

    public OBPressReleaseBase(ESolution sol) {
        super(sol);
    }

    abstract T_Action getPressAction();
    abstract T_Action getReleaseAction();
    
    @Override
    public void compile() {
        EPort portItem = (EPort)port.getSelectedItem();
        ESensor sensor = subPort.getSensor(portItem);
                
        T_Action press   = getPressAction();
        T_Action release = getReleaseAction();
        T_Action nothing = new T_Action(EAction.NONE, 256);
        T_Action buzz    = new T_Action(EAction.BUZZER, (200 << 16) + 100);
        T_Action hibuzz  = new T_Action(EAction.BUZZER, (800 << 16) + 100);
        
        if (audioFeedback.isSelected()) {
            makeTrigger(sensor, 1, T_Signal.BTN_PRESS,     0, buzz,      1);
        }
        makeTrigger(sensor, 1, T_Signal.BTN_PRESS,     0, press,     2);            
        makeTrigger(sensor, 2, T_Signal.BTN_RELEASE,   0, nothing,   3);
        if (audioFeedback.isSelected()) {
            makeTrigger(sensor, 3, T_Signal.BTN_PRESS,     0, hibuzz ,   3);
        }
        makeTrigger(sensor, 3, T_Signal.BTN_PRESS,     0, release,   4);
        makeTrigger(sensor, 4, T_Signal.BTN_RELEASE,   0, nothing,   1);
    }
    
    @Override
    public void printDescription(PrintStream out) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }}
