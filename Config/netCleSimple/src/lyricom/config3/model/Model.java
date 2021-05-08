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
 * Static constant values.
 * Static method for accessing the model.
 * Values here match values used in the netCle.
 * @author Andrew
 */
public class Model {
    // Commands sent to sensact
    public static final Byte CMD_VERSION        = (byte) 'V';
    public static final Byte CMD_RUN            = (byte) 'R';
    public static final Byte CMD_DISPLAY        = (byte) 'Q';
    public static final Byte CMD_GET_TRIGGERS   = (byte) 'U';
    public static final Byte START_OF_DATA      = (byte) 'S';
    public static final Byte START_OF_TRIGGERS  = (byte) 'T';
    public static final Byte MOUSE_SPEED        = (byte) 'Y';
    public static final Byte END_OF_BLOCK       = (byte) 'Z';
    
    // Communication protocol constants
    static final byte NUMBER_MASK = 0x60;
    static final byte ID_MASK = 0x40;
    static final byte CONDITION_MASK = '0';
    static final byte BOOL_TRUE = 'p';
    static final byte BOOL_FALSE = 'q';
    static final byte TRIGGER_MASK = '0';

    // Mouse Action values
    static public final int MOUSE_UP = 1;
    static public final int MOUSE_DOWN = 2;
    static public final int MOUSE_LEFT = 3;
    static public final int MOUSE_RIGHT = 4;
    static public final int MOUSE_CLICK = 5;
    static public final int MOUSE_PRESS = 6;
    static public final int MOUSE_RELEASE = 7;
    static public final int MOUSE_RIGHT_CLICK = 8;
    static public final int NUDGE_UP = 10;
    static public final int NUDGE_DOWN = 11;
    static public final int NUDGE_LEFT = 12;
    static public final int NUDGE_RIGHT = 13;
    static public final int NUDGE_STOP = 14;
    static public final int MOUSE_WHEEL_UP = 20;
    static public final int MOUSE_WHEEL_DOWN = 21;
    // Extentions - added for v1.02 of the hub.
    static public final int MOUSE_RIGHT_PRESS = 30;
    static public final int MOUSE_RIGHT_RELEASE = 31;
    static public final int MOUSE_MIDDLE_CLICK = 32;
    static public final int MOUSE_MIDDLE_PRESS = 33;
    static public final int MOUSE_MIDDLE_RELEASE = 34;
        
    // IR TV Control values
    static public final int IR_TV_ON_OFF = 1;
    static public final int IR_VOLUME_UP = 2;
    static public final int IR_VOLUME_DOWN = 3;
    static public final int IR_MUTE = 4;
    static public final int IR_BOX_ON_OFF = 101;
    static public final int IR_CHANNEL_UP = 102;
    static public final int IR_CHANNEL_DOWN = 103;
    static public final int IR_DIGIT_0 = 110;
    static public final int IR_DIGIT_1 = 111;
    static public final int IR_DIGIT_2 = 112;
    static public final int IR_DIGIT_3 = 113;
    static public final int IR_DIGIT_4 = 114;
    static public final int IR_DIGIT_5 = 115;
    static public final int IR_DIGIT_6 = 116;
    static public final int IR_DIGIT_7 = 117;
    static public final int IR_DIGIT_8 = 118;
    static public final int IR_DIGIT_9 = 119;
    
    // Light box action options
    static public final int LBO_ONLY = 0;
    static public final int LBO_ADD = 0x0100;
    static public final int LBO_REMOVE = 0x0200;
    static public final int LBO_PULSE = 0x0300;
    
    // Relay values
    static public final int RELAY_PULSE = 0;
    static public final int RELAY_ON = 1;
    static public final int RELAY_OFF = 2;
    
    // Press and Release values - added to key values
    static public final int KEY_PRESS = 0xff000000;
    static public final int KEY_RELEASE = 0xfe000000;
    static public final int KEY_MODIFIER = 0xfd000000;

}
