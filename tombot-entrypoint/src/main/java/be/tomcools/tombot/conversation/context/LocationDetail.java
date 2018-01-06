package be.tomcools.tombot.conversation.context;

import be.tomcools.tombot.model.facebook.Coordinates;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
public class LocationDetail {
    private Date retrieved;
    private Coordinates coordinates;
}
