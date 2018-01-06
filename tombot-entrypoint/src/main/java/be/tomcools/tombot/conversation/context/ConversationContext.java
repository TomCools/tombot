package be.tomcools.tombot.conversation.context;

import be.tomcools.tombot.conversation.flows.ConversationFlow;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
public class ConversationContext {
    private Date lastChat;
    private boolean firstContact;
    private LocationDetail location;
    private ConversationFlow flow;

    public ConversationContext() {
        this.lastChat = new Date();
        this.firstContact = true;
    }

    public boolean hasFlow() {
        return flow != null;
    }

    public void setFlow(ConversationFlow flow) {
        if (hasFlow()) {
            flow.complete();
        }
        this.flow = flow;
    }

    public void stopFlow() {
        if (flow.isComplete()) {
            this.flow = null;
        }
    }
}
