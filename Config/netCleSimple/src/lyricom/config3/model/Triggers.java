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

import lyricom.config3.comms.IOError;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.TreeSet;
import java.util.zip.DataFormatException;

/**
 * Holds the list of triggers.
 * 
 * @author Andrew
 */
public class Triggers {
    private static final ResourceBundle RES = ResourceBundle.getBundle("strings");

    public static boolean DATA_IN_SYNC;
    
    // singleton pattern
    private static Triggers instance = null;
    
    public static Triggers getInstance() {
        if (instance == null) {
            instance = new Triggers();
        }
        return instance;
    }
    
    private List<Trigger> triggers = new ArrayList<>();
    private List<TriggerCallback> callbacks = new ArrayList<>();
    
    private Triggers() {        
    }
    
    public int length() {
        return triggers.size();
    }
    
    private void sizeChanged() {
        for(TriggerCallback tc: callbacks) {
            tc.newTriggerCount(length());
        }
    }
    
    public void addCallback(TriggerCallback tc) {
        callbacks.add(tc);
        tc.newTriggerCount(length());
    }
    
    public void removeCallback(TriggerCallback tc) {
        callbacks.remove(tc);
    }
    
    public Trigger get(int index) {
        return triggers.get(index);
    }
    
    public List<Trigger> getAll() {
        return triggers;
    }
    
    public Trigger newTrigger() {
        DATA_IN_SYNC = false;
        Trigger t = new Trigger();
        triggers.add(t);
        sizeChanged();
        return t;
    }
    
    public void replace(TmpImport tmp) {
        triggers = tmp.getList();
        sizeChanged();
    }

    public boolean isSensorUsed(ESensor s) {
        for(Trigger t: triggers) {
            if (t.getSensor() == s) {
                return true;
            }
        }
        return false;
    }
    
    public void deleteAll() {
        DATA_IN_SYNC = false;
        triggers = new ArrayList<>();
        sizeChanged();
    }
    
    public void deleteTriggerSet(ESensor s) {
        DATA_IN_SYNC = false;
        List<Trigger> list = new ArrayList<>();
        for(Trigger t: triggers) {
            if (t.getSensor() != s) {
                list.add(t);
            }
        }
        triggers = list;
        sizeChanged();
    }
    
    public void deleteTrigger(Trigger t) {
        DATA_IN_SYNC = false;
        triggers.remove(t);
        sizeChanged();
    }
    
    // Insert a new trigger, either before or after
    // the reference trigger.
    public void insertTrigger(Trigger t, Trigger ref, boolean after) {
        int refIndex = 0;

        DATA_IN_SYNC = false;
        // Find the reference location
        if (ref != null) {
            for(refIndex = 0; refIndex < triggers.size(); refIndex++) {  
                if (triggers.get(refIndex) == ref) {
                    break;
                }
            }  
            if (refIndex == triggers.size()) {
                sizeChanged();
                return;  // not found
            }
        }
        
        if (after) {
            triggers.add(refIndex+1, t);
        } else {
            triggers.add(refIndex, t);
        }
        sizeChanged();
    }

    // Load triggers from the device or a file.
    public void loadDataFromDevice(InStream in) throws IOError{
        TmpImport tmp = readTriggers(in);
        replace(tmp);
        int[] mouseSpeeds = tmp.getMouseSpeeds();
        if (mouseSpeeds != null) {
            CursorSpeedTransfer.getInstance().setSpeeds(mouseSpeeds);
        }
        DATA_IN_SYNC = true;
    }
        
    public TmpImport readTriggers(InStream in) throws IOError {
        TmpImport tmp = new TmpImport();
        
        if (!Objects.equals(in.getChar(), Model.START_OF_TRIGGERS)) {
            throw new IOError(RES.getString("CDE_INVALID_START"));
        }
        // Version check
        int ch = in.getChar();
        if (ch != '1') {
            throw new IOError(RES.getString("CDE)INVALID_PROTOCOL_VERSION"));
        } 
        int triggerCount = in.getNum(1);
        for(int i=0; i<triggerCount; i++) {
            Trigger t = new Trigger();
            t.fromStream(in);
            tmp.add(t);
        }
        
        ch = in.getChar();
        if (ch == Model.MOUSE_SPEED) {
            int[] speeds = CursorSpeedTransfer.getInstance().fromStream(in);
            if (speeds != null) {
                tmp.setMouseSpeeds(speeds);
            }
            ch = in.getChar();
        }
        if (ch != Model.END_OF_BLOCK) {
            throw new IOError(RES.getString("CDE_INVALID_END"));
        }
        return tmp;
    }
    
    public OutStream getTriggerData() throws DataFormatException {
        OutStream os = new OutStream();
        os.putChar(Model.START_OF_TRIGGERS);
        os.putChar((byte)'1');   // Protocol version
        os.putNum(length(), 1);
        
        for(Trigger t: Triggers.getInstance().getAll()) {
            t.toStream(os);
        }
            
        CursorSpeedTransfer.getInstance().toStream(os);
        
         os.putChar(Model.END_OF_BLOCK);
        return os;
    }
}
