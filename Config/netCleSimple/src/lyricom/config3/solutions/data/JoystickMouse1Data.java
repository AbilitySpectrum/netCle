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
public class JoystickMouse1Data extends SolutionsDataBase {

    // Setup
    final private JComboBox port;
    
    // Options
    final private JCheckBox leftRightClick;
    final private JCheckBox audioFeedback;
    
    public JoystickMouse1Data() {
        super(ESolution.S_JOYSTICK_MOUSE1);
        sensorCount = 2;
        
        port = comboSelection(EPort.class);
        leftRightClick = checkBox(RES.getString("JS1_LEFT_RIGHT_OPTION"));
        audioFeedback = checkBox(RES.getString("Q_AUDIO_FEEDBACK"));
    }
    
    @Override
    public void compile() {
        int portNum = ((EPort) port.getSelectedItem()).getPortNum();
        ESensor sensorA = ESensor.getSensorA(portNum);
        ESensor sensorB = ESensor.getSensorB(portNum);
        
        T_Action mouseUp =    T_Action.MOUSE_UP;
        T_Action mouseDown =  T_Action.MOUSE_DOWN;
        T_Action mouseLeft =  T_Action.MOUSE_LEFT;
        T_Action mouseRight = T_Action.MOUSE_RIGHT;
        T_Action leftClick =  T_Action.MOUSE_LCLICK;
        T_Action rightClick = T_Action.MOUSE_RCLICK;
                
        T_Action nothing = T_Action.NONE;
        T_Action buzz = T_Action.createBuzzerAction(400, 250);
        
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
            if (audioFeedback.isSelected()) {
                makeTrigger(sensorA, 2, T_Signal.JS_NOT_LOW, 20, rightClick, 5);
                makeTrigger(sensorA, 5, T_Signal.JS_NOT_LOW,  0, buzz, 1);
            } else {
                makeTrigger(sensorA, 2, T_Signal.JS_NOT_LOW, 20, rightClick, 1);                
            }
            makeTrigger(sensorA, 1, T_Signal.JS_HIGH,     0, nothing, 6);
            makeTrigger(sensorA, 6, T_Signal.JS_HIGH,   300, mouseLeft, 7);
            makeTrigger(sensorA, 7, T_Signal.JS_HIGH,     0, mouseLeft, 7);
            makeTrigger(sensorA, 7, T_Signal.JS_NOT_HIGH, 0, nothing, 1);
            if (audioFeedback.isSelected()) {
                makeTrigger(sensorA, 6, T_Signal.JS_NOT_HIGH, 20, leftClick, 8);
                makeTrigger(sensorA, 8, T_Signal.JS_NOT_HIGH,  0, buzz, 1);
            } else {
                makeTrigger(sensorA, 6, T_Signal.JS_NOT_HIGH, 20, leftClick, 1);                
            }
        }
        // Simple up/down
        makeTrigger(sensorB, 1, T_Signal.JS_HIGH, 0, mouseDown, 1);
        makeTrigger(sensorB, 1, T_Signal.JS_LOW,  0, mouseUp, 1);
    }
    
    @Override
    public void printDescription(PrintStream out) {
        out.println(getType().toString());
        out.println(String.format("   Port: %s", port.getSelectedItem()));
        out.println(String.format("   LR-Click: %s", leftRightClick.isSelected()));
        out.println(String.format("   Audio On: %s", audioFeedback.isSelected()));
    }

    public JComboBox getPortCombo() {
        return port;
    }

    public JCheckBox getLeftRightClick() {
        return leftRightClick;
    }

    public JCheckBox getAudioFeedback() {
        return audioFeedback;
    }

    
    @Override
    public EPort getPortUsed() { 
        return (EPort) port.getSelectedItem();
    }
    
    // --------------------------------------
    // XML Support
    @XmlElement(name = "Port")
    String getXPort() {
        EPort p = (EPort) port.getSelectedItem();
        return p.name();
    }
    
    void setXPort(String name) {
        EPort p = EPort.valueOf(name);
        port.setSelectedItem(p);
    }
    
    @XmlElement(name = "LeftRightClickOption")
    boolean getXLeftRightClick() {
        return leftRightClick.isSelected();
    }
    
    void setXLeftRightClick(boolean val) {
        leftRightClick.setSelected(val);
    }
    
    @XmlElement(name = "AudioFeedback")
    boolean getXAudioFeedback() {
        return audioFeedback.isSelected();
    }
    
    void setXAudioFeedback(boolean val) {
        audioFeedback.setSelected(val);
    }
}
