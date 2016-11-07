package be.tomcools.tombot;

import io.restassured.RestAssured;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.redis.RedisClient;
import io.vertx.redis.RedisOptions;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class HttpVerticleTest {
    private static Integer TESTPORT = 8080;
    private static String PATH = "http://localhost:" + TESTPORT;

    @BeforeClass
    public static void startServer() {
        JsonObject o = new JsonObject();
        o.put("http.port", TESTPORT);
        DeploymentOptions options = new DeploymentOptions();
        options.setConfig(o);
        Vertx.vertx().deployVerticle(HttpVerticle.class.getName(), options);
    }

    @Test
    public void serverCanReturnIsLive() {
        String result = RestAssured.get(PATH).asString();

        assertThat(result, is("I'm Alive!"));
    }

    @Test
    public void serverDoesNotReturnIsAliveOnTheWebhookEndpoint() {
        String result = RestAssured.get(PATH).asString();

        assertThat(result, is(not("I'm Alive")));
    }
}