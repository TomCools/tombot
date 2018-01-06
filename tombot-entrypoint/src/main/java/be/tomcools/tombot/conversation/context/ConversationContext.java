package be.tomcools.tombot.conversation.context;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
public class ConversationContext {
    private Date lastChat;
    private boolean firstContact;
    private LocationDetail location;

    public ConversationContext() {
        this.lastChat = new Date();
        this.firstContact = true;
    }
}
