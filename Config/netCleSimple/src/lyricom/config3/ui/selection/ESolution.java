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
package lyricom.config3.ui.selection;

import lyricom.config3.solutions.SolutionsDataBase;
import lyricom.config3.solutions.SolutionsUIBase;
import lyricom.config3.solutions.data.*;
import lyricom.config3.solutions.ui.*;

/**
 *
 * @author Andrew
 */
public enum ESolution {
    S_ONE_BTN_MOUSE  (EDevice.CURSOR_ONE_BTN, true) {  // ok
        @Override
        public SolutionsUIBase createSolution(SolutionsDataBase data) {
            if (data == null) data = new OneBtnMouseData();
            return new OneBtnMouseUI((OneBtnMouseData)data);
        }        
    },
    S_TWO_BTN_MOUSE  (EDevice.CURSOR_TWO_BTN, true) {  // ok
        @Override
        public SolutionsUIBase createSolution(SolutionsDataBase data) {
            if (data == null) data = new TwoBtnMouseData();
            return new PortOnlyUI((PortOnlyBase)data);
        }                
    },
    S_JOYSTICK_MOUSE1(EDevice.CURSOR_JOYSTICK, true) {  // ok
        @Override
        public SolutionsUIBase createSolution(SolutionsDataBase data) {
            if (data == null) data = new JoystickMouse1Data();
            return new JoystickMouse1UI((JoystickMouse1Data)data);
        }        
    },
    S_JOYSTICK_MOUSE2(EDevice.CURSOR_JOYSTICK, true) {  // ok
        @Override
        public SolutionsUIBase createSolution(SolutionsDataBase data) {
            if (data == null) data = new JoystickMouse2Data();
            return new JoystickMouse2UI((JoystickMouse2Data)data);
        }        
    },
    S_GYRO_MOUSE     (EDevice.CURSOR_GYRO, true) {  // ok
        @Override
        public SolutionsUIBase createSolution(SolutionsDataBase data) {
            if (data == null) data = new GyroMouseData();
            return new GyroMouseUI((GyroMouseData)data);
        }
    },
   
    S_LEFT_CLICK       (EDevice.MOUSE_ONE_BTN, true) { // ok
        @Override
        public SolutionsUIBase createSolution(SolutionsDataBase data) {
            if (data == null) data = new OBS_LeftClick();
            return new PortAndAudioUI((PortAndAudioBase)data);
        }
    },
    S_RIGHT_CLICK       (EDevice.MOUSE_ONE_BTN, true) {  // ok
        @Override
        public SolutionsUIBase createSolution(SolutionsDataBase data) {
            if (data == null) data = new OBS_RightClick( );
            return new PortAndAudioUI((PortAndAudioBase)data);
        }
    },
    S_LEFT_PRESS_RELEASE_TOGGLE (EDevice.MOUSE_ONE_BTN, true) {  // ok
        @Override
        public SolutionsUIBase createSolution(SolutionsDataBase data) {
            if (data == null) data = new OBT_LPressRelease();
            return new PortOnlyUI((PortOnlyBase)data);
        }
    },
    S_LEFT_EMULATION   (EDevice.MOUSE_ONE_BTN, true) {  // ok
        @Override
        public SolutionsUIBase createSolution(SolutionsDataBase data) {           
            if (data == null) data = new OBE_LeftButton();
            return new PortOnlyUI((PortOnlyBase)data);
        }
    },
    S_RIGHT_EMULATION  (EDevice.MOUSE_ONE_BTN, true) {  // ok
        @Override
        public SolutionsUIBase createSolution(SolutionsDataBase data) {           
            if (data == null) data = new OBE_RightButton();
            return new PortOnlyUI((PortOnlyBase)data);
        }
    },
    S_THREE_FUNC_BUTTON (EDevice.MOUSE_ONE_BTN, true) {  // ok
        @Override
        public SolutionsUIBase createSolution(SolutionsDataBase data) {
            if (data == null) data = new ThreeFuncBtnData();
            return new PortOnlyUI((PortOnlyBase)data);
        }
    },    
    S_LEFT_RIGHT_CLICK     (EDevice.MOUSE_TWO_BTN, true) {  // ok
        @Override
        public SolutionsUIBase createSolution(SolutionsDataBase data) {
            if (data == null) data = new TBS_LRClick();
            return new PortAndAudioUI((PortAndAudioBase)data);
        }
    },
    S_LEFT_PRESS_RELEASE   (EDevice.MOUSE_TWO_BTN, true) {  // ok
        @Override
        public SolutionsUIBase createSolution(SolutionsDataBase data) {
            if (data == null) data = new TBS_LPressRelease();
            return new PortAndAudioUI((PortAndAudioBase)data);
        }
    },
    S_LEFT_RIGHT_EMULATION (EDevice.MOUSE_TWO_BTN, true) {  // ok
        @Override
        public SolutionsUIBase createSolution(SolutionsDataBase data) {
            if (data == null) data = new TBS_LREmulation();
            return new PortAndAudioUI((PortAndAudioBase)data);
        }
    },
    
    S_JOYSTICK_CLICKS (EDevice.MOUSE_JOYSTICK, true) {
        @Override
        public SolutionsUIBase createSolution(SolutionsDataBase data) {
            // TBD - requires adjustment of another solution
            return null;
        }
    },
    S_GYRO_CLICKS     (EDevice.MOUSE_GYRO, true) {
        @Override
        public SolutionsUIBase createSolution(SolutionsDataBase data) {
            // TBD - requires adjustment of another solution
            return null;
        }
    },
    S_SCROLL_UP_DOWN_TOGGLE  (EDevice.SCROLL_ONE_BTN, true) {  // ok
        @Override
        public SolutionsUIBase createSolution(SolutionsDataBase data) {           
            if (data == null) data = new OBT_ScrollUpDown();
            return new PortOnlyUI((PortOnlyBase)data);
        }
    },
    S_SCROLL_UP_DOWN (EDevice.SCROLL_TWO_BTN, true) {  // ok
        @Override
        public SolutionsUIBase createSolution(SolutionsDataBase data) {
            if (data == null) data = new TBS_ScrollUpDown();
            return new PortAndAudioUI((PortAndAudioBase)data);
        }
    },
    S_JOYSTICK       (EDevice.SCROLL_JOYSTICK, true) {
        @Override
        public SolutionsUIBase createSolution(SolutionsDataBase data) {
            // TBD - requires adjustment of another solution
            return null;
        }
    },
    
    S_KEYBOARD_TEXT    (EDevice.KEYBOARD_BTN, true) {
        @Override
        public SolutionsUIBase createSolution(SolutionsDataBase data) {
            return null;
        }
    },
    S_KEYBOARD_SPECIAL (EDevice.KEYBOARD_BTN, true) {
        @Override
        public SolutionsUIBase createSolution(SolutionsDataBase data) {
            // TBD - Keyboard
            return null;
        }
    },
    S_KEYBOARD_MODIFIER(EDevice.KEYBOARD_BTN, false) {
        @Override
        public SolutionsUIBase createSolution(SolutionsDataBase data) {
            // TBD - Keyboard
            return null;
        }
    },
    S_UP_DOWN_ARROW_TOGGLE   (EDevice.KEYBOARD_BTN, true) {
        @Override
        public SolutionsUIBase createSolution(SolutionsDataBase data) {
            // TBD - Keyboard
            return null;
        }
    },
    S_KEYBOARD_SHIFT   (EDevice.KEYBOARD_BTN, false) {
        @Override
        public SolutionsUIBase createSolution(SolutionsDataBase data) {
            // TBD - Keyboard
            return null;
        }
    },
    S_KEYBOARD_CONTROL (EDevice.KEYBOARD_BTN, false) {
        @Override
        public SolutionsUIBase createSolution(SolutionsDataBase data) {
            // TBD - Keyboard
            return null;
        }
    };
    
    private final EDevice device;
    private final boolean canDoBluetooth;
    private final String localizedName;
    private final String shortDescription;
    private final String longDescription;
    
    ESolution(EDevice device, boolean blue) {
        this.device = device;
        canDoBluetooth = blue;
        localizedName = SelectionResource.getStr(this.name());
        // Key for the short description is ENum name prefixed with 'D'.
        shortDescription = SelectionResource.getStr("D" + this.name());
        // Key for the long description is ENum name prefixed with 'LD'.
        longDescription = SelectionResource.getStr("LD" + this.name());
    }
    
    abstract public SolutionsUIBase createSolution(SolutionsDataBase data);
    
    public static SolutionsUIBase rebuildSolution(SolutionsDataBase data) {
//        ESolution type = data.getType();
 //       return type.createSolution(data);
 return null;
    }
    
    
    public EDevice getDevice() {
        return device;
    }
    
    public String getText() {
        return localizedName;
    }
    
    @Override
    public String toString() {
        return localizedName;
    }
    
    public String getShortDescription() {
        return shortDescription;
    }
    
    public String getLongDescription() {
        return longDescription;
    }
    
    public boolean worksOverBluetooth() {
        return canDoBluetooth;
    }
}
