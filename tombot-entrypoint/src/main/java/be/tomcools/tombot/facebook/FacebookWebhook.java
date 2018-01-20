package be.tomcools.tombot.facebook;

import be.tomcools.tombot.conversation.FacebookMessageHandler;
import be.tomcools.tombot.conversation.context.ConversationContext;
import be.tomcools.tombot.conversation.context.ConversationContextCache;
import be.tomcools.tombot.model.facebook.messages.FacebookMessage;
import be.tomcools.tombot.model.facebook.messages.FacebookMessageEntry;
import be.tomcools.tombot.model.facebook.messages.FacebookMessageMessaging;
import be.tomcools.tombot.model.facebook.messages.partials.SenderAction;
import be.tomcools.tombot.model.facebook.settings.SettingConstants;
import be.tomcools.tombot.tools.JSON;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.RoutingContext;
import lombok.Builder;

@Builder
public class FacebookWebhook {
    private static final Logger LOG = LoggerFactory.getLogger(FacebookWebhook.class);
    private EventBus eventbus;

    public void webhookRequestHandler(RoutingContext route) {
        FacebookRequest request = new FacebookRequest(route.request());

        if (request.isSubscribeRequest()) {
            request.respondToRequest();
        } else {
            request.handleBody(b -> {
                LOG.info("Got Request from Facebook: " + b.toString());
                JsonObject jsonObject = b.toJsonObject();
                FacebookMessage message = JSON.fromJson(jsonObject.toString(), FacebookMessage.class);
                handleMessage(message);
            });
        }
    }

    private void handleMessage(FacebookMessage message) {
        for (FacebookMessageEntry entry : message.getEntry()) {
            handleFacebookMessageEntry(entry);
        }
    }

    private void handleFacebookMessageEntry(FacebookMessageEntry entry) {
        for (FacebookMessageMessaging entryMessage : entry.getMessaging()) {
            this.handleFacebookMessageMessaging(entryMessage);
        }
    }

    private void handleFacebookMessageMessaging(FacebookMessageMessaging entryMessage) {
        FacebookContext context = new FacebookContext(eventbus, entryMessage);

        if (context.isMessage()) {
            LOG.info("Got Message from Facebook: " + context.getMessageText());
            context.senderAction(SenderAction.TYPING_ON);
            handleFacebookMessage(context);
        } else if (context.isDelivery()) {
            LOG.info("Message delivery confirmation");
        } else if (context.isPostback()) {
            LOG.info("Got POSTBACK");
            if (SettingConstants.GET_STARTED.equalsIgnoreCase(entryMessage.getPostback().getPayload())) {
                handleGettingStarted(context);
            } else {
                context.sendReply("Oh look, a postback" + entryMessage.getPostback().getPayload());
            }
        } else if (context.isReadConfirmation()) {
            LOG.info("Got Read Confirmation");
        }
    }

    private void handleGettingStarted(FacebookContext context) {
        context.sendReply("Hi! I am Avelo. I can help you find a bike station for Velo Antwerp!");
    }

    private void handleFacebookMessage(FacebookContext facebookContext) {
        ConversationContext conversationContext = ConversationContextCache.getConversation(facebookContext.getSender().getId());
        this.handleConversation(facebookContext, conversationContext);
    }

    private void handleConversation(FacebookContext fbContext, ConversationContext conversationContext) {
        new FacebookMessageHandler(fbContext, conversationContext).handle();
    }
}
