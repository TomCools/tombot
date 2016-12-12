package be.tomcools.tombot.model;

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

    private String type;
    private AttachementPayload payload;

    public static FacebookMessageAttachment forImage(String image) {
        return FacebookMessageAttachment.builder().type(IMAGE_TYPE).payload(AttachementPayload.builder().url(image).build()).build();
    }
}
