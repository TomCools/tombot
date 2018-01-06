package be.tomcools.tombot.model.facebook;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
public class FacebookOutgoingMessageContent extends FacebookMessageContent {
    //sending message
    private FacebookMessageAttachment attachment;
    private List<FacebookQuickReply> quick_replies;

    @Builder
    public FacebookOutgoingMessageContent(String mid, Long seq, String text, FacebookMessageAttachment attachment, List<FacebookQuickReply> quick_replies) {
        super(mid, seq, text);
        this.attachment = attachment;
        this.quick_replies = quick_replies;
    }
}
