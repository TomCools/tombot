package be.tomcools.tombot.conversation.flows.implementations;

import be.tomcools.tombot.conversation.ConversationContext;
import be.tomcools.tombot.conversation.FacebookContext;
import be.tomcools.tombot.conversation.flows.ConversationFlow;
import be.tomcools.tombot.conversation.flows.HandleResult;

public class ShowMeSomeDataConversationFlow extends ConversationFlow {

    @Override
    public String getFlowActivatorMessage() {
        return "Show Velo Data";
    }

    @Override
    public String getFlowName() {
        return "SHOW_VELO_DATA";
    }

    @Override
    public HandleResult tryToHandle(FacebookContext fbContext, ConversationContext convo) {
       // VeloData.getVeloAnalytics(fbContext::sendReply);
        complete();
        return HandleResult.SUCCES;
    }

}
