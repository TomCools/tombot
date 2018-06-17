package be.tomcools.tombot.conversation;

import java.util.HashMap;
import java.util.Map;

public class ConversationContextCache {
    private static Map<String, ConversationContext> contextMap = new HashMap<>();

    public static ConversationContext getConversation(String userId) {
        if (!contextMap.containsKey(userId)) {
            contextMap.put(userId, new ConversationContext());
        }
        return contextMap.get(userId);
    }
}
