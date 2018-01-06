package be.tomcools.tombot.conversation.quickreplies.payloads;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FlowActivation {
    private String type = "FLOW_ACTIVATION";
    private String flowName;
}
