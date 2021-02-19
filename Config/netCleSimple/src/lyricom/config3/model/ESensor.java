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
package lyricom.config3.model;

/**
 * ENum for possible sensors
 * 
 * @author Andrew
 */
public enum ESensor {
    SENSOR_1A (5, 0, 1023, true),
    SENSOR_1B (6, 0, 1023, true),
    SENSOR_2A (3, 0, 1023, true),
    SENSOR_2B (4, 0, 1023, true),
    SENSOR_3A (1, 0, 1023, true),
    SENSOR_3B (2, 0, 1023, true),
    
    USB_INPUT(7, 0, 255, false),

    ACCEL_X (8, -16000, 16000, true),
    ACCEL_Y (9, -16000, 16000, true),
    ACCEL_Z (10, -16000, 16000, true),
    /*
    * Gyro values go from -32768 to + 32767, but the high values
    * are for very violent motions.  
    * Here we chop off the highest values to improve sensitivity 
    * for the smaller motions.
    * Simlarly the "Any Motion sensor will deliver values from
    * 0 to 28,377 but we chop off the most violent motions.
    */
    GYRO_X (11, -15000, 15000, true),
    GYRO_Y (12, -15000, 15000, true),
    GYRO_Z (13, -15000, 15000, true),
    GYRO_ANY (14, 0, 13000, true);
    
    private final int id;
    private final int minValue;
    private final int maxValue;
    private final boolean continuous;
    
    ESensor(int id, int min, int max, boolean cont) {
        this.id = id;
        minValue = min;
        maxValue = max;
        continuous = cont;
    }
    
    public static ESensor getSensorA(int port) {
        if (port == 1) return SENSOR_1A;
        else if (port == 2) return SENSOR_2A;
        else return SENSOR_3A;
    }
    
    public static ESensor getSensorB(int port) {
        if (port == 1) return SENSOR_1B;
        else if (port == 2) return SENSOR_2B;
        else return SENSOR_3B;
    }
    
    public static ESensor getSensorByID(int id) {
        for(ESensor s: ESensor.values()) {
            if (s.id == id) return s;
        }
        return null;
    }

    public int getId() {
        return id;
    }

    public int getMinValue() {
        return minValue;
    }

    public int getMaxValue() {
        return maxValue;
    }

    public boolean isContinuous() {
        return continuous;
    }
}
