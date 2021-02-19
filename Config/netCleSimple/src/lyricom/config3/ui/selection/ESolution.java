package lyricom.config3.ui.selection;

/**
 *
 * @author Andrew
 */
public enum ESolution {
    S_ONE_BTN_MOUSE (EDevice.CURSOR_ONE_BTN, "One Button Mouse", "Stuff blah blah blah"),
    S_TWO_BTN_MOUSE (EDevice.CURSOR_TWO_BTN, "Two Button Mouse", "Stuff"),
    S_JOYSTICK_MOUSE(EDevice.CURSOR_JOYSTICK, "Joystick Mouse", "Stuff"),
    S_GYRO_MOUSE1    (EDevice.CURSOR_GYRO, "Gyro Mouse 1", "Stuff"),
    S_GYRO_MOUSE2    (EDevice.CURSOR_GYRO, "Gyro Mouse 2", "Stuff"),
    
    S_LEFT_CLICK     (EDevice.MOUSE_ONE_BTN, "Left Click Button", "Stuff"),
    S_RIGHTCLICK     (EDevice.MOUSE_ONE_BTN, "Right Click Button", "Stuff"),
    S_LEFT_PRESS_RELEASE_TOGGLE (EDevice.MOUSE_ONE_BTN, "Left Press-Release Toggle", "Stuff"),
    S_LEFT_EMULATION (EDevice.MOUSE_ONE_BTN, "Left Button Emulation", "Stuff"),
    S_RIGHT_EMULATION (EDevice.MOUSE_ONE_BTN, "Right Button Emulation", "Stuff"),
    S_THREE_FUNC_MOUSE (EDevice.MOUSE_ONE_BTN, "Three Function Mouse Button", "Stuff"),
    
    S_LEFT_RIGHT_CLICK (EDevice.MOUSE_TWO_BTN, "Left-Right Click", "Stuff"),
    S_LEFT_PRESS_RELEASE (EDevice.MOUSE_TWO_BTN, "Left Press-Release", "Stuff"),
    S_LEFT_RIGHT_EMULATION (EDevice.MOUSE_TWO_BTN, "Left-Right Emulation", "Stuff"),
    
    S_JOYSTICK_CLICKS (EDevice.MOUSE_JOYSTICK, "Joystick Clicks", "Stuff"),
    S_GYRO_CLICKS (EDevice.MOUSE_GYRO, "Gyro Clicks", "Stuff"),
    
    S_SCROLL_TOGGLE (EDevice.SCROLL_ONE_BTN, "Scroll Up-Down Toggle", "Stuff"),
    S_SCROLL_UP_DOWN (EDevice.SCROLL_TWO_BTN, "Scroll Up-Down", "Stuff"),
    S_JOYSTICK (EDevice.SCROLL_JOYSTICK, "Scroll With Joystick", "Stuff"),
    
    S_KEYBOARD_TEXT(EDevice.KEYBOARD_BTN, "Send a text string", "Stuff"),
    S_KEYBOARD_SPECIAL(EDevice.KEYBOARD_BTN, "Send a special character", "Stuff"),
    S_KEYBOARD_MODIFIER(EDevice.KEYBOARD_BTN, "Send a character plus a modifier", "Stuff"),
    S_KEYBOARD_SHIFT(EDevice.KEYBOARD_BTN, "Press-Release toggle for shift key", "Stuff"),
    S_KEYBOARD_CONTROL(EDevice.KEYBOARD_BTN, "Press-Release toggle for control key", "Stuff");
    
    private final EDevice device;
    private final String text;
    private final String desc;
    
    ESolution(EDevice device, String text, String desc) {
        this.device = device;
        this.text = text;
        this.desc = desc;
    }
    
    public EDevice getDevice() {
        return device;
    }
    
    public String getText() {
        return text;
    }
    
    public String getDesc() {
        return desc;
    }
}
