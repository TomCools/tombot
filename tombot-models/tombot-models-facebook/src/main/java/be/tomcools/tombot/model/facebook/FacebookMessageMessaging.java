package be.tomcools.tombot.model.facebook;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FacebookMessageMessaging {
    private FacebookIdentifier sender;
    private FacebookIdentifier recipient;
    private Long timestamp;
    private FacebookIncommingMessageContent message;
    private FacebookMessageDelivery delivery;
    private FacebookMessageRead read;
    private FacebookMessagePostback postback;

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
