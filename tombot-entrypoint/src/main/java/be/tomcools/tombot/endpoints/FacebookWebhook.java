package be.tomcools.tombot.endpoints;

import be.tomcools.tombot.FacebookUtils;
import be.tomcools.tombot.model.core.EventBusConstants;
import be.tomcools.tombot.model.core.UserDetails;
import be.tomcools.tombot.model.facebook.*;
import be.tomcools.tombot.model.facebook.settings.SettingConstants;
import com.google.gson.Gson;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.RoutingContext;
import lombok.Builder;

@Builder
public class FacebookWebhook {
    private static final Logger LOG = LoggerFactory.getLogger(FacebookWebhook.class);
    private static final Gson GSON = new Gson();
    private EventBus eventbus;

    public void webhookRequestHandler(RoutingContext route) {
        HttpServerRequest r = route.request();
        String mode = r.getParam("hub.mode");
        String token = r.getParam("hub.verify_token");
        String challenge = r.getParam("hub.challenge");
        if (mode != null && "subscribe".equalsIgnoreCase(mode)) {
            HttpServerResponse response = r.response();
            LOG.info("Subscribe mode");
            response.setStatusCode(200)
                    .end(challenge);
        } else {
            r.bodyHandler(b -> {
                FacebookMessage message = GSON.fromJson(b.toJsonObject().toString(), FacebookMessage.class);
                LOG.info("Got Message: {}",message);
                handleMessage(message);
                r.response().end();
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
            eventbus.send(EventBusConstants.PROFILE_DETAILS, entryMessage.getSender().getId(), response -> {
                if (response.succeeded()) {
                    UserDetails userDetails = GSON.fromJson(response.result().body().toString(), UserDetails.class);
                    FacebookContext context = new FacebookContext(eventbus, entryMessage, userDetails);
                    LOG.debug("Got User Details: " + userDetails);
                    if (entryMessage.isMessage()) {
                        handleFacebookMessage(context);
                    } else if (entryMessage.isDelivery()) {
                        System.out.println(entryMessage.getDelivery().getSeq() + " delivered");
                    } else if (entryMessage.isPostback()) {
                        if (SettingConstants.GET_STARTED.equalsIgnoreCase(entryMessage.getPostback().getPayload())) {
                            handleGettingStarted(context);
                        }
                        LOG.debug("POSTBACK :-)");
                    } else if (entryMessage.isReadConfirmation()) {
                        LOG.debug("ReadConfirmation");
                    }
                } else {
                    LOG.error("Couldn't successfully get the Profile Details :-(.");
                }
            });
        }
    }

    private void handleGettingStarted(FacebookContext context) {
        context.sendReply("You clicked getting started! :-)");
    }

    private void handleFacebookMessage(FacebookContext context) {
        eventbus.send(EventBusConstants.WIT_AI_ANALYSE_SENTENCE, context.getMessageText(), h -> {
            if (h.succeeded()) {
                context.sendReply("DEBUG: " + h.result().body().toString());
            } else {
                LOG.error("Failed to get response from NLP", h.cause());
                context.sendReply("I failed to understand what you want :(. My WIT-ty friend was not available.");
            }
        });
    }
}
