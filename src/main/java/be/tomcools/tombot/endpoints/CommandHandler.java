package be.tomcools.tombot.endpoints;

import io.vertx.core.eventbus.EventBus;
import io.vertx.ext.web.RoutingContext;
import lombok.Builder;

/**
 * Created by tomco on 7/11/2016.
 */
@Builder
public class CommandHandler {
    private EventBus eventbus;

    public void handleRequest(RoutingContext routingContext) {
        String command = routingContext.request().getParam("command");
        if (command == null) {
            System.out.println("Eek! invalid command received");
            routingContext.response().end("ERROR");
        } else {
            routingContext.response().end("Successfully received command: " + command);
        }
    }
}
