package be.tomcools.tombot;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;

public class VertxStarter extends AbstractVerticle {

    public static void main(String[] args) {
        Vertx.vertx().deployVerticle(VertxStarter.class.getName());
    }

    @Override
    public void start() throws Exception {
        super.start();

        vertx.createHttpServer()
                .requestHandler(r -> r.bodyHandler(b -> {
                            String stringBody = b.toString();
                            r.response().end("From Vertx: " + stringBody);
                        }
                ))
                .listen(9999);
    }
}
