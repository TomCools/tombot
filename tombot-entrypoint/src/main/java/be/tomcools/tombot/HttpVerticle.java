package be.tomcools.tombot;

import be.tomcools.tombot.facebook.FacebookWebhook;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Router;

import java.time.Instant;

public class HttpVerticle extends AbstractVerticle {
    private static final Logger LOG = LoggerFactory.getLogger(HttpVerticle.class);

    @Override
    public void start() throws Exception {
        Router router = Router.router(vertx);
        router.exceptionHandler(throwable -> LOG.error(throwable.getCause() + ":" + throwable.getMessage()));
        router.route("/webhook").handler(FacebookWebhook.builder().eventbus(vertx.eventBus()).build()::webhookRequestHandler);

        Instant startupTime = Instant.now();
        router.route("/*").handler(r -> {
            r.request().response().end("I'm Alive! since: " + startupTime.toString());
        });

        Integer portNumber = config().getInteger("http.port", 80);

        vertx.createHttpServer()
                .requestHandler(router::accept)
                .listen(portNumber);
        LOG.info("Hi :-) Bot has started on port: " + portNumber);
    }
}
