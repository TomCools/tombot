package be.tomcools.tombot.model.facebook.messages.incomming;

import be.tomcools.tombot.model.facebook.messages.FacebookMessageContent;
import be.tomcools.tombot.model.facebook.messages.attachement.FacebookMessageAttachment;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
public class FacebookIncommingMessageContent extends FacebookMessageContent {
    //Retrieving message
    private List<FacebookMessageAttachment> attachments;
    private FacebookQuickReplyAnswer quick_reply;

    @Builder
    public FacebookIncommingMessageContent(String mid, Long seq, String text, List<FacebookMessageAttachment> attachments, FacebookQuickReplyAnswer quick_reply) {
        super(mid, seq, text);
        this.attachments = attachments;
        this.quick_reply = quick_reply;
    }

    public boolean hasQuickReply() {
        return quick_reply != null;
    }

    public boolean hasAttachments() {
        return attachments != null && !attachments.isEmpty();
    }
}