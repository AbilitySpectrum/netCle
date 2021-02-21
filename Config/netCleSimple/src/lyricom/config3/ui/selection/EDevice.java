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

/**
 *
 * @author Andrew
 */
public enum EDevice {
    CURSOR_ONE_BTN  (EActivity.CURSOR),
    CURSOR_TWO_BTN  (EActivity.CURSOR),
    CURSOR_JOYSTICK (EActivity.CURSOR),
    CURSOR_GYRO     (EActivity.CURSOR),
    
    MOUSE_ONE_BTN   (EActivity.MOUSE_BUTTONS),
    MOUSE_TWO_BTN   (EActivity.MOUSE_BUTTONS),
    MOUSE_JOYSTICK  (EActivity.MOUSE_BUTTONS),
    MOUSE_GYRO      (EActivity.MOUSE_BUTTONS),
    
    SCROLL_ONE_BTN  (EActivity.SCROLLING),
    SCROLL_TWO_BTN  (EActivity.SCROLLING),
    SCROLL_JOYSTICK (EActivity.SCROLLING),
    
    KEYBOARD_BTN    (EActivity.KEYBOARD);

    private final String localizedName;
    private final EActivity activity;
    EDevice(EActivity act) {
        activity = act;
        localizedName = SelectionResource.getStr(this.name());
    }
    
    public String getText() {
        return localizedName;
    }
    
    public EActivity getActivity() {
        return activity;
    }
}
