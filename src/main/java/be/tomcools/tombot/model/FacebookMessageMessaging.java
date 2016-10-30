package be.tomcools.tombot.model;

public class FacebookMessageMessaging {
    public FacebookIdentifier sender;
    public FacebookIdentifier recipient;
    public Long timestamp;
    public FacebookMessageContent message;
    public FacebookMessageDelivery delivery;
    public FacebookMessageRead read;
    public FacebookMessagePostback postback;

    public boolean isMessage() {
        return message != null;
    }

    public boolean isDelivery() {
        return delivery != null;
    }

    public boolean isReadConfirmation() {
        return read != null;
    }

    public boolean isPostback() {
        return postback != null;
    }
}
