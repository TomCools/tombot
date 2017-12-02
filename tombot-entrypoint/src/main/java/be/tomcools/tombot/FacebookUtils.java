package be.tomcools.tombot;

import be.tomcools.tombot.model.facebook.FacebookIdentifier;
import be.tomcools.tombot.model.facebook.FacebookMessageContent;
import be.tomcools.tombot.model.facebook.FacebookReplyMessage;
import be.tomcools.tombot.model.facebook.SenderAction;

public class FacebookUtils {
    public static FacebookReplyMessage replyMessage(FacebookIdentifier recipient, String message) {
        return FacebookReplyMessage.builder()
                .messaging_type("RESPONSE")
                .recipient(recipient)
                .message(FacebookMessageContent.builder().text(message).build())
                .build();
    }

    public static FacebookReplyMessage senderAction(FacebookIdentifier recipient, SenderAction action) {
        return FacebookReplyMessage.builder()
                .messaging_type("RESPONSE")
                .recipient(recipient)
                .sender_action(action.toString())
                .build();
    }
}
