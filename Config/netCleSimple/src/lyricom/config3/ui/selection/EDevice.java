package lyricom.config3.ui.selection;

/**
 *
 * @author Andrew
 */
public enum EDevice {
    CURSOR_ONE_BTN  (EActivity.CURSOR),
    CURSOR_TWO_BTN  (EActivity.CURSOR),
    CURSOR_JOYSTICK (EActivity.CURSOR),
    CURSOR_GYRO     (EActivity.CURSOR),
    
    MOUSE_ONE_BTN   (EActivity.MOUSE_BUTTONS),
    MOUSE_TWO_BTN   (EActivity.MOUSE_BUTTONS),
    MOUSE_JOYSTICK  (EActivity.MOUSE_BUTTONS),
    MOUSE_GYRO      (EActivity.MOUSE_BUTTONS),
    
    SCROLL_ONE_BTN  (EActivity.SCROLLING),
    SCROLL_TWO_BTN  (EActivity.SCROLLING),
    SCROLL_JOYSTICK (EActivity.SCROLLING),
    
    KEYBOARD_BTN    (EActivity.KEYBOARD);

    private final String localizedName;
    private final EActivity activity;
    EDevice(EActivity act) {
        activity = act;
        localizedName = SelectionResource.getStr(this.name());
    }
    
    public String getText() {
        return localizedName;
    }
    
    public EActivity getActivity() {
        return activity;
    }
}
