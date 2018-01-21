package be.tomcools.tombot.conversation.context;

import be.tomcools.tombot.conversation.flows.ConversationFlow;
import be.tomcools.tombot.conversation.replies.quickreplies.QuickReplies;
import be.tomcools.tombot.conversation.replies.quickreplies.QuickReply;
import be.tomcools.tombot.model.facebook.messages.partials.Coordinates;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
        this.pastFlows = new Stack<>();
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
        if (this.activeFlow != null) {
            pastFlows.push(this.activeFlow);
            this.activeFlow = null;
        }
    }

    public boolean locationIsNewerThan(int amount, ChronoUnit unit) {
        return hasLocation() && getLocation().getRetrieved().isAfter(Instant.now().minus(amount, unit));
    }

    public boolean locationIsOlderThan(int amount, ChronoUnit unit) {
        return !locationIsNewerThan(amount, unit);
    }

    public boolean previousFlowWasNot(ConversationFlow flow) {
        return !previousFlowWas(flow);
    }

    public boolean previousFlowWas(ConversationFlow flow) {
        return !getPastFlows().empty() && getPastFlows().peek().getFlowName().equals(flow.getFlowName());
    }

    public boolean hasLocation() {
        return getLocation() != null;
    }

    public List<QuickReply> requestLocationQuickReplies(ConversationContext conversationContext) {
        List<QuickReply> quickReplies = new ArrayList<>();
        if (conversationContext.hasLocation() && conversationContext.locationIsNewerThan(10, ChronoUnit.MINUTES)) {
            //it is less than 10 minutes ago since your last location.
            Coordinates coordinates = conversationContext.getLocation().getCoordinates();
            quickReplies.add(QuickReplies.previousLocation(coordinates));
        }
        quickReplies.add(QuickReplies.location());
        return quickReplies;
    }
}
