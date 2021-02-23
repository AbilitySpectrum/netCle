package lyricom.config3.calibration;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import lyricom.config3.comms.Connection;
import lyricom.config3.comms.IOError;
import lyricom.config3.comms.SensorDataCallback;
import lyricom.config3.comms.Serial;
import lyricom.config3.model.ESensor;
import lyricom.config3.model.InStream;
import lyricom.config3.model.Model;
import lyricom.config3.solutions.data.GyroMouseData;

/**
 *
 * @author Andrew
 */
public class CalibratorXX implements Runnable, SensorDataCallback {
    protected static final ResourceBundle RES = ResourceBundle.getBundle("strings");
    static enum DataType {
        NORMAL, LEFT, RIGHT, UP, DOWN, TILT, SUMMARY
    };

    class SavedData {
       final DataType type;
       int saccelZMax, saccelZMin;
       int sgyroYMax, sgyroYMin;
       int sgyroZMax, sgyroZMin;
       
       SavedData(SavedData d) {
           type = DataType.SUMMARY;
           saccelZMax = d.saccelZMax;
           saccelZMin = d.saccelZMin;
           sgyroYMax = d.sgyroYMax;
           sgyroYMin = d.sgyroYMin;
           sgyroZMax = d.sgyroZMax;
           sgyroZMin = d.sgyroZMin;
       }
       
       SavedData(DataType t) {
           type = t;
           saccelZMax = accelZMax;
           saccelZMin = accelZMin;
           sgyroYMax = gyroYMax;
           sgyroYMin = gyroYMin;
           sgyroZMax = gyroZMax;
           sgyroZMin = gyroZMin;
       }
       
       void debugDump() {
           System.out.println(type.name());
           System.out.println(String.format("   AccelZ Max: %s.  AccelZ Min: %s", saccelZMax, saccelZMin));
           System.out.println(String.format("   GyroY  Max: %s.  GyroY  Min: %s", sgyroYMax, sgyroYMin));
           System.out.println(String.format("   GyroZ  Max: %s.  GyroZ  Min: %s", sgyroZMax, sgyroZMin));
       }
    };  
    
    List<SavedData> dataList;

    // Final threshold values
    int Tleft, Tright, Tup, Tdown, Ttilt;
    
    private final CalibrationUI ui;
    private final GyroMouseData data;
    AtomicBoolean cancelling = new AtomicBoolean();
    Thread runThread;
    Serial serial;
    
    public CalibratorXX(CalibrationUI ui, GyroMouseData data) {
        this.ui = ui;
        this.data = data;
        serial = Serial.getInstance();
    }
    
    public void begin() {
        runThread = new Thread(this);
        runThread.start();
    }
    
    void cancel() {
        cancelling.set(true);
    }
        
    @Override
    public void run() {
//        ui.setCalibrator(this);
        dataList = new ArrayList<>();
        doCalibration();
        
        // Wrap up
        serial.writeByte(Model.CMD_RUN);
        Connection.getInstance().setSensorDataCallback(null);
        ui.closeDialog();
    }
    
    private void doCalibration() {
        ui.presentMessage("Attach the gyro as you plan to use it,<br/>"
                + "with the green side towards your head and the wire hanging down.<br/><br/>"
                + "Press <b>Continue</b> when ready.");
        ui.waitForContinue();
        if (cancelling.get() == true) return;
        
        Connection.getInstance().setSensorDataCallback(this);
        serial.writeByte(Model.CMD_DISPLAY); 
        
        ui.presentMessage("Now keep your head centered and relaxed for a few seconds<br/>"
                + "so we can see what is normal for you.<br/><br/>"
                + "Press <b>Continue</b> when ready.");
        
        ui.waitForContinue();
        if (cancelling.get() == true) return;
        ui.presentMessage("Measuring ...");
        startRangeCheck();
        snooze(3000);
        stopRangeCheck();
        dataList.add(new SavedData(DataType.NORMAL));
       
        // ---------------------------
        // Left Turn
        ui.presentMessage("Next you will turn your head quickly to the left<br/>"
                + "using the gesture you hope to use to move the cursor left.<br/>"
                + "After the turn, bring your head slowly back to the center.<br/><br/>"
                + "Press <b>Continue</b> when ready.");
        ui.waitForContinue();
        if (cancelling.get() == true) return;
        startRangeCheck();
        ui.presentMessage("Turn left");
        snooze(3000);
        stopRangeCheck();
        dataList.add(new SavedData(DataType.LEFT));
        ui.presentMessage("Thank you.");
        snooze(1000);
        startRangeCheck();
        ui.presentMessage("And again - turn left.");
        snooze(3000);
        stopRangeCheck();
        dataList.add(new SavedData(DataType.LEFT));
        
        // --------------------------------
        // Right turn
        ui.presentMessage("Good! Now you will turn your head quickly to the right<br/>"
                + "using the gesture you hope to use to move the cursor right.<br/>"
                + "After the turn, bring your head slowly back to the center.<br/><br/>"
                + "Press <b>Continue</b> when ready.");
        ui.waitForContinue();
        if (cancelling.get() == true) return;
        startRangeCheck();
        ui.presentMessage("Turn right");
        snooze(3000);
        stopRangeCheck();
        dataList.add(new SavedData(DataType.RIGHT));
        ui.presentMessage("Thank you.");
        snooze(1000);
        startRangeCheck();
        ui.presentMessage("And again - turn right.");
        snooze(3000);
        stopRangeCheck();
        dataList.add(new SavedData(DataType.RIGHT));

         // --------------------------------
        // Up turn
        ui.presentMessage("OK! Now we will measure the up motion.<br/>"
                + "Tilt your head up quickly and then bring it back to the center slowly.<br/><br/>"
                + "Press <b>Continue</b> when ready.");
        ui.waitForContinue();
        if (cancelling.get() == true) return;
        startRangeCheck();
        ui.presentMessage("Tilt up.");
        snooze(3000);
        stopRangeCheck();
        dataList.add(new SavedData(DataType.UP));
        ui.presentMessage("Thank you.");
        snooze(1000);
        startRangeCheck();
        ui.presentMessage("And again - tilt up.");
        snooze(3000);
        stopRangeCheck();
        dataList.add(new SavedData(DataType.UP));

         // --------------------------------
        // Down turn
        ui.presentMessage("And now, the down motion.<br/>"
                + "Tilt your head down quickly and then bring it back to the center slowly.<br/><br/>"
                + "Press <b>Continue</b> when ready.");
        ui.waitForContinue();
        if (cancelling.get() == true) return;
        startRangeCheck();
        ui.presentMessage("Tilt down.");
        snooze(3000);
        stopRangeCheck();
        dataList.add(new SavedData(DataType.DOWN));
        ui.presentMessage("Thank you.");
        snooze(1000);
        startRangeCheck();
        ui.presentMessage("And again - tilt down.");
        snooze(3000);
        stopRangeCheck();
        dataList.add(new SavedData(DataType.DOWN));

        // --------------------------------------
        // Tilt
        ui.presentMessage("Finally, we will measure the tilt used to turn the gyro on and off.<br/>"
                + "Tilt your head to the left a comfortable amount.<br/><br/>"
                + "Press <b>Continue</b> when when you are in the tilted position.");
        ui.waitForContinue();
        if (cancelling.get() == true) return;
        ui.presentMessage("Measuring ...");
        startRangeCheck();
        snooze(3000);
        stopRangeCheck();
        dataList.add(new SavedData(DataType.TILT));
        
        ui.presentMessage("Great. We're all done.<br/><br/>"
                + "Press <b>Continue</b> to end the calibration.");
        ui.waitForContinue();
        if (cancelling.get() == true) return;
        
        for(SavedData d: dataList) {
            d.debugDump();
        }
    
        computeResults();
        
        return;        
    }
    
    private void computeResults() {
        Tleft = computeNegativeValues(DataType.LEFT, (d) -> d.sgyroYMin);
        Tright = computePositiveValues(DataType.RIGHT, (d) -> d.sgyroYMax);
        Tup = computeNegativeValues(DataType.UP, (d) -> d.sgyroZMin);
        Tdown = computePositiveValues(DataType.DOWN, (d) -> d.sgyroZMax);
        Ttilt = computeTilt();
        
        System.out.println(String.format("Tleft = %d", Tleft));
        System.out.println(String.format("Tright = %d", Tright));
        System.out.println(String.format("Tup = %d", Tup));
        System.out.println(String.format("Tdown = %d", Tdown));
        System.out.println(String.format("Ttilt = %d", Ttilt));
    }

    private int computePositiveValues(DataType type, Function<SavedData, Integer> mapper) {
        // For positive pulses - which means larger values are stronger gestures.
        
        // Get the sensor values when the gesture is active and when it is not.
        List<Integer> normalValues = getValues(DataType.NORMAL, true, mapper);
        List<Integer> activeValues = getValues(type, true, mapper);
        List<Integer> inActiveValues = getValues(type, false, mapper);
        
        int normal = normalValues.get(0);
        // Get normalized values.
        int weakerActiveValue = getMin(activeValues) - normal;  // Get the weakest gesture.
        int strongerActiveValue = getMax(activeValues) - normal; // Get the strongest gesture.
        int largestInActiveValue = getMax(inActiveValues) - normal;  // and the strongest non-gesture.
        
        // Decide which active value to use.  Often one will be much stronger than the other.
        int referenceActiveValue;
        if ( (weakerActiveValue < strongerActiveValue * 0.7)
                || (weakerActiveValue < largestInActiveValue * 2) ){
            referenceActiveValue = strongerActiveValue;
        } else {
            referenceActiveValue = weakerActiveValue;
        }
        
        // The threshold should be 
        if (referenceActiveValue < largestInActiveValue * 4) {
            // Reference value is less than four times the inactive value.
            // use the reference value
            return referenceActiveValue + normal;
        } else if (referenceActiveValue < largestInActiveValue * 6) {
            // .. or use the reference value * 4
            return largestInActiveValue * 4 + normal;
        } else {
            // ... or use a value 2/3 of the way from inactvie to reference.
            return (referenceActiveValue * 2 + largestInActiveValue) / 3 + normal;
        }
    }
    
    private int computeNegativeValues(DataType type, Function<SavedData, Integer> mapper) {
        // For negative pulses - which means lesser values (more negative) are stronger.
        
        // Get the sensor values when the gesture is active and when it is not.
        List<Integer> normalValues = getValues(DataType.NORMAL, true, mapper);
        List<Integer> activeValues = getValues(type, true, mapper);
        List<Integer> inActiveValues = getValues(type, false, mapper);
        
        int normal = normalValues.get(0);
        // Get normalized values.
        int weakerActiveValue = getMax(activeValues) - normal;  // Get the weakest gesture.
        int strongerActiveValue = getMin(activeValues) - normal; // Get the strongest gesture.
        int largestInActiveValue = getMin(inActiveValues) - normal;  // and the strongest non-gesture.
        
        // Decide which active value to use.  Often one will be much stronger than the other.
        int referenceActiveValue;
        if ( (weakerActiveValue > strongerActiveValue * 0.7)
                || (weakerActiveValue > largestInActiveValue * 2) ){
            referenceActiveValue = strongerActiveValue;
        } else {
            referenceActiveValue = weakerActiveValue;
        }
        
        // The threshold should be 
        if (referenceActiveValue > largestInActiveValue * 4) {
            // Reference value is less than four times the inactive value.
            // use the reference value
            return referenceActiveValue + normal;
        } else if (referenceActiveValue > largestInActiveValue * 6) {
            // .. or use the reference value * 4
            return largestInActiveValue * 4 + normal;
        } else {
            // ... or use a value 2/3 of the way from inactvie to reference.
            return (referenceActiveValue * 2 + largestInActiveValue) / 3 + normal;
        }
    }
    
    private int computeTilt() {
        int normal = getValues(DataType.NORMAL, true, d -> d.saccelZMin).get(0);
        List<Integer> nonTiltValues = getValues(DataType.TILT, false, d -> d.saccelZMin);
        int tiltValue = getValues(DataType.TILT, true, d -> d.saccelZMin).get(0);
        
        int nonTiltNorm = getMin(nonTiltValues) - normal;
        int tiltNorm = tiltValue - normal;
        
         // The threshold should be 
        if (tiltNorm > nonTiltNorm * 4) {
            // Reference value is less than four times the inactive value.
            // use the reference value
            return tiltNorm + normal;
        } else if (tiltNorm > nonTiltNorm * 6) {
            // .. or use the reference value * 4
            return nonTiltNorm * 4 + normal;
        } else {
            // ... or use a value 2/3 of the way from inactvie to reference.
            return (tiltNorm * 2 + nonTiltNorm) / 3 + normal;
        }
       
    }
    
    // Gets a list of values from one sensor that either match or do not match a type.
    private List<Integer> getValues(DataType t, boolean matchType, Function<SavedData, Integer> mapper) {
        List<Integer> list = new ArrayList<>();
        
        for(SavedData d: dataList) {
            if ((d.type == t) == matchType) {
                list.add(mapper.apply(d));
            }
        }
        return list;
    }
    
    private int getMax(List<Integer> list) {
        int value = Integer.MIN_VALUE;
        for(int v: list) {
            if (v > value) value = v;
        }
        return value;
    }
    
    private int getMin(List<Integer> list) {
        int value = Integer.MAX_VALUE;
        for(int v: list) {
            if (v < value) value = v;
        }
        return value;
    }
    //
    // END Computation Section
    // --------------------------------------------------
    
    private void snooze(int time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException ex) {
            
        }
    }

    // - - - - - - - - - - - - - - - - - - - - - - 
    // Sensor data
    int accelZ, accelZMax, accelZMin, accelZAvg;
    int gyroY, gyroYMax, gyroYMin, gyroYAvg;
    int gyroZ, gyroZMax, gyroZMin, gyroZAvg;
    long accelZSum, gyroYSum, gyroZSum;
    int dataCount;
    
    List<Integer> values = new ArrayList<>();
    
    AtomicBoolean rangeCheckActive = new AtomicBoolean();
    AtomicBoolean firstData = new AtomicBoolean();
    
    @Override
    public void newSensorData(InStream in) throws IOError {
       if (in.getChar() != Model.START_OF_DATA) {
            throw new IOError(RES.getString("CDE_INVALID_SENSOR_DATA"));
        }
        
        int sensorCount = in.getNum(2);
        for(int i=0; i<sensorCount; i++) {
            int id = in.getID(2);
            int value = in.getNum(2);
            ESensor s = ESensor.getSensorByID(id);
            switch (s) {
                case ACCEL_Z:
                    accelZ = value;
                    break;
                case GYRO_Y:
                    gyroY = value;
                    break;
                case GYRO_Z:
                    gyroZ = value;
                    break;
                // Don't care about the rest.
            }
        } 
        if (rangeCheckActive.get() == true) {
            values.add(gyroY);
            if (firstData.get() == true) {
                accelZSum = accelZMax = accelZMin = accelZ;
                gyroYSum = gyroYMax = gyroYMin = gyroY;
                gyroZSum = gyroZMax = gyroZMin = gyroZ;
                dataCount = 1;
                firstData.set(false);
                
            } else {
                accelZSum += accelZ;
                gyroYSum += gyroY;
                gyroZSum += gyroZ;
                dataCount++;
                if (accelZ > accelZMax) accelZMax = accelZ;
                if (accelZ < accelZMin) accelZMin = accelZ;
                if (gyroY > gyroYMax) gyroYMax = gyroY;
                if (gyroY < gyroYMin) gyroYMin = gyroY;
                if (gyroZ > gyroZMax) gyroZMax = gyroZ;
                if (gyroZ < gyroZMin) gyroZMin = gyroZ;
                
            }
        }
    }
    
    private void startRangeCheck() {
        values.clear();
        dataCount = 0;
        firstData.set(true);
        rangeCheckActive.set(true);
    }
    
    private void stopRangeCheck() {
        rangeCheckActive.set(false);
        accelZAvg = (int)(accelZSum / dataCount);
        gyroYAvg = (int)(gyroYSum / dataCount);
        gyroZAvg = (int)(gyroZSum / dataCount);
        for(Integer v: values) {
            System.out.print(v); System.out.print(", ");
        }
        System.out.println(); 
        System.out.println(String.format("Accel Z Avg: %d", accelZAvg));
        System.out.println(String.format("Gyro Y Avg: %d", gyroYAvg));
        System.out.println(String.format("Gyro Z Avg: %d", gyroZAvg));
    }
}
