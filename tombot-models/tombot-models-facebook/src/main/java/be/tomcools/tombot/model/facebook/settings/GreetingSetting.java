package be.tomcools.tombot.model.facebook.settings;

import lombok.Builder;
import lombok.Data;

@Data
public class GreetingSetting {

    private String setting_type;
    private Greeting greeting;

    @Builder
    public GreetingSetting(String greeting) {
        this.setting_type = "greeting";
        this.greeting = Greeting.builder().text(greeting).build();
    }
}
