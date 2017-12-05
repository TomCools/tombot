package be.tomcools.tombot.velo;

import com.google.gson.Gson;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class VeloData extends AbstractVerticle {
    private static final Logger LOG = LoggerFactory.getLogger(VeloData.class);
    private static final Gson GSON = new Gson();

    private static HttpClient client;
    private static List<VeloStation> stations = new ArrayList<>();

    @Override
    public void start() throws Exception {
        HttpClientOptions options = new HttpClientOptions()
                .setLogActivity(true)
                .setKeepAlive(true)
                .setTrustAll(true)
                .setSsl(true)
                .setDefaultPort(443)
                .setDefaultHost("www.velo-antwerpen.be");
        client = vertx.createHttpClient(options);

        vertx.setPeriodic(30000, h -> {
            clearDataCache();
        });
    }

    private void clearDataCache() {
        stations = new ArrayList<>();
    }

    public static Future<List<VeloStation>> getData() {
        Future<List<VeloStation>> future = Future.future();
        if (!stations.isEmpty()) {
            future.complete(stations);
        } else {
            LOG.info("Updating Velo Data");
            String url = "/availability_map/getJsonObject";
            client.get(url, response -> {
                response.bodyHandler(b -> {
                    LOG.info("Loading new Station Data...");
                    try {
                        List<VeloStation> stationList = Arrays.asList(GSON.fromJson(b.toString(), VeloStation[].class));
                        future.complete(stationList);
                        stations = stationList;
                    } catch (Exception ex) {
                        LOG.error(ex);
                    }
                    LOG.info("Finished loading station data.");
                });
            }).end();
        }
        return future;
    }
}