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
import lyricom.config3.solutions.EPort;
import lyricom.config3.solutions.ESubPort;
import lyricom.config3.solutions.SolutionsDataBase;
import lyricom.config3.ui.Utils;
import lyricom.config3.ui.selection.ESolution;

/**
 *
 * @author Andrew
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class KeyboardTextData extends SolutionsDataBase {

    final private JComboBox port;
    protected ESubPort subPort = ESubPort.SubPortA;
    final TextField textField;
    // Options
    final private JCheckBox endWithReturn;
    
    public KeyboardTextData() {
        super(ESolution.S_KEYBOARD_TEXT);
        sensorCount = 1;
        
        port = comboSelection(EPort.class);
        textField = new TextField(20); 
        textField.setFont(Utils.MONO_FONT);
        endWithReturn = checkBox(RES.getString("KT_END_WITH_RETURN"));
    }

    @Override
    public void setSubPort(ESubPort sp) {
         subPort = sp;
    }
 
    @Override
    public void compile() {
        EPort portItem = (EPort)port.getSelectedItem();
        ESensor sensor = subPort.getSensor(portItem);
        
        String text = textField.getValue();
        int i = 0;
        while(i < text.length()) {
            int end = i + 4;
            if (end > (text.length())) end = text.length();
            String data = text.substring(i, end);
            T_Action keys = new T_Action(EAction.HID_KEYBOARD, convertString(data));
            i = end;
            makeTrigger(sensor, 1, T_Signal.BTN_PRESS, 0, keys, 1);
        }
        if (endWithReturn.isSelected()) {
            T_Action returnKey = new T_Action(EAction.HID_KEYBOARD, EKeyCode.ACT_KEY_RETURN.getCode());
            makeTrigger(sensor, 1, T_Signal.BTN_PRESS, 0, returnKey, 1);            
        }
    }
    
    private int convertString(String txt) {
        int val = 0;
        byte[] bytes = txt.getBytes();
        
        for(int i=0; i<bytes.length; i++) {
            val = (val << 8) + (((int) bytes[i]) & 0xff);
           
        }
        return val;
    }

    @Override
    public void printDescription(PrintStream out) {
    }

    public JComboBox getPortCombo() {
        return port;
    }
    
    @Override
    public EPort getPortUsed() {
        return (EPort) port.getSelectedItem();
    }
    
    public TextField getTextField() {
        return textField;
    }
    
    public JCheckBox getEndWithReturn() {
        return  endWithReturn;
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
        
    @XmlElement(name = "Text")
    String getXText() {
        return textField.getValue();
    }
    
    void setXText(String val) {
        textField.setValue(val);
    }    
    
    @XmlElement(name = "EndWithReturn")
    boolean getXEndWithReturn() {
        return endWithReturn.isSelected();
    }
    
    void setXEndWithReturn(boolean val) {
        endWithReturn.setSelected(val);
}

}
