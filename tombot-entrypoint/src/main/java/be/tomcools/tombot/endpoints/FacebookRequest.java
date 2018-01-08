package be.tomcools.tombot.endpoints;

import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class FacebookRequest {
    private HttpServerRequest r;

    public boolean isSubscribeRequest() {
        String mode = r.getParam("hub.mode");
        String token = r.getParam("hub.verify_token");

        return mode != null && "subscribe".equalsIgnoreCase(mode);
    }

    public void handleBody(Handler<Buffer> handler) {
        HttpServerResponse response = r.response();
        try {
            r.bodyHandler(handler);
            response.setStatusCode(200).end();
        } catch(Exception ex) {
            response.setStatusCode(500).end(ex.getMessage());
        }
    }

    public void respondToRequest() {
        HttpServerResponse response = r.response();
        String challenge = r.getParam("hub.challenge");
        response.setStatusCode(200)
                .end(challenge);
    }
}
