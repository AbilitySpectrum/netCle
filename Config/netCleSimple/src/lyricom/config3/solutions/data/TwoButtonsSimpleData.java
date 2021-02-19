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
import lyricom.config3.solutions.EOneButtonSimple;
import lyricom.config3.solutions.EPort;
import lyricom.config3.solutions.ESolutionType;
import lyricom.config3.solutions.ETwoButtonSimple;
import lyricom.config3.solutions.SolutionsDataBase;

/**
 *
 * @author Andrew
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class TwoButtonsSimpleData extends SolutionsDataBase {

    // Setup
    final private JComboBox port;
    final private JComboBox function;
    
    // Option
    final private JCheckBox audio;
    
    public TwoButtonsSimpleData() {
        super (ESolutionType.SOL_TWO_BUTTON_SIMPLE);
        sensorCount = 2;
        
        port = comboSelection(EPort.class);
        function = comboSelection(ETwoButtonSimple.class);
        audio = checkBox(RES.getString("TBS_AUDIO_FEEDBACK"));
    }
    
    @Override
    public void compile() {
        int portNum = ((EPort) port.getSelectedItem()).getPortNum();
        ESensor sensorA = ESensor.getSensorA(portNum);
        ESensor sensorB = ESensor.getSensorB(portNum);
        
        ETwoButtonSimple option = (ETwoButtonSimple) function.getSelectedItem();
        T_Action action1 = null;
        T_Action action2 = null;
        switch(option) {
            case TBS_LEFT_PRESS_RELEASE:
                action1 = new T_Action(EAction.HID_MOUSE, Model.MOUSE_PRESS);
                action2 = new T_Action(EAction.HID_MOUSE, Model.MOUSE_RELEASE);
                break;
           case TBS_LEFT_RIGHT_CLICK:
                action1 = new T_Action(EAction.HID_MOUSE, Model.MOUSE_CLICK);
                action2 = new T_Action(EAction.HID_MOUSE, Model.MOUSE_RIGHT_CLICK);
                break;
            case TBS_SCROLL_UP_DOWN:
                action1 = new T_Action(EAction.HID_MOUSE, Model.MOUSE_WHEEL_UP);
                action2 = new T_Action(EAction.HID_MOUSE, Model.MOUSE_WHEEL_DOWN);
                break;
        }
        T_Action buzz    = new T_Action(EAction.BUZZER, (200 << 16) + 100);
        
        if (option == ETwoButtonSimple.TBS_LEFT_RIGHT_BUTTON) {
            T_Action leftPress = new T_Action(EAction.HID_MOUSE, Model.MOUSE_PRESS);
            T_Action rightPress = new T_Action(EAction.HID_MOUSE, Model.MOUSE_RIGHT_PRESS);
            T_Action leftRelease = new T_Action(EAction.HID_MOUSE, Model.MOUSE_RELEASE);
            T_Action rightRelease = new T_Action(EAction.HID_MOUSE, Model.MOUSE_RIGHT_RELEASE);
 
            makeTrigger(sensorA, 1, T_Signal.BTN_PRESS, 0, leftPress, 2);
            if (audio.isSelected()) {
                makeTrigger(sensorA, 2, T_Signal.BTN_PRESS, 0, buzz, 2);                
            }
            makeTrigger(sensorA, 2, T_Signal.BTN_RELEASE, 0, leftRelease, 1);
            makeTrigger(sensorB, 1, T_Signal.BTN_PRESS, 0, rightPress, 2);
            if (audio.isSelected()) {
                makeTrigger(sensorB, 2, T_Signal.BTN_PRESS, 0, buzz, 2);                                
            }
            makeTrigger(sensorB, 2, T_Signal.BTN_RELEASE, 0, rightRelease, 1);
        } else { 
            // All other cases.
            makeTrigger(sensorA, 1, T_Signal.BTN_PRESS, 0, action1, 1);
            makeTrigger(sensorB, 1, T_Signal.BTN_PRESS, 0, action2, 1);
            if (audio.isSelected()) {
                makeTrigger(sensorA, 1, T_Signal.BTN_PRESS, 0, buzz, 1);
                makeTrigger(sensorB, 1, T_Signal.BTN_PRESS, 0, buzz, 1);                
            }
        }
    }
    
    @Override
    public void printDescription(PrintStream out) {
        out.println(getType().toString());
        out.println(String.format("   Port: %s", port.getSelectedItem().toString()));
        out.println(String.format("   Function: %s", function.getSelectedItem().toString()));
        out.println(String.format("   Audio Feedback: %s", audio.isSelected()));
    }

    public JComboBox getPortCombo() {
        return port;
    }

    public JComboBox getFunction() {
        return function;
    }

    public JCheckBox getAudio() {
        return audio;
    }
    
    @Override
    public EPort getPortUsed() {
        return (EPort) port.getSelectedItem();
    }

    // -----------------------------------------
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
    
    @XmlElement(name = "Function")
    String getXFunction() {
        ETwoButtonSimple val = (ETwoButtonSimple) function.getSelectedItem();
        return val.name();
    }
    
    void setXFunction(String name) {
        ETwoButtonSimple val = ETwoButtonSimple.valueOf(name);
        function.setSelectedItem(val);
    }
    
    @XmlElement(name = "AudioFeedback")
    boolean getXAudioFeedback() {
        return audio.isSelected();
    }
    
    void setXAudioFeedback(boolean val) {
        audio.setSelected(val);
    }
}
