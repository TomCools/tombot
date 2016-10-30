package be.tomcools.tombot;

import be.tomcools.tombot.model.*;
import be.tomcools.tombot.model.settings.GreetingSetting;
import be.tomcools.tombot.model.settings.SettingConstants;
import be.tomcools.tombot.model.settings.StartedButton;
import com.google.gson.Gson;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;

public class VertxStarter extends AbstractVerticle {

    public static void main(String[] args) {
        Vertx.vertx().deployVerticle(VertxStarter.class.getName());
    }


    @Override
    public void start() throws Exception {
        vertx.deployVerticle(MessengerConnector.class.getName(), r -> {
            if (r.succeeded()) {
                pushSettings();
            } else {
                throw new IllegalStateException("MessengerConnector could not be started");
            }
        });

        vertx.createHttpServer()
                .requestHandler(this::requestHandler)
                .listen(9999);
    }

    private void pushSettings() {
        GreetingSetting setting = GreetingSetting.builder()
                .greeting("Hi {{user_first_name}}, welcome to TomBot.").build();

        vertx.eventBus().send(EventBusConstants.CHANGE_SETTINGS, new Gson().toJson(setting));

        StartedButton startButton = new StartedButton();
        vertx.eventBus().send(EventBusConstants.CHANGE_SETTINGS, new Gson().toJson(startButton));
    }

    public void requestHandler(HttpServerRequest r) {
        String mode = r.getParam("hub.mode");
        String token = r.getParam("hub.verify_token");
        String challenge = r.getParam("hub.challenge");
        if (mode != null && "subscribe".equalsIgnoreCase(mode)) {
            HttpServerResponse response = r.response();
            response.setStatusCode(200)
                    .end(challenge);
        } else {
            r.bodyHandler(b -> {
                FacebookMessage message = new Gson().fromJson(b.toJsonObject().toString(), FacebookMessage.class);

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

        vertx.eventBus().send(EventBusConstants.SEND_MESSAGE, new Gson().toJson(replyMessage));
    }

    private void handleFacebookMessage(FacebookMessageMessaging message) {
        FacebookReplyMessage replyMessage = FacebookReplyMessage.builder()
                .recipient(message.getSender())
                .message(FacebookMessageContent.builder().text("Hello from the bot :-)").build())
                .build();

        vertx.eventBus().send(EventBusConstants.SEND_MESSAGE, new Gson().toJson(replyMessage));
    }
}
