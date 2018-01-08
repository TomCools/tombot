package be.tomcools.tombot.conversation.flows;

import be.tomcools.tombot.conversation.context.ConversationContext;
import be.tomcools.tombot.conversation.replies.quickreplies.QuickReplies;
import be.tomcools.tombot.conversation.replies.quickreplies.QuickReply;
import be.tomcools.tombot.conversation.replies.quickreplies.payloads.FlowActivation;
import be.tomcools.tombot.conversation.replies.text.Answers;
import be.tomcools.tombot.facebook.FacebookContext;
import be.tomcools.tombot.model.facebook.messages.outgoing.FacebookQuickReply;
import be.tomcools.tombot.model.facebook.messages.partials.Coordinates;
import be.tomcools.tombot.tools.JSON;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static be.tomcools.tombot.conversation.replies.quickreplies.QuickReplies.LOCATION;

public abstract class ConversationFlow {

    private Instant timeStarted = Instant.now();
    private Instant timeCompleted;

    public abstract HandleResult tryToHandle(FacebookContext fbContext, ConversationContext conversationContext);

    public boolean isComplete() {
        return timeCompleted != null;
    }

    public void complete() {
        timeCompleted = Instant.now();
    }

    public abstract String getFlowActivatorMessage();

    public abstract String getFlowName();

    public Instant getTimeStarted() {
        return timeStarted;
    }

    public Instant getTimeCompleted() {
        return timeCompleted;
    }

    public QuickReply getFlowActivator() {
        return new QuickReply() {
            @Override
            public FacebookQuickReply getReply() {
                FlowActivation activation = FlowActivation.builder().flowName(getFlowName()).build();
                return FacebookQuickReply.builder().title(getFlowActivatorMessage()).payload(JSON.toJson(activation)).content_type("text").build();
            }
        };
    }

    protected void requestLocation(FacebookContext fbContext, ConversationContext conversationContext) {
        List<QuickReply> quickReplies = new ArrayList<>();
        if (conversationContext.getLocation() != null && conversationContext.getLocation().getRetrieved().isAfter(Instant.now().minusSeconds(300))) {
            //it is less than 5 minutes ago since your last location.
            Coordinates coordinates = conversationContext.getLocation().getCoordinates();
            quickReplies.add(QuickReplies.previousLocation(coordinates));
        }
        quickReplies.add(LOCATION);
        fbContext.sendReply(Answers.askForLocation(), quickReplies);
    }


}
