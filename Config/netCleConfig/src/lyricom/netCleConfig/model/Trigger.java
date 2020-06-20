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

import java.util.ResourceBundle;
import java.util.zip.DataFormatException;

/**
 *
 * @author Andrew
 */
public class Trigger {
    private static final ResourceBundle RES = ResourceBundle.getBundle("strings");

    public static enum Level {
        LEVEL1, LEVEL2
    }
    
    public static final int TRIGGER_ON_LOW   = 1;
    public static final int TRIGGER_ON_HIGH  = 2;
    public static final int TRIGGER_ON_EQUAL = 3;
    
    public static final byte TRIGGER_START = (byte) 't';
    public static final byte TRIGGER_END = (byte) 'z';
    
    public static final int DEFAULT_STATE = 1;
    
    private int reqdState;
    private Sensor sensor;
    private int triggerValue;
    private int condition;
    private int delay;
    private boolean repeat = false;
    private SaAction action;
    private int actionParam;
    private int actionState;
    
    private Level level;
    
    // Package visibility only.  UI must create triggers via 
    // Triggers.newTrigger.
    Trigger() {
        sensor = null;
        triggerValue = 0;
        condition = TRIGGER_ON_HIGH;
        initValues();
    }
    
    public Trigger(Sensor s) {
        initValues();
        setSensor(s);
    }
    
    public void copyValue(Trigger other) {
        // Safety check?
        if (sensor != other.sensor) return;
        
        reqdState = other.reqdState;
        if (getSensor().isContinuous()) {
            triggerValue = sensor.getLevel(other.level);
        } else {
            triggerValue = other.triggerValue;        
        }
        level = other.level;
        condition = other.condition;
        delay = other.delay;
        repeat = other.repeat;
        action = other.action;
        actionParam = other.actionParam;
        actionState = other.actionState;
        level = other.level;
    }
    
    public final void initValues() {
        reqdState = DEFAULT_STATE;
        actionState = DEFAULT_STATE;
        delay = 0;
        repeat = false;
        action = Model.getActionByID(0, 0);
        actionParam = 0;
        level = Level.LEVEL1;
    }
    
    public final void setSensor(Sensor s) {
        sensor = s;
        if (getSensor().isContinuous()) {
            triggerValue = sensor.getLevel(level);
            setCondition(TRIGGER_ON_HIGH);
        } else {
            triggerValue = 'a';
            setCondition(TRIGGER_ON_EQUAL);
        }
    }
    
    public void toStream(OutStream os) throws DataFormatException {
        os.putChar((byte)'\n');
        if (Model.getVersionID() < 102) { // use old format
            os.putChar(TRIGGER_START);
        }
        os.putID(sensor.getId(), 2);
        os.putID(reqdState, 1);
        os.putNum(triggerValue, 2);
        if (Model.getVersionID() < 102) { // use old format
            os.putCondition(condition);
        } else {
            // Combine condition and repeat in one byte.
            int rval = repeat ? 4 : 0;
            os.putCondition(condition + rval);
        }
        os.putID(action.getId(), 2);
        os.putID(actionState, 1);

        int transmittedActionParam = actionParam;
        if (action.getType() == ActionType.IR) {
            // Map IR Action code parameter into the IR code signal
            // needed for the selected TV type.
            transmittedActionParam = TVInfo.getInstance().ID2Code(actionParam);
        }
        
        os.putNum(transmittedActionParam, 4);
        os.putNum(delay, 2);
        if (Model.getVersionID() < 102) {  // use old format
            os.putBoolean(repeat);
            os.putChar(TRIGGER_END);
        }
    }
    
    public void fromStream(InStream is, int version) throws IOError {
        if (version == 0) {
            if (is.getChar() != TRIGGER_START) {
                throw new IOError(RES.getString("CDE_INVALID_TRIGGER_START"));
            }
        }
        int sensorID = is.getID(2);
        Sensor tmp = Model.getSensorByID(sensorID);
        if (tmp == null) {
            throw new IOError(RES.getString("CDE_INVALID_SENSOR_ID"));
        }
        setSensor(tmp);
        reqdState = is.getID(1);
        triggerValue = is.getNum(2);
        condition = is.getCondition();
        if (version == 1) {
            // Split condition and repeat
            repeat = (condition & 4) == 4;
            condition = condition & ~4;
        }
        int actionID = is.getID(2);
        actionState = is.getID(1);
        actionParam = is.getNum(4);
        action = Model.getActionByID(actionID, actionParam);
        if (action == null) {
            throw new IOError(RES.getString("CDE_INVALID_ACTION_ID"));
        }
        if (action.getType() == ActionType.IR) {
            // Map action paramter from IR code to an action ID
            actionParam = TVInfo.getInstance().Code2ID(actionParam);
            if (actionParam == 0) {
                throw new IOError(RES.getString("CDE_INVALID_TV_CODE"));
            }            
        }
        delay = is.getNum(2);
        if (version == 0) {
            repeat = is.getBoolean();
            if (is.getChar() != TRIGGER_END) {
                throw new IOError(RES.getString("CDE_INVALID_TRIGGER_END"));
            }
        }
    }

     public int getReqdState() {
        return reqdState;
    }

     public void setReqdState(int reqdState) {
        this.reqdState = reqdState;
        Triggers.DATA_IN_SYNC = false;
    }

    public Sensor getSensor() {
        return sensor;
    }

    public int getTriggerValue() {
        return triggerValue;
    }

    // Should only be called to update a non-continuous trigger
    // e.g. USB serial input.
    // For continous triggers, set the levels in the sensor
    // and call setLevel to set the trigger value from the sensor level.
    public void setTriggerValue(int triggerValue) {
        this.triggerValue = triggerValue;
        Triggers.DATA_IN_SYNC = false;
    } 
    
    // Should never be called really - see groupLevels in TmpImport.java
    public void forceTriggerValue(int triggerValue) {
        this.triggerValue = triggerValue;
        Triggers.DATA_IN_SYNC = false;
    } 

    public int getCondition() {
        return condition;
    }

    public void setCondition(int condition) {
        if (sensor.isContinuous()) {
            this.condition = condition;
            Triggers.DATA_IN_SYNC = false;
        } else if (condition == TRIGGER_ON_EQUAL) {
            this.condition = condition;
            Triggers.DATA_IN_SYNC = false;
        }
    }

    public int getDelay() {
        return delay;
    }

    public void setDelay(int delay) {
        this.delay = delay;
        Triggers.DATA_IN_SYNC = false;
    }

    public boolean isRepeat() {
        return repeat;
    }

    public void setRepeat(boolean repeat) {
        this.repeat = repeat;
        Triggers.DATA_IN_SYNC = false;
    }

    public SaAction getAction() {
        return action;
    }

    public void setAction(SaAction action) {
        this.action = action;
        Triggers.DATA_IN_SYNC = false;
    }

    public int getActionParam() {
        return actionParam;
    }

    public void setActionParam(int actionParam) {
        this.actionParam = actionParam;
        Triggers.DATA_IN_SYNC = false;
    }

    public int getActionState() {
        return actionState;
    }

    public void setActionState(int actionState) {
        this.actionState = actionState;
         Triggers.DATA_IN_SYNC = false;
   }

    public Level getLevel() {
        return level;
    }

    // Set the level and set the value from the sensor level.
    public void setLevel(Level level) {
        this.level = level;
        this.triggerValue = sensor.getLevel(level);
        Triggers.DATA_IN_SYNC = false;
    }
    
    // Called when the sensor level has been changed
    // (by the user moving a slider in the Set Threshold window.
    public void updateLevel() {
        this.triggerValue = sensor.getLevel(level);
        Triggers.DATA_IN_SYNC = false;
    }
}
