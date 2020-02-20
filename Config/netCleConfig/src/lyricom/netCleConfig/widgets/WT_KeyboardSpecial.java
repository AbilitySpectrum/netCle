package lyricom.netCleConfig.widgets;

import lyricom.netCleConfig.model.Trigger;

/**
 * WT_KeyboardSpecial is a variant of WT_ValueLabelOption.
 * It became necessary as the number of ways keyboard commands could be 
 * sent increased.  The possible HID special keyboard commands, as of
 * version 1.02 of the hub are:
 *   Key Press:   encoded as FF 00 00 keycode
 *   Key Release:            FE 00 00 keycode
 *   Key + Modifier:         FD 00 modifier keycode
 * 
 * It is necessary to tell this code what the control code is
 * (FF, FE or FD) and whether the widget is controlling the keycode
 * or the modifier.
 * 
 * @author Andrew
 */
public class WT_KeyboardSpecial extends W_Combo {
    public static enum KeyType { BASEKEY, MODIFIER };

    private final ValueLabelPair[] actions;
    private final Trigger theTrigger;
    private final int overlay;
    private final KeyType keyType;
    
    public WT_KeyboardSpecial(String label, int overlay, KeyType kt, Trigger t, 
            ValueLabelPair[] actions) {
        super(label, actions);
        theTrigger = t;
        this.actions = actions;
        this.overlay = overlay;
        this.keyType = kt;
        update();
    }
    
    @Override
    public void widgetChanged() {
        ValueLabelPair p = (ValueLabelPair) theBox.getSelectedItem();
        int ap = theTrigger.getActionParam();
        
        if (keyType == KeyType.BASEKEY) {
                            // overlay + existing modifier + selected base key
            theTrigger.setActionParam (overlay | (ap & 0xff00) | (p.getValue() & 0xff));
        } else { // MODIFIER
                            // overlay + selected modifier + existing base key
            theTrigger.setActionParam (overlay | (p.getValue() & 0xff00) | (ap & 0xff));            
        }
        
        // Repeat is set from the selected ValueLabelPair.
        theTrigger.setRepeat(p.getRepeat());        
    }
    
    @Override
    public void update() {
        int param = theTrigger.getActionParam() & 0xff;
        int value;
        if (keyType == KeyType.BASEKEY) {
            value = param & 0xff;
        } else { // Modifier
            value = param & 0xff00;
        }
        for(ValueLabelPair p: actions) {
            if (p.getValue() == value) {
                theBox.setSelectedItem(p);
            }
        }        
    }
}
