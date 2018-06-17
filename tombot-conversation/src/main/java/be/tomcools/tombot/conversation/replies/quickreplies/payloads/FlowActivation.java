package be.tomcools.tombot.conversation.replies.quickreplies.payloads;

import lombok.Builder;
import lombok.Data;

@Data
public class FlowActivation {
    private String type;
    private String flowName;

    @Builder
    public FlowActivation(String flowName) {
        this.type = "FLOW_ACTIVATION";
        this.flowName = flowName;
    }
}
