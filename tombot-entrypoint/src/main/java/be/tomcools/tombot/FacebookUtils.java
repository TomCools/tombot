package be.tomcools.tombot;

import be.tomcools.tombot.model.facebook.*;

import java.util.Arrays;
import java.util.List;

public class FacebookUtils {
    public static FacebookReplyMessage replyMessage(FacebookIdentifier recipient, String message) {
        return FacebookReplyMessage.builder()
                .messaging_type("RESPONSE")
                .recipient(recipient)
                .message(FacebookMessageContent.builder().text(message).build())
                .build();
    }

    public static FacebookReplyMessage replyMessage(FacebookIdentifier recipient, String message, List<FacebookQuickReply> quickReplies) {
        return FacebookReplyMessage.builder()
                .messaging_type("RESPONSE")
                .recipient(recipient)
                .message(FacebookMessageContent.builder().text(message).quick_replies(quickReplies).build())
                .build();
    }

    public static FacebookReplyMessage replyLocation(FacebookIdentifier recipient, Coordinates coordinates) {
        return FacebookReplyMessage.builder()
                .messaging_type("RESPONSE")
                .recipient(recipient)
                .message(FacebookMessageContent.builder()
                        .attachment(FacebookMessageAttachment.forLocation(coordinates)).build())
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
