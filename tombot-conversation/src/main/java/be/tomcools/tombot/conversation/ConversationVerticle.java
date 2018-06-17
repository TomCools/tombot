package be.tomcools.tombot.conversation;

import be.tomcools.tombot.model.facebook.messages.FacebookMessageMessaging;
import be.tomcools.tombot.model.facebook.settings.SettingConstants;
import be.tomcools.tombot.models.core.EventBusConstants;
import be.tomcools.tombot.models.core.EventBusWrapper;
import com.google.gson.Gson;
import io.vertx.core.AbstractVerticle;

public class ConversationVerticle extends AbstractVerticle {

    private EventBusWrapper bus;

    @Override
    public void start() throws Exception {
        super.start();

        vertx.eventBus().consumer(EventBusConstants.GET_STARTED, (msg) -> this.handleGettingStarted((String)msg.body()));
        vertx.eventBus().consumer(EventBusConstants.PROCESS_MSG, (msg) -> this.handleFacebookMessage(new Gson().fromJson((String)msg.body(),FacebookMessageMessaging.class)));

        bus = new EventBusWrapper(vertx.eventBus());
    }

    private void handleGettingStarted(String senderId) {
        bus.sendMessage(senderId,"Hi! I am Avelo. I can help you find a bike station for Velo Antwerp!");
    }

    private void handleFacebookMessage(FacebookMessageMessaging facebookContext) {
        //new FacebookContext()
        //ConversationContext conversationContext = ConversationContextCache.getConversation(facebookContext.getSender().getId());
        //this.handleConversation(facebookContext, conversationContext);
    }

   /* private void handleConversation(FacebookContext fbContext, ConversationContext conversationContext) {
        //new FacebookMessageHandler(fbContext, conversationContext).handle();
    }*/
}
