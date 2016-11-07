package be.tomcools.tombot.endpoints;

import be.tomcools.tombot.model.*;
import be.tomcools.tombot.model.settings.SettingConstants;
import com.google.gson.Gson;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;
import lombok.Builder;

@Builder
public class FacebookWebhook {
    private static final Gson GSON = new Gson();
    private EventBus eventbus;

    public void webhookRequestHandler(RoutingContext route) {
        HttpServerRequest r = route.request();
        String mode = r.getParam("hub.mode");
        String token = r.getParam("hub.verify_token");
        String challenge = r.getParam("hub.challenge");
        if (mode != null && "subscribe".equalsIgnoreCase(mode)) {
            HttpServerResponse response = r.response();
            response.setStatusCode(200)
                    .end(challenge);
        } else {
            r.bodyHandler(b -> {
                FacebookMessage message = GSON.fromJson(b.toJsonObject().toString(), FacebookMessage.class);

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
            if (entryMessage.isMessage()) {
                handleFacebookMessage(entryMessage);
            } else if (entryMessage.isDelivery()) {
                System.out.println(entryMessage.getDelivery().getSeq() + " delivered");
            } else if (entryMessage.isPostback()) {
                if (SettingConstants.GET_STARTED.equalsIgnoreCase(entryMessage.getPostback().getPayload())) {
                    handleGettingStarted(entryMessage);
                }
                System.out.println("POSTBACK :-)");
            } else if (entryMessage.isReadConfirmation()) {
                System.out.println("ReadConfirmation");
            }
        }
    }

    private void handleGettingStarted(FacebookMessageMessaging message) {
        FacebookReplyMessage replyMessage = FacebookReplyMessage.builder()
                .recipient(message.getSender())
                .message(FacebookMessageContent.builder().text("You clicked getting started! :-)").build())
                .build();

        eventbus.send(EventBusConstants.SEND_MESSAGE, GSON.toJson(replyMessage));
    }

    private void handleFacebookMessage(FacebookMessageMessaging message) {
        FacebookReplyMessage replyMessage = FacebookReplyMessage.builder()
                .recipient(message.getSender())
                .message(FacebookMessageContent.builder().text("Hello from the bot :-)").build())
                .build();

        eventbus.send(EventBusConstants.SEND_MESSAGE, GSON.toJson(replyMessage));
    }
}
