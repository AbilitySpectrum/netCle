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

import lyricom.netCleConfig.model.Sensor;
import lyricom.netCleConfig.model.Trigger;

/**
 *
 * @author Andrew
 */
public class Location {
    Sensor sensor;
    int value;
    int condition;
    Trigger.Level level;
    
    Location getReverse() {
        Location l = new Location();
        l.sensor = sensor;
        l.value = value;
        l.level = level;
        if (condition == Trigger.TRIGGER_ON_HIGH) {
            l.condition = Trigger.TRIGGER_ON_LOW;
        } else {
            l.condition = Trigger.TRIGGER_ON_HIGH;
        }
        return l;
    }
}
