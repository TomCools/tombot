package be.tomcools.tombot.endpoints;


import be.tomcools.tombot.FacebookUtils;
import be.tomcools.tombot.model.core.EventBusConstants;
import be.tomcools.tombot.model.core.UserDetails;
import be.tomcools.tombot.model.facebook.FacebookMessageEntry;
import be.tomcools.tombot.model.facebook.FacebookMessageMessaging;
import be.tomcools.tombot.model.facebook.FacebookReplyMessage;
import com.google.gson.Gson;
import io.vertx.core.eventbus.EventBus;
import lombok.Builder;

@Builder
public class FacebookContext {
    private static final Gson GSON = new Gson();
    private EventBus eventBus;
    private FacebookMessageMessaging message;
    private UserDetails userDetails;

    public String getMessageText() {
        return message.getMessage().getText();
    }

    public void sendReply(String textReply) {
        FacebookReplyMessage replyMessage = FacebookUtils.replyMessage(message.getSender(),textReply);
        eventBus.send(EventBusConstants.SEND_MESSAGE, GSON.toJson(replyMessage));
    }
}
