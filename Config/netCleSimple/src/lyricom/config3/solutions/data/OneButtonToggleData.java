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
import lyricom.config3.solutions.EOneButtonToggle;
import lyricom.config3.solutions.EPort;
import lyricom.config3.solutions.ESolutionType;
import lyricom.config3.solutions.ESubPort;
import lyricom.config3.solutions.SolutionsDataBase;

/**
 *
 * @author Andrew
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class OneButtonToggleData extends SolutionsDataBase {

    // Setup
    final private JComboBox port;
    final private JComboBox function;
    private ESubPort subPort = ESubPort.SubPortA;
    
    public OneButtonToggleData() {
        super(ESolutionType.SOL_ONE_BUTTON_TOGGLE);
        sensorCount = 1;
        
        port = comboSelection(EPort.class);
        function = comboSelection(EOneButtonToggle.class);
    }
    
    @Override
    public void setSubPort(ESubPort sp) {
        subPort = sp;
    }

    @Override
    public void compile() {
        EPort portItem = (EPort)port.getSelectedItem();
        ESensor sensor = subPort.getSensor(portItem);
        
        EOneButtonToggle option = (EOneButtonToggle) function.getSelectedItem();
        
        T_Action action1 = null;
        T_Action action2 = null;
        switch(option) {
            case OBT_V_MOUSE:
                action1 = new T_Action(EAction.HID_MOUSE, Model.MOUSE_UP);
                action2 = new T_Action(EAction.HID_MOUSE, Model.MOUSE_DOWN);
                break;
            case OBT_H_MOUSE:
                action1 = new T_Action(EAction.HID_MOUSE, Model.MOUSE_LEFT);
                action2 = new T_Action(EAction.HID_MOUSE, Model.MOUSE_RIGHT);
                break;
            case OBT_SCROLL:
                action1 = new T_Action(EAction.HID_MOUSE, Model.MOUSE_WHEEL_DOWN);
                action2 = new T_Action(EAction.HID_MOUSE, Model.MOUSE_WHEEL_UP);
                break;
        }
        T_Action nothing = new T_Action(EAction.NONE, 256);
        T_Action buzz    = new T_Action(EAction.BUZZER, (200 << 16) + 100);
        T_Action hibuzz    = new T_Action(EAction.BUZZER, (800 << 16) + 100);
        
        makeTrigger(sensor, 1, T_Signal.BTN_PRESS,     0, nothing,   2);
        makeTrigger(sensor, 2, T_Signal.BTN_PRESS,     0, action1,   2);
        makeTrigger(sensor, 2, T_Signal.BTN_RELEASE, 500, buzz,      3);
        makeTrigger(sensor, 3, T_Signal.BTN_RELEASE, 2000, hibuzz,   1);
        makeTrigger(sensor, 3, T_Signal.BTN_PRESS,     0, nothing,   4);
        makeTrigger(sensor, 4, T_Signal.BTN_PRESS,     0, action2,   4);
        makeTrigger(sensor, 4, T_Signal.BTN_RELEASE, 500, buzz,      1);
    }
    
    @Override
    public void printDescription(PrintStream out) {
        out.println(getType().toString());
        out.println(String.format("   Port: %s", port.getSelectedItem().toString()));
        out.println(String.format("   Function: %s", function.getSelectedItem().toString()));
    }

    public JComboBox getPortCombo() {
        return port;
    }

    public JComboBox getFunction() {
        return function;
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
        EOneButtonToggle val = (EOneButtonToggle) function.getSelectedItem();
        return val.name();
    }
    
    void setXFunction(String name) {
        EOneButtonToggle val = EOneButtonToggle.valueOf(name);
        function.setSelectedItem(val);
    }
}
