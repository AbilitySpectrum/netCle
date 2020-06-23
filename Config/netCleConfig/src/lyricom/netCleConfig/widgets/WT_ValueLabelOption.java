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

import lyricom.netCleConfig.model.Trigger;

/**
 * Displays the mouse option combo box.
 * Handles the synchronization of the mouse option combo selection
 * and the trigger.
 * 
 * @author Andrew
 */
public class WT_ValueLabelOption extends W_Combo {
    
    private final ValueLabelPair[] actions;
    private final Trigger theTrigger;
    private final boolean updateRepeat;
    private final int overlay;
    
    public WT_ValueLabelOption(String label, Trigger t, 
            ValueLabelPair[] actions, boolean updateRepeat) {
        super(label, actions);
        this.updateRepeat = updateRepeat;
        theTrigger = t;
        this.actions = actions;
        overlay = 0;
        update();
    }
    
    public WT_ValueLabelOption(String label, int overlay, Trigger t, 
            ValueLabelPair[] actions) {
        super(label, actions);
        this.updateRepeat = false;
        theTrigger = t;
        this.actions = actions;
        this.overlay = overlay;
        update();
    }

    public WT_ValueLabelOption(String label, Trigger t, ValueLabelPair[] actions) {
        this(label, t, actions, true);
    }

    @Override
    public void widgetChanged() {
        ValueLabelPair p = (ValueLabelPair) theBox.getSelectedItem();
        theTrigger.setActionParam(p.getValue() | overlay);
        // Mostly the repeat option is taken from the ValueLabelPair.
        // e.g. a mouse click action has no repeat,
        //      a mouse move action always repeats.
        // However, for IR actions the repeat option is set via a 
        // separate "Repeat" checkbox.   So for IR actions, updateRepeat 
        // gets set to false.
        if ( updateRepeat ) {     
            theTrigger.setRepeat(p.getRepeat());
        }
    }
    
    @Override
    public void update() {
        // Note: most options are just 8 bits long (& 0xff would do)
        // but for the lightbox they are 16 bits long.
        int param = theTrigger.getActionParam() & 0xffff;
        for(ValueLabelPair p: actions) {
            if (p.getValue() == param) {
                theBox.setSelectedItem(p);
            }
        }        
    }
}
