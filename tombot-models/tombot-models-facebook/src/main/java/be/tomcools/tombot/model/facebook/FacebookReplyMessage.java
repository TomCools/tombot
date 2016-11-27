package be.tomcools.tombot.model.facebook;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FacebookReplyMessage {
    private FacebookIdentifier recipient;
    private FacebookMessageContent message;
    private String sender_action;
}
