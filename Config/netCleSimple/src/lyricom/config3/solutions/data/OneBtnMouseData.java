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
import lyricom.config3.solutions.EPort;
import lyricom.config3.solutions.ESubPort;
import lyricom.config3.solutions.SolutionsDataBase;
import lyricom.config3.ui.selection.ESolution;

/**
 *
 * @author Andrew
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class OneBtnMouseData extends SolutionsDataBase {

    final JComboBox port;
    final private NumericField delayBetweenStates;
    final private NumericField buzzerDuration;
    private ESubPort subPort = ESubPort.SubPortA;
    
    public OneBtnMouseData() {
        super(ESolution.S_ONE_BTN_MOUSE);
        sensorCount = 1;
        
        port = comboSelection(EPort.class);
        
        delayBetweenStates = new NumericField(RES.getString("OBM_DELAY_ERROR_TAG"), 4, 300, 2000);
        delayBetweenStates.setValue(1000); //default
        buzzerDuration = new NumericField(RES.getString("OBM_BEEP_ERROR_TAG"), 3, 50, 400);
        buzzerDuration.setValue(250);                
    }

    @Override
    public void setSubPort(ESubPort sp) {
        subPort = sp;
    }
    
    @Override
    public void compile() {
        EPort portItem = (EPort)port.getSelectedItem();
        ESensor sensor = subPort.getSensor(portItem);
        
        T_Action mouseUp    = T_Action.MOUSE_UP;
        T_Action mouseDown  = T_Action.MOUSE_DOWN;
        T_Action mouseRight = T_Action.MOUSE_RIGHT;
        T_Action mouseLeft  = T_Action.MOUSE_LEFT;
        T_Action leftClick  = T_Action.MOUSE_LCLICK;
                
        T_Action nothing = new T_Action(EAction.NONE, 0);
        int buzzDuration = buzzerDuration.getValue();
        int lowDur = buzzDuration * 2 / 3;
        if (lowDur < 50) lowDur = 50;
        T_Action upBuzz    = T_Action.createBuzzerAction(800, buzzDuration);
        T_Action downBuzz  = T_Action.createBuzzerAction(400, buzzDuration);
        T_Action leftBuzz  = T_Action.createBuzzerAction(600, buzzDuration);
        T_Action rightBuzz = T_Action.createBuzzerAction(500, buzzDuration);
        T_Action resetBuzz = T_Action.createBuzzerAction(200, lowDur);
        int delay = delayBetweenStates.getValue();
        
        makeTrigger(sensor, 1, T_Signal.BTN_PRESS,     0, nothing,   2);
        makeTrigger(sensor, 2, T_Signal.BTN_RELEASE,   0, leftClick, 1);
        makeTrigger(sensor, 2, T_Signal.BTN_PRESS, delay, upBuzz,    3);
        makeTrigger(sensor, 3, T_Signal.BTN_RELEASE,   0, nothing,   4);
        makeTrigger(sensor, 4, T_Signal.BTN_PRESS,     0, mouseUp,   4);
        makeTrigger(sensor, 3, T_Signal.BTN_PRESS, delay, downBuzz,  5);
        makeTrigger(sensor, 5, T_Signal.BTN_RELEASE,   0, nothing,   6);
        makeTrigger(sensor, 6, T_Signal.BTN_PRESS,     0, mouseDown, 6);
        makeTrigger(sensor, 5, T_Signal.BTN_PRESS, delay, leftBuzz,  7);
        makeTrigger(sensor, 7, T_Signal.BTN_RELEASE,   0, nothing,   8);
        makeTrigger(sensor, 8, T_Signal.BTN_PRESS,     0, mouseLeft, 8);
        makeTrigger(sensor, 7, T_Signal.BTN_PRESS, delay, rightBuzz, 9);
        makeTrigger(sensor, 9, T_Signal.BTN_RELEASE,   0, nothing,   10);
        makeTrigger(sensor,10, T_Signal.BTN_PRESS,     0, mouseRight,10);
        makeTrigger(sensor, 0, T_Signal.BTN_RELEASE, delay*2, resetBuzz, 1);
    }
    
    @Override
    public void printDescription(PrintStream out) {
        out.println(getType().toString());
        out.println(String.format("   Port: %s", port.getSelectedItem().toString()));
        out.println(String.format("   Delay between states: %d", delayBetweenStates.getValue()));
        out.println(String.format("   Buzzer duration: %d", buzzerDuration.getValue()));
    }
    
    public JComboBox getPortCombo() {
        return port;
    }

    public NumericField getDelayBetweenStates() {
        return delayBetweenStates;
    }

    public NumericField getBuzzerDuration() {
        return buzzerDuration;
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
    
    @XmlElement (name = "DelayBetweenStates")
    int getXDelayBetweenStates() { return delayBetweenStates.getValue(); }
    void setXDelayBetweenStates(int val) { delayBetweenStates.setValue(val); }
    
    @XmlElement (name = "BuzzerDuration")
    int getXBuzzerDuration() { return buzzerDuration.getValue(); }
    void setXBuzzerDuration(int val) { buzzerDuration.setValue(val); }

}
