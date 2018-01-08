package be.tomcools.tombot.conversation.context;

import be.tomcools.tombot.conversation.flows.ConversationFlow;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Stack;

@Setter
@Getter
public class ConversationContext {
    private Date lastChat;
    private boolean firstContact;
    private LocationDetail location;
    private ConversationFlow activeFlow;
    private Stack<ConversationFlow> pastFlows;

    public ConversationContext() {
        this.lastChat = new Date();
        this.firstContact = true;
    }

    public boolean hasActiveFlow() {
        return activeFlow != null;
    }

    public void setActiveFlow(ConversationFlow activeFlow) {
        archiveFlow();
        this.activeFlow = activeFlow;
    }

    public void stopFlow() {
        archiveFlow();
    }

    private void archiveFlow() {
        pastFlows.push(this.activeFlow);
        this.activeFlow = null;
    }

    public boolean locationIsNewerThan(int amount, ChronoUnit unit) {
        return getLocation().getRetrieved().isAfter(Instant.now().minus(amount, unit));
    }

    public boolean previousFlowWasNot(ConversationFlow flow) {
        return getPastFlows().peek().getFlowName().equals(flow.getFlowName());
    }
}
