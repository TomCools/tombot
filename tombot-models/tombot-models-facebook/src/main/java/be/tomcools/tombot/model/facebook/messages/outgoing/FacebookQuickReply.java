package be.tomcools.tombot.model.facebook.messages.outgoing;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FacebookQuickReply {
    private String content_type;
    private String title;
    private String payload;
    private String image_url;
}
