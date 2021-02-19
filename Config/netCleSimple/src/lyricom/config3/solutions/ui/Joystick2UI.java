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
package lyricom.config3.solutions.ui;

import javax.swing.Box;
import lyricom.config3.solutions.SolutionsUIBase;
import lyricom.config3.solutions.data.Joystick2Data;

/**
 *
 * @author Andrew
 */
public class Joystick2UI extends SolutionsUIBase {

    Joystick2Data data;
    
    public Joystick2UI(Joystick2Data data) {
        super(data);
        this.data = data;
        
        descriptionText.setText(RES.getString("JS2_DESCRIPTION"));
        
        // Setup
        Box vb = Box.createVerticalBox();
        vb.add(labelledItem(RES.getString("Q_JOYSTICK_LOCATION"), data.getJoystickPortCombo()));
        vb.add(labelledItem(RES.getString("Q_ONE_BTN_PORT_LOCATION"), data.getButtonPortCombo()));
        setupArea.add(vb);
        
        // Options
        vb = Box.createVerticalBox();
        vb.add(data.getLeftRightClick());
        vb.add(data.getLrAudioSuppression());
        vb.add(data.getToggleAudioSuppression());
        optionsArea.add(vb);
    }
}
