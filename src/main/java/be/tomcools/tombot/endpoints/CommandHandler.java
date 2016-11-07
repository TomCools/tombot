package be.tomcools.tombot.endpoints;

import be.tomcools.tombot.model.EventBusConstants;
import be.tomcools.tombot.model.FacebookIdentifier;
import be.tomcools.tombot.model.FacebookMessageContent;
import be.tomcools.tombot.model.FacebookReplyMessage;
import com.google.gson.Gson;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.web.RoutingContext;
import lombok.Builder;

/**
 * Created by tomco on 7/11/2016.
 */
@Builder
public class CommandHandler {
    private EventBus eventbus;

    public void handleRequest(RoutingContext routingContext) {
        String command = routingContext.request().getParam("action");
        if (command == null) {
            System.out.println("Eek! invalid action received");
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

                    eventbus.send(EventBusConstants.SEND_MESSAGE, new Gson().toJson(replyMessage));
                }

            }
        });
    }
}
