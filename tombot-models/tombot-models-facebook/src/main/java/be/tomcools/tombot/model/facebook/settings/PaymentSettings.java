package be.tomcools.tombot.model.facebook.settings;

import com.google.gson.annotations.SerializedName;
import lombok.Builder;
import lombok.Data;
import lombok.Singular;

import java.util.List;

@Builder
@Data
public class PaymentSettings {
    @SerializedName("privacy_url")
    private String privacyUrl;
    @SerializedName("public_key")
    private String publicKey;
    @Singular
    private List<Integer> testers;
}
