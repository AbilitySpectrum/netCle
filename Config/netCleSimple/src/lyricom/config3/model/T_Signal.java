package lyricom.config3.model;

/**
 *
 * @author Andrew
 */
public class T_Signal {
    // Common pre-set signal levels.
    public static final T_Signal BTN_PRESS   = new T_Signal(500, Trigger.TRIGGER_ON_HIGH);
    public static final T_Signal BTN_RELEASE = new T_Signal(500, Trigger.TRIGGER_ON_LOW);
    public static final T_Signal JS_HIGH     = new T_Signal (700, Trigger.TRIGGER_ON_HIGH);
    public static final T_Signal JS_NOT_HIGH = new T_Signal (700, Trigger.TRIGGER_ON_LOW);
    public static final T_Signal JS_LOW      = new T_Signal (250, Trigger.TRIGGER_ON_LOW);
    public static final T_Signal JS_NOT_LOW  = new T_Signal (250, Trigger.TRIGGER_ON_HIGH);
    
    private final int level;
    private final int condition;
    
    public T_Signal(int level, int condition) {
        this.level = level;
        this.condition = condition;
    }

    public int getLevel() {
        return level;
    }

    public int getCondition() {
        return condition;
    }
    
    // Return the inverse of this signal.
    public T_Signal not() {
        if (condition == Trigger.TRIGGER_ON_HIGH) {
            return new T_Signal(level, Trigger.TRIGGER_ON_LOW);
        } else if (condition == Trigger.TRIGGER_ON_LOW) {
            return new T_Signal(level, Trigger.TRIGGER_ON_HIGH);
        } else {
            return null;
        }
    }
}
