package be.tomcools.tombot.model.facebook.settings;

import lombok.Builder;
import lombok.Data;

import java.util.List;

//https://developers.facebook.com/docs/messenger-platform/reference/messenger-profile-api/target-audience
@Data
@Builder
public class TargetAudienceCountries {
    private List<String> blacklist;
    private List<String> whitelist;
}
