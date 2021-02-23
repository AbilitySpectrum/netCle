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

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.xml.bind.annotation.XmlElement;
import lyricom.config3.solutions.EPort;
import lyricom.config3.solutions.ESubPort;
import lyricom.config3.solutions.SolutionsDataBase;
import lyricom.config3.ui.selection.ESolution;
/**
 * A base class for the data side, for solution types that
 * ask the user to select a port and an audio option.
 * 
 * @author Andrew
 */
public abstract class PortAndAudioBase extends SolutionsDataBase {
    
    // Setup
    final protected JComboBox port;
    protected ESubPort subPort = ESubPort.SubPortA;
    
    // Options
    protected final JCheckBox audioFeedback;
        
    public PortAndAudioBase(ESolution t) {
        super(t);
        
        port = comboSelection(EPort.class);
        audioFeedback = checkBox(RES.getString("OBS_AUDIO"));
    }
       
    @Override
    public void setSubPort(ESubPort sp) {
         subPort = sp;
    }
    
    public JComboBox getPortCombo() {
        return port;
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
        
    @XmlElement(name = "AudioFeedback")
    boolean getXAudioFeedback() {
        return audioFeedback.isSelected();
    }
    
    void setXAudioFeedback(boolean val) {
        audioFeedback.setSelected(val);
    }
}
