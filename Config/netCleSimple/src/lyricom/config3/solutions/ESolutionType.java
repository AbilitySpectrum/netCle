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
package lyricom.config3.solutions;

import lyricom.config3.solutions.ui.*;
import lyricom.config3.solutions.data.*;
import lyricom.config3.model.Resource;

/**
 *
 * @author Andrew
 */
public enum ESolutionType {

    SOL_ONE_BUTTON_MOUSE(true) {
        @Override
        public SolutionsUIBase createSolution(SolutionsDataBase data) {
            if (data == null) data = new OneBtnMouseData();
            return new OneBtnMouseUI((OneBtnMouseData)data);
        }        
    },
    SOL_KEYPRESS(false) {
        @Override
        public SolutionsUIBase createSolution(SolutionsDataBase data) {
            if (data == null) data = new KeypressData();
            return new KeypressUI((KeypressData)data);
        }        
    },
    SOL_KEYBOARD(true) {
        @Override
        public SolutionsUIBase createSolution(SolutionsDataBase data) {
            if (data == null) data = new KeyboardData();
            return new KeyboardUI((KeyboardData)data);
        }                
    },


    SOL_JOYSTICK_1(true) {
        @Override
        public SolutionsUIBase createSolution(SolutionsDataBase data) {
            if (data == null) data = new JoystickMouse1Data();
            return new JoystickMouse1UI((JoystickMouse1Data)data);
        }        
    },
    SOL_JOYSTICK_2(true) {
        @Override
        public SolutionsUIBase createSolution(SolutionsDataBase data) {
            if (data == null) data = new JoystickMouse2Data();
            return new JoystickMouse2UI((JoystickMouse2Data)data);
        }        
    },
    SOL_GYRO_MOUSE(true) {
        @Override
        public SolutionsUIBase createSolution(SolutionsDataBase data) {
            if (data == null) data = new GyroMouseData();
            return new GyroMouseUI((GyroMouseData)data);
        }
    };
    
/*    public static SolutionsUIBase rebuildSolution(SolutionsDataBase data) {
        ESolutionType type = data.getType();
        return type.createSolution(data);
    } */
    
    String localizedName;
    boolean worksOverBluetooth;
    
    ESolutionType(boolean bt) {
        localizedName = Resource.getStr(this.name());
        worksOverBluetooth = bt;
    }
    
    abstract public SolutionsUIBase createSolution(SolutionsDataBase data);
        
    @Override
    public String toString() {
        return localizedName;
    }
    
    public boolean worksOverBT() {
        return worksOverBluetooth;
    }
}
