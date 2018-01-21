package be.tomcools.tombot.model.facebook.messages;

import be.tomcools.tombot.model.facebook.messages.attachement.FacebookMessageAttachment;
import be.tomcools.tombot.model.facebook.messages.outgoing.FacebookOutgoingMessageContent;
import be.tomcools.tombot.model.facebook.messages.outgoing.FacebookQuickReply;
import be.tomcools.tombot.model.facebook.messages.outgoing.FacebookReplyMessage;
import be.tomcools.tombot.model.facebook.messages.partials.Coordinates;
import be.tomcools.tombot.model.facebook.messages.partials.FacebookIdentifier;
import be.tomcools.tombot.model.facebook.messages.partials.SenderAction;

import java.util.List;

public class FacebookModelFactory {
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

    public static FacebookReplyMessage replyMessage(FacebookIdentifier recipient, List<FacebookQuickReply> quickReplies) {
        return FacebookReplyMessage.builder()
                .messaging_type("RESPONSE")
                .recipient(recipient)
                .message(FacebookOutgoingMessageContent.builder().text(null).quick_replies(quickReplies).build())
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
