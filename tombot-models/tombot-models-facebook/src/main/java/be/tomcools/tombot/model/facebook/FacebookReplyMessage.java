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
    private String messaging_type;
    private FacebookIdentifier recipient;
    private FacebookOutgoingMessageContent message;
    private String sender_action;
}
