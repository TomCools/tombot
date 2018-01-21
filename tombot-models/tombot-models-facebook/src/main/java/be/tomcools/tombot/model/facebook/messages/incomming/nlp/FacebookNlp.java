package be.tomcools.tombot.model.facebook.messages.incomming.nlp;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Builder
public class FacebookNlp {
    private Map<String, List<FacebookNlpEntity>> entities;
}
