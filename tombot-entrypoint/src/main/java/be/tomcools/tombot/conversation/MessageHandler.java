package be.tomcools.tombot.conversation;

import be.tomcools.tombot.conversation.context.ConversationContext;
import be.tomcools.tombot.endpoints.FacebookContext;

public interface MessageHandler {
    void handle(FacebookContext fbContext, ConversationContext conversationContext);
}
