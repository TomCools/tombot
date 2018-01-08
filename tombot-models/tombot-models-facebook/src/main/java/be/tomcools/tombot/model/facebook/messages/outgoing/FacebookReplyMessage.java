package be.tomcools.tombot.model.facebook.messages.outgoing;

import be.tomcools.tombot.model.facebook.messages.partials.FacebookIdentifier;
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
