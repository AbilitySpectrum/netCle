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

import lyricom.config3.model.Resource;

/**
 *
 * @author Andrew
 */
public enum ETwoButtonSimple {
    TBS_SCROLL_UP_DOWN,
    TBS_LEFT_RIGHT_CLICK,
    TBS_LEFT_PRESS_RELEASE,
    TBS_LEFT_RIGHT_BUTTON;
    
    final private String localizedName;
    
    ETwoButtonSimple() {
        localizedName = Resource.getStr(this.name());        
    }

    @Override
    public String toString() {
        return localizedName;
    }
}
