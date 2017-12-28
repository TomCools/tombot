package be.tomcools.tombot;

import be.tomcools.tombot.conversation.ConversationContextVerticle;
import be.tomcools.tombot.velo.VeloData;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;

public class VertxStarter extends AbstractVerticle {

    public static void main(String[] args) {
        Vertx.vertx().deployVerticle(VertxStarter.class.getName());
    }

    @Override
    public void start() throws Exception {
        vertx.deployVerticle(MessengerConnector.class.getName(),new DeploymentOptions().setInstances(10));
        vertx.deployVerticle(HttpVerticle.class.getName(), new DeploymentOptions().setInstances(10));
        vertx.deployVerticle(VeloData.class.getName());
        vertx.deployVerticle(ConversationContextVerticle.class.getName(),new DeploymentOptions().setInstances(10));
        //vertx.deployVerticle(UserProfileVerticle.class.getName());
        //vertx.deployVerticle(WitAiConnector.class.getName());
    }
}
