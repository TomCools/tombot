package be.tomcools.tombot.model.settings;

import java.util.Arrays;
import java.util.List;

public class StartedButton {
    private String setting_type = "call_to_actions";
    private String thread_state = "new_thread";
    private List<Payload> call_to_actions = Arrays.asList(new Payload(SettingConstants.GET_STARTED));
}
