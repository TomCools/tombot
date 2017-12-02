package be.tomcools.tombot;

import be.tomcools.tombot.model.core.EventBusConstants;
import be.tomcools.tombot.model.facebook.settings.GreetingSetting;
import be.tomcools.tombot.model.facebook.settings.StartedButton;
import be.tomcools.tombot.tools.JSON;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.Message;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

import java.util.Map;

public class MessengerConnector extends AbstractVerticle {
    private static final Logger LOG = LoggerFactory.getLogger(MessengerConnector.class);

    private HttpClient client;
    private String messagesEndpoint;
    private String threadSettingsEndpoint;
    private String accesstoken;

    @Override
    public void start() throws Exception {
        String facebookToken = findFacebookToken();

        accesstoken = "access_token=" + facebookToken;

        //TODO make configurable
        messagesEndpoint = "/v2.6/me/messages?" + accesstoken;
        threadSettingsEndpoint = "/v2.6/me/thread_settings?" + accesstoken;


        HttpClientOptions options = new HttpClientOptions()
                .setLogActivity(true)
                .setKeepAlive(true)
                .setTrustAll(true)
                .setSsl(true)
                .setDefaultPort(443)
                .setDefaultHost("graph.facebook.com");
        client = vertx.createHttpClient(options);

        vertx.eventBus().consumer(EventBusConstants.SEND_MESSAGE, this::handleMessage);
        vertx.eventBus().consumer(EventBusConstants.PROFILE_DETAILS_FACEBOOK, this::handleProfileDetails);

        changeSettings();
    }

    private <T> void handleProfileDetails(Message<T> tMessage) {
        T userId = tMessage.body();
        String url = "/v2.6/" + userId.toString() + "?fields=first_name,last_name,profile_pic,locale,timezone,gender&" + accesstoken;
        client.get(url, response -> {
            response.bodyHandler(b -> tMessage.reply(b.toString()));
        }).putHeader("content-type", "application/json").end();
    }

    private String findFacebookToken() {
        Map<String, String> environmentProps = System.getenv();
        String token;
        if (!environmentProps.containsKey("FACEBOOK_TOKEN")) {
            throw new IllegalStateException("Property not set!: FACEBOOK_TOKEN");
        } else {
            token = environmentProps.get("FACEBOOK_TOKEN");
        }
        return token;
    }

    public void handleMessage(Message<String> tMessage) {
        String body = tMessage.body();
        client.post(messagesEndpoint, response -> {
            LOG.info("Received response with status code " + response.statusCode());
            response.bodyHandler(b -> LOG.debug("Received response with status code " + b.toString()));
        }).putHeader("content-type", "application/json").end(body);
    }

    private void changeSettings() {
        GreetingSetting greetingSetting = GreetingSetting.builder()
                .greeting("Hi {{user_first_name}}, welcome to TomBot.").build();
        sendSettings(greetingSetting);

        StartedButton startButton = new StartedButton();
        sendSettings(startButton);
    }

    private void sendSettings(Object settings) {
        client.post(threadSettingsEndpoint, response -> {
            LOG.info("Received response with status code " + response.statusCode());
            response.bodyHandler(b -> LOG.debug("Received response with status code " + b.toString()));
        }).putHeader("content-type", "application/json").end(JSON.toJson(settings));
    }
}
