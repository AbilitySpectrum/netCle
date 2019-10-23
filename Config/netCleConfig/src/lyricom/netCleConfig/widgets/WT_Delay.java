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
package lyricom.netCleConfig.widgets;

import java.util.ResourceBundle;
import javax.swing.JLabel;
import lyricom.netCleConfig.model.Trigger;

/**
 *
 * @author Andrew
 */
public class WT_Delay extends W_Number {
    private static final ResourceBundle RES = ResourceBundle.getBundle("strings");

    private final Trigger theTrigger;
    public WT_Delay(String label, Trigger t) {
        super(label, RES.getString("NE_FLD_DELAY"), 5, 0, 60000);
        add(new JLabel(RES.getString("TIME_MSEC")));
        theTrigger = t;
        update();
    }

    @Override
    public void widgetChanged() {
        int delay = getValue(); 
        theTrigger.setDelay(delay);
    }
    
    @Override
    public void update() {
        int delay = theTrigger.getDelay();
        setValue(delay);
    }
}
