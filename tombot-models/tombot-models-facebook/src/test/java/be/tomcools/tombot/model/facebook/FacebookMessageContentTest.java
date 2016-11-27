package be.tomcools.tombot.model.facebook;

import com.google.gson.Gson;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;

/**
 * Created by tomco on 27/11/2016.
 */
public class FacebookMessageContentTest {

    @Test
    public void whenConvertingToJsonString_stringDoesNotContainEmptySeq() {
        FacebookMessageContent content = FacebookMessageContent.builder()
                .text("Hello")
                .build();

        String encode = new Gson().toJson(content);

        assertThat(encode, not(containsString("seq")));
    }


}