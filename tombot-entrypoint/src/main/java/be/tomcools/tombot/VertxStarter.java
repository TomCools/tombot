package be.tomcools.tombot;

import be.tomcools.tombot.conversation.context.ConversationContext;
import be.tomcools.tombot.tools.codecs.JsonCodec;
import be.tomcools.tombot.velo.VeloData;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;

public class VertxStarter extends AbstractVerticle {

    public static void main(String[] args) {
        Vertx.vertx().deployVerticle(VertxStarter.class.getName());
    }

    @Override
    public void start() throws Exception {
        vertx.eventBus().registerDefaultCodec(ConversationContext.class, JsonCodec.forClass(ConversationContext.class));

        vertx.deployVerticle(MessengerConnector.class.getName());
        vertx.deployVerticle(HttpVerticle.class.getName());
        vertx.deployVerticle(VeloData.class.getName());
        //vertx.deployVerticle(UserProfileVerticle.class.getName());
        //vertx.deployVerticle(WitAiConnector.class.getName());
    }
}
