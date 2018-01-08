package be.tomcools.tombot.model.facebook.messages.attachement.payloads;

import be.tomcools.tombot.model.facebook.messages.partials.Coordinates;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LocationAttachementPayload implements AttachementPayloadContent{
    private Coordinates coordinates;
}
