package be.tomcools.tombot.conversation.flows;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class HandleResult {
    private boolean isSuccess;
}
