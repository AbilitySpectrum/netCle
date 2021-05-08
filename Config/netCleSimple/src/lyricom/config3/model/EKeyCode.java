package lyricom.config3.model;

import lyricom.config3.model.Resource;
import lyricom.config3.ui.MainFrame;

/**
 *
 * @author Andrew
 */
public enum EKeyCode {
        ACT_KEY_A(0x61, 0, false, false),
        ACT_KEY_B(0x62, 0, false, false),
        ACT_KEY_C(0x63, 0, false, false),
        ACT_KEY_D(0x64, 0, false, false),
        ACT_KEY_E(0x65, 0, false, false),
        ACT_KEY_F(0x66, 0, false, false),
        ACT_KEY_G(0x67, 0, false, false),
        ACT_KEY_H(0x68, 0, false, false),
        ACT_KEY_I(0x69, 0, false, false),
        ACT_KEY_J(0x6A, 0, false, false),
        ACT_KEY_K(0x6B, 0, false, false),
        ACT_KEY_L(0x6C, 0, false, false),
        ACT_KEY_M(0x6D, 0, false, false),
        ACT_KEY_N(0x6E, 0, false, false),
        ACT_KEY_O(0x6F, 0, false, false),
        ACT_KEY_P(0x70, 0, false, false),
        ACT_KEY_Q(0x71, 0, false, false),
        ACT_KEY_R(0x72, 0, false, false),
        ACT_KEY_S(0x73, 0, false, false),
        ACT_KEY_T(0x74, 0, false, false),
        ACT_KEY_U(0x75, 0, false, false),
        ACT_KEY_V(0x76, 0, false, false),
        ACT_KEY_W(0x77, 0, false, false),
        ACT_KEY_X(0x78, 0, false, false),
        ACT_KEY_Y(0x79, 0, false, false),
        ACT_KEY_Z(0x7A, 0, false, false),
        
        ACT_KEY_0(0x30, 0, false, false),
        ACT_KEY_1(0x31, 0, false, false),
        ACT_KEY_2(0x32, 0, false, false),
        ACT_KEY_3(0x33, 0, false, false),
        ACT_KEY_4(0x34, 0, false, false),
        ACT_KEY_5(0x35, 0, false, false),
        ACT_KEY_6(0x36, 0, false, false),
        ACT_KEY_7(0x37, 0, false, false),
        ACT_KEY_8(0x38, 0, false, false),
        ACT_KEY_9(0x39, 0, false, false),
        
        ACT_KEY_SPACE(0x20, 0, false, false),
        ACT_PLUS_KEY (0x2B, 0, false, false),
        ACT_MINUS_KEY(0x2D, 0, false, false),
        ACT_BACK_QUOTE(0x60, 0, false, false),
        ACT_OPEN_SQ_BRACKET(0x5B, 0, false, false),
        ACT_CLOSED_SQ_BRACKET(0x5D, 0, false, false),
        ACT_OPEN_CURLY_BRACKET(0x7B, 0, false, false),
        ACT_CLOSED_CURLY_BRACKET(0x7D, 0, false, false),     
        
        ACT_KEY_UP_ARROW   (0xDA, 14, false, true),
        ACT_KEY_DOWN_ARROW (0xD9, 12, false, true),
        ACT_KEY_LEFT_ARROW (0xD8, 11, false, true),
        ACT_KEY_RIGHT_ARROW(0xD7, 7, false, true),
  
        ACT_KEY_BACKSPACE(0xB2, 8, false, true),
        ACT_KEY_TAB      (0xB3, 9, false, true),
        ACT_KEY_RETURN   (0xB0, 10, false, true),
        ACT_KEY_ESCAPE   (0xB1, 27, false, true),
        ACT_KEY_INSERT   (0xD1, 1, false, true),
        ACT_KEY_DELETE   (0xD4, 4, false, true),
        ACT_KEY_PAGE_UP  (0xD3, 3, false, true),
        ACT_KEY_PAGE_DOWN(0xD6, 6, false, true),
        ACT_KEY_HOME     (0xD2, 2, false, true),
        ACT_KEY_END      (0xD5, 5, false, true),
        
        ACT_CAPS_LOCK    (0xC1, 0, false, false),
        
        ACT_KEY_COMMA    (0x2C, 0, false, false),
        ACT_KEY_PERIOD   (0x2E, 0, false, false),
        ACT_KEY_GT       (0x3C, 0, false, false),
        ACT_KEY_LT       (0x3E, 0, false, false),
        
        ACT_F1 (0xC2, 15, false, true),
        ACT_F2 (0xC3, 16, false, true),
        ACT_F3 (0xC4, 17, false, true),
        ACT_F4 (0xC5, 18, false, true),
        ACT_F5 (0xC6, 19, false, true),
        ACT_F6 (0xC7, 20, false, true),
        ACT_F7 (0xC8, 21, false, true),
        ACT_F8 (0xC9, 22, false, true),
        ACT_F9 (0xCA, 23, false, true),
        ACT_F10(0xCB, 24, false, true),
        ACT_F11(0xCC, 25, false, true),
        ACT_F12(0xCD, 26, false, true),
        
        ACT_LCONTROL(0x80, 0, true, false),
        ACT_LSHIFT  (0x81, 0, true, false),
        ACT_LALT    (0x82, 0, true, false),
        ACT_LWINDOWS(0x83, 0, true, false),
        ACT_RCONTROL(0x84, 0, true, false),
        ACT_RSHIFT  (0x85, 0, true, false),
        ACT_RALT    (0x86, 0, true, false),
        ACT_RWINDOWS(0x87, 0, true, false);
    
    
    private final String localizedName;
    private final int wiredCode;
    private final int btCode;
    private final boolean modifier;
    private final boolean special;

    EKeyCode(int wiredCode, int btCode, boolean isMod, boolean isSpec) {
        localizedName = Resource.getStr(this.name());  
        this.wiredCode = wiredCode;
        this.btCode = btCode;
        modifier = isMod;
        special = isSpec;
    }
    
    @Override
    public String toString() {
        return localizedName;
    }
    
    public int getCode() {
        if (MainFrame.getInstance().getBluetooth() == true) {
            return getBtCode();
        } else {
            return getWiredCode();
        }
    }

    public int getWiredCode() {
        return wiredCode;
    }

    public int getBtCode() {
        return btCode;
    }

    public boolean isModifier() {
        return modifier;
    }
    
    public boolean isSpecial() {
        return special;
    }
}
