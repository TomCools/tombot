package be.tomcools.tombot.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FacebookMessage {
    private String object;
    private List<FacebookMessageEntry> entry;
}
