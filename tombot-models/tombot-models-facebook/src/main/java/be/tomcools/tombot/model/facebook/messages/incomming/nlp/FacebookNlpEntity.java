package be.tomcools.tombot.model.facebook.messages.incomming.nlp;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FacebookNlpEntity {
    private Double confidence;
    private String value;
}
