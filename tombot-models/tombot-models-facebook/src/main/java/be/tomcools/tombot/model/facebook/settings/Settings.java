package be.tomcools.tombot.model.facebook.settings;

import com.google.gson.annotations.SerializedName;
import lombok.Builder;
import lombok.Getter;
import lombok.Singular;

import java.util.List;

@Builder
@Getter
public class Settings {
    @Singular("greeting")
    @SerializedName("greeting")
    private List<Greeting> greeting;
    @SerializedName("account_linking_url")
    private String accountLinkingUrl;
    /*@SerializedName("persistent_menu")
    Not implemented yet
    */
    @SerializedName("payment_settings")
    private PaymentSettings paymentSettings;
    @SerializedName("get_started")
    private Payload gettingStartedPayload;
    @SerializedName("whitelisted_domains")
    private List<String> whitelistedDomains;
    @SerializedName("target_audience")
    private TargetAudienceSettings targetAudience;
    /* For Chat Extension Only
    @SerializedName("home_url")
    private HomeUrlSetting homeUrl;
    */
}
