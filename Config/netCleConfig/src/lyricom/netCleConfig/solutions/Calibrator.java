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

import java.util.ArrayList;
import java.util.List;
import lyricom.netCleConfig.comms.Connection;
import lyricom.netCleConfig.comms.Serial;
import lyricom.netCleConfig.model.Model;
import lyricom.netCleConfig.model.Sensor;
import lyricom.netCleConfig.model.SensorGroup;
import lyricom.netCleConfig.model.Trigger;

/**
 *
 * @author Andrew
 */
public class Calibrator {
    
    private class Sampling {
        int minval;
        int maxval;
        int allowance;
        boolean sampleStarted = false;
        Sensor sensor;
        
        Sampling(Sensor s) {
            sensor = s;
            allowance = (sensor.getMaxval()- sensor.getMinval()) / 10;
        }
        
        // Sample the sensor and record min and max sample values.
        void takeSample() {
            int val = sensor.getCurrentValue();
            if (sampleStarted) {
                if (val < minval) minval = val;
                if (val > maxval) maxval = val;
            } else {
                minval = maxval = val;
                sampleStarted = true;
            }
        }
        
        // Is the sensor currently within the sampled range?
        boolean inRange() {
            int val = sensor.getCurrentValue();
//            System.out.print("Current for " + sensor.getName() + ": ");
//            System.out.println(val);
            if ( (val >= (minval - allowance)) 
                    && (val <= (maxval + allowance))) {
                return true;
            } else {
                return false;
            }
        }
        
        int midPoint() {
            return (minval + maxval) / 2;
        }        
    }
    
    private Sampling[] restValues;
    private final SensorGroup theGroup;
    private final SolutionsUI theUI;
    private volatile boolean cancelling;
    
    Calibrator(SolutionsUI ui, SensorGroup sg) {
        theGroup = sg;
        theUI = ui;
        cancelling = false;
    }
    
    void startCalibration() {
        Connection.getInstance().writeByte(Model.CMD_DISPLAY);
    }
    
    void endCalibration() {
        Connection.getInstance().writeByte(Model.CMD_VERSION);        
    }
    
    void cancel() {
        cancelling = true;
    }
    
    void getRestValues() {
        // Create an array of Sampling - one per sensor.
        List<Sensor> sensors = new ArrayList<>();
        if (theGroup.getID().isBtnGroup()) {
            // If INPUT 1, 2 or 3 is theGroup the
            // search all of them for a signal.
            for(SensorGroup g: Model.getSensorGroups()) {
                if (g.getID().isBtnGroup()) {
                    sensors.addAll(g.getMembers());
                }
            }
        } else {
            sensors.addAll(theGroup.getMembers());
        }
        restValues = new Sampling[sensors.size()];
        for(int i=0; i< sensors.size(); i++) {
            restValues[i] = new Sampling(sensors.get(i));
        }
        
        // Gather signal levels.
        for(int i=0; i<5; i++) {
            if (sleepAndCancelCheck(100)) return;
            for(Sampling s: restValues) {
                s.takeSample();
            }
        }
    }
    
    // Combination of sleep and cancel check.
    private boolean sleepAndCancelCheck(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException ex) {
        }
        return cancelling;
    }
    
    // Find out which sensor is not at rest and provide the trigger information 
    // for it.
    Location getLocation(String message) {
        theUI.presentMessage(message);
        if (sleepAndCancelCheck(1000)) return null;
        
        int count = 0;
        Sampling target = null;
        Sampling restValue = null;
        
        while(target == null && count < 50) {
            count++;
            if (sleepAndCancelCheck(100)) return null;
            
            for(Sampling s: restValues) {
                if (! s.inRange() ) { // This one moved!
                    target = new Sampling(s.sensor);
                    restValue = s;
                    break;
                }
            }
        }
        
        if (target == null || restValue == null) return null; // not found
        
        // Get values for the activated sensor
        for(int i=0; i<5; i++) {
            if (sleepAndCancelCheck(100)) return null;
            target.takeSample();
        }
        
        theUI.presentMessage(SRes.getStr("SW_THANK_YOU"));
        if (sleepAndCancelCheck(1000)) return null;

        if (target.midPoint() < restValue.midPoint()) {
            if (  (target.maxval + target.allowance) 
                    >= (restValue.minval - restValue.allowance) ) {
                // Insufficient separation.
                return null;
            } else {
                Location l = new Location();
                l.sensor = target.sensor;
                l.value = (restValue.midPoint() + target.midPoint()) / 2;
                l.condition = Trigger.TRIGGER_ON_LOW;
                return l;
            }
        } else {
            if (  (target.minval - target.allowance) 
                    <= (restValue.maxval + restValue.allowance) ) {
                // Insufficient separation.
                return null;
            } else {
                Location l = new Location();
                l.sensor = target.sensor;
                l.value = (restValue.midPoint() + target.midPoint()) / 2;
                l.condition = Trigger.TRIGGER_ON_HIGH;
                return l;
            }            
        }
    }
}
