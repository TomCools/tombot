package be.tomcools.tombot.conversation;

import be.tomcools.tombot.model.facebook.messages.FacebookMessageMessaging;
import be.tomcools.tombot.model.facebook.messages.partials.FacebookIdentifier;
import be.tomcools.tombot.models.core.EventBusWrapper;
import io.vertx.core.AbstractVerticle;

public class ConversationVerticle extends AbstractVerticle {

    private EventBusWrapper bus;

    @Override
    public void start() throws Exception {
        super.start();

        bus = new EventBusWrapper(vertx.eventBus());

        bus.handleGettinStarted(this::handleGettingStarted);
        bus.handleReceivedMsg(this::handleFacebookMessage);
    }

    private void handleGettingStarted(FacebookIdentifier senderId) {
        bus.sendMessage(senderId, "Hi! I am Avelo. I can help you find a bike station for Velo Antwerp!");
    }

    private void handleFacebookMessage(FacebookMessageMessaging incomingMessage) {
        ConversationContext conversationContext = ConversationContextCache.getConversation(incomingMessage.getSender().getId());
        FacebookContext context = FacebookContext.builder()
                .eventBus(bus)
                .message(incomingMessage)
                .build();
        this.handleConversation(context, conversationContext);
    }

    private void handleConversation(FacebookContext fbContext, ConversationContext conversationContext) {
        FacebookMessageHandler.builder()
                .fbContext(fbContext)
                .conversationContext(conversationContext)
                .build()
                .handle();
    }
}
