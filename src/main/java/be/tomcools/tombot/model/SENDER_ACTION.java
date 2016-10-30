package be.tomcools.tombot.model;

/**
 * Created by tomco on 30/10/2016.
 */
public enum SENDER_ACTION {
    MARK_SEEN("mark_seen"),
    TYPING_OFF("typing_off"),
    TYPING_ON("typing_on");

    private String value;

    SENDER_ACTION(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
