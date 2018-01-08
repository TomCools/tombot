package be.tomcools.tombot.model.facebook.messages.attachement.payloads;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImageAttachementPayload implements AttachementPayloadContent{
    private String url;
}
