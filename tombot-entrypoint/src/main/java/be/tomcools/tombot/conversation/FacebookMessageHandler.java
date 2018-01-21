package be.tomcools.tombot.conversation;

import be.tomcools.tombot.conversation.context.ConversationContext;
import be.tomcools.tombot.conversation.context.LocationDetail;
import be.tomcools.tombot.conversation.flows.ConversationFlow;
import be.tomcools.tombot.conversation.flows.ConversationFlows;
import be.tomcools.tombot.conversation.replies.quickreplies.QuickReply;
import be.tomcools.tombot.conversation.replies.quickreplies.payloads.FlowActivation;
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
        } else {
            if (msg.hasQuickReply() && msg.getQuick_reply().isFlowActivationMessage()) {
                //Activate flow and ask next details
                switchFlowAndContinue(msg);
            } else {
                presentFlowOptions();
            }
        }
    }

    private void tryExecuteFlow(ConversationFlow flow) {
        if (flow.tryToHandle(fbContext, conversationContext).isSuccess()) {
            //YEEAH!
            if (flow.isComplete()) {
                conversationContext.stopFlow();
            }
        } else {
            fbContext.sendReply("No idea what to say to this...");
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

    private void presentFlowOptions() {
        List<QuickReply> quickReplies = ConversationFlows.all().stream().map(ConversationFlow::getFlowActivator).collect(Collectors.toList());
        fbContext.sendReply("What do you want to do?", quickReplies);
    }

    private void switchFlowAndContinue(FacebookIncommingMessageContent msg) {
        FlowActivation flowActivation = JSON.fromJson(msg.getQuick_reply().getPayload(), FlowActivation.class);
        ConversationFlow flow = ConversationFlows.forName(flowActivation.getFlowName());
        conversationContext.setActiveFlow(flow);
        tryExecuteFlow(flow);
    }
}
