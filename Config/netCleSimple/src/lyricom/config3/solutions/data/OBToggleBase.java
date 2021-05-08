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

public abstract class OBToggleBase extends PortOnlyBase {
        
    public OBToggleBase(ESolution type) {
        super(type);
        sensorCount = 1;
    }
    
    abstract T_Action getAction1();
    abstract T_Action getAction2();
    
    @Override
    public void compile() {
        EPort portItem = (EPort)port.getSelectedItem();
        ESensor sensor = subPort.getSensor(portItem);
                
        T_Action action1 = getAction1();
        T_Action action2 = getAction2();
        T_Action nothing = T_Action.NONE;
        T_Action buzz    = T_Action.createBuzzerAction(200, 100);
        T_Action hibuzz    = T_Action.createBuzzerAction(800, 100);
        
        makeTrigger(sensor, 1, T_Signal.BTN_PRESS,     0, nothing,   2);
        makeTrigger(sensor, 2, T_Signal.BTN_PRESS,     0, action1,   2);
        makeTrigger(sensor, 2, T_Signal.BTN_RELEASE, 800, buzz,      3);
        makeTrigger(sensor, 3, T_Signal.BTN_RELEASE, 3000, hibuzz,   1);
        makeTrigger(sensor, 3, T_Signal.BTN_PRESS,     0, nothing,   4);
        makeTrigger(sensor, 4, T_Signal.BTN_PRESS,     0, action2,   4);
        makeTrigger(sensor, 4, T_Signal.BTN_RELEASE, 800, buzz,      1);
    }
    
    @Override
    public void printDescription(PrintStream out) {
        out.println(getType().toString());
        out.println(String.format("   Port: %s", port.getSelectedItem().toString()));
    }
}
