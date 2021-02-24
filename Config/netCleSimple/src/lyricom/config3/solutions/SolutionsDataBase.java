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

import java.io.PrintStream;
import java.util.ResourceBundle;
import javax.swing.*;
import javax.xml.bind.annotation.XmlSeeAlso;
import lyricom.config3.model.ESensor;
import lyricom.config3.model.T_Action;
import lyricom.config3.model.T_Signal;
import lyricom.config3.model.Trigger;
import lyricom.config3.model.Triggers;
import lyricom.config3.solutions.data.*;
import lyricom.config3.ui.Utils;
import lyricom.config3.ui.selection.ESolution;

/**
 *
 * @author Andrew
 */
@XmlSeeAlso({
    OneBtnMouseData.class,   // ok
    TwoBtnMouseData.class,   // ok
    JoystickMouse1Data.class,  // ok
    JoystickMouse2Data.class, // ok
    GyroMouseData.class,  // ok
    
    OBS_LeftClick.class,  // ok
    OBS_RightClick.class,   // ok

    OBP_LPressRelease.class,  // ok
    OBT_ScrollUpDown.class,  // ok

    OBE_LeftButton.class,  // ok

    ThreeFuncBtnData.class, // ok
        
    TBS_LRClick.class,  // ok
    TBS_ScrollUpDown.class,  // ok
        
    KeyboardTextData.class,
    KeyboardSpecialData.class,
    KeyboardModifierData.class,
    OBT_UpDownArrow.class,
    OBP_ShiftKey.class,
    OBP_ControlKey.class
})

public abstract class SolutionsDataBase {
    protected static final ResourceBundle RES = ResourceBundle.getBundle("strings");
    private final ESolution type;
    
    protected int sensorCount = 0;  // 1 for single-switch solutions, 2 for double-switch, 0 for others.
    protected int sensorCountB = 0; // for the rare case where a solution uses two ports.
    
    public SolutionsDataBase(ESolution t) {
        type = t;
        SolutionsDataList.getInstance().add(this);
    }
    
    public ESolution getType() {
        return type;
    }
    
    abstract public void compile();
    abstract public void printDescription(PrintStream out);
    
    // Solution Widgets - consistantly styled
    protected final JCheckBox checkBox(String title) {
        JCheckBox item = new JCheckBox(title);
        item.setFont(Utils.STD_BOLD_FONT);
        return item;
    }
    
    protected final JRadioButton radioButton(String title) {
        JRadioButton item =  new JRadioButton(title);
        item.setFont(Utils.STD_BOLD_FONT);
        return item;
    }
    
    protected final Slider slider(String l, String r) {
        return new Slider(l, r);
    }
     
    /*
     * A generic function which generates a combo box from 
     * an Enum class.
     */
    protected final <E extends Enum<E>>
    JComboBox  comboSelection(Class<E> clazz) {
        JComboBox box = new JComboBox();
        for(E val : clazz.getEnumConstants()) {
            box.addItem(val);
        }
        return box;
    }
    
    protected void makeTrigger(ESensor sensor, int startState, T_Signal sig, int delay, 
            T_Action action, int finalState) {
                
        Trigger t = Triggers.getInstance().newTrigger();
        t.setSensor(sensor);
        t.setReqdState(startState);
        t.setSignal( sig );
        t.setDelay(delay);
        t.setAction(action);
        t.setActionState(finalState);
    }
    
    protected void makeTriggerWRepeat(ESensor sensor, int startState, T_Signal sig, int delay, 
            T_Action action, int finalState) {
                
        Trigger t = Triggers.getInstance().newTrigger();
        t.setSensor(sensor);
        t.setReqdState(startState);
        t.setSignal( sig );
        t.setDelay(delay);
        t.setAction(action);
        t.setActionState(finalState);
        t.setRepeat(true);
    }
     
    // Routines used to check for port over-usage
    
    public EPort getPortUsed() {
        return null;        // Default - for gyro and cursor speed.
    } 
    
    public EPort getPortBUsed() { // For solutions that use two ports.
        return null;
    }
    
    // Get the number of sensors used by the solution.
    public int getSensorCount() {  // returns 0, 1 or 2.
        return sensorCount;
    }
    public int getSensorCountB() {  // returns 0, 1 or 2.
        return sensorCountB;
    }
    
    // setSubPort is only used by single-switch solutions.
    // and thus defaults to do-nothing.
    public void setSubPort(ESubPort subPort) {
    }
    
}
