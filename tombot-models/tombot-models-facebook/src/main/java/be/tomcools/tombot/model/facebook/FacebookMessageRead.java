package be.tomcools.tombot.model.facebook;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FacebookMessageRead {
    private Long watermark;
    private Long seq;
}
