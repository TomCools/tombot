package be.tomcools.tombot.endpoints;


import be.tomcools.tombot.FacebookUtils;
import be.tomcools.tombot.model.core.EventBusConstants;
import be.tomcools.tombot.model.core.UserDetails;
import be.tomcools.tombot.model.facebook.FacebookMessageEntry;
import be.tomcools.tombot.model.facebook.FacebookMessageMessaging;
import be.tomcools.tombot.model.facebook.FacebookReplyMessage;
import be.tomcools.tombot.model.facebook.SenderAction;
import com.google.gson.Gson;
import io.vertx.core.eventbus.EventBus;
import lombok.Builder;

@Builder
public class FacebookContext {
    private static final Gson GSON = new Gson();
    private EventBus eventBus;
    private FacebookMessageMessaging message;
    //private UserDetails userDetails;

    public String getMessageText() {
        return message.getMessage().getText();
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
        FacebookReplyMessage replyMessage = FacebookUtils.replyMessage(message.getSender(), textReply);
        eventBus.send(EventBusConstants.SEND_MESSAGE, GSON.toJson(replyMessage));
    }

    public void senderAction(SenderAction action) {
        FacebookReplyMessage replyMessage = FacebookUtils.senderAction(message.getSender(), action);
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
