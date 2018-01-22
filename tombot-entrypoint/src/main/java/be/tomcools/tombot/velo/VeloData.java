package be.tomcools.tombot.velo;

import be.tomcools.tombot.conversation.replies.text.Answers;
import be.tomcools.tombot.facebook.FacebookContext;
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
import java.util.function.Consumer;
import java.util.stream.Stream;

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

    public static void openStations(FacebookContext fbContext, Consumer<Stream<VeloStation>> handling) {
        VeloData.getData().setHandler(handler -> {
            if (handler.succeeded()) {
                handling.accept(handler.result().stream().filter(VeloStation::isOpen));
            } else {
                fbContext.sendReply(Answers.couldNotRetrieveVeloData());
            }
        });
    }

    public static void getVeloAnalytics(Consumer<String> handling) {
        VeloData.getData().setHandler(handler -> {
            if (handler.succeeded()) {
                List<VeloStation> stations = handler.result();
                long openStations = stations.stream()
                        .map(VeloStation::isOpen)
                        .count();
                long amountOfBikes = stations.stream()
                        .mapToInt(VeloStation::getAvailableBikes)
                        .sum();
                long openStationsWith0Bikes = stations.stream()
                        .filter(VeloStation::isOpen)
                        .filter(veloStation -> veloStation.getAvailableBikes() == 0)
                        .count();

                String veloAnalytics = new StringBuilder().append("There are a total of ").append(stations.size()).append(" stations in Antwerp.").append(System.lineSeparator()).append(openStations).append(" of those stations are open, ").append(stations.size() - openStations).append(" are closed.").append(System.lineSeparator()).append("A total of ").append(amountOfBikes).append(" bikes are available at stations").append(System.lineSeparator()).append("Even tho Velo does it's best, there are ").append(openStationsWith0Bikes).append(" stations without bikes. :-(").toString();
                handling.accept(veloAnalytics);
            }
        });
    }
}