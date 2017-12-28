package be.tomcools.tombot.model.facebook;

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
    Double lat;
    @SerializedName("long")
    Double longitude;
}
