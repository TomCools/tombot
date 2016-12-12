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
public class FacebookMessageDelivery {
    private List<String> mids;
    private Long watermark;
    private Long seq;
}
