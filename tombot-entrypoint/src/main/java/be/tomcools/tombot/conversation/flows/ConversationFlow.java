package be.tomcools.tombot.conversation.flows;

import be.tomcools.tombot.conversation.context.ConversationContext;
import be.tomcools.tombot.conversation.replies.quickreplies.QuickReplies;
import be.tomcools.tombot.conversation.replies.quickreplies.QuickReply;
import be.tomcools.tombot.conversation.replies.quickreplies.payloads.FlowActivation;
import be.tomcools.tombot.conversation.replies.text.Answers;
import be.tomcools.tombot.facebook.FacebookContext;
import be.tomcools.tombot.model.facebook.messages.partials.Coordinates;
import be.tomcools.tombot.tools.ActionFunctionalInterface;
import io.vertx.core.Vertx;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public abstract class ConversationFlow {

    private Vertx vertx = Vertx.vertx();
    private Instant timeStarted = Instant.now();
    private Instant timeStopped;

    public abstract HandleResult tryToHandle(FacebookContext fbContext, ConversationContext conversationContext);

    public boolean isComplete() {
        return timeStopped != null;
    }

    public void complete() {
        timeStopped = Instant.now();
    }

    public abstract String getFlowActivatorMessage();

    public abstract String getFlowName();

    public Instant getTimeStarted() {
        return timeStarted;
    }

    public Instant getTimeStopped() {
        return timeStopped;
    }

    public QuickReply getFlowActivator() {
        return QuickReplies.flowActivation(getFlowActivatorMessage(), flowActivation());
    }

    protected FlowActivation flowActivation() {
        return FlowActivation.builder().flowName(getFlowName()).build();
    }

    protected void requestLocation(FacebookContext fbContext, ConversationContext conversationContext) {
        this.requestLocation(Answers.askForLocation(), fbContext, conversationContext);
    }

    protected void requestLocation(String text, FacebookContext fbContext, ConversationContext conversationContext) {
        List<QuickReply> quickReplies = new ArrayList<>();
        if (conversationContext.hasLocation() && conversationContext.locationIsNewerThan(10, ChronoUnit.MINUTES)) {
            //it is less than 10 minutes ago since your last location.
            Coordinates coordinates = conversationContext.getLocation().getCoordinates();
            quickReplies.add(QuickReplies.previousLocation(coordinates));
        }
        quickReplies.add(QuickReplies.location());
        fbContext.sendReply(text, quickReplies);
    }

    protected void doDelayed(ActionFunctionalInterface handler, int miliseconds) {
        vertx.setTimer(miliseconds, (i) -> handler.doSomething());
    }
}
