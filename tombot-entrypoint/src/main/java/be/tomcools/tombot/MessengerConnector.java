package be.tomcools.tombot;

import be.tomcools.tombot.model.core.EventBusConstants;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.Message;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;

import java.util.Map;

public class MessengerConnector extends AbstractVerticle {

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
        vertx.eventBus().consumer(EventBusConstants.CHANGE_SETTINGS, this::handleSettingsChange);
        vertx.eventBus().consumer(EventBusConstants.PROFILE_DETAILS_FACEBOOK, this::handleProfileDetails);
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
            System.out.println("Received response with status code " + response.statusCode());
            System.out.println("Received response with status code " + response.bodyHandler(b -> System.out.println(b.toString())));

        }).putHeader("content-type", "application/json").end(body);
    }

    public void handleSettingsChange(Message<String> tMessage) {
        String body = tMessage.body();
        client.post(threadSettingsEndpoint, response -> {
            System.out.println("Received response with status code " + response.statusCode());
            System.out.println("Received response with status code " + response.bodyHandler(b -> System.out.println(b.toString())));

        }).putHeader("content-type", "application/json").end(body);
    }
}
