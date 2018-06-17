package be.tomcools.tombot.models.core;

import be.tomcools.tombot.model.facebook.messages.FacebookMessageMessaging;
import be.tomcools.tombot.model.facebook.messages.FacebookModelFactory;
import be.tomcools.tombot.model.facebook.messages.outgoing.FacebookReplyMessage;
import be.tomcools.tombot.model.facebook.messages.partials.FacebookIdentifier;
import com.google.gson.Gson;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import lombok.AllArgsConstructor;

import java.util.function.Consumer;

@AllArgsConstructor
public class EventBusWrapper {
    private EventBus eventBus;

    public void sendMessage(FacebookIdentifier target, String textReply) {
        FacebookReplyMessage replyMessage = FacebookModelFactory.replyMessage(target, textReply);
        eventBus.send(EventBusConstants.SEND_MESSAGE, new Gson().toJson(replyMessage));
    }

    public void handleSendMessage(Consumer<FacebookReplyMessage> consumer) {
        eventBus.consumer(EventBusConstants.SEND_MESSAGE, (msg) -> {
                    consumer.accept(to(msg,FacebookReplyMessage.class));
                }
        );
    }

    public void receivedMsg(FacebookMessageMessaging entryMessage) {
        eventBus.send(EventBusConstants.RECEIVED_MESSAGE, entryMessage);
    }

    public void handleReceivedMsg(Consumer<FacebookMessageMessaging> consumer) {
        eventBus.consumer(EventBusConstants.RECEIVED_MESSAGE, (msg) -> {
                    consumer.accept(to(msg,FacebookMessageMessaging.class));
                }
        );
    }

    public void receivedGettingStarted(FacebookIdentifier sender) {
        eventBus.send(EventBusConstants.GET_STARTED, sender);
    }

    public void handleGettinStarted(Consumer<FacebookIdentifier> consumer) {
        eventBus.consumer(EventBusConstants.GET_STARTED, (msg) -> {
                    consumer.accept(to(msg,FacebookIdentifier.class));
                }
        );
    }

    private <T> T to(Message msg, Class<T> aClass) {
        return new Gson().fromJson((String) msg.body(), aClass);
    }
}
