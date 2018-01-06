package be.tomcools.tombot;

import be.tomcools.tombot.model.facebook.*;

import java.util.List;

public class FacebookUtils {
    public static FacebookReplyMessage replyMessage(FacebookIdentifier recipient, String message) {
        return FacebookReplyMessage.builder()
                .messaging_type("RESPONSE")
                .recipient(recipient)
                .message(FacebookOutgoingMessageContent.builder().text(message).build())
                .build();
    }

    public static FacebookReplyMessage replyMessage(FacebookIdentifier recipient, String message, List<FacebookQuickReply> quickReplies) {
        return FacebookReplyMessage.builder()
                .messaging_type("RESPONSE")
                .recipient(recipient)
                .message(FacebookOutgoingMessageContent.builder().text(message).quick_replies(quickReplies).build())
                .build();
    }

    public static FacebookReplyMessage replyLocation(FacebookIdentifier recipient, Coordinates coordinates) {
        return replyLocation(recipient, "", coordinates);
    }

    public static FacebookReplyMessage replyLocation(FacebookIdentifier recipient, String locationName, Coordinates coordinates) {
        return FacebookReplyMessage.builder()
                .messaging_type("RESPONSE")
                .recipient(recipient)
                .message(FacebookOutgoingMessageContent.builder()
                        .attachment(FacebookMessageAttachment.forLocation(coordinates, locationName)).build())
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
