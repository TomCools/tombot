package be.tomcools.tombot.conversation.flows.implementations;

import be.tomcools.tombot.conversation.context.ConversationContext;
import be.tomcools.tombot.conversation.flows.ConversationFlow;
import be.tomcools.tombot.conversation.flows.HandleResult;
import be.tomcools.tombot.facebook.FacebookContext;
import be.tomcools.tombot.velo.VeloData;

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
        VeloData.getVeloAnalytics(fbContext::sendReply);
        complete();
        return HandleResult.SUCCES;
    }

}
