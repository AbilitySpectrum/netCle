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
    
    List<Trigger> getList() {
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
    
    private class Cluster {
        private long sum;
        private int count;
        private int avg;
        private int width;
        
        public void reset(int w) {
            width = w;
            sum = 0;
            avg = count = 0;
        }
        
        public boolean empty() {
            return (count == 0);
        }
        
        public void add(int val) {
            sum += val;
            count++;
            avg = (int) sum/count;
        }
        
        public boolean inRange(int val) {
            return (proximity(val) < width);
        }
        
        public int proximity(int val) {
            return (int) (Math.abs(val - avg));
        }
        
        public int avg() {
            return (int) avg;
        }
    }
    
    // Now the hard bit.  Deduce and set levels.
    // All data within 15% of the average of a group
    // is clusters in that group.
    // Two groups are collected.
    // Data belonging to neither group is put into the nearest group.
    // DO NOT apply this to sensors that are not continuous.
    // This code was needed to support the transition to fixed levels.
    // That transition is long-ago complete, so perhaps this is no longer
    // needed??
    public void groupLevels() {
        Cluster group1 = new Cluster();
        Cluster group2 = new Cluster();
        
        // For each sensor ...
        for(Sensor s: Model.sensorList) {
            if (!s.isContinuous()) {
                continue;
            }
            int clusterWidth = ((s.getMaxval() - s.getMinval()) * 15) / 100;
            group1.reset(clusterWidth);
            group2.reset(clusterWidth);
            
            // .. go through all the triggers and cluster for that sensor.
            for(Trigger t: triggers) {
                if (t.getSensor() == s) {
                    int tval = t.getTriggerValue();
                    if (group1.empty()) {
                        group1.add(tval);
                    } else {
                        // Group 1 already started
                        if (group1.inRange(tval)) {
                            // This one can be added to group 1.
                            group1.add(tval);
                        } else {
                            if (group2.empty()) {
                                group2.add(tval);
                            } else {
                                // Add to the nearest group.
                                if (group1.proximity(tval) < group2.proximity(tval)) {
                                    group1.add(tval);
                                } else {
                                    group2.add(tval);
                                }
                            }
                        }                        
                    }
                }
            }
            if (!group1.empty()) {
                // ... then set sensor levels and adjust trigger values
                // to reflect the results of clustering.
                if (group2.empty()) {
                    s.setLevels(group1.avg(), s.getLevel2());
                } else {
                    s.setLevels(group1.avg(), group2.avg());
                }
                for(Trigger t: triggers) {
                    if (t.getSensor() == s) {
                        if (group2.empty()) {
                            // All are in group 1
                            t.setTriggerValue(group1.avg());
                            t.setLevel(Trigger.Level.LEVEL1);
                        } else {                        
                            int tval = t.getTriggerValue();
                            if (group1.proximity(tval) < group2.proximity(tval)) {
                                t.setTriggerValue(group1.avg());
                                t.setLevel(Trigger.Level.LEVEL1);
                            } else {
                                t.setTriggerValue(group2.avg());
                                t.setLevel(Trigger.Level.LEVEL2);                        
                            }
                        }
                    }
                }
            }
        }
    }    
}
