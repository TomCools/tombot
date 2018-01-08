package be.tomcools.tombot.model.facebook.messages.partials;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Coordinates {
    @SerializedName("lat")
    Double latitude;
    @SerializedName("long")
    Double longitude;
}
