package be.tomcools.tombot.conversation.context;

import be.tomcools.tombot.conversation.flows.ConversationFlow;
import be.tomcools.tombot.conversation.flows.HandleResult;
import be.tomcools.tombot.facebook.FacebookContext;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ConversationContextTest {

    @Test
    public void givenNoPreviousFlow_checkingPreviousFlowWasSame_resultIsFalse() {
        ConversationContext context = new ConversationContext();

        boolean previousFlowWas = context.previousFlowWas(new FirstTestConversationFlow());

        assertThat(previousFlowWas, is(false));
    }

    @Test
    public void givenSamePreviousFlow_checkingPreviousFlowWasSame_resultIsFalse() {
        ConversationContext context = new ConversationContext();
        context.setActiveFlow(new FirstTestConversationFlow());
        context.setActiveFlow(new FirstTestConversationFlow());

        boolean previousFlowWas = context.previousFlowWas(new FirstTestConversationFlow());

        assertThat(previousFlowWas, is(true));
    }

    class FirstTestConversationFlow extends ConversationFlow {

        @Override
        public HandleResult tryToHandle(FacebookContext fbContext, ConversationContext conversationContext) {
            return HandleResult.builder().isSuccess(true).build();
        }

        @Override
        public String getFlowActivatorMessage() {
            return "FIRST";
        }

        @Override
        public String getFlowName() {
            return "FIRST_TEST";
        }
    }

}