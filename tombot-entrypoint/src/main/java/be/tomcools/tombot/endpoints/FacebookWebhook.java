package be.tomcools.tombot.endpoints;

import be.tomcools.tombot.model.core.EventBusConstants;
import be.tomcools.tombot.model.core.UserDetails;
import be.tomcools.tombot.model.facebook.*;
import be.tomcools.tombot.model.facebook.settings.SettingConstants;
import be.tomcools.tombot.tools.JSON;
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
                FacebookMessage message = JSON.fromJson(b.toString(), FacebookMessage.class);

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
            FacebookIdentifier sender = entryMessage.getSender();

            eventbus.send(EventBusConstants.PROFILE_DETAILS, sender.getId(), response -> {
                if (response.succeeded()) {
                    UserDetails userDetails = JSON.fromJson(response.result().body().toString(), UserDetails.class);
                    LOG.debug("Got User Details: " + userDetails);
                    if (entryMessage.isMessage()) {
                        handleFacebookMessage(entryMessage, userDetails);
                    } else if (entryMessage.isDelivery()) {
                        System.out.println(entryMessage.getDelivery().getSeq() + " delivered");
                    } else if (entryMessage.isPostback()) {
                        if (SettingConstants.GET_STARTED.equalsIgnoreCase(entryMessage.getPostback().getPayload())) {
                            handleGettingStarted(entryMessage);
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

    private void handleGettingStarted(FacebookMessageMessaging message) {
        FacebookReplyMessage replyMessage = FacebookReplyMessage.builder()
                .recipient(message.getSender())
                .message(FacebookMessageContent.builder().text("You clicked getting started! :-)").build())
                .build();

        eventbus.send(EventBusConstants.SEND_MESSAGE, JSON.toJson(replyMessage));
    }

    private void handleFacebookMessage(FacebookMessageMessaging message, UserDetails userDetails) {
        FacebookReplyMessage replyMessage = FacebookReplyMessage.builder()
                .recipient(message.getSender())
                .message(FacebookMessageContent.builder().text("Hello " + (userDetails.isMale() ? "Sir " : "Melady ") + userDetails.getFirst_name() + "." +
                        " I am truely sorry, but I am not able to anything yet except greet you. I hope this changes soon :-(.")
                        .build())
                .build();

        eventbus.send(EventBusConstants.SEND_MESSAGE, JSON.toJson(replyMessage));

        FacebookReplyMessage consolidationMessage = FacebookReplyMessage.builder()
                .recipient(message.getSender())
                .message(FacebookMessageContent.builder()
                        .attachment(FacebookMessageAttachment.builder().type("image")
                                .payload(AttachementPayload.builder().url("https://scontent-amt2-1.xx.fbcdn.net/v/t1.0-9/14937422_1818596438353291_2136991225262339188_n.jpg?oh=37e02fe9ac8d010bf19d0bfda9b1bb21&oe=58912E55").build())
                                .build())
                        .build())
                .build();

        eventbus.send(EventBusConstants.SEND_MESSAGE, JSON.toJson(consolidationMessage));
    }
}
