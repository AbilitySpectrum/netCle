/* * * * * * * * * * * * * * * * * * * * * * * * * * * * 
    Copyright (C) 2019 Andrew Hodgson

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
package lyricom.netCleConfig.solutions;

import lyricom.netCleConfig.model.ActionType;
import lyricom.netCleConfig.model.Model;
import lyricom.netCleConfig.model.SaAction;
import lyricom.netCleConfig.model.SensorGroup;
import lyricom.netCleConfig.model.Trigger;
import lyricom.netCleConfig.model.Triggers;

/**
 *
 * @author Andrew
 */
public class MouseClickButton extends SolutionBase {
    
    public MouseClickButton( SolutionsUI ui, SensorGroup sg ) {
        super(ui, sg);
    }
    
    @Override
    boolean doSolution() {
        Location btnLocHi = getButton();
        if (btnLocHi == null) return false;
                                
        // Gather the required actions ...
        SaAction mouse = mouseSelection();       
        if (cancelling) return false;

        SaAction buzz = Model.getActionByType(ActionType.BUZZER);
        SaAction none = Model.getActionByType(ActionType.NONE);
        // ... and the required button positions.        
        btnLocHi.level = Trigger.Level.LEVEL1;        
        Location btnLocLo = btnLocHi.getReverse();
        
        Triggers.getInstance().deleteTriggerSet(btnLocHi.sensor);
        
        makeTrigger(1, btnLocHi,     0,  none,                  0, 2);
        makeTrigger(2, btnLocLo,     0,  mouse, Model.MOUSE_CLICK, 1);
        makeTrigger(2, btnLocHi,   500,  buzz,   (200 << 16) + 50, 3);
        makeTrigger(3, btnLocLo,     0,  mouse, Model.MOUSE_RIGHT_CLICK, 1);
        makeTrigger(3, btnLocHi,   500,  buzz,   (200 << 16) + 50, 4);
        makeTrigger(4, btnLocLo,     0,  mouse, Model.MOUSE_PRESS, 1);

        return true;
    }

}
