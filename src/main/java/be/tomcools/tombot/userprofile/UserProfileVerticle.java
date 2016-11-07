package be.tomcools.tombot.userprofile;

import be.tomcools.tombot.model.EventBusConstants;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.Message;
import io.vertx.redis.RedisClient;
import io.vertx.redis.RedisOptions;

public class UserProfileVerticle extends AbstractVerticle {

    private RedisClient redis;

    @Override
    public void start() throws Exception {
        RedisOptions options = new RedisOptions();
        options.setHost("tomcools.cloudapp.net");
        redis = RedisClient.create(vertx, options);
        vertx.eventBus().consumer(EventBusConstants.PROFILE_DETAILS, this::handleProfileDetailsMessage);
    }

    private <T> void handleProfileDetailsMessage(Message<T> userIdMessage) {
        redis.get("tombot:" + userIdMessage.body().toString(), res -> {
            if (res.succeeded() && res.result() != null) {
                System.out.println("Succesfully retrieved from REDIS!" + res.result());
                userIdMessage.reply(res.result());
            } else {
                retrieveProfileFromAPI(userIdMessage);
            }
        });
    }

    private <T> void retrieveProfileFromAPI(Message<T> userIdMessage) {
        vertx.eventBus().send(EventBusConstants.PROFILE_DETAILS_FACEBOOK, userIdMessage.body(), response -> {
            if (response.succeeded()) {
                System.out.println("Succesfully retrieved from API");
                Object body = response.result().body();
                userIdMessage.reply(body);
                saveInRedis((String) userIdMessage.body(), (String) body);
            } else {
                System.out.println("Call to Profile Details on facebook failed.");
            }
        });
    }

    private <T> void saveInRedis(String userId, String body) {
        redis.set("tombot:" + userId, body, result -> {
            if (!result.succeeded()) {
                System.err.println("Could not save in redis :(");
            }
        });
    }
}
