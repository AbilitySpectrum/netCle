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
import lyricom.config3.model.T_Action;
import lyricom.config3.model.T_Signal;
import lyricom.config3.solutions.EOneButtonSimple;
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
public class OneButtonSimpleData extends SolutionsDataBase {
    
    // Setup
    final private JComboBox port;
    final private JComboBox function;
    private ESubPort subPort = ESubPort.SubPortA;
    
    // Options
    private final JCheckBox audioFeedback;
    
    public OneButtonSimpleData() {
        super(ESolutionType.SOL_ONE_BUTTON_SIMPLE);
        sensorCount = 1;
        
        port = comboSelection(EPort.class);
        function = comboSelection(EOneButtonSimple.class);
        audioFeedback = checkBox(RES.getString("OBS_AUDIO"));
    }
    
    @Override
    public void setSubPort(ESubPort sp) {
        subPort = sp;
    }
    
    @Override
    public void compile() {
        EPort portItem = (EPort)port.getSelectedItem();
        ESensor sensor = subPort.getSensor(portItem);
        EOneButtonSimple selectedFunction = (EOneButtonSimple) function.getSelectedItem();
        T_Action action = selectedFunction.getAction();
        
         
        makeTrigger(sensor, 1, T_Signal.BTN_PRESS, 0, action, 1); 
        
        if (audioFeedback.isSelected()) {
            T_Action buzz = new T_Action(EAction.BUZZER, ((400<<16) + 100));
            makeTrigger(sensor, 1, T_Signal.BTN_PRESS, 0, buzz, 1);
        }
    }
    
    @Override
    public void printDescription(PrintStream out) {        
        out.println(getType().toString());
        out.println(String.format("   Port: %s", port.getSelectedItem().toString()));
        out.println(String.format("   Function: %s", function.getSelectedItem().toString()));
        out.println(String.format("   Audio Feedback: %s", audioFeedback.isSelected())); 
    }

    public JComboBox getPortCombo() {
        return port;
    }

    public JComboBox getFunction() {
        return function;
    }

    public JCheckBox getAudioFeedback() {
        return audioFeedback;
    }
    
    @Override
    public EPort getPortUsed() {
        return (EPort) port.getSelectedItem();
    }
    
    // XML Support ---------------------------
    //
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
        EOneButtonSimple val = (EOneButtonSimple) function.getSelectedItem();
        return val.name();
    }
    
    void setXFunction(String name) {
        EOneButtonSimple val = EOneButtonSimple.valueOf(name);
        function.setSelectedItem(val);
    }
    
    @XmlElement(name = "AudioFeedback")
    boolean getXAudioFeedback() {
        return audioFeedback.isSelected();
    }
    
    void setXAudioFeedback(boolean val) {
        audioFeedback.setSelected(val);
    }
}
