package be.tomcools.tombot.model.facebook;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.awt.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FacebookMessageAttachment {
    private final static String IMAGE_TYPE = "image";
    public final static String LOCATION_TYPE = "location";

    private String type;
    private AttachementPayload payload;

    public static FacebookMessageAttachment forImage(String image) {
        return FacebookMessageAttachment.builder().type(IMAGE_TYPE).payload(AttachementPayload.builder().url(image).build()).build();
    }

    public static FacebookMessageAttachment forLocation(Coordinates location) {
        //SEE https://stackoverflow.com/questions/38017382/how-to-send-location-from-facebook-messenger-platform
        //return FacebookMessageAttachment.builder().type(IMAGE_TYPE).payload(AttachementPayload.builder()).build();
        return null;
    }

    public boolean isLocation() {
        return this.type != null && this.type.equalsIgnoreCase(LOCATION_TYPE);
    }
}
