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
    SOL_ONE_BUTTON_SIMPLE(true) {
        @Override
        public SolutionsUIBase createSolution(SolutionsDataBase data) {
            if (data == null) data = new OneButtonSimpleData();
            return new OneButtonSimpleUI((OneButtonSimpleData)data);
        }        
    },
    SOL_ONE_BUTTON_TOGGLE(true) {
        @Override
        public SolutionsUIBase createSolution(SolutionsDataBase data) {
            if (data == null) data = new OneButtonToggleData();
            return new OneButtonToggleUI((OneButtonToggleData)data);
        }       
    },
    SOL_ONE_BUTTON_MOUSE_CLICKS(true) {
        @Override
        public SolutionsUIBase createSolution(SolutionsDataBase data) {
            if (data == null) data = new OneButtonMouseClicksData();
            return new OneButtonMouseClicksUI((OneButtonMouseClicksData)data);
        }                        
    },
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
    SOL_TWO_BUTTON_SIMPLE(true) {
        @Override
        public SolutionsUIBase createSolution(SolutionsDataBase data) {
            if (data == null) data = new TwoButtonsSimpleData();
            return new TwoButtonsSimpleUI((TwoButtonsSimpleData)data);
        }        
    },
    SOL_TWO_BUTTON_CURSOR_CONTROL(true) {
        @Override
        public SolutionsUIBase createSolution(SolutionsDataBase data) {
            if (data == null) data = new TwoButtonCursorControlData();
            return new TwoButtonCursorControlUI((TwoButtonCursorControlData)data);
        }                
    },
    SOL_JOYSTICK_1(true) {
        @Override
        public SolutionsUIBase createSolution(SolutionsDataBase data) {
            if (data == null) data = new Joystick1Data();
            return new Joystick1UI((Joystick1Data)data);
        }        
    },
    SOL_JOYSTICK_2(true) {
        @Override
        public SolutionsUIBase createSolution(SolutionsDataBase data) {
            if (data == null) data = new Joystick2Data();
            return new Joystick2UI((Joystick2Data)data);
        }        
    },
    SOL_GYRO_MOUSE(true) {
        @Override
        public SolutionsUIBase createSolution(SolutionsDataBase data) {
            if (data == null) data = new GyroData();
            return new GyroUI((GyroData)data);
        }
    };
    
    public static SolutionsUIBase rebuildSolution(SolutionsDataBase data) {
        ESolutionType type = data.getType();
        return type.createSolution(data);
    }
    
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
