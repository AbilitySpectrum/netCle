package lyricom.config3.ui.selection;

import java.util.ResourceBundle;

/**
 *
 * @author Andrew
 */
public class SelectionResource {

    private static final ResourceBundle RES = ResourceBundle.getBundle("strings");    

    static public String getStr(String key) {
        return RES.getString(key);
    }
}
