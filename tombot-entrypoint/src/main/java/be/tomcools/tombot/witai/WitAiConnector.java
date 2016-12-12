package be.tomcools.tombot.witai;

import be.tomcools.tombot.model.core.EventBusConstants;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.Message;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

public class WitAiConnector extends AbstractVerticle {
    private static final Logger LOG = LoggerFactory.getLogger(WitAiConnector.class);

    private HttpClient client;
    private String accesstoken;
    private String witGetMessageEndpoint;

    @Override
    public void start() throws Exception {
        String witToken = findWitToken();

        accesstoken = "Bearer " + witToken;

        //TODO make configurable

        witGetMessageEndpoint = "https://api.wit.ai/message?v=20161112";

        HttpClientOptions options = new HttpClientOptions()
                .setLogActivity(true)
                .setKeepAlive(true)
                .setTrustAll(true)
                .setSsl(true)
                .setDefaultPort(443);
        client = vertx.createHttpClient(options);

        vertx.eventBus().consumer(EventBusConstants.WIT_AI_ANALYSE_SENTENCE, this::handleWitGetRequest);
    }

    private <T> void handleWitGetRequest(Message<T> tMessage) {
        T messageBody = tMessage.body();
        try {
            String encodedMessage = URLEncoder.encode(messageBody.toString(), "UTF-8");
            String url = witGetMessageEndpoint + "&q=" + encodedMessage;
            client.getAbs(url, response -> {
                response.bodyHandler(b -> tMessage.reply(b.toString()));
            }).putHeader("Accept", "application/json").putHeader("Authorization", accesstoken).end();

        } catch (UnsupportedEncodingException e) {
            tMessage.fail(500, e.getMessage());
        }

    }

    private String findWitToken() {
        Map<String, String> environmentProps = System.getenv();
        String token;
        if (!environmentProps.containsKey("WIT_TOKEN")) {
            throw new IllegalStateException("Property not set!: WIT_TOKEN");
        } else {
            token = environmentProps.get("WIT_TOKEN");
        }
        return token;
    }
}
