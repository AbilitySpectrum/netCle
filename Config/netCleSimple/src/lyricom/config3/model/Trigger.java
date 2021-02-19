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
import java.util.ResourceBundle;
import java.util.zip.DataFormatException;

/**
 *
 * @author Andrew
 */
public class Trigger {
    private static final ResourceBundle RES = ResourceBundle.getBundle("strings");

    public static final int TRIGGER_ON_LOW   = 1;
    public static final int TRIGGER_ON_HIGH  = 2;
    public static final int TRIGGER_ON_EQUAL = 3;
        
    public static final int DEFAULT_STATE = 1;
    
    private int reqdState;
    private ESensor sensor;
    private int triggerValue;
    private int condition;
    private int delay;
    private boolean repeat = false;
    private EAction action;
    private int actionParam;
    private int actionState;
        
    public Trigger() {
        sensor = null;
        triggerValue = 0;
        condition = TRIGGER_ON_HIGH;
        initValues();
    }
    
        
    public final void initValues() {
        reqdState = DEFAULT_STATE;
        actionState = DEFAULT_STATE;
        delay = 0;
        repeat = false;
        action = EAction.getActionByID(0, 0);
        actionParam = 0;
    }
    
    public final void setSensor(ESensor s) {
        sensor = s;
    }
    
    public void toStream(OutStream os) throws DataFormatException {
        os.putChar((byte)'\n');
        os.putID(sensor.getId(), 2);
        os.putID(reqdState, 1);
        os.putNum(triggerValue, 2);
        
        // Combine condition and repeat in one byte.
        int rval = repeat ? 4 : 0;
        os.putCondition(condition + rval);
        
        os.putID(action.getActionID(), 2);
        os.putID(actionState, 1);

        int transmittedActionParam = actionParam;
        /** Disable IR for now
        if (action.getType() == ActionType.IR) {
            // Map IR Action code parameter into the IR code signal
            // needed for the selected TV type.
            transmittedActionParam = TVInfo.getInstance().ID2Code(actionParam);
        } **/
        
        os.putNum(transmittedActionParam, 4);
        os.putNum(delay, 2);
    }
    
    public void fromStream(InStream is) throws IOError {
        int sensorID = is.getID(2);
        ESensor tmp = ESensor.getSensorByID(sensorID);
        if (tmp == null) {
            throw new IOError(RES.getString("CDE_INVALID_SENSOR_ID"));
        }
        setSensor(tmp);
        reqdState = is.getID(1);
        triggerValue = is.getNum(2);
        condition = is.getCondition();

        // Split condition and repeat
        repeat = (condition & 4) == 4;
        condition = condition & ~4;
        
        int actionID = is.getID(2);
        actionState = is.getID(1);
        actionParam = is.getNum(4);
        action = EAction.getActionByID(actionID, actionParam);
        if (action == null) {
            throw new IOError(RES.getString("CDE_INVALID_ACTION_ID"));
        }
        /** Disable IR for now
        if (action.getType() == ActionType.IR) {
            // Map action parameter from IR code to an action ID
            actionParam = TVInfo.getInstance().Code2ID(actionParam);
            if (actionParam == 0) {
                throw new IOError(RES.getString("CDE_INVALID_TV_CODE"));
            }            
        } **/
        delay = is.getNum(2);
    }


     public int getReqdState() {
        return reqdState;
    }

     public void setReqdState(int reqdState) {
        this.reqdState = reqdState;
        Triggers.DATA_IN_SYNC = false;
    }

    public ESensor getSensor() {
        return sensor;
    }

    public int getTriggerValue() {
        return triggerValue;
    }

    public void setTriggerValue(int triggerValue) {
        this.triggerValue = triggerValue;
        Triggers.DATA_IN_SYNC = false;
    }

    public int getCondition() {
        return condition;
    }

    public void setCondition(int condition) {
        this.condition = condition;
        Triggers.DATA_IN_SYNC = false;
    }
    
    // A routine to set signal level (a.k.a. triggerValue) and condition.
    public void setSignal(T_Signal sig) {
        triggerValue = sig.getLevel();
        condition = sig.getCondition();
        Triggers.DATA_IN_SYNC = false;
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

    public EAction getAction() {
        return action;
    }

    // A routine for setting action, action parameter and repeat.
    public void setAction(T_Action action) {
        this.action = action.getAction();
        this.actionParam = action.getActionParam();
        this.repeat = action.isRepeat();
        Triggers.DATA_IN_SYNC = false;        
    }
    
    public void setAction(EAction action) {
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
}
