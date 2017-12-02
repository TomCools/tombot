package be.tomcools.tombot;

import be.tomcools.tombot.model.core.EventBusConstants;
import be.tomcools.tombot.model.facebook.settings.GreetingSetting;
import be.tomcools.tombot.model.facebook.settings.StartedButton;
import be.tomcools.tombot.tools.JSON;
import be.tomcools.tombot.userprofile.UserProfileVerticle;
import be.tomcools.tombot.velo.VeloData;
import be.tomcools.tombot.witai.WitAiConnector;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;

public class VertxStarter extends AbstractVerticle {

    public static void main(String[] args) {
        Vertx.vertx().deployVerticle(VertxStarter.class.getName());
    }

    @Override
    public void start() throws Exception {
        vertx.deployVerticle(MessengerConnector.class.getName());
        vertx.deployVerticle(HttpVerticle.class.getName());
        vertx.deployVerticle(VeloData.class.getName());
        //vertx.deployVerticle(UserProfileVerticle.class.getName());
        //vertx.deployVerticle(WitAiConnector.class.getName());
    }
}
