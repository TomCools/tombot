package be.tomcools.tombot.model.facebook.settings;

import com.google.gson.annotations.SerializedName;
import lombok.Builder;
import lombok.Data;

@Data
public class TargetAudienceSettings {
    @SerializedName("audience_type")
    private String audienceType;
    private TargetAudienceCountries countries;

    @Builder
    TargetAudienceSettings(AudienceType audienceType, TargetAudienceCountries countries) {
        this.audienceType = audienceType.typeName;
        this.countries = countries;
    }

    enum AudienceType {
        ALL("all"), NONE("none"), CUSTOM("custom");

        private String typeName;

        AudienceType(String typeName) {
            this.typeName = typeName;
        }
    }
}
