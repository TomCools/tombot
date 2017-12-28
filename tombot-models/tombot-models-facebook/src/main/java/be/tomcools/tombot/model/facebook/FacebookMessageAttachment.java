package be.tomcools.tombot.model.facebook;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    public boolean isLocation() {
        return this.type != null && this.type.equalsIgnoreCase(LOCATION_TYPE);
    }
}
