package be.tomcools.tombot.model.facebook;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public abstract class FacebookMessageContent {
    protected String mid;
    protected Long seq;
    protected String text;
}
