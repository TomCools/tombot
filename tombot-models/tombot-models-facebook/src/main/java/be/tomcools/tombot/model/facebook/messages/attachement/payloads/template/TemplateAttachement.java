package be.tomcools.tombot.model.facebook.messages.attachement.payloads.template;

import be.tomcools.tombot.model.facebook.messages.attachement.payloads.AttachementPayloadContent;
import com.google.gson.annotations.SerializedName;
import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TemplateAttachement implements AttachementPayloadContent {
    @SerializedName("template_type")
    private String templateType;

    @Singular
    private List<TemplateElement> elements;
}
