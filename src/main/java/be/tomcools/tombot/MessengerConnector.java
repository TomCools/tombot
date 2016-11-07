package be.tomcools.tombot;

import be.tomcools.tombot.model.EventBusConstants;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.MultiMap;
import io.vertx.core.eventbus.DeliveryOptions;
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
        vertx.eventBus().consumer(EventBusConstants.PROFILE_DETAILS, this::handleProfileDetails);
    }

    private <T> void handleProfileDetails(Message<T> tMessage) {
        T userId = tMessage.body();
        String url = "/v2.6/" + tMessage.body() + "?fields=first_name,last_name,profile_pic,locale,timezone,gender&" + accesstoken;
        client.get(url, response -> {
            this.handleMessage(new Message<String>() {
                @Override
                public String address() {
                    return null;
                }

                @Override
                public MultiMap headers() {
                    return null;
                }

                @Override
                public String body() {
                    return "Getting your profile data: " + response.statusMessage();
                }

                @Override
                public String replyAddress() {
                    return null;
                }

                @Override
                public void reply(Object o) {

                }

                @Override
                public <R> void reply(Object o, Handler<AsyncResult<Message<R>>> handler) {

                }

                @Override
                public void reply(Object o, DeliveryOptions deliveryOptions) {

                }

                @Override
                public <R> void reply(Object o, DeliveryOptions deliveryOptions, Handler<AsyncResult<Message<R>>> handler) {

                }

                @Override
                public void fail(int i, String s) {

                }
            });
            tMessage.reply("Test");
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
