package be.tomcools.tombot.witai;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class NlpResponse {
    private Intents intent;
    private String fullReply;
}
