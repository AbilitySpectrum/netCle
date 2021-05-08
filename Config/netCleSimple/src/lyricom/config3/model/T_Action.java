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
    // Commonly used actions
    public static final T_Action NONE = new T_Action(EAction.NONE, 0);
    public static final T_Action MOUSE_UP = new T_Action(EAction.HID_MOUSE, Model.MOUSE_UP);
    public static final T_Action MOUSE_DOWN = new T_Action(EAction.HID_MOUSE, Model.MOUSE_DOWN);
    public static final T_Action MOUSE_LEFT = new T_Action(EAction.HID_MOUSE, Model.MOUSE_LEFT);
    public static final T_Action MOUSE_RIGHT = new T_Action(EAction.HID_MOUSE, Model.MOUSE_RIGHT);
    public static final T_Action MOUSE_LCLICK = new T_Action(EAction.HID_MOUSE, Model.MOUSE_CLICK);
    public static final T_Action MOUSE_RCLICK = new T_Action(EAction.HID_MOUSE, Model.MOUSE_RIGHT_CLICK);
    public static final T_Action MOUSE_PRESS = new T_Action(EAction.HID_MOUSE, Model.MOUSE_PRESS);
    public static final T_Action MOUSE_RELEASE = new T_Action(EAction.HID_MOUSE, Model.MOUSE_RELEASE);
    public static final T_Action MOUSE_WHEEL_UP = new T_Action(EAction.HID_MOUSE, Model.MOUSE_WHEEL_UP);
    public static final T_Action MOUSE_WHEEL_DOWN = new T_Action(EAction.HID_MOUSE, Model.MOUSE_WHEEL_DOWN);
    
    // Useful action constructors
    public static T_Action createSetStateAction(ESensor sens, int state) {
        return new T_Action(EAction.SET_STATE, (sens.getId() << 8) + state);
    }
    
    public static T_Action createBuzzerAction(int frequency, int duration) {
        return new T_Action(EAction.BUZZER, (frequency << 16) + duration);
    }
    
    public static T_Action createLightBoxAction(int type, int light) {
        return new T_Action(EAction.LIGHT_BOX, type + (1 << (light-1)));
    }
    
    public static T_Action createKeyboardAction(int values) {
        return new T_Action(EAction.HID_KEYBOARD, values);
    }
    
    public static T_Action createKeyboardAction(EKeyCode key) {
        return new T_Action(EAction.HID_KEYBOARD, key.getCode());
    }
    
    public static T_Action createModifiedKeyAction(EKeyCode key, EKeyCode modifier) {
        return new T_Action(EAction.HID_KEYBOARD, 
                Model.KEY_MODIFIER + (modifier.getCode() << 8) + key.getCode());
    }
    
    public static T_Action createKeyPressAction(EKeyCode code) {
        return new T_Action(EAction.HID_KEYBOARD, Model.KEY_PRESS + code.getCode());
    }
    public static T_Action createKeyReleaseAction(EKeyCode code) {
        return new T_Action(EAction.HID_KEYBOARD, Model.KEY_RELEASE + code.getCode());
    }
    
    // -----------------------------------------------------------------
    // The simple POJO under it all.
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
    
    // A list of things which automatically get the repeat option.
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
