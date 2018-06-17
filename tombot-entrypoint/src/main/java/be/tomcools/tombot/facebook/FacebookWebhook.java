package be.tomcools.tombot.facebook;

import be.tomcools.tombot.model.facebook.messages.FacebookMessage;
import be.tomcools.tombot.model.facebook.messages.FacebookMessageEntry;
import be.tomcools.tombot.model.facebook.messages.FacebookMessageMessaging;
import be.tomcools.tombot.model.facebook.settings.SettingConstants;
import be.tomcools.tombot.models.core.EventBusConstants;
import com.google.gson.Gson;
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
                FacebookMessage message = new Gson().fromJson(jsonObject.toString(), FacebookMessage.class);
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
        if (entryMessage.isMessage()) {
            LOG.info("Got Message from Facebook: " + entryMessage.getMessage().getText());
            eventbus.send(EventBusConstants.PROCESS_MSG, entryMessage);
        } else if (entryMessage.isDelivery()) {
            LOG.info("Message delivery confirmation");
        } else if (entryMessage.isPostback()) {
            LOG.info("Got POSTBACK");
            if (SettingConstants.GET_STARTED.equalsIgnoreCase(entryMessage.getPostback().getPayload())) {
                eventbus.send(EventBusConstants.GET_STARTED, entryMessage.getSender().getId());
            } else {
                LOG.info("Oh look, a postback" + entryMessage.getPostback().getPayload());
            }
        } else if (entryMessage.isReadConfirmation()) {
            LOG.info("Got Read Confirmation");
        }
    }
}
