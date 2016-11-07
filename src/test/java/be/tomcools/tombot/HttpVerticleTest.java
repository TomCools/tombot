package be.tomcools.tombot;

import io.restassured.RestAssured;
import io.vertx.core.Vertx;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

/**
 * Created by tomco on 7/11/2016.
 */
public class HttpVerticleTest {
    @BeforeClass
    public static void startServer() {
        Vertx.vertx().deployVerticle(HttpVerticle.class.getName());
    }

    @Test
    public void serverCanReturnIsLive() {
        String result = RestAssured.get("http://localhost:9999").asString();

        assertThat(result, is("I'm Alive!"));
    }

    @Test
    public void serverDoesNotReturnIsAliveOnTheWebhookEndpoint() {
        String result = RestAssured.get("http://localhost:9999").asString();

        assertThat(result, is(not("I'm Alive")));
    }

}