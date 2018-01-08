package be.tomcools.tombot.conversation.replies.quickreplies;

import be.tomcools.tombot.model.facebook.messages.outgoing.FacebookQuickReply;
import be.tomcools.tombot.model.facebook.messages.partials.Coordinates;
import be.tomcools.tombot.tools.JSON;

public enum QuickReplies implements QuickReply {
    LOCATION(FacebookQuickReply.builder().content_type("location").build());

    private FacebookQuickReply reply;

    QuickReplies(FacebookQuickReply reply) {
        this.reply = reply;
    }

    public FacebookQuickReply getReply() {
        return reply;
    }

    public static QuickReply previousLocation(Coordinates coordinates) {
        return new QuickReply() {
            @Override
            public FacebookQuickReply getReply() {
                return FacebookQuickReply.builder()
                        .title("Previous Location")
                        .content_type("text")
                        .payload(JSON.toJson(coordinates))
                        .build();
            }
        };
    }
}
