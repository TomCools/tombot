package be.tomcools.tombot.velo;

import be.tomcools.tombot.model.core.EventBusConstants;
import com.google.gson.Gson;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.DeliveryOptions;
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

        vertx.eventBus().consumer(EventBusConstants.GET_VELO_DATA, this::handleMessage);
    }

    private void clearDataCache() {
        stations = new ArrayList<>();
    }

    private <T> void handleMessage(final Message<T> tMessage) {
        if(!stations.isEmpty()) {
            tMessage.reply(stations);
        } else {
            LOG.info("Updating Velo Data");
            String url = "/availability_map/getJsonObject";
            client.get(url, response -> {
                response.bodyHandler(b -> {
                    LOG.info("Loading new Station Data...");
                    try {
                        List<VeloStation> stationList = Arrays.asList(GSON.fromJson(b.toString(), VeloStation[].class));
                        tMessage.reply(stationList);
                        stations = stationList;
                    } catch (Exception ex) {
                        LOG.error(ex);
                    }
                    LOG.info("Finished loading station data.");
                });
            }).end();
        }
        }
}