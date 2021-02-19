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

import static lyricom.config3.model.Model.KEY_PRESS;
import static lyricom.config3.model.Model.KEY_RELEASE;

/**
 * The ActionType is used to provide a string name to each action
 * and is also the key needed to retrieve an action by name.
 * 
 * Each action has an associated sensactActionID.  This is the ID for the 
 * action taken by the netCle and this must be in sync with the netCle Arduino code.
 * In some cases several actions listed here have the same sensactActionID.
 * This is because different UIs are needed for different facets of the
 * action.  e.g. The UI to enter a letter (a text box) is different from the
 * UI to select a special key (a combo box) but both result in a keyboard
 * action with an appropriate key code on the netCle.
 * 
 * @author Andrew
 */
public enum EAction {
    NONE(0, null),
    RELAY_A(1, null),
    RELAY_B(2, null),
    BT_KEYBOARD(3, (p) -> p >= 32),
    BT_SPECIAL(3, (p) -> p <  32),
    BT_MOUSE(9, null),
    HID_KEYBOARD(4,  (p) -> (((p & 0x80000000) == 0) && !((0x100 > p) && (p > 0x7f))) ),
    HID_SPECIAL (4,  (p) -> ((0xfe > p) && (p > 0x7f)) ),
    HID_KEYPRESS(4,  (p) -> ((p & 0xff000000) == KEY_PRESS)),
    HID_KEYRELEASE(4,(p) -> ((p & 0xff000000) == KEY_RELEASE)),
    HID_MOUSE(5, null),
    BUZZER(7, null),
    IR(8, null),
    SERIAL(6, null),
    SET_STATE(10, null),
    LIGHT_BOX(11, null),
    LCD_DISPLAY(12, null);

    
    private final int actionID;
    private final ParameterCheck check;
    
    EAction(int id, ParameterCheck check) {
        actionID = id;
        this.check = check;
    }
    
    /*
     * getActionByID uses the parameter passed to determine whether
     * the BT or HID action is 'Keyboard' or 'Special'.
     * or Press or Release.
    */
    public static EAction getActionByID(int id, int param) {
        for (EAction a: EAction.values()) {
            if (a.getActionID() == id) {
                if (a.check == null) {
                    return a;
                } else if (a.check.doCheck(param)) {
                    return a;
                }
            }
        }
        return null;
    }
         
    public int getActionID() {
        return actionID;
    }
 
}