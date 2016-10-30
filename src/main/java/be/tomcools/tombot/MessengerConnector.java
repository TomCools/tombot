package be.tomcools.tombot;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.Message;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;

public class MessengerConnector extends AbstractVerticle {
    public static String SEND_MESSAGE = "SEND_MESSAGE";
    private HttpClient client;

    @Override
    public void start() throws Exception {
        HttpClientOptions options = new HttpClientOptions()
                .setLogActivity(true)
                .setDefaultHost("https://graph.facebook.com/v2.6/me/messages?access_token=EAARJ94lKpNIBAEP6rHAVTj1ZCZBLcIBW2MeFK2h9zZAr8kZCVLNq98ojE97ckZC56C2iQLvDq8fHv6ZBTLdGzVC8LxZBZBGXNSQ5D3MLMDQmPjfv0mWRyb3hV7WsOJRoicxTqze0w9sHAKau6lgGtMbULbb1xqJM9Azu29G7ZA4UhTgZDZD");
        client = vertx.createHttpClient(options);

        vertx.eventBus().consumer(SEND_MESSAGE, this::handleMessage);
    }

    public <JsonObject> void handleMessage(Message<JsonObject> tMessage) {
        JsonObject body = tMessage.body();
        client.post("").putHeader("content-type", "application/json").end(body.toString());
    }
}
