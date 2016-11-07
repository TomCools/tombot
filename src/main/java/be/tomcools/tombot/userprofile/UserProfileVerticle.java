package be.tomcools.tombot.userprofile;

import be.tomcools.tombot.model.EventBusConstants;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.Message;

public class UserProfileVerticle extends AbstractVerticle {

    @Override
    public void start() throws Exception {
        vertx.eventBus().consumer(EventBusConstants.PROFILE_DETAILS, this::handleProfileDetailsMessage);
    }

    private <T> void handleProfileDetailsMessage(Message<T> tMessage) {
        //Change so it first looks up in local database
        vertx.eventBus().send(EventBusConstants.PROFILE_DETAILS_FACEBOOK, tMessage.body(), response -> {
            if (response.succeeded()) {
                tMessage.reply(response.result().body());
            } else {
                System.out.println("Call to Profile Details on facebook failed.");
            }
        });
    }
}
