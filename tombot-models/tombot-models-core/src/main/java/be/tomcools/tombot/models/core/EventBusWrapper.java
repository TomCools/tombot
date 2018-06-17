package be.tomcools.tombot.models.core;

import be.tomcools.tombot.model.facebook.messages.FacebookModelFactory;
import be.tomcools.tombot.model.facebook.messages.outgoing.FacebookReplyMessage;
import be.tomcools.tombot.model.facebook.messages.partials.FacebookIdentifier;
import com.google.gson.Gson;
import io.vertx.core.eventbus.EventBus;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class EventBusWrapper {
    private EventBus eventBus;

    public void sendMessage(String receiver, String textReply) {
        FacebookReplyMessage replyMessage = FacebookModelFactory.replyMessage(new FacebookIdentifier(receiver), textReply);
        eventBus.send(EventBusConstants.SEND_MESSAGE, new Gson().toJson(replyMessage));
    }
}
