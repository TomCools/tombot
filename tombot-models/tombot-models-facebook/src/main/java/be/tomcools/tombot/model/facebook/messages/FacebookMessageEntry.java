package be.tomcools.tombot.model.facebook.messages;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FacebookMessageEntry {
    private String id;
    private Long time;
    private List<FacebookMessageMessaging> messaging;

    public List<FacebookMessageMessaging> getMessaging() {
        if (messaging == null) {
            return new ArrayList<>();
        } else {
            return messaging.stream().filter(Objects::nonNull).collect(Collectors.toList());
        }
    }
}
