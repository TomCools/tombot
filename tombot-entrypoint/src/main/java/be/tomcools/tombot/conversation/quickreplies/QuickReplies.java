package be.tomcools.tombot.conversation.quickreplies;

import be.tomcools.tombot.model.facebook.Coordinates;
import be.tomcools.tombot.model.facebook.FacebookQuickReply;
import be.tomcools.tombot.tools.JSON;

public enum QuickReplies implements QuickReply {
    LOCATION(FacebookQuickReply.builder().content_type("location").build()),
    BIKE_RETURN(FacebookQuickReply.builder().title("Return a Bike").payload("BIKE_RETURN").content_type("text").build()),
    BIKE_RETRIEVE(FacebookQuickReply.builder().title("Find a Bike").payload("BIKE_RETRIEVE").content_type("text").build());

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
