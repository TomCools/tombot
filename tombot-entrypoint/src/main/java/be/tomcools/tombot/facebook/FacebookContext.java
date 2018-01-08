package be.tomcools.tombot.facebook;


import be.tomcools.tombot.EventBusConstants;
import be.tomcools.tombot.conversation.quickreplies.QuickReply;
import be.tomcools.tombot.model.facebook.messages.FacebookMessageMessaging;
import be.tomcools.tombot.model.facebook.messages.FacebookModelFactory;
import be.tomcools.tombot.model.facebook.messages.incomming.FacebookIncommingMessageContent;
import be.tomcools.tombot.model.facebook.messages.outgoing.FacebookQuickReply;
import be.tomcools.tombot.model.facebook.messages.outgoing.FacebookReplyMessage;
import be.tomcools.tombot.model.facebook.messages.partials.Coordinates;
import be.tomcools.tombot.model.facebook.messages.partials.FacebookIdentifier;
import be.tomcools.tombot.model.facebook.messages.partials.SenderAction;
import com.google.gson.Gson;
import io.vertx.core.eventbus.EventBus;
import lombok.Builder;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Builder
public class FacebookContext {
    private static final Gson GSON = new Gson();
    private EventBus eventBus;
    private FacebookMessageMessaging message;
    //private UserDetails userDetails;

    public FacebookIdentifier getSender() {
        return message.getSender();
    }

    public String getMessageText() {
        return message.getMessage().getText();
    }

    public FacebookIncommingMessageContent getMessage() {
        return message.getMessage();
    }

    public boolean isMessage() {
        return message.isMessage();
    }

    public boolean isDelivery() {
        return message.isDelivery();
    }

    public boolean isReadConfirmation() {
        return message.isReadConfirmation();
    }

    public boolean isPostback() {
        return message.isPostback();
    }

    public void sendReply(String textReply) {
        FacebookReplyMessage replyMessage = FacebookModelFactory.replyMessage(message.getSender(), textReply);
        eventBus.send(EventBusConstants.SEND_MESSAGE, GSON.toJson(replyMessage));
    }

    public void sendReply(String textReply, List<QuickReply> quickReplies) {
        List<FacebookQuickReply> facebookQuickReplies = quickReplies.stream().map(QuickReply::getReply).collect(Collectors.toList());

        FacebookReplyMessage replyMessage = FacebookModelFactory.replyMessage(message.getSender(), textReply, facebookQuickReplies);
        eventBus.send(EventBusConstants.SEND_MESSAGE, GSON.toJson(replyMessage));
    }

    public void sendReply(String textReply, QuickReply... quickReplies) {
        this.sendReply(textReply, Arrays.asList(quickReplies));
    }

    public void sendLocation(String locationName, Coordinates coordinates) {
        FacebookReplyMessage replyMessage = FacebookModelFactory.replyLocation(message.getSender(), locationName, coordinates);
        eventBus.send(EventBusConstants.SEND_MESSAGE, GSON.toJson(replyMessage));
    }

    public void sendLocation(Coordinates coordinates) {
        FacebookReplyMessage replyMessage = FacebookModelFactory.replyLocation(message.getSender(), coordinates);
        eventBus.send(EventBusConstants.SEND_MESSAGE, GSON.toJson(replyMessage));
    }

    public void senderAction(SenderAction action) {
        FacebookReplyMessage replyMessage = FacebookModelFactory.senderAction(message.getSender(), action);
        eventBus.send(EventBusConstants.SEND_MESSAGE, GSON.toJson(replyMessage));
    }

    public void nlpAnalysis() {
        eventBus.send(EventBusConstants.WIT_AI_ANALYSE_SENTENCE, this.getMessageText(), h -> {
            if (h.succeeded()) {
                this.sendReply("DEBUG: " + h.result().body().toString());
            } else {
                this.sendReply("I failed to understand what you want :(. My WIT-ty friend was not available.");
            }
        });
    }
}
