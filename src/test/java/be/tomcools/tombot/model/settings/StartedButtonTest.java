package be.tomcools.tombot.model.settings;

import com.google.gson.Gson;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * Created by tomco on 7/11/2016.
 */
public class StartedButtonTest {

    @Test
    public void whenTurnedIntoJson_createsExpectedJsonSettings() {
        StartedButton button = new StartedButton();

        String json = new Gson().toJson(button);

        assertThat(json, is("{" +
                "\"setting_type\":\"call_to_actions\"," +
                "\"thread_state\":\"new_thread\"," +
                "\"call_to_actions\":[" +
                "{" +
                "\"payload\":\"GET_STARTED\"" +
                "}" +
                "]}"));
    }

}