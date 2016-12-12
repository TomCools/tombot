package be.tomcools.tombot.command;

import be.tomcools.tombot.model.core.EventBusConstants;
import be.tomcools.tombot.model.facebook.FacebookIdentifier;
import be.tomcools.tombot.model.facebook.FacebookMessageContent;
import be.tomcools.tombot.model.facebook.FacebookReplyMessage;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.RoutingContext;
import lombok.Builder;

/**
 * Created by tomco on 7/11/2016.
 */
@Builder
public class CommandHandler {
    private static final Logger LOG = LoggerFactory.getLogger(CommandHandler.class);

    private EventBus eventbus;

    public void handleRequest(RoutingContext routingContext) {
        String command = routingContext.request().getParam("action");
        if (command == null) {
            LOG.warn("Eek! invalid action received");
            routingContext.response().end("ERROR");
        } else {
            handleMessageAllEvent(routingContext);

        }
    }

    private void handleMessageAllEvent(RoutingContext routingContext) {
        String message = routingContext.request().getParam("message");
        routingContext.response().end("Successfully received command to message all known users.");

        eventbus.send(EventBusConstants.GET_ALL_CLIENT_IDS, "", handler -> {
            if (handler.succeeded()) {
                JsonArray result = new JsonArray(handler.result().body().toString());
                for (Object i : result) {
                    FacebookReplyMessage replyMessage = FacebookReplyMessage.builder()
                            .recipient(FacebookIdentifier.builder().id(i.toString()).build())
                            .message(FacebookMessageContent.builder().text("Someone trigger me to spam you :( Sorry, i have no control...: " + message)
                                    .build())
                            .build();

                    eventbus.send(EventBusConstants.SEND_MESSAGE, Json.encode(replyMessage));
                }

            }
        });
    }
}


//http://tomcools.cloudapp.net:9999/command?action=%22test%27&message=%22SomeSpamMessage%22