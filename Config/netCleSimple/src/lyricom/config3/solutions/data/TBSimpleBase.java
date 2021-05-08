/* * * * * * * * * * * * * * * * * * * * * * * * * * * * 
    Copyright (C) 2021 Andrew Hodgson

    This file is part of the netClé Configuration software.

    netClé Configuration software is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    netClé Configuration software is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this netClé configuration software.  
    If not, see <https://www.gnu.org/licenses/>.   
 * * * * * * * * * * * * * * * * * * * * * * * * * * * */
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
public abstract class TBSimpleBase extends PortAndAudioBase {
    
    public TBSimpleBase(ESolution t) {
        super(t);
        sensorCount = 2;
    }
    
    abstract T_Action getAction1();
    abstract T_Action getAction2();
    
    @Override
    public void compile() {
        int portNum = ((EPort) port.getSelectedItem()).getPortNum();
        ESensor sensorA = ESensor.getSensorA(portNum);
        ESensor sensorB = ESensor.getSensorB(portNum);
        
        T_Action action1 = getAction1();
        T_Action action2 = getAction2();
        T_Action buzz    = T_Action.createBuzzerAction(200, 100);
        
        makeTrigger(sensorA, 1, T_Signal.BTN_PRESS, 0, action1, 1);
        makeTrigger(sensorB, 1, T_Signal.BTN_PRESS, 0, action2, 1);
        if (audioFeedback.isSelected()) {
            makeTrigger(sensorA, 1, T_Signal.BTN_PRESS, 0, buzz, 1);
            makeTrigger(sensorB, 1, T_Signal.BTN_PRESS, 0, buzz, 1);                
        }        
    }
    
    @Override
    public void printDescription(PrintStream out) {
        out.println(getType().toString());
        out.println(String.format("   Port: %s", port.getSelectedItem().toString()));
        out.println(String.format("   Audio Feedback: %s", audioFeedback.isSelected()));
    }
}
