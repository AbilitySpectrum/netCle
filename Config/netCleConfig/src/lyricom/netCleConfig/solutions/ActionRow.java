/* * * * * * * * * * * * * * * * * * * * * * * * * * * * 
    Copyright (C) 2019 Andrew Hodgson

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
package lyricom.netCleConfig.solutions;

import javax.swing.JCheckBox;
import lyricom.netCleConfig.model.Trigger;

/**
 * This class is used to pass prompt and action information from
 * the SolutionsUI to the PressHoldSelect and PressReleaseWaitSelect
 * solutions.
 * 
 * @author Andrew
 */
public class ActionRow {
    Trigger prompt;
    Trigger action;
    JCheckBox latch;
        
    ActionRow(Trigger p, Trigger a, JCheckBox b) {
        prompt = p;
        action = a;
        latch = b;
    }    
}
