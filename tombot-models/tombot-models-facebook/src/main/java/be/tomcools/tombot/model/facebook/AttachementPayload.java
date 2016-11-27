package be.tomcools.tombot.model.facebook;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by tomco on 7/11/2016.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttachementPayload {
    private String url;
}
