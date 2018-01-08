package be.tomcools.tombot.model.facebook.messages.incomming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FacebookQuickReplyAnswer {
    private String payload;

    public boolean isFlowActivationMessage() {
        return payload.contains("FLOW_ACTIVATION");
    }

    public boolean isFlowSwitchConfirmation() {
        return payload.contains("FLOW_SWITCH_CONFIRMATION");
    }
}
