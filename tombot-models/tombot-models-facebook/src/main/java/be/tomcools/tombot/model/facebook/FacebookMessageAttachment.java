package be.tomcools.tombot.model.facebook;

import be.tomcools.tombot.model.facebook.attachement.AttachementPayloadContent;
import be.tomcools.tombot.model.facebook.attachement.AttachementTypes;
import be.tomcools.tombot.model.facebook.attachement.ImageAttachementPayload;
import be.tomcools.tombot.model.facebook.attachement.LocationAttachementPayload;
import be.tomcools.tombot.model.facebook.attachement.template.TemplateAttachement;
import be.tomcools.tombot.model.facebook.attachement.template.TemplateElement;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FacebookMessageAttachment {
    private final static String GOOGLE_LOCATION_URL_FORMAT = "https://maps.googleapis.com/maps/api/staticmap?size=764x400&center=%s,%s&zoom=25&markers=%s,%s";
    private final static String APPLE_LOCATION_URL_FORMAT = "http://maps.apple.com/maps?q=%s,%s&z=16";

    private String type;
    private AttachementPayloadContent payload;

    public static FacebookMessageAttachment forImage(String image) {
        return FacebookMessageAttachment.builder().type(AttachementTypes.IMAGE.getName()).payload(ImageAttachementPayload.builder().url(image).build()).build();
    }

    public static FacebookMessageAttachment forLocation(Coordinates location, String title) {
        //SEE https://stackoverflow.com/questions/38017382/how-to-send-location-from-facebook-messenger-platform
        return FacebookMessageAttachment.builder().type(AttachementTypes.TEMPLATE.getName()).payload(
                TemplateAttachement.builder()
                        .templateType("generic")
                        .element(TemplateElement.builder()
                                .title(title)
                                .imageUrl(String.format(GOOGLE_LOCATION_URL_FORMAT, location.getLatitude(), location.getLongitude(), location.getLatitude(), location.getLongitude()))
                                .itemUrl(String.format(APPLE_LOCATION_URL_FORMAT, location.getLatitude(), location.getLongitude()))
                                .build())
                        .build()
        ).build();
    }

    public Coordinates getLocation() {
        if (isLocation()) {
            return ((LocationAttachementPayload) payload).getCoordinates();
        } else {
            throw new IllegalArgumentException("Payload is not a location. Please check if it is a location before getting the location.");
        }
    }

    public boolean isLocation() {
        return this.type != null && this.type.equalsIgnoreCase(AttachementTypes.LOCATION.getName());
    }
}
