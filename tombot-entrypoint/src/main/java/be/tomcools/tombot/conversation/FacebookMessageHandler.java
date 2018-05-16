package be.tomcools.tombot.conversation;

import be.tomcools.tombot.conversation.context.ConversationContext;
import be.tomcools.tombot.conversation.context.LocationDetail;
import be.tomcools.tombot.conversation.flows.ConversationFlow;
import be.tomcools.tombot.conversation.flows.ConversationFlows;
import be.tomcools.tombot.conversation.flows.HandleResult;
import be.tomcools.tombot.conversation.replies.quickreplies.QuickReply;
import be.tomcools.tombot.conversation.replies.quickreplies.payloads.FlowActivation;
import be.tomcools.tombot.conversation.replies.text.Answers;
import be.tomcools.tombot.conversation.replies.text.Emoticons;
import be.tomcools.tombot.facebook.FacebookContext;
import be.tomcools.tombot.model.facebook.messages.incomming.FacebookIncommingMessageContent;
import be.tomcools.tombot.model.facebook.messages.partials.Coordinates;
import be.tomcools.tombot.tools.JSON;
import lombok.AllArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
public class FacebookMessageHandler {

    private FacebookContext fbContext;
    private ConversationContext conversationContext;

    public void handle() {
        FacebookIncommingMessageContent msg = fbContext.getMessage();
        appendConversationDetails(msg);
        if (conversationContext.hasActiveFlow()) {
            this.handleActiveFlow(msg);
        } else {
            this.handleNewConversation(msg);
        }
    }

    private void handleNewConversation(FacebookIncommingMessageContent msg) {
        if (msg.hasQuickReply() && msg.getQuick_reply().isFlowActivationMessage()) {
            //Activate flow and ask next details
            switchFlowAndContinue(msg);
        } else {
            //Try NLP to determine correct flow or else
            if (msg.isGreeting()) {
                fbContext.sendReply(Answers.answerGreeting() + "How can I help you today?", allFlowOptions());
            } else if (msg.isThanks()) {
                fbContext.sendReply("You are welcome " + Emoticons.BIKE_DRIVING);
            } else {
                fbContext.sendReply("So... what you wanna do?", allFlowOptions());
            }
        }
    }

    private void handleActiveFlow(FacebookIncommingMessageContent msg) {
        if (msg.hasQuickReply() && msg.getQuick_reply().isFlowActivationMessage()) {
            //Request confirmation --> should be confirmation
            switchFlowAndContinue(msg);
        } else if (msg.hasQuickReply() && msg.getQuick_reply().isFlowSwitchConfirmation()) {
            //Got confirmation, so switch and continue with new flow.
            switchFlowAndContinue(msg);
        } else {
            // NORMAL FLOW
            ConversationFlow flow = conversationContext.getActiveFlow();
            tryExecuteFlow(flow);
        }
    }

    private void tryExecuteFlow(ConversationFlow flow) {
        HandleResult flowHandleResult = flow.tryToHandle(fbContext, conversationContext);
        if (flowHandleResult.isSuccess()) {
            //YEEAH!
            if (flow.isComplete()) {
                conversationContext.stopFlow();
            }
        } else {
            //->try nlp to detect  cancel/trolling/flowswitching
            //->Either give up or try backup handling.
            if (flowHandleResult.hasBackupAction()) {
                flowHandleResult.getBackupAction().doIt();
            } else {
                fbContext.sendReply("No idea what to say to this....");
            }
        }
    }

    private void appendConversationDetails(FacebookIncommingMessageContent msg) {
        if (msg.hasAttachments()) {
            msg.getAttachments().forEach(facebookMessageAttachment -> {
                if (facebookMessageAttachment.isLocation()) {
                    Coordinates coordinates = facebookMessageAttachment.getLocation();
                    conversationContext.setLocation(new LocationDetail(Instant.now(), coordinates));
                    msg.setHasLocation(true);
                }
            });
        }
        if (msg.getText() != null && msg.getText().equalsIgnoreCase("Previous Location")) {
            Coordinates previousCoordinates = JSON.fromJson(msg.getQuick_reply().getPayload(), Coordinates.class);
            conversationContext.setLocation(new LocationDetail(Instant.now(), previousCoordinates));
            msg.setHasLocation(true);
        }
    }

    private List<QuickReply> allFlowOptions() {
        return ConversationFlows.all().stream().map(ConversationFlow::getFlowActivator).collect(Collectors.toList());
    }

    private void switchFlowAndContinue(FacebookIncommingMessageContent msg) {
        FlowActivation flowActivation = JSON.fromJson(msg.getQuick_reply().getPayload(), FlowActivation.class);
        ConversationFlow flow = ConversationFlows.forName(flowActivation.getFlowName());
        conversationContext.setActiveFlow(flow);
        tryExecuteFlow(flow);
    }
}
