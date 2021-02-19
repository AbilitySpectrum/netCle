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
import lyricom.config3.solutions.data.GyroData;

/**
 *
 * @author Andrew
 */
public class Calibrator implements Runnable, SensorDataCallback {
    protected static final ResourceBundle RES = ResourceBundle.getBundle("strings");
        
    private final CalibrationUI ui;
    private final GyroData data;
    AtomicBoolean cancelling = new AtomicBoolean();
    Thread runThread;
    Serial serial;
    
    // Useful data values.
    private int gyroYBias;
    private int gyroZBias;
    private int accelZRestingPoint;
    private int tiltPoint;
    private boolean tiltIsNegative;
    
    public Calibrator(CalibrationUI ui, GyroData data) {
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
    
    public boolean wasCancelled() {
        return cancelling.get();
    }
        
    @Override
    public void run() {
        ui.setCalibrator(this);
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
        startDataCollection();
        snooze(3000);
        stopDataCollection();
        if (cancelling.get() == true) return;
        
        // Save the values we need.
        gyroYBias = gyroYAcc.getAvg();
        gyroZBias = gyroZAcc.getAvg();
        accelZRestingPoint = accelZAcc.getAvg();
 
//        System.out.println(String.format("Y Biax: %d", gyroYBias));
//        System.out.println(String.format("Z Bias: %d", gyroZBias));

        // --------------------------------------
        // Tilt
        ui.presentMessage("Now, we will measure the tilt used to turn the gyro on and off.<br/>"
                + "Tilt your head to the left a comfortable amount.<br/><br/>"
                + "Press <b>Continue</b> when when you are in the tilted position.");
        ui.waitForContinue();
        if (cancelling.get() == true) return;
        ui.presentMessage("Measuring ...");
        startDataCollection();
        snooze(3000);
        stopDataCollection();
        if (cancelling.get() == true) return;
        
        // Calculate tilt point - left or right.
        int maxTilt = accelZAcc.getMin();  // on tilt left we expect values to go negative.
        if (maxTilt < accelZRestingPoint) {
            // Left tilt
            int tiltRange = maxTilt - accelZRestingPoint;  // Large negative value
            tiltPoint = accelZRestingPoint + tiltRange * 2 / 3;
            tiltIsNegative = true;
        } else {
            // Right tilt - OK
            maxTilt = accelZAcc.getMax();
            int tiltRange = maxTilt - accelZRestingPoint; // Positive number.
            tiltPoint = accelZRestingPoint + tiltRange * 2 / 3;
            tiltIsNegative = false;
        }
        
//        System.out.println(String.format("Accel Z Resting: %d", accelZRestingPoint));
//        System.out.println(String.format("Max Tilt: %d", maxTilt));
//        System.out.println(String.format("tilt point: %d", tiltPoint));
        
        ui.presentMessage("Great. We're all done.<br/><br/>"
                + "Press <b>Continue</b> to end the calibration.");
        ui.waitForContinue();
        if (cancelling.get() == true) return;
        
        return;        
    }
    
    private void snooze(int time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException ex) {
            
        }
    }

    // - - - - - - - - - - - - - - - - - - - - - - 
    // Sensor data
    class Accumulator {
        private int min, max;
        private int dataCount;
        private long dataSum;
        
        Accumulator() {
            dataCount = 0;
        }
        
        void reset() {
            dataCount = 0;
        }
        
        void addData(int value) {
            if (dataCount == 0) {
                dataSum = min = max = value;
                dataCount++;
            } else {
                if (value < min) min = value;
                if (value > max) max = value;
                dataSum += value;
                dataCount++;
            }
        }
        
        int getMax() { return max; }
        int getMin() { return min; }
        int getAvg() { return (int) (dataSum / dataCount); }
    };
    
    Accumulator accelZAcc = new Accumulator();
    Accumulator gyroYAcc = new Accumulator();
    Accumulator gyroZAcc = new Accumulator();
    
    AtomicBoolean rangeCheckActive = new AtomicBoolean();
    
    @Override
    public void newSensorData(InStream in) throws IOError {
        if (in.getChar() != Model.START_OF_DATA) {
            throw new IOError(RES.getString("CDE_INVALID_SENSOR_DATA"));
        }
        
        if (rangeCheckActive.get() == true) {
            int sensorCount = in.getNum(2);
            for(int i=0; i<sensorCount; i++) {
                int id = in.getID(2);
                int value = in.getNum(2);
                ESensor s = ESensor.getSensorByID(id);
                switch (s) {
                    case ACCEL_Z:
                        accelZAcc.addData(value);
                        break;
                    case GYRO_Y:
                        gyroYAcc.addData(value);
                        break;
                    case GYRO_Z:
                        gyroZAcc.addData(value);
                        break;
                    // Don't care about the rest.
                }
            }                         
        }
    }
    
    private void startDataCollection() {
        accelZAcc.reset();
        gyroYAcc.reset();
        gyroZAcc.reset();
        rangeCheckActive.set(true);
    }
    
    private void stopDataCollection() {
        rangeCheckActive.set(false);
    }

    public int getGyroYBias() {
        return gyroYBias;
    }

    public int getGyroZBias() {
        return gyroZBias;
    }

    public void setGyroZBias(int gyroZBias) {
        this.gyroZBias = gyroZBias;
    }

    public int getTiltPoint() {
        return tiltPoint;
    }

    public boolean isTiltIsNegative() {
        return tiltIsNegative;
    }
}
