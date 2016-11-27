package be.tomcools.tombot.model.facebook;

import be.tomcools.tombot.model.facebook.settings.Payload;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FacebookMessageAttachment {
    private String type;
    private AttachementPayload payload;
}
