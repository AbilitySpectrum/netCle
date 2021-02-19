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

import lyricom.config3.model.EAction;
import lyricom.config3.model.Model;
import lyricom.config3.model.Resource;
import lyricom.config3.model.T_Action;

/**
 *
 * @author Andrew
 */
public enum EOneButtonSimple {
    OBS_LEFT_CLICK   (new T_Action(EAction.HID_MOUSE, Model.MOUSE_CLICK)),
    OBS_LEFT_PRESS   (new T_Action(EAction.HID_MOUSE, Model.MOUSE_PRESS)),
    OBS_LEFT_RELEASE (new T_Action(EAction.HID_MOUSE, Model.MOUSE_RELEASE)),
    OBS_RIGHT_CLICK  (new T_Action(EAction.HID_MOUSE, Model.MOUSE_RIGHT_CLICK));
    
    final private String localizedName;
    final private T_Action action;
    
    EOneButtonSimple(T_Action a) {
        localizedName = Resource.getStr(this.name());       
        action = a;
    }

    @Override
    public String toString() {
        return localizedName;
    }

    public T_Action getAction() {
        return action;
    }
}
