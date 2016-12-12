package be.tomcools.tombot;

import be.tomcools.tombot.model.core.EventBusConstants;
import be.tomcools.tombot.model.facebook.settings.GreetingSetting;
import be.tomcools.tombot.model.facebook.settings.StartedButton;
import be.tomcools.tombot.tools.JSON;
import be.tomcools.tombot.userprofile.UserProfileVerticle;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.json.Json;

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
        vertx.deployVerticle(HttpVerticle.class.getName());
        vertx.deployVerticle(UserProfileVerticle.class.getName());
    }

    private void pushSettings() {
        GreetingSetting setting = GreetingSetting.builder()
                .greeting("Hi {{user_first_name}}, welcome to TomBot.").build();

        vertx.eventBus().send(EventBusConstants.CHANGE_SETTINGS, JSON.toJson(setting));

        StartedButton startButton = new StartedButton();
        vertx.eventBus().send(EventBusConstants.CHANGE_SETTINGS, JSON.toJson(startButton));
    }
}
