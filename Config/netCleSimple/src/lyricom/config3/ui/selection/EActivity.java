package lyricom.config3.ui.selection;

/**
 *
 * @author Andrew
 */
public enum EActivity {
    CURSOR,
    MOUSE_BUTTONS,
    SCROLLING,
    KEYBOARD;
    
    private final String localizedName;
    
    EActivity() {
        localizedName = SelectionResource.getStr(this.name());
    }

    String getText() {
        return localizedName;
    }
}
