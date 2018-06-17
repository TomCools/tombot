package be.tomcools.tombot.conversation.replies.quickreplies;

import be.tomcools.tombot.conversation.replies.quickreplies.payloads.FlowActivation;
import be.tomcools.tombot.model.facebook.messages.outgoing.FacebookQuickReply;
import be.tomcools.tombot.model.facebook.messages.partials.Coordinates;
import com.google.gson.Gson;

public class QuickReplies {

    private FacebookQuickReply reply;

    QuickReplies(FacebookQuickReply reply) {
        this.reply = reply;
    }

    public FacebookQuickReply getReply() {
        return reply;
    }

    public static QuickReply location() {
        return () -> FacebookQuickReply.builder().content_type("location").build();
    }

    public static QuickReply location(String name, String payload) {
        return () -> FacebookQuickReply.builder().title(name).payload(payload).content_type("location").build();
    }

    public static QuickReply flowActivation(String text, FlowActivation flowActivation) {
        return () -> FacebookQuickReply.builder().title(text).payload(new Gson().toJson(flowActivation)).content_type("text").build();
    }

    public static QuickReply previousLocation(Coordinates coordinates) {
        return () -> FacebookQuickReply.builder()
                .title("Previous Location")
                .content_type("text")
                .payload(new Gson().toJson(coordinates))
                .build();
    }

    public static QuickReply thanks() {
        return new QuickReply() {
            @Override
            public FacebookQuickReply getReply() {
                return FacebookQuickReply.builder()
                        .title("Thanks!")
                        .content_type("text")
                        .payload("THANKS")
                        .build();
            }
        };
    }
}
