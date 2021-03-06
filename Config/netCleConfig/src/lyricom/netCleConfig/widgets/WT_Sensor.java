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
package lyricom.netCleConfig.widgets;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import lyricom.netCleConfig.model.Model;
import lyricom.netCleConfig.model.Sensor;
import lyricom.netCleConfig.model.Trigger;

/**
 * A combo box which displays all sensors.  
 * This is used to set the sensor value in State Change actions.
 * 
 * @author Andrew
 */
public class WT_Sensor extends W_Combo {

    private final Trigger theTrigger;
    public WT_Sensor(String label, Trigger t) {
        super(label, Model.sensorList);
        
        theTrigger = t;  
        update();
    }

    @Override
    public void widgetChanged() {
        Sensor s = (Sensor) theBox.getSelectedItem();
        
        int state = theTrigger.getActionParam() & 0xff;
        
        theTrigger.setActionParam( (s.getId() << 8) + state);
    }
    
    @Override
    public void update() {
        int sensorID = (theTrigger.getActionParam() >> 8) & 0xff;
        
        for(Sensor s: Model.sensorList) {
            if (s.getId() == sensorID) {
                theBox.setSelectedItem(s);
            }
        }
        
    }
}
