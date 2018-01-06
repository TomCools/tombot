package be.tomcools.tombot.conversation;

import be.tomcools.tombot.conversation.context.ConversationContext;
import be.tomcools.tombot.conversation.context.LocationDetail;
import be.tomcools.tombot.endpoints.FacebookContext;
import be.tomcools.tombot.model.facebook.Coordinates;
import be.tomcools.tombot.model.facebook.FacebookIncommingMessageContent;
import be.tomcools.tombot.model.facebook.SenderAction;
import be.tomcools.tombot.velo.VeloData;
import be.tomcools.tombot.velo.VeloStation;
import be.tomcools.tombot.velo.datautils.SortOnDistance;

import java.util.Date;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static be.tomcools.tombot.conversation.quickreplies.QuickReplies.*;

public class FacebookMessageHandler implements MessageHandler {
    @Override
    public void handle(FacebookContext fbContext, ConversationContext conversationContext) {
        FacebookIncommingMessageContent msg = fbContext.getMessage();
        if (msg.hasQuickReply()) {
            if (BIKE_RETRIEVE.getReply().getPayload().equalsIgnoreCase(msg.getQuick_reply().getPayload())) {
                handleQuickReplyBikeRetrieve(fbContext, conversationContext);
            } else if (BIKE_RETURN.getReply().getPayload().equalsIgnoreCase(msg.getQuick_reply().getPayload())) {
                handleQuickReplyBikeReturn(fbContext, conversationContext);
            }
        } else if (msg.hasAttachments()) {
            msg.getAttachments().forEach(facebookMessageAttachment -> {
                if (facebookMessageAttachment.isLocation()) {
                    Coordinates coordinates = facebookMessageAttachment.getLocation();
                    conversationContext.setLocation(new LocationDetail(new Date(), coordinates));
                    fbContext.sendReply("Thank you for your location. What do you want to do?", BIKE_RETRIEVE, BIKE_RETURN);
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
        this.openStations(fbContext, stream -> {
            Optional<VeloStation> closestStationWithAvailableBikes = stream
                    .sorted(SortOnDistance.from(location.getCoordinates()))
                    .limit(10)
                    .filter(s -> s.getAvailableBikes() > 0)
                    .findFirst();
            if (closestStationWithAvailableBikes.isPresent()) {
                VeloStation station = closestStationWithAvailableBikes.get();
                fbContext.sendReply("Closest station for pickup is: " + station.getName());
                fbContext.sendLocation(bikeStationImageDetails(station), station.getCoordinates());
            } else {
                fbContext.sendReply("Damn yo! No bikes available nowhere :(");
            }
        });
    }

    private void handleQuickReplyBikeReturn(FacebookContext fbContext, ConversationContext conversationContext) {
        LocationDetail location = conversationContext.getLocation();
        fbContext.sendReply("Finding you a place to return your bike...");
        this.openStations(fbContext, (veloStationStream -> {
            Optional<VeloStation> closestStationWithAvailableBikes = veloStationStream
                    .sorted(SortOnDistance.from(location.getCoordinates()))
                    .limit(10)
                    .filter(s -> s.getAvailableSlots() > 0)
                    .findFirst();
            if (closestStationWithAvailableBikes.isPresent()) {
                VeloStation station = closestStationWithAvailableBikes.get();
                fbContext.sendReply("Closest station for dropoff is: " + station.getName());
                fbContext.sendLocation(bikeStationImageDetails(station), station.getCoordinates());
            } else {
                fbContext.sendReply("Damn yo! All nearby station are full :(");
            }
        }));
    }

    private void openStations(FacebookContext fbContext, Consumer<Stream<VeloStation>> handling) {
        VeloData.getData().setHandler(handler -> {
            if (handler.succeeded()) {
                handling.accept(handler.result().stream().filter(VeloStation::isOpen));
            } else {
                fbContext.sendReply("Oh sorry, we couldn't get the Velodata :( something is going on...");
            }
        });
    }

    private String bikeStationImageDetails(VeloStation station) {
        return String.format("Station: %s\nOpen Spaces: %d\nAvailable Bikes: %d", station.getName(), station.getAvailableSlots(), station.getAvailableBikes());
    }
}
