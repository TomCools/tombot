package be.tomcools.tombot.model.facebook.messages.attachement;

import be.tomcools.tombot.model.facebook.messages.attachement.payloads.ImageAttachementPayload;
import be.tomcools.tombot.model.facebook.messages.attachement.payloads.LocationAttachementPayload;
import be.tomcools.tombot.model.facebook.messages.attachement.payloads.template.TemplateAttachement;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum AttachementTypes {
    LOCATION("location", LocationAttachementPayload.class),
    IMAGE("image", ImageAttachementPayload.class),
    TEMPLATE("template", TemplateAttachement.class);

    private Class type;
    private String name;

    AttachementTypes(String name, Class<?> templateAttachementClass) {
        this.name = name;
        this.type = templateAttachementClass;
    }

    public static AttachementTypes forName(String nameOfType) {
        return Arrays.stream(values())
                .filter(t -> t.name.equalsIgnoreCase(nameOfType))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No known attachment type for : " + nameOfType));
    }
}
