package be.tomcools.tombot.model.user;

import lombok.Builder;
import lombok.Getter;

/**
 * Created by tomco on 7/11/2016.
 */
@Builder
@Getter
public class UserDetails {
    private String first_name;
    private String last_name;
    private String profile_picture;
    private String locale;
    private String gender;

    public boolean isMale() {
        return "MALE".equalsIgnoreCase(gender);
    }
}
