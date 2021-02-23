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
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JRadioButton;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import lyricom.config3.calibration.CalibrationUI;
import lyricom.config3.calibration.Calibrator;
import lyricom.config3.comms.Connection;
import lyricom.config3.model.EAction;
import lyricom.config3.model.ESensor;
import lyricom.config3.model.Model;
import lyricom.config3.model.T_Action;
import lyricom.config3.model.T_Signal;
import lyricom.config3.model.Trigger;
import lyricom.config3.solutions.EPort;
import lyricom.config3.solutions.ESolutionType;
import lyricom.config3.solutions.Slider;
import lyricom.config3.solutions.SolutionsDataBase;
import lyricom.config3.ui.selection.ESolution;

/**
 *
 * @author Andrew
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class GyroMouseData extends SolutionsDataBase {

    // Setup
    final private JRadioButton leftSide;
    final private JRadioButton rightSide;
    
    // Options 
    final private JCheckBox leftClickOption;
    final private JCheckBox rightClickOption;
    final private JCheckBox headTiltOption;
    final private Slider sensitivity;
    final private JButton calibrateBtn;

    private Calibrator cal = null;
    private int yBias = 0;
    private int zBias = 0;
    private int tiltThreshold = -2000;
    private boolean tiltIsNegative = false;
    
    T_Action nothing  = new T_Action(EAction.NONE, 0);
    T_Action buzzLo   = new T_Action(EAction.BUZZER, (400 << 16) + 250);
    T_Action buzzHi   = new T_Action(EAction.BUZZER, (800 << 16) + 100);
    T_Action buzzVLo  = new T_Action(EAction.BUZZER, (250 << 16) + 50);
    
    public GyroMouseData() { 
        super(ESolution.S_GYRO_MOUSE);
        // Setup
        leftSide = radioButton(RES.getString("GM_LEFT_SIDE"));
        leftSide.setSelected(true);
        rightSide = radioButton(RES.getString("GM_RIGHT_SIDE"));
        ButtonGroup grp = new ButtonGroup();
        grp.add(leftSide);
        grp.add(rightSide);
        
        sensitivity = new Slider(RES.getString("GM_LOW"), RES.getString("GM_HIGH"));
        calibrateBtn = new JButton("Calibrate");
        calibrateBtn.addActionListener((e) -> calibrate());
        // Options
        leftClickOption = checkBox(RES.getString("GM_LEFT_CLICK_OPTION"));
        rightClickOption = checkBox(RES.getString("GM_RIGHT_CLICK_OPTION"));
        headTiltOption = checkBox(RES.getString("GM_HEAD_TILT_OPTION"));
//       rightClickOption.setToolTipText(RES.getString("GM_RIGHT_CLICK_OPTION_TT"));
    }
    
    
    private void calibrate() {
        Connection conn = Connection.getInstance();
        if (!conn.isConnected()) {
            if (!conn.establishConnection()) {
                return;
            }
        }
        CalibrationUI uix = new CalibrationUI();
        cal = new Calibrator(uix, this);
        cal.begin();
    }
    
    @Override
    public void compile() {  
        if (cal != null && !cal.wasCancelled()) {
            yBias = cal.getGyroYBias();
            zBias = cal.getGyroZBias();   
            tiltThreshold = cal.getTiltPoint();
            tiltIsNegative = cal.isTiltIsNegative();
        }   

        int sens = sensitivity.getValue();
        int z_threshold = 2500 - (sens-50) * 30;
        int y_threshold = 3500 - (sens-50) * 30;
//        System.out.println( String.format("Y: %d, Z: %d", y_threshold, z_threshold));
        T_Action mouseUp = new T_Action(EAction.HID_MOUSE, Model.MOUSE_UP);
        T_Action mouseDown = new T_Action(EAction.HID_MOUSE, Model.MOUSE_DOWN);
        T_Action mouseLeft = new T_Action(EAction.HID_MOUSE, Model.MOUSE_LEFT);
        T_Action mouseRight = new T_Action(EAction.HID_MOUSE, Model.MOUSE_RIGHT);
        T_Action mouseLClick = new T_Action(EAction.HID_MOUSE, Model.MOUSE_CLICK);
        T_Action mouseRClick = new T_Action(EAction.HID_MOUSE, Model.MOUSE_RIGHT_CLICK);
        
        // ------------------------------
        // Left-Right
        generate(ESensor.GYRO_Y, y_threshold, yBias, true, mouseLeft, 2);
        if (rightClickOption.isSelected()) {
            generate2(ESensor.GYRO_Y, y_threshold, yBias, false, mouseRight, mouseRClick);
        } else {
            generate(ESensor.GYRO_Y, y_threshold, yBias, false, mouseRight, 4);
        }
        
        // ------------------------------
        // Up-Down
        if (leftSide.isSelected()) {
            generate(ESensor.GYRO_Z, z_threshold, zBias, true, mouseUp, 2);
            if (leftClickOption.isSelected()) {
                generate2(ESensor.GYRO_Z, z_threshold, zBias, false, mouseDown, mouseLClick);
            } else {
                generate(ESensor.GYRO_Z, z_threshold, zBias, false, mouseDown, 4);
            }
        } else {
            generate(ESensor.GYRO_Z, z_threshold, zBias, false, mouseUp, 2);
            if (leftClickOption.isSelected()) {
                generate2(ESensor.GYRO_Z, z_threshold, zBias, true, mouseDown, mouseLClick);                
            } else {
                generate(ESensor.GYRO_Z, z_threshold, zBias, true, mouseDown, 4);    
            }
        }
        
        // ------------------------------------------
        // Left tilt to turn the gyro on and off.
        if (headTiltOption.isSelected()) {
            T_Action gyroYOff = new T_Action(EAction.SET_STATE, (ESensor.GYRO_Y.getId() << 8) + 9);
            T_Action gyroZOff = new T_Action(EAction.SET_STATE, (ESensor.GYRO_Z.getId() << 8) + 9);
            T_Action gyroYOn = new T_Action(EAction.SET_STATE, (ESensor.GYRO_Y.getId() << 8) + 1);
            T_Action gyroZOn = new T_Action(EAction.SET_STATE, (ESensor.GYRO_Z.getId() << 8) + 1);
            T_Action offBeep = new T_Action(EAction.BUZZER, (200<<16) + 500);
            T_Action readyBeep = new T_Action(EAction.BUZZER, (800<<16) + 100);
            T_Action onBeep = new T_Action(EAction.BUZZER, (1200<<16) + 100);

            T_Signal low, high;
            if (tiltIsNegative) {
                 low = new T_Signal(tiltThreshold, Trigger.TRIGGER_ON_LOW);
                 high = low.not();
             } else {
                 high = new T_Signal(tiltThreshold, Trigger.TRIGGER_ON_LOW);
                 low = high.not();
             }           

            makeTrigger(ESensor.ACCEL_Z, 1, low, 0, gyroYOff, 2);
            makeTrigger(ESensor.ACCEL_Z, 2, low, 0, gyroZOff, 3);
            makeTrigger(ESensor.ACCEL_Z, 3, low, 0, offBeep, 4);
            makeTrigger(ESensor.ACCEL_Z, 4, high, 500, nothing, 8);
            makeTrigger(ESensor.ACCEL_Z, 6, high, 0, gyroYOn, 7);
            makeTrigger(ESensor.ACCEL_Z, 7, high, 0, gyroZOn, 1);
            makeTrigger(ESensor.ACCEL_Z, 8, low, 0, readyBeep, 9);
            makeTrigger(ESensor.ACCEL_Z, 9, high, 100, onBeep, 6);
        }       
    }
    
    private void generate(ESensor sens, int threshold, int bias, boolean startLow, T_Action func, int next) {
        T_Signal low, notLow, high, notHigh;
        
        if (startLow) {
            low = new T_Signal(-threshold + bias, Trigger.TRIGGER_ON_LOW);
            high = new T_Signal(threshold + bias, Trigger.TRIGGER_ON_HIGH);
        } else {
            // Reverse high-low sense
            high = new T_Signal(-threshold + bias, Trigger.TRIGGER_ON_LOW);
            low = new T_Signal(threshold + bias, Trigger.TRIGGER_ON_HIGH);            
        }
        notLow = low.not();
        notHigh = high.not();
        
        // Left-Right
        makeTrigger(sens, 1,      low,      80, buzzLo,  next);
        makeTrigger(sens, next,   notLow,    0, func,    next);
        makeTrigger(sens, next,   high,     50, buzzVLo, next+1);
        makeTrigger(sens, next+1, notHigh, 250, nothing, 1);        
    }
    
    private void generate2(ESensor sens, int threshold, int bias, boolean startLow, T_Action func1, T_Action func2) {
        T_Signal low, notLow, high, notHigh;
        
        if (startLow) {
            low = new T_Signal(-threshold + bias, Trigger.TRIGGER_ON_LOW);
            high = new T_Signal(threshold + bias, Trigger.TRIGGER_ON_HIGH);
        } else {
            // Reverse high-low sense
            high = new T_Signal(-threshold + bias, Trigger.TRIGGER_ON_LOW);
            low = new T_Signal(threshold + bias, Trigger.TRIGGER_ON_HIGH);            
        }
        notLow = low.not();
        notHigh = high.not();
    
        makeTrigger(sens, 1, low,     80, buzzLo,  4);
        makeTrigger(sens, 4, notLow, 300, func1,   5);
        makeTrigger(sens, 5, notHigh,  0, func1,   5);
        makeTrigger(sens, 5, high,    50, buzzVLo, 8);
        makeTrigger(sens, 4, high,    50, buzzHi,  7);
        makeTrigger(sens, 7, notHigh,  0, func2,   8);
        makeTrigger(sens, 8, notHigh,250, nothing, 1);
}

    @Override
    public void printDescription(PrintStream out ) {
        out.println(getType().toString());
        out.println(String.format("   Location: %s", leftClickOption.isSelected() ? "Left Side" : "RightSide"));
        out.println(String.format("   Left-Click: %s", leftClickOption.isSelected()));
        out.println(String.format("   Right-Click: %s", rightClickOption.isSelected()));
        out.println(String.format("   Sensitivity: %d ", sensitivity.getValue()));
    }

    public JRadioButton getLeftSide() {
        return leftSide;
    }

    public JRadioButton getRightSide() {
        return rightSide;
    }
    
    public Slider getSensitivity() {
        return sensitivity;
    }
    
    public JButton getCalibrateBtn() {
        return calibrateBtn;
    }
    
    public JCheckBox getLeftClickOption() {
        return leftClickOption;
    }

    public JCheckBox getRightClickOption() {
        return rightClickOption;
    }

    public JCheckBox getHeadTiltOption() {
        return headTiltOption;
    }
    
    // ---------------------------------
    // XML Support
    @XmlElement(name = "LeftSide")
    boolean getXLeftSide() { return leftSide.isSelected(); }
    void setXLeftSide(boolean val) { leftSide.setSelected(val); }

    @XmlElement(name = "RightSide")
    boolean getXRightSide() { return rightSide.isSelected(); }
    void setXRightSide(boolean val) { rightSide.setSelected(val); }

    @XmlElement(name = "LeftClickOption")
    boolean getXLeftClickOption() { return leftClickOption.isSelected(); }
    void setXLeftClickOption(boolean val) { leftClickOption.setSelected(val); }
    
    @XmlElement(name = "RightClickOption")
    boolean getXRightClickOption() { return rightClickOption.isSelected(); }
    void setXRightClickOption(boolean val) { rightClickOption.setSelected(val); }
    
    @XmlElement(name = "HeadTiltOption")
    boolean getXHeadTiltOption() { return headTiltOption.isSelected(); }
    void setXHeadTiltOption(boolean val) { headTiltOption.setSelected(val); }

    @XmlElement(name = "Sensitivity")
    int getXSensitivity() { return sensitivity.getValue(); }
    void setXSensitivity(int val) {sensitivity.setValue(val); }
    
    @XmlElement
    int getYBias() { return yBias; }
    void setYBias(int val) { yBias = val; }
    
    @XmlElement
    int getZBias() { return zBias; }
    void setZBias(int val) { zBias = val; }
    
    @XmlElement
    int getTiltThreshold() { return tiltThreshold; }
    void setTiltThreshold(int val) { tiltThreshold = val; }
    
    @XmlElement
    boolean getTiltIsNegative() { return tiltIsNegative; }
    void setTiltIsNegative(boolean val) { tiltIsNegative = val; }
}
