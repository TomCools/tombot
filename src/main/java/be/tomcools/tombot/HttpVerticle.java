package be.tomcools.tombot;

import be.tomcools.tombot.endpoints.FacebookWebhook;
import be.tomcools.tombot.model.EventBusConstants;
import be.tomcools.tombot.model.settings.GreetingSetting;
import be.tomcools.tombot.model.settings.StartedButton;
import com.google.gson.Gson;
import io.vertx.core.AbstractVerticle;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

/**
 * Created by tomco on 7/11/2016.
 */
public class HttpVerticle extends AbstractVerticle {

    @Override
    public void start() throws Exception {
        Router router = Router.router(vertx);
        router.route("/webhook").handler(FacebookWebhook.builder().eventbus(vertx.eventBus()).build()::webhookRequestHandler);
        router.route("/*").handler(this::isAlive);

        vertx.createHttpServer()
                .requestHandler(router::accept)
                .listen(9999);
        System.out.println("Hi :-) Bot has started.");
    }

    private void isAlive(RoutingContext routingContext) {
        routingContext.request().response().end("I'm Alive!");
    }
}
