package be.tomcools.tombot.model.facebook.messages.outgoing;

import be.tomcools.tombot.model.facebook.messages.FacebookMessageContent;
import be.tomcools.tombot.model.facebook.messages.attachement.FacebookMessageAttachment;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.util.List;

@Getter
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
