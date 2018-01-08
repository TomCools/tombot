package be.tomcools.tombot.model.facebook.messages.attachement.payloads.template;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TemplateElement {
    private String title;
    @SerializedName("image_url")
    private String imageUrl;
    @SerializedName("item_url")
    private String itemUrl;
}
