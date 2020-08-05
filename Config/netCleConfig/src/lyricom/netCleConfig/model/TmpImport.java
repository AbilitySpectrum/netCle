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
package lyricom.netCleConfig.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * This class is used to hold triggers and mouse speeds that are being loaded.
 * It allows the loaded data to be examined and possibly adjusted
 * before being added to the Triggers singleton.
 * 
 * @author Andrew
 */
public class TmpImport {
    private List<Trigger> triggers = new ArrayList<>();
    private int[] mouseSpeeds = null;
    
    public TmpImport() {
        
    }
    
    public void add(Trigger t) {
        triggers.add(t);
    }
    
    public void setMouseSpeeds(int[] ms) {
        mouseSpeeds = ms;
    }
    
    public int[] getMouseSpeeds() {
        return mouseSpeeds;
    }
    
    public void eraseMouseSpeeds() {
        mouseSpeeds = null;
    }
    
    public List<Trigger> getList() {
        return triggers;
    }
    
    public Set<Sensor> getUsedSensors() {
        TreeSet<Sensor> theSet = new TreeSet<>();
        for(Trigger t: triggers) {
            theSet.add(t.getSensor());
        }        
        return theSet;
    }
    
    public void deleteTriggerSet(Sensor s) {
        List<Trigger> list = new ArrayList<>();
        for(Trigger t: triggers) {
            if (t.getSensor() != s) {
                list.add(t);
            }
        }
        triggers = list;
    }    
    
    // There should be, at most, two values for all the
    // triggers associated with a single sensor.
    // These are the two threshold values.
    // This code goes through all the triggers to deduce
    // what these two levels are.
    public void groupLevels() {
        int level1 = 0;
        boolean level1Set;
        int level2 = 0;
        boolean level2Set;
        
        // For each sensor ...
        for(Sensor s: Model.sensorList) {
            if (!s.isContinuous()) {
                continue;  // ... but not the non-continuos ones.
            }
            level1Set = level2Set = false;
            
            // .. go through all the triggers and find the two levels
            // for the sensor.
            for(Trigger t: triggers) {
                if (t.getSensor() == s) {
                    int tval = t.getTriggerValue();
                    if (!level1Set) {
                        level1 = tval;
                        level1Set = true;
                    } else {
                        // Group 1 already started
                        if (tval != level1) {
                            if (!level2Set) {
                                level2 = tval;
                                level2Set = true;
                            } else if (tval != level2) {
                                // level1 and level2 already set.
                                // value matches neither - should not happen.
                                // Go with closest match
                                int diff1, diff2;
                                
                                diff1 = level1 - tval;
                                if (diff1 < 0) diff1 *= -1;
                                
                                diff2 = level2 - tval;
                                if (diff2 < 0) diff2 *= -1;
                                
                                if (diff1 < diff2) {
                                    t.forceTriggerValue(level1);
                                } else {
                                    t.forceTriggerValue(level2);
                                }
                            }
                        }
                    }
                }
            }
            if (level1Set) {
                // ... then set sensor levels and adjust trigger values
                // to reflect the results of clustering.
                if (level2Set) {
                    s.setLevels(level1, level2);
                } else {
                    s.setLevels(level1, s.getLevel2());
                }
                for(Trigger t: triggers) {
                    if (t.getSensor() == s) {
                        if (!level2Set) {
                            // All are in group 1
                           t.setLevel(Trigger.Level.LEVEL1);
                        } else {                        
                            int tval = t.getTriggerValue();
                            if (tval == level1) {
                                t.setLevel(Trigger.Level.LEVEL1);
                            } else {
                                t.setLevel(Trigger.Level.LEVEL2);                        
                            }
                        }
                    }
                }
            }
        }
    }    
}
