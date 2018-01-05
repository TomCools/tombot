package be.tomcools.tombot.endpoints;

import be.tomcools.tombot.conversation.ConversationContext;
import be.tomcools.tombot.conversation.ConversationContextCache;
import be.tomcools.tombot.conversation.LocationDetail;
import be.tomcools.tombot.model.facebook.*;
import be.tomcools.tombot.model.facebook.settings.SettingConstants;
import be.tomcools.tombot.tools.JSON;
import be.tomcools.tombot.velo.DistanceCalculator;
import be.tomcools.tombot.velo.VeloData;
import be.tomcools.tombot.velo.VeloStation;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.RoutingContext;
import lombok.Builder;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static be.tomcools.tombot.conversation.quickreplies.QuickReplies.*;

@Builder
public class FacebookWebhook {
    private static final Logger LOG = LoggerFactory.getLogger(FacebookWebhook.class);
    private EventBus eventbus;

    public void webhookRequestHandler(RoutingContext route) {
        FacebookRequest request = new FacebookRequest(route.request());

        if (request.isSubscribeRequest()) {
            request.respondToRequest();
        } else {
            request.handleBody(b -> {
                JsonObject jsonObject = b.toJsonObject();
                LOG.info("Got Request from Facebook: " + jsonObject);
                FacebookMessage message = JSON.fromJson(jsonObject.toString(), FacebookMessage.class);
                handleMessage(message);
            });
        }
    }

    private void handleMessage(FacebookMessage message) {
        for (FacebookMessageEntry entry : message.getEntry()) {
            handleFacebookMessageEntry(entry);
        }
    }

    private void handleFacebookMessageEntry(FacebookMessageEntry entry) {
        for (FacebookMessageMessaging entryMessage : entry.getMessaging()) {
            this.handleFacebookMessageMessaging(entryMessage);
        }
    }

    private void handleFacebookMessageMessaging(FacebookMessageMessaging entryMessage) {
        FacebookContext context = new FacebookContext(eventbus, entryMessage);

        if (context.isMessage()) {
            LOG.info("Got Message from Facebook: " + context.getMessageText());
            context.senderAction(SenderAction.TYPING_ON);
            handleFacebookMessage(context);
        } else if (context.isDelivery()) {
            LOG.info("Message delivery confirmation");
        } else if (context.isPostback()) {
            LOG.info("Got POSTBACK");
            if (SettingConstants.GET_STARTED.equalsIgnoreCase(entryMessage.getPostback().getPayload())) {
                handleGettingStarted(context);
            } else {
                context.sendReply("Oh look, a postback" + entryMessage.getPostback().getPayload());
            }
        } else if (context.isReadConfirmation()) {
            LOG.info("Got Read Confirmation");
        }
    }

    private void handleGettingStarted(FacebookContext context) {
        context.sendReply("Hi! I am Avelo. I can help you find a bike station for Velo Antwerp!");
    }

    private void handleFacebookMessage(FacebookContext facebookContext) {
        ConversationContext conversationContext = ConversationContextCache.getConversation(facebookContext.getSender().getId());
        this.handleConversation(facebookContext, conversationContext);
    }

    private void handleConversation(FacebookContext fbContext, ConversationContext conversationContext) {
        FacebookMessageContent msg = fbContext.getMessage();
        if (msg.hasQuickReply()) {
            if (BIKE_RETRIEVE.getReply().getPayload().equalsIgnoreCase(msg.getQuick_reply().getPayload())) {
                handleQuickReplyBikeRetrieve(fbContext, conversationContext);
            } else if (BIKE_RETURN.getReply().getPayload().equalsIgnoreCase(msg.getQuick_reply().getPayload())) {
                fbContext.sendReply("Finding you a place to drop off your bike...");
            }
        } else if (msg.hasAttachments()) {
            msg.getAttachments().forEach(facebookMessageAttachment -> {
                if (facebookMessageAttachment.isLocation()) {
                    Coordinates coordinates = facebookMessageAttachment.getPayload().getCoordinates();
                    fbContext.sendReply("Thank you for location: " + coordinates);
                    conversationContext.setLocation(new LocationDetail(new Date(), coordinates));
                    fbContext.sendReply("What do you want to do?", BIKE_RETRIEVE, BIKE_RETURN);
                }
            });
        } else {
            fbContext.sendReply("Where are you?", LOCATION);
        }
        fbContext.senderAction(SenderAction.TYPING_OFF);
    }

    private void handleQuickReplyBikeRetrieve(FacebookContext fbContext, ConversationContext conversationContext) {
        LocationDetail location = conversationContext.getLocation();
        fbContext.sendReply("Finding you a place to get a bike...");
        VeloData.getData().setHandler(handler -> {
            if (handler.succeeded()) {
                List<VeloStation> stations = handler.result();
                Optional<VeloStation> closestStationWithAvailableBikes = stations.stream()
                        .sorted((s1, s2) -> {
                            double distanceTo1 = DistanceCalculator.calculateDistanceKm(location.getCoordinates(), s1.getCoordinates());
                            double distanceTo2 = DistanceCalculator.calculateDistanceKm(location.getCoordinates(), s2.getCoordinates());
                            return Double.compare(distanceTo1, distanceTo2);
                        }).limit(10)
                        .filter(VeloStation::isOpen)
                        .filter(s->s.getAvailableBikes() > 0)
                        .findFirst();
                if(closestStationWithAvailableBikes.isPresent()) {
                    VeloStation station = closestStationWithAvailableBikes.get();
                    fbContext.sendReply("Closest station for dropoff is: " + station.getName() + " " + station.getCoordinates());
                } else {
                    fbContext.sendReply("Damn yo! No bikes available nowhere :(");
                }
            } else {
                fbContext.sendReply("Oh sorry, we couldn't get the Velodata :( something is going on...");
            }
        });
    }

    private void sendVeloAnalytics(FacebookContext context) {
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

                String veloAnalytics = new StringBuilder().append("There are a total of ").append(stations.size()).append(" stations in Antwerp.").append(System.lineSeparator())
                        .append(openStations).append(" of those stations are open, ").append(stations.size() - openStations).append(" are closed.").append(System.lineSeparator())
                        .append("A total of ").append(amountOfBikes).append(" bikes are available at stations").append(System.lineSeparator())
                        .append("Even tho Velo does it's best, there are ").append(openStationsWith0Bikes).append(" stations without bikes. :-(")
                        .toString();

                context.sendReply(veloAnalytics);
            }
        });

    }
}
