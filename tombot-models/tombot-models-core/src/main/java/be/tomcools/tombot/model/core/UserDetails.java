package be.tomcools.tombot.model.core;

import lombok.Builder;
import lombok.Getter;

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
