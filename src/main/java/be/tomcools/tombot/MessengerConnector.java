package be.tomcools.tombot;

import be.tomcools.tombot.model.EventBusConstants;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.Message;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;

import java.util.Map;

public class MessengerConnector extends AbstractVerticle {

    private HttpClient client;
    private String messagesEndpoint;
    private String threadSettingsEndpoint;

    @Override
    public void start() throws Exception {
        String facebookToken = findFacebookToken();

        //TODO make configurable
        messagesEndpoint = "/v2.6/me/messages?access_token=" + facebookToken;
        threadSettingsEndpoint = "/v2.6/me/thread_settings?access_token=" + facebookToken;

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
