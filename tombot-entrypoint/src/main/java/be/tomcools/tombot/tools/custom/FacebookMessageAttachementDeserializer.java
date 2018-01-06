package be.tomcools.tombot.tools.custom;

import be.tomcools.tombot.model.facebook.FacebookMessageAttachment;
import be.tomcools.tombot.model.facebook.attachement.AttachementPayloadContent;
import be.tomcools.tombot.model.facebook.attachement.AttachementTypes;
import com.google.gson.*;

import java.lang.reflect.Type;

public class FacebookMessageAttachementDeserializer implements JsonDeserializer<FacebookMessageAttachment> {
    @Override
    public FacebookMessageAttachment deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext json) throws JsonParseException {
        JsonObject object = jsonElement.getAsJsonObject();
        JsonElement typeField = object.get("type");
        JsonElement payload = object.get("payload");
        if (typeField != null && typeField.isJsonPrimitive()) {
            String payloadTypeString = typeField.getAsString();
            AttachementTypes attachementType = AttachementTypes.forName(payloadTypeString);
            AttachementPayloadContent payloadContent = json.deserialize(payload, attachementType.getType());
            return FacebookMessageAttachment.builder()
                    .type(payloadTypeString)
                    .payload(payloadContent)
                    .build();
        } else {
            return null;
        }
    }
}