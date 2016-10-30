package be.tomcools.tombot;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.Message;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;

import java.util.Map;

public class MessengerConnector extends AbstractVerticle {

    public static String SEND_MESSAGE = "SEND_MESSAGE";
    private HttpClient client;
    private String token;

    @Override
    public void start() throws Exception {
        Map<String, String> environmentProps = System.getenv();
        if (!environmentProps.containsKey("FACEBOOK_TOKEN")) {
            throw new IllegalStateException("Property not set!: FACEBOOK_TOKEN");
        } else {
            token = environmentProps.get("FACEBOOK_TOKEN");
        }

        HttpClientOptions options = new HttpClientOptions()
                .setLogActivity(true)
                .setKeepAlive(true)
                .setTrustAll(true)
                .setSsl(true)
                .setDefaultPort(443)
                .setDefaultHost("graph.facebook.com");
        client = vertx.createHttpClient(options);

        vertx.eventBus().consumer(SEND_MESSAGE, this::handleMessage);
    }

    public void handleMessage(Message<String> tMessage) {
        String body = tMessage.body();
        client.post("/v2.6/me/messages?access_token=" + token, response -> {
            System.out.println("Received response with status code " + response.statusCode());
            System.out.println("Received response with status code " + response.bodyHandler(b -> System.out.println(b.toString())));

        }).putHeader("content-type", "application/json").end(body);
    }
}
