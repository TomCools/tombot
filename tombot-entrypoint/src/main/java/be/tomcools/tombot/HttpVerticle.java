package be.tomcools.tombot;

import be.tomcools.tombot.command.CommandHandler;
import be.tomcools.tombot.endpoints.FacebookWebhook;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

public class HttpVerticle extends AbstractVerticle {
    private static final Logger LOG = LoggerFactory.getLogger(HttpVerticle.class);

    @Override
    public void start() throws Exception {
        Router router = Router.router(vertx);
        router.exceptionHandler(throwable -> System.err.println(throwable.getCause() + ":" + throwable.getMessage()));
        router.route("/webhook").handler(FacebookWebhook.builder().eventbus(vertx.eventBus()).build()::webhookRequestHandler);
        router.route("/command/*").handler(CommandHandler.builder().eventbus(vertx.eventBus()).build()::handleRequest);
        router.route("/*").handler(this::isAlive);

        Integer portNumber = config().getInteger("http.port", 80);

        vertx.createHttpServer()
                .requestHandler(router::accept)
                .listen(portNumber);
        LOG.debug("Hi :-) Bot has started on port: " + portNumber);
    }

    private void isAlive(RoutingContext routingContext) {
        routingContext.request().response().end("I'm Alive!");
    }
}
