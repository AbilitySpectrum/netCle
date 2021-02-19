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
    
    public Set<ESensor> getUsedSensors() {
        TreeSet<ESensor> theSet = new TreeSet<>();
        for(Trigger t: triggers) {
            theSet.add(t.getSensor());
        }        
        return theSet;
    }
    
    public void deleteTriggerSet(ESensor s) {
        List<Trigger> list = new ArrayList<>();
        for(Trigger t: triggers) {
            if (t.getSensor() != s) {
                list.add(t);
            }
        }
        triggers = list;
    }       
}
