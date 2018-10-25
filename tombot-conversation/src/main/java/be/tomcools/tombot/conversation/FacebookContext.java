package be.tomcools.tombot.conversation;

import be.tomcools.tombot.conversation.replies.quickreplies.QuickReply;
import be.tomcools.tombot.model.facebook.messages.FacebookMessageMessaging;
import be.tomcools.tombot.model.facebook.messages.incomming.FacebookIncommingMessageContent;
import be.tomcools.tombot.model.facebook.messages.outgoing.FacebookQuickReply;
import be.tomcools.tombot.model.facebook.messages.partials.Coordinates;
import be.tomcools.tombot.model.facebook.messages.partials.FacebookIdentifier;
import be.tomcools.tombot.model.facebook.messages.partials.SenderAction;
import be.tomcools.tombot.models.core.EventBusWrapper;
import com.google.gson.Gson;
import lombok.Builder;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Builder
public class FacebookContext {
    private static final Gson GSON = new Gson();
    private EventBusWrapper eventBus;
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
        eventBus.sendMessage(message.getSender(), textReply);
    }

    public void sendReply(String textReply, List<QuickReply> quickReplies) {
        List<FacebookQuickReply> facebookQuickReplies = quickReplies.stream().map(QuickReply::getReply).collect(Collectors.toList());
        eventBus.sendMessage(message.getSender(), textReply, facebookQuickReplies);
    }

    public void sendReply(QuickReply... quickReplies) {
        this.sendReply(null, Arrays.asList(quickReplies));
    }

    public void sendReply(String textReply, QuickReply... quickReplies) {
        this.sendReply(textReply, Arrays.asList(quickReplies));
    }

    public void sendLocation(String locationName, Coordinates coordinates) {
        eventBus.sendLocation(message.getSender(), locationName, coordinates);
    }

    public void sendLocation(Coordinates coordinates) {
        this.sendLocation("", coordinates);
    }

    public void senderAction(SenderAction action) {
        eventBus.sendAction(message.getSender(), action);
    }
}
