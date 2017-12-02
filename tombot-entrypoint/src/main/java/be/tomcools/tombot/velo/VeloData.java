package be.tomcools.tombot.velo;

import com.google.gson.Gson;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.Message;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class VeloData extends AbstractVerticle {
    private static final Logger LOG = LoggerFactory.getLogger(VeloData.class);
    private static final Gson GSON = new Gson();

    private HttpClient client;
    public static List<VeloStation> stations = new ArrayList<>();

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

        loadData();
        vertx.setPeriodic(30000, h-> {
            loadData();
        });
    }

    private void loadData() {
        LOG.info("Updating Velo Data");
        String url = "/availability_map/getJsonObject";
        client.get(url, response -> {
            response.bodyHandler(b -> {
                LOG.info("Loading new Station Data...");
                try {
                    List<VeloStation> stationList = Arrays.asList(GSON.fromJson(b.toString(), VeloStation[].class));
                    stations = stationList.stream().filter(VeloStation::isOpen).collect(Collectors.toList());
                } catch (Exception ex) {
                    LOG.error(ex);
                }
                LOG.info("Finished loading station data.");
            });
        }).end();
    }
}