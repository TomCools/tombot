package be.tomcools.tombot;

import be.tomcools.tombot.model.FacebookIdentifier;
import be.tomcools.tombot.model.FacebookMessage;
import be.tomcools.tombot.model.FacebookMessageContent;
import be.tomcools.tombot.model.FacebookReplyMessage;
import com.google.gson.Gson;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;

public class VertxStarter extends AbstractVerticle {

    public static void main(String[] args) {
        Vertx.vertx().deployVerticle(VertxStarter.class.getName());
    }


    @Override
    public void start() throws Exception {
        vertx.deployVerticle(MessengerConnector.class.getName());
        super.start();

        vertx.createHttpServer()
                .requestHandler(this::requestHandler)
                .listen(9999);
    }

    public void requestHandler(HttpServerRequest r) {
        String mode = r.getParam("hub.mode");
        String token = r.getParam("hub.verify_token");
        String challenge = r.getParam("hub.challenge");
        if (mode != null && "subscribe".equalsIgnoreCase(mode)) {
            HttpServerResponse response = r.response();
            response.setStatusCode(200)
                    .end(challenge);
        } else {
            r.bodyHandler(b -> {
 //               FacebookMessage message = new Gson().fromJson(b.toJsonObject().toString(), FacebookMessage.class);

                FacebookReplyMessage replyMessage = new FacebookReplyMessage();
                //replyMessage.recipient = message.entry.get(0).messaging.get(0).sender;
                replyMessage.recipient = new FacebookIdentifier();
                replyMessage.recipient.id = "EXAMPLE";
                replyMessage.message = new FacebookMessageContent();
                replyMessage.message.text = "Hello from the bot :-)";

                vertx.eventBus().send(MessengerConnector.SEND_MESSAGE, new Gson().toJson(replyMessage));
                r.response().end();
            });
        }
    }
}
