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

import javax.swing.JOptionPane;
import lyricom.netCleConfig.model.ActionType;
import lyricom.netCleConfig.model.Model;
import lyricom.netCleConfig.model.SaAction;
import lyricom.netCleConfig.model.SensorGroup;
import lyricom.netCleConfig.model.Trigger;
import lyricom.netCleConfig.model.Triggers;
import lyricom.netCleConfig.ui.MainFrame;
import lyricom.netCleConfig.ui.SensorPanel;

/**
 *
 * @author Andrew
 */
//@RegisterInfo(
//        name = "Joystick Mouse",
//        applicaton = {"Input 1", "Input 2", "Input 3"}
//)

public class JoystickMouseSolution extends SolutionBase {    
    private static final String YES = SRes.getStr("TMW_YES");
    private static final String NO = SRes.getStr("TMW_NO");
    private static final String[] YES_NO = {YES, NO};
    
    public JoystickMouseSolution( SolutionsUI ui, SensorGroup sg ) {
        super(ui, sg);
    }
    
    @Override
    boolean doSolution() {                
        Calibrator c = getCalibrator();
        c.startCalibration();
        c.getRestValues();
        
        Location upLocation, downLocation, leftLocation, rightLocation;
        upLocation = downLocation = leftLocation = rightLocation = null;
        
        boolean success = false;
        upLocation = c.getLocation(SRes.getStr("JW_UP"));
        if (upLocation != null) {
            downLocation = c.getLocation(SRes.getStr("JW_DOWN"));
            if (downLocation != null) {
                leftLocation = c.getLocation(SRes.getStr("JW_LEFT"));
                if (leftLocation != null) {
                    rightLocation = c.getLocation(SRes.getStr("JW_RIGHT"));
                    if (rightLocation != null) {
                        success = true;
                    }
                }
            }
        }
        c.endCalibration();
        if (cancelling) return false;
        
        if (!success) {
            JOptionPane.showMessageDialog(theUI,
                    SRes.getStr("JW_FAIL_MSG"),
                    SRes.getStr("SW_SOLUTION_FAIL_TITLE"),
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // Sanity check.  All locations should be in same group.
        // Right == Left != Up == Down
        SensorGroup g = upLocation.sensor.getGroup();
        if (downLocation.sensor.getGroup() != g ||
                leftLocation.sensor.getGroup() != g ||
                rightLocation.sensor.getGroup() != g ) {
            JOptionPane.showMessageDialog(theUI,
                    SRes.getStr("JW_GROUP_ERROR"),
                    SRes.getStr("SW_SOLUTION_FAIL_TITLE"),
                    JOptionPane.ERROR_MESSAGE);
            return false;                    
        }
        if (upLocation.sensor != downLocation.sensor) {
            JOptionPane.showMessageDialog(theUI,
                    SRes.getStr("JW_UPDOWN_ERROR"),
                    SRes.getStr("SW_SOLUTION_FAIL_TITLE"),
                    JOptionPane.ERROR_MESSAGE);
            return false;            
        }
        if (leftLocation.sensor != rightLocation.sensor) {
            JOptionPane.showMessageDialog(theUI,
                    SRes.getStr("JW_LEFTRIGHT_ERROR"),
                    SRes.getStr("SW_SOLUTION_FAIL_TITLE"),
                    JOptionPane.ERROR_MESSAGE);
            return false;                      
        }
        if (leftLocation.sensor == upLocation.sensor) {
            JOptionPane.showMessageDialog(theUI,
                    SRes.getStr("JW_UPLEFT_ERROR"),
                    SRes.getStr("SW_SOLUTION_FAIL_TITLE"),
                    JOptionPane.ERROR_MESSAGE);
            return false;                        
        }
        
        // Usage Check
        if (Triggers.getInstance().isSensorUsed(upLocation.sensor)) {
            int result = JOptionPane.showConfirmDialog(null,
                upLocation.sensor.getName() + " " + SRes.getStr("ALREADY_PROGRAMMED_TEXT"),
                SRes.getStr("ALREADY_PROGRAMMED_TITLE"),
                JOptionPane.YES_NO_OPTION); 
            if (result == JOptionPane.NO_OPTION) {
                return false;
            }               
        }
        
        if (Triggers.getInstance().isSensorUsed(leftLocation.sensor)) {
            int result = JOptionPane.showConfirmDialog(null,
                leftLocation.sensor.getName() + " " + SRes.getStr("ALREADY_PROGRAMMED_TEXT"),
                SRes.getStr("ALREADY_PROGRAMMED_TITLE"),
                JOptionPane.YES_NO_OPTION); 
            if (result == JOptionPane.NO_OPTION) {
                return false;
            }               
        }
        
        MainFrame.TheFrame.showGroupPanel(upLocation.sensor.getGroup());
   
        SaAction action = mouseSelection(); // HID or Bluetooth
        if (action == null) {
            return false;
        }
        SaAction none = Model.getActionByType(ActionType.NONE);
        
        DetailsDlg.showDlg(SRes.getStr("JW_TAP_OPTION_EXPLAINED"));
        String lrClick = theUI.getOption(SRes.getStr("JW_LR_OPTION"), YES_NO);
        if (cancelling) return false;
        
        String upDown = theUI.getOption(SRes.getStr("JW_UD_OPTION"), YES_NO);
        if (cancelling) return false;
        
        DetailsDlg.showDlg(SRes.getStr("JW_DELAY_OPTION_EXPLAINED"));
        
        int delay = 0;
        SaAction resetAction = none;
        int resetOption = 0;
        if (lrClick == YES || upDown == YES) {
            delay = theUI.getDelay(SRes.getStr("JW_DELAY"), 500);
            if (delay > 0) {
                String resetSound = theUI.getOption(SRes.getStr("JW_RESET_SOUND"), YES_NO);
                if (resetSound == YES) {
                    resetAction = Model.getActionByType(ActionType.BUZZER);
                    resetOption = (200 << 16) | 50;
                }
            }
        }
        
        upLocation.level = Trigger.Level.LEVEL1;
        downLocation.level = Trigger.Level.LEVEL2;
        leftLocation.level = Trigger.Level.LEVEL1;
        rightLocation.level = Trigger.Level.LEVEL2;
       
        Triggers trigs = Triggers.getInstance();
        trigs.deleteTriggerSet(upLocation.sensor);
        trigs.deleteTriggerSet(leftLocation.sensor);
        
        if (upDown == YES) {
            Location notUp = upLocation.getReverse();
            Location notDown = downLocation.getReverse();
            SaAction keyboard;
            int upKey;
            int downKey;
            // Hard-wired values for the arrow keys are taken from ui.ActionUI
            if (action == Model.getActionByType(ActionType.HID_MOUSE)) {
                keyboard = Model.getActionByType(ActionType.HID_SPECIAL);
                upKey = 0xDA;
                downKey = 0xD9;
            } else {
                keyboard = Model.getActionByType(ActionType.BT_SPECIAL);
                upKey = 14;
                downKey =12;
            }
            
            makeTrigger(1, upLocation,  0,   none, 0,   2);
            makeTrigger(2, notUp,       0, keyboard, upKey, 1);
            makeTrigger(2, upLocation, 300,  none, 0,   3);
            makeTrigger(3, upLocation,  0, action, Model.MOUSE_UP,  3);
            makeTrigger(3, notUp,   delay, resetAction, resetOption,    1);
            
            makeTrigger(1, downLocation, 0,   none, 0,   5);
            makeTrigger(5, notDown,      0, keyboard, downKey, 1);
            makeTrigger(5, downLocation,300,  none, 0,   6);
            makeTrigger(6, downLocation, 0, action, Model.MOUSE_DOWN,  6);
            makeTrigger(6, notDown,  delay, resetAction, resetOption,    1);
            
        } else {
            makeTrigger(1, upLocation,    0, action, Model.MOUSE_UP,    1);
            makeTrigger(1, downLocation,  0, action, Model.MOUSE_DOWN,  1);
        }
        
        if (lrClick == YES) {
            Location notLeft = leftLocation.getReverse();
            Location notRight = rightLocation.getReverse();
            
            makeTrigger(1, leftLocation,  0,   none, 0,   2);
            makeTrigger(2, notLeft,       0, action, Model.MOUSE_CLICK, 1);
            makeTrigger(2, leftLocation, 300,  none, 0,   3);
            makeTrigger(3, leftLocation,  0, action, Model.MOUSE_LEFT,  3);
            makeTrigger(3, notLeft,   delay, resetAction, resetOption,    1);
            
            makeTrigger(1, rightLocation, 0,   none, 0,   5);
            makeTrigger(5, notRight,      0, action, Model.MOUSE_RIGHT_CLICK, 1);
            makeTrigger(5, rightLocation,300,  none, 0,   6);
            makeTrigger(6, rightLocation, 0, action, Model.MOUSE_RIGHT,  6);
            makeTrigger(6, notRight,  delay, resetAction, resetOption,    1);
        } else {
            makeTrigger(1, leftLocation,  0, action, Model.MOUSE_LEFT,  1);
            makeTrigger(1, rightLocation, 0, action, Model.MOUSE_RIGHT, 1);
        }

        return true;
    }
    

}
