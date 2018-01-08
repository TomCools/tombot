package be.tomcools.tombot.conversation.context;

import be.tomcools.tombot.model.facebook.messages.partials.Coordinates;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;

@Data
@AllArgsConstructor
public class LocationDetail {
    private Instant retrieved;
    private Coordinates coordinates;
}
