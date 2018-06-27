package be.tomcools.tombot.conversation;

import be.tomcools.tombot.model.facebook.messages.FacebookMessageMessaging;
import be.tomcools.tombot.model.facebook.messages.partials.FacebookIdentifier;
import be.tomcools.tombot.models.core.EventBusWrapper;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.vertx.core.AbstractVerticle;

import java.io.File;
import java.util.stream.Stream;

public class ConversationVerticle extends AbstractVerticle {

    private EventBusWrapper bus;

    @Override
    public void start() throws Exception {
        super.start();

        ObjectMapper mapper = new ObjectMapper(new JsonFactory());
        ConversationVerticle result = mapper.readValue(new File(""), this.getClass());


        bus = new EventBusWrapper(vertx.eventBus());

        bus.handleGettinStarted(this::handleGettingStarted);
        bus.handleReceivedMsg(this::handleFacebookMessage);
    }

    private void handleGettingStarted(FacebookIdentifier senderId) {
        bus.sendMessage(senderId, "Hi! I am Avelo. I can help you find a bike station for Velo Antwerp!");
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
