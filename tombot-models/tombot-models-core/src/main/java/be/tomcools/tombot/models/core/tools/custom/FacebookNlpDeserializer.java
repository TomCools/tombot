package be.tomcools.tombot.models.core.tools.custom;

import be.tomcools.tombot.model.facebook.messages.incomming.nlp.FacebookNlp;
import be.tomcools.tombot.model.facebook.messages.incomming.nlp.FacebookNlpEntity;
import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FacebookNlpDeserializer implements JsonDeserializer<FacebookNlp> {
    @Override
    public FacebookNlp deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext json) throws JsonParseException {
        JsonObject object = jsonElement.getAsJsonObject();
        JsonObject entitiesField = object.get("entities").getAsJsonObject();
        HashMap<String, List<FacebookNlpEntity>> entityHashMap = new HashMap<>();
        for (Map.Entry<String, JsonElement> entry : entitiesField.entrySet()) {
            JsonArray array = entry.getValue().getAsJsonArray();
            List<FacebookNlpEntity> entitiesListForCertainType = new ArrayList<>();
            for (JsonElement element : array) {
                entitiesListForCertainType.add(json.deserialize(element, FacebookNlpEntity.class));
            }
            entityHashMap.put(entry.getKey(), entitiesListForCertainType);
        }
        return FacebookNlp.builder()
                .entities(entityHashMap)
                .build();
    }
}