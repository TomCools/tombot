package be.tomcools.tombot.model.facebook;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FacebookMessageContent {
    private String mid;
    private Long seq;
    private String text;
    private FacebookMessageAttachment attachment;
    private List<FacebookQuickReply> quick_replies;
    private FacebookQuickReplyAnswer quick_reply;

    public boolean hasQuickReply() {
        return quick_reply != null;
    }
    public boolean hasAttachment() {
        return attachment != null;
    }
}
