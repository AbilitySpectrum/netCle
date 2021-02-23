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

import javax.swing.*;
import lyricom.config3.solutions.SolutionsUIBase;
import lyricom.config3.solutions.data.GyroMouseData;

/**
 *
 * @author Andrew
 */
public class GyroMouseUI extends SolutionsUIBase {
    
    GyroMouseData data;
    
    public GyroMouseUI(GyroMouseData data) {
        super(data);
        this.data = data;
                        
        // Setup
        Box vb = Box.createVerticalBox();
        JPanel whichSide = labelledItem(RES.getString("GM_LOCATION"), data.getLeftSide());
        whichSide.add(data.getRightSide());
        vb.add(whichSide);
        
        vb.add(Box.createVerticalStrut(5));
        
        JPanel sense = labelledItem(RES.getString("GM_SENSITIVITY"), data.getSensitivity());
         vb.add(sense);
        vb.add(data.getCalibrateBtn());
        setupArea.add(vb);
                
        // Options
        vb = Box.createVerticalBox();
        vb.add(data.getLeftClickOption());
        vb.add(data.getRightClickOption());
        vb.add(data.getHeadTiltOption());
        optionsArea.add(vb);
    }
    
}
