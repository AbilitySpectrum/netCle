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
package lyricom.config3.solutions.data;

import java.io.PrintStream;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import lyricom.config3.model.EAction;
import lyricom.config3.model.ESensor;
import lyricom.config3.model.Model;
import lyricom.config3.model.T_Action;
import lyricom.config3.model.T_Signal;
import lyricom.config3.solutions.EPort;
import lyricom.config3.solutions.SolutionsDataBase;
import lyricom.config3.ui.selection.ESolution;

/**
 *
 * @author Andrew
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class JoystickMouse2Data extends SolutionsDataBase {

    // Setup
    final private JComboBox joystickPort;
    final private JComboBox buttonPort;
    
    // Options
    final private JCheckBox leftRightClick;
    final private JCheckBox lrAudio;
    final private JCheckBox toggleAudio;
    
     public JoystickMouse2Data() {
        super(ESolution.S_JOYSTICK_MOUSE2);
        sensorCount = 2;
        sensorCountB = 1;
        
        joystickPort = comboSelection(EPort.class);
        buttonPort = comboSelection(EPort.class);
        
        leftRightClick = checkBox(RES.getString("JS1_LEFT_RIGHT_OPTION"));
        lrAudio = checkBox(RES.getString("Q_AUDIO_FEEDBACK"));
        toggleAudio = checkBox(RES.getString("JS2_TOGGLE_AUDIO"));
    }
    
    @Override
    public void compile() {
        int portNum = ((EPort) joystickPort.getSelectedItem()).getPortNum();
        ESensor sensorA = ESensor.getSensorA(portNum);
        ESensor sensorB = ESensor.getSensorB(portNum);
        
        portNum = ((EPort) buttonPort.getSelectedItem()).getPortNum();
        ESensor button = ESensor.getSensorA(portNum);
        
        // Mouse actions
        T_Action mouseUp    = new T_Action(EAction.HID_MOUSE, Model.MOUSE_UP);
        T_Action mouseDown  = new T_Action(EAction.HID_MOUSE, Model.MOUSE_DOWN);
        T_Action mouseRight = new T_Action(EAction.HID_MOUSE, Model.MOUSE_RIGHT);
        T_Action mouseLeft  = new T_Action(EAction.HID_MOUSE, Model.MOUSE_LEFT);
        T_Action leftClick  = new T_Action(EAction.HID_MOUSE, Model.MOUSE_CLICK);
        T_Action rightClick  = new T_Action(EAction.HID_MOUSE, Model.MOUSE_RIGHT_CLICK);
        T_Action scrollUp   = new T_Action(EAction.HID_MOUSE, Model.MOUSE_WHEEL_UP);
        T_Action scrollDown = new T_Action(EAction.HID_MOUSE, Model.MOUSE_WHEEL_DOWN);
                
        // Other actions
        T_Action nothing = new T_Action(EAction.NONE, 0);
        T_Action buzzlow = new T_Action(EAction.BUZZER, (400 << 16) + 250);
        T_Action buzzhigh = new T_Action(EAction.BUZZER, (800 << 16) + 250);
        T_Action light2 = new T_Action(EAction.LIGHT_BOX, Model.LBO_ONLY + 2);
        T_Action light7 = new T_Action(EAction.LIGHT_BOX, Model.LBO_ONLY + 64);
        T_Action setState1 = new T_Action(EAction.SET_STATE, (sensorB.getId() << 8) + 1);
        T_Action setState2 = new T_Action(EAction.SET_STATE, (sensorB.getId() << 8) + 2);
        
        if (leftRightClick.isSelected() == false) {
            // Simple Joystick
            makeTrigger(sensorA, 1, T_Signal.JS_HIGH, 0, mouseLeft, 1);
            makeTrigger(sensorA, 1, T_Signal.JS_LOW,  0, mouseRight, 1);
        } else {            
            // Joystick with L and R click.
            makeTrigger(sensorA, 1, T_Signal.JS_LOW,      0, nothing, 2);
            makeTrigger(sensorA, 2, T_Signal.JS_LOW,    300, mouseRight, 3);
            makeTrigger(sensorA, 3, T_Signal.JS_LOW,      0, mouseRight, 3);
            makeTrigger(sensorA, 3, T_Signal.JS_NOT_LOW,  0, nothing, 1);
            if (lrAudio.isSelected()) {
                makeTrigger(sensorA, 2, T_Signal.JS_NOT_LOW, 20, rightClick, 5);
                makeTrigger(sensorA, 5, T_Signal.JS_NOT_LOW,  0, buzzlow, 1);
            } else {
                makeTrigger(sensorA, 2, T_Signal.JS_NOT_LOW, 20, rightClick, 1);                
            }
            makeTrigger(sensorA, 1, T_Signal.JS_HIGH,     0, nothing, 6);
            makeTrigger(sensorA, 6, T_Signal.JS_HIGH,   300, mouseLeft, 7);
            makeTrigger(sensorA, 7, T_Signal.JS_HIGH,     0, mouseLeft, 7);
            makeTrigger(sensorA, 7, T_Signal.JS_NOT_HIGH, 0, nothing, 1);
            if (lrAudio.isSelected()) {
                makeTrigger(sensorA, 6, T_Signal.JS_NOT_HIGH, 20, leftClick, 8);
                makeTrigger(sensorA, 8, T_Signal.JS_NOT_HIGH,  0, buzzlow, 1);
            } else {
                makeTrigger(sensorA, 6, T_Signal.JS_NOT_HIGH, 20, leftClick, 1);                
            }
        }
        // up/down & scrolling
        makeTrigger(sensorB, 1, T_Signal.JS_HIGH, 0, mouseDown, 1);
        makeTrigger(sensorB, 1, T_Signal.JS_LOW,  0, mouseUp, 1);        
        makeTrigger(sensorB, 2, T_Signal.JS_HIGH, 0, scrollDown, 2);
        makeTrigger(sensorB, 2, T_Signal.JS_LOW,  0, scrollUp, 2); 
        
        // Toggle Button
        if (toggleAudio.isSelected()) {
            makeTrigger(button, 1, T_Signal.BTN_PRESS,   0, buzzlow,  2);
            makeTrigger(button, 2, T_Signal.BTN_PRESS,   0, light2,   3);
        } else {
            makeTrigger(button, 1, T_Signal.BTN_PRESS,   0, light2,   3);
        }
        makeTrigger(button, 3, T_Signal.BTN_PRESS,   0, setState2,  4);
        makeTrigger(button, 4, T_Signal.BTN_RELEASE, 0, nothing,  5);
        if (toggleAudio.isSelected()) {
            makeTrigger(button, 5, T_Signal.BTN_PRESS,   0, buzzhigh, 6);
            makeTrigger(button, 6, T_Signal.BTN_PRESS,   0, light7,   7);            
        } else {
            makeTrigger(button, 5, T_Signal.BTN_PRESS,   0, light7,   7);
        }
        makeTrigger(button, 7, T_Signal.BTN_PRESS,   0, setState1,  8);
        makeTrigger(button, 8, T_Signal.BTN_RELEASE, 0, nothing,  1);
    }
    
    @Override
    public void printDescription(PrintStream out) {
        out.println(getType().toString());
        out.println(String.format("   Joystick: %s", joystickPort.getSelectedItem().toString()));
        out.println(String.format("   Button: %s", buttonPort.getSelectedItem().toString()));
        out.println(String.format("   LR-Click: %s", leftRightClick.isSelected()));
        out.println(String.format("   LR-Audio-Suppression: %s ", lrAudio.isSelected()));
        out.println(String.format("   Toggle-Audio-Suppression: %s ", toggleAudio.isSelected()));
    }

    /**
     * @return the joystickPort
     */
    public JComboBox getJoystickPortCombo() {
        return joystickPort;
    }

    /**
     * @return the buttonPort
     */
    public JComboBox getButtonPortCombo() {
        return buttonPort;
    }

    /**
     * @return the leftRightClick
     */
    public JCheckBox getLeftRightClick() {
        return leftRightClick;
    }

    /**
     * @return the lrAudioSuppression
     */
    public JCheckBox getLrAudio() {
        return lrAudio;
    }

    /**
     * @return the toggleAudioSuppresion
     */
    public JCheckBox getToggleAudio() {
        return toggleAudio;
    }

    @Override
    public EPort getPortUsed() {
        return (EPort) joystickPort.getSelectedItem();
    }
        
    @Override
    public EPort getPortBUsed() {
        return (EPort) buttonPort.getSelectedItem();
    }
    
    // --------------------------------------------------
    // XML Support
    @XmlElement(name = "JoystickPort")
    String getXJoystickPort() {
        EPort p = (EPort) joystickPort.getSelectedItem();
        return p.name();
    }
    
    void setXJoystickPort(String name) {
        EPort p = EPort.valueOf(name);
        joystickPort.setSelectedItem(p);
    }
    
    @XmlElement(name = "ButtonPort")
    String getXButtonPort() {
        EPort p = (EPort) buttonPort.getSelectedItem();
        return p.name();
    }
    
    void setXButtonPort(String name) {
        EPort p = EPort.valueOf(name);
        buttonPort.setSelectedItem(p);
    }

    @XmlElement(name = "LeftRightClickOption")
    boolean getXLeftRightClick() {
        return leftRightClick.isSelected();
    }
    
    void setXLeftRightClick(boolean val) {
        leftRightClick.setSelected(val);
    }
    
    @XmlElement(name = "LRAudio")
    boolean getXLRAudio() {
        return lrAudio.isSelected();
    }
    
    void setXLRAudio(boolean val) {
        lrAudio.setSelected(val);
    }    
    
    @XmlElement(name = "ToggleAudio")
    boolean getXToggleAudio() {
        return toggleAudio.isSelected();
    }
    
    void setXToggleAudio(boolean val) {
        toggleAudio.setSelected(val);
    }    

}
