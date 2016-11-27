package be.tomcools.tombot.model.facebook;

/**
 * Created by tomco on 30/10/2016.
 */
public enum SenderAction {
    MARK_SEEN("mark_seen"),
    TYPING_OFF("typing_off"),
    TYPING_ON("typing_on");

    private String value;

    SenderAction(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
