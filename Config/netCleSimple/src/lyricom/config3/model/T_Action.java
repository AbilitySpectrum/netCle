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
package lyricom.config3.model;

import lyricom.config3.ui.MainFrame;

/**
 * Immutable POJO for action data.
 * Can be set to return either the HID or the BT form of the action.
 * This class is the repository for all HID->Bluetooth mappings.
 * @author Andrew
 */
public class T_Action {
    
    private final EAction action;
    private final int actionParam;

    public T_Action(EAction action, int param) {
        this.action = action;
        actionParam = param;
    }
    
    public EAction getAction() { // The Enum for the action
        if (MainFrame.getInstance().getBluetooth()) {
            switch(action) {
                case HID_MOUSE:
                    return EAction.BT_MOUSE;
                case HID_KEYBOARD:
                    return EAction.BT_KEYBOARD;
                case HID_SPECIAL:
                    return EAction.BT_SPECIAL;
                case HID_KEYPRESS:
                case HID_KEYRELEASE:
                    return null;
                default:
                    return action;  // All other actions are the same for BT and wired.
            }    
        } else {
            return action;
        }
    }
    
    public int getActionID() {  // The numeric id for the action.
        return getAction().getActionID();
    }
    
    public int getActionParam() {
        return actionParam;
    }
    
    public boolean isRepeat() {
        if (action == EAction.BT_MOUSE || action == EAction.HID_MOUSE) {
            if (actionParam == Model.MOUSE_DOWN 
                || actionParam == Model.MOUSE_LEFT
                || actionParam == Model.MOUSE_RIGHT
                || actionParam == Model.MOUSE_UP
                || actionParam == Model.MOUSE_WHEEL_UP
                || actionParam == Model.MOUSE_WHEEL_DOWN) {
                return true;
            }
        }
        return false;
    }
}
