package be.tomcools.tombot.conversation;

import be.tomcools.tombot.model.core.EventBusConstants;
import be.tomcools.tombot.tools.codecs.JsonCodec;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.Message;

import java.util.HashMap;
import java.util.Map;

public class ConversationContextVerticle extends AbstractVerticle {
    private Map<String, ConversationContext> contextMap;

    @Override
    public void start() throws Exception {
        contextMap = new HashMap<>();
        vertx.eventBus().consumer(EventBusConstants.GET_CONVERSATION_CONTEXT, this::getConversation);
    }

    private void getConversation(Message<String> userIdMessage) {
        String userId = userIdMessage.body();
        if (!contextMap.containsKey(userId)) {
            contextMap.put(userId, new ConversationContext());
        }
        userIdMessage.reply(contextMap.get(userId));
    }
}
