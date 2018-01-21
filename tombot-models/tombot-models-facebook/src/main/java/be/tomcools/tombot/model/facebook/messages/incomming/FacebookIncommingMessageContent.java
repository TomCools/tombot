package be.tomcools.tombot.model.facebook.messages.incomming;

import be.tomcools.tombot.model.facebook.messages.FacebookMessageContent;
import be.tomcools.tombot.model.facebook.messages.attachement.FacebookMessageAttachment;
import be.tomcools.tombot.model.facebook.messages.incomming.nlp.FacebookNlp;
import lombok.Builder;
import lombok.Data;
import lombok.Setter;

import java.util.List;

@Data
@Setter
public class FacebookIncommingMessageContent extends FacebookMessageContent {
    //Retrieving message
    private List<FacebookMessageAttachment> attachments;
    private FacebookQuickReplyAnswer quick_reply;
    private FacebookNlp nlp;

    private boolean hasLocation = false;

    @Builder
    public FacebookIncommingMessageContent(String mid, Long seq, String text, List<FacebookMessageAttachment> attachments, FacebookQuickReplyAnswer quick_reply, FacebookNlp nlp) {
        super(mid, seq, text);
        this.attachments = attachments;
        this.quick_reply = quick_reply;
        this.nlp = nlp;
    }

    public boolean hasQuickReply() {
        return quick_reply != null;
    }

    public boolean hasAttachments() {
        return attachments != null && !attachments.isEmpty();
    }

    public boolean hasLocation() {
        return hasLocation;
    }

    public boolean hasNlp() {
        return nlp != null;
    }

    public boolean isGreeting() {
        return hasNlp() && this.nlp.getEntities().containsKey("greetings");
    }

    public boolean isThanks() {
        return hasNlp() && this.nlp.getEntities().containsKey("thanks");
    }
}
