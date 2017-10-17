package be.tomcools.tombot;

import be.tomcools.tombot.model.facebook.FacebookIdentifier;
import be.tomcools.tombot.model.facebook.FacebookMessageContent;
import be.tomcools.tombot.model.facebook.FacebookReplyMessage;

public class FacebookUtils {
    public static FacebookReplyMessage replyMessage(FacebookIdentifier recipient, String message) {
        return FacebookReplyMessage.builder()
                .recipient(recipient)
                .message(FacebookMessageContent.builder().text(message).build())
                .build();
    }
}
