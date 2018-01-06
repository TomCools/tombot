package be.tomcools.tombot.conversation;

import be.tomcools.tombot.conversation.answering.Answers;
import be.tomcools.tombot.conversation.context.ConversationContext;
import be.tomcools.tombot.conversation.context.LocationDetail;
import be.tomcools.tombot.conversation.quickreplies.QuickReplies;
import be.tomcools.tombot.conversation.quickreplies.QuickReply;
import be.tomcools.tombot.endpoints.FacebookContext;
import be.tomcools.tombot.model.facebook.Coordinates;
import be.tomcools.tombot.model.facebook.FacebookIncommingMessageContent;
import be.tomcools.tombot.model.facebook.SenderAction;
import be.tomcools.tombot.tools.JSON;
import be.tomcools.tombot.velo.VeloData;
import be.tomcools.tombot.velo.VeloStation;
import be.tomcools.tombot.velo.datautils.SortOnDistance;
import lombok.AllArgsConstructor;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static be.tomcools.tombot.conversation.quickreplies.QuickReplies.*;

@AllArgsConstructor
public class FacebookMessageHandler {

    private FacebookContext fbContext;
    private ConversationContext conversationContext;

    public void handle() {
        FacebookIncommingMessageContent msg = fbContext.getMessage();
        if (msg.hasQuickReply()) {
            if (BIKE_RETRIEVE.getReply().getPayload().equalsIgnoreCase(msg.getQuick_reply().getPayload())) {
                handleQuickReplyBikeRetrieve(fbContext, conversationContext);
            } else if (BIKE_RETURN.getReply().getPayload().equalsIgnoreCase(msg.getQuick_reply().getPayload())) {
                handleQuickReplyBikeReturn(fbContext, conversationContext);
            } else if (msg.getText().equalsIgnoreCase("Previous Location")) {
                Coordinates previousCoordinates = JSON.fromJson(msg.getQuick_reply().getPayload(), Coordinates.class);
                locationRetrieved(previousCoordinates);
            }
        } else if (msg.hasAttachments()) {
            msg.getAttachments().forEach(facebookMessageAttachment -> {
                if (facebookMessageAttachment.isLocation()) {
                    Coordinates coordinates = facebookMessageAttachment.getLocation();
                    locationRetrieved(coordinates);
                }
            });
        } else {
            List<QuickReply> quickReplies = new ArrayList<>();
            if (conversationContext.getLocation() != null && conversationContext.getLocation().getRetrieved().isAfter(Instant.now().minusSeconds(300))) {
                //it is less than 5 minutes ago since your last location.
                Coordinates coordinates = conversationContext.getLocation().getCoordinates();
                quickReplies.add(QuickReplies.previousLocation(coordinates));
            }
            quickReplies.add(LOCATION);
            fbContext.sendReply(Answers.askForLocation(), quickReplies);
        }
        fbContext.senderAction(SenderAction.TYPING_OFF);
    }

    private void locationRetrieved(Coordinates coordinates) {
        conversationContext.setLocation(new LocationDetail(Instant.now(), coordinates));
        fbContext.sendReply("Thank you for your location. What do you want to do?", BIKE_RETRIEVE, BIKE_RETURN);
    }

    private void handleQuickReplyBikeRetrieve(FacebookContext fbContext, ConversationContext conversationContext) {
        LocationDetail location = conversationContext.getLocation();
        fbContext.sendReply(Answers.findingPlaceToRetrieveBike());
        this.openStations(fbContext, stream -> {
            Optional<VeloStation> closestStationWithAvailableBikes = stream
                    .sorted(SortOnDistance.from(location.getCoordinates()))
                    .limit(10)
                    .filter(s -> s.getAvailableBikes() > 0)
                    .findFirst();
            if (closestStationWithAvailableBikes.isPresent()) {
                VeloStation station = closestStationWithAvailableBikes.get();
                fbContext.sendReply(Answers.closestLocationPickup(station));
                fbContext.sendLocation(bikeStationImageDetails(station), station.getCoordinates());
            } else {
                fbContext.sendReply("Damn yo! No bikes available nowhere.");
            }
        });
    }

    private void handleQuickReplyBikeReturn(FacebookContext fbContext, ConversationContext conversationContext) {
        LocationDetail location = conversationContext.getLocation();
        fbContext.sendReply(Answers.findingPlaceToReturnBike());
        this.openStations(fbContext, (veloStationStream -> {
            Optional<VeloStation> closestStationWithAvailableBikes = veloStationStream
                    .sorted(SortOnDistance.from(location.getCoordinates()))
                    .limit(10)
                    .filter(s -> s.getAvailableSlots() > 0)
                    .findFirst();
            if (closestStationWithAvailableBikes.isPresent()) {
                VeloStation station = closestStationWithAvailableBikes.get();
                fbContext.sendReply(Answers.closestLocationDropoff(station));
                fbContext.sendLocation(bikeStationImageDetails(station), station.getCoordinates());
            } else {
                fbContext.sendReply("Damn yo! All nearby station are full.");
            }
        }));
    }

    private void openStations(FacebookContext fbContext, Consumer<Stream<VeloStation>> handling) {
        VeloData.getData().setHandler(handler -> {
            if (handler.succeeded()) {
                handling.accept(handler.result().stream().filter(VeloStation::isOpen));
            } else {
                fbContext.sendReply(Answers.couldNotRetrieveVeloData());
            }
        });
    }

    private String bikeStationImageDetails(VeloStation station) {
        return String.format("Station: %s\nOpen Spaces: %d\nAvailable Bikes: %d", station.getCleanName(), station.getAvailableSlots(), station.getAvailableBikes());
    }
}
