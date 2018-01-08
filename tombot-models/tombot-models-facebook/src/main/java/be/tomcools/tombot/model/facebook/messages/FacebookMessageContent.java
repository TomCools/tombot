package be.tomcools.tombot.model.facebook.messages;

import lombok.Getter;

@Getter
public abstract class FacebookMessageContent {
    protected String mid;
    protected Long seq;
    protected String text;

    public FacebookMessageContent(String mid, Long seq, String text) {
        this.mid = mid;
        this.seq = seq;
        this.text = text;
    }
}
