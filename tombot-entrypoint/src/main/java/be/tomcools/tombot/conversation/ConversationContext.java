package be.tomcools.tombot.conversation;

import java.util.Date;

public class ConversationContext {
    private Date lastChat;
    private boolean firstContact;

    public ConversationContext() {
        this.lastChat = new Date();
        this.firstContact = true;
    }

    public Date getLastChat() {
        return lastChat;
    }

    public boolean isFirstContact() {
        return firstContact;
    }
}
