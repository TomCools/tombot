package be.tomcools.tombot.model;

import com.google.gson.Gson;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

public class FacebookMessageTest {

    @Test
    public void canTransformJsonStringIntoObject() {
        String example = "{\n" +
                "  \"object\":\"page\",\n" +
                "  \"entry\":[\n" +
                "    {\n" +
                "      \"id\":\"PAGE_ID\",\n" +
                "      \"time\":1458692752478,\n" +
                "      \"messaging\":[\n" +
                "        {\n" +
                "          \"sender\":{\n" +
                "            \"id\":\"USER_ID\"\n" +
                "          },\n" +
                "          \"recipient\":{\n" +
                "            \"id\":\"PAGE_ID\"\n" +
                "          }\n"+
                "        }\n" +
                "      ]\n" +
                "    }\n" +
                "  ]\n" +
                "}";


        FacebookMessage facebookMessage = new Gson().fromJson(example, FacebookMessage.class);

        assertThat(facebookMessage.getObject(), is("page"));

        FacebookMessageEntry entry = facebookMessage.getEntry().get(0);
        assertThat(entry.getId(), is("PAGE_ID"));
        assertThat(entry.getTime(), is(1458692752478L));

        FacebookMessageMessaging messaging = entry.getMessaging().get(0);
        assertThat(messaging.getSender().getId(), is("USER_ID"));
        assertThat(messaging.getRecipient().getId(), is("PAGE_ID"));
    }
}