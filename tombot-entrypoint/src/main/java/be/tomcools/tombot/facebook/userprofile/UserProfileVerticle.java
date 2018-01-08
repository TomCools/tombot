package be.tomcools.tombot.facebook.userprofile;

import be.tomcools.tombot.EventBusConstants;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonArray;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.redis.RedisClient;
import io.vertx.redis.RedisOptions;

public class UserProfileVerticle extends AbstractVerticle {
    private static final Logger LOG = LoggerFactory.getLogger(UserProfileVerticle.class);

    private RedisClient redis;

    @Override
    public void start() throws Exception {
        RedisOptions options = new RedisOptions();
        options.setHost("tomcools.cloudapp.net");
        redis = RedisClient.create(vertx, options);
        vertx.eventBus().consumer(EventBusConstants.PROFILE_DETAILS, this::handleProfileDetailsMessage);
        vertx.eventBus().consumer(EventBusConstants.GET_ALL_CLIENT_IDS, this::retrieveAllClientIds);
    }

    private <T> void retrieveAllClientIds(Message<T> tMessage) {
        redis.keys("tombot:*", keys -> {
            if (keys.succeeded()) {
                JsonArray result = keys.result();
                tMessage.reply(result.toString().replace("tombot:", ""));
            } else {
                tMessage.fail(1, "Couldn't retrieve Client ID's from Redis");
            }
        });
    }

    private <T> void handleProfileDetailsMessage(Message<T> userIdMessage) {
        redis.get("tombot:" + userIdMessage.body().toString(), res -> {
            if (res.succeeded() && res.result() != null) {
                LOG.debug("Succesfully retrieved from REDIS!" + res.result());
                userIdMessage.reply(res.result());
            } else {
                retrieveProfileFromAPI(userIdMessage);
            }
        });
    }

    private <T> void retrieveProfileFromAPI(Message<T> userIdMessage) {
        vertx.eventBus().send(EventBusConstants.PROFILE_DETAILS_FACEBOOK, userIdMessage.body(), response -> {
            if (response.succeeded()) {
                LOG.debug("Succesfully retrieved from API");
                Object body = response.result().body();
                userIdMessage.reply(body);
                saveInRedis((String) userIdMessage.body(), (String) body);
            } else {
                LOG.error("Call to Profile Details on facebook failed.");
            }
        });
    }

    private <T> void saveInRedis(String userId, String body) {
        redis.set("tombot:" + userId, body, result -> {
            if (!result.succeeded()) {
                LOG.error("Could not save in redis :(");
            } else {
                LOG.debug("Successfully saved in Redis. userID: {}" + userId);
            }
        });
    }
}
