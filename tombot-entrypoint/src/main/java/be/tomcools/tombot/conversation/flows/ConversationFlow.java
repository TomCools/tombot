package be.tomcools.tombot.conversation.flows;

import be.tomcools.tombot.conversation.answering.Answers;
import be.tomcools.tombot.conversation.context.ConversationContext;
import be.tomcools.tombot.conversation.quickreplies.QuickReplies;
import be.tomcools.tombot.conversation.quickreplies.QuickReply;
import be.tomcools.tombot.conversation.quickreplies.payloads.FlowActivation;
import be.tomcools.tombot.endpoints.FacebookContext;
import be.tomcools.tombot.model.facebook.Coordinates;
import be.tomcools.tombot.model.facebook.FacebookQuickReply;
import be.tomcools.tombot.tools.JSON;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static be.tomcools.tombot.conversation.quickreplies.QuickReplies.LOCATION;

public abstract class ConversationFlow {

    private boolean isComplete;

    public abstract HandleResult tryToHandle(FacebookContext fbContext, ConversationContext conversationContext);

    public boolean isComplete() {
        return isComplete;
    }

    public void complete() {
        isComplete = true;
    }

    abstract String getFlowActivatorMessage();

    abstract String getFlowName();

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
