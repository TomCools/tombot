package be.tomcools.tombot.facebook;

import be.tomcools.tombot.EventBusConstants;
import be.tomcools.tombot.model.facebook.messages.outgoing.FacebookReplyMessage;
import be.tomcools.tombot.model.facebook.settings.Greeting;
import be.tomcools.tombot.model.facebook.settings.Payload;
import be.tomcools.tombot.model.facebook.settings.SettingConstants;
import be.tomcools.tombot.model.facebook.settings.Settings;
import be.tomcools.tombot.tools.JSON;
import com.google.gson.JsonObject;
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
        threadSettingsEndpoint = "/v2.6/me/messenger_profile?" + accesstoken;


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
        FacebookReplyMessage message = JSON.fromJson(body, FacebookReplyMessage.class);
        sendMessage(message, body);
    }

    private void sendMessage(FacebookReplyMessage facebookReplyMessage, String body) {
        client.post(messagesEndpoint, response -> {
            LOG.info("Received response with status code " + response.statusCode());
            response.bodyHandler(b -> LOG.debug("Received response with status code " + b.toString()));
        }).putHeader("content-type", "application/json").end(body);
    }

    private void changeSettings() {
        Settings settings = Settings.builder()
                .greeting(Greeting.builder()
                        .locale("default")
                        .text("Hi {{user_first_name}}, welcome to TomBot.")
                        .build())
                .gettingStartedPayload(Payload.builder().payload(SettingConstants.GET_STARTED).build())
                .build();
        sendSettings(settings);
    }

    private void sendSettings(Object settings) {
        String settingsJson = JSON.toJson(settings);
        LOG.info("Sending Settings: " + settingsJson);
        client.post(threadSettingsEndpoint, response -> {
            LOG.info("Received response with status code " + response.statusCode() + " for setting changes.");
            response.bodyHandler(b -> {
                LOG.info("Updated settings result: " + b.toString());
                if (!b.toString().contains("success")) {
                    LOG.error("Error while updating settings.");
                }
                verifySettings(settingsJson);
            });
        }).putHeader("content-type", "application/json").end(settingsJson);
    }

    private void verifySettings(String sentSettings) {
        JsonObject propertiesObj = JSON.fromJson(sentSettings, JsonObject.class);

        String properties = propertiesObj
                .entrySet().stream()
                .map(Map.Entry::getKey).reduce("", (s, s2) -> {
                    s += "," + s2;
                    return s;
                }).replaceFirst(",", "");

        String requestUrl = String.format("%s&fields=%s", threadSettingsEndpoint, properties);

        client.get(requestUrl, response -> {
            LOG.info("Received response with status code " + response.statusCode() + " for validating setting changes.");
            response.bodyHandler(b -> {
                JsonObject responseObject = JSON.fromJson(b.toString(), JsonObject.class);
                if (!sentSettings.equalsIgnoreCase(responseObject.get("data").getAsString())) {
                    LOG.warn(String.format("Settings not updated succesfully!%sExpected: %s%s Actual %s",
                            System.lineSeparator(), sentSettings, System.lineSeparator(), b.toString()));
                } else {
                    LOG.info("Current Settings: " + b.toString());
                }
            });
        }).end();
    }
}
