package be.tomcools.tombot.conversation.flows;

import be.tomcools.tombot.conversation.flows.implementations.BikeRetrieveConversationFlow;
import be.tomcools.tombot.conversation.flows.implementations.BikeReturnConversationFlow;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class ConversationFlows {
    final static List<Supplier<ConversationFlow>> FLOWS = Arrays.asList(BikeReturnConversationFlow::new, BikeRetrieveConversationFlow::new);

    public static ConversationFlow forName(String flowName) {
        return FLOWS.stream()
                .filter(f -> f.get().getFlowName().equalsIgnoreCase(flowName))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Flow with name " + flowName + " not found.")).get();
    }

    public static List<ConversationFlow> all() {
        return FLOWS.stream().map(Supplier::get).collect(Collectors.toList());
    }
}
