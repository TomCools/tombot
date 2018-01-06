package be.tomcools.tombot.conversation.flows;

import be.tomcools.tombot.conversation.answering.Answers;
import be.tomcools.tombot.conversation.context.ConversationContext;
import be.tomcools.tombot.endpoints.FacebookContext;
import be.tomcools.tombot.model.facebook.Coordinates;
import be.tomcools.tombot.velo.VeloData;
import be.tomcools.tombot.velo.VeloStation;
import be.tomcools.tombot.velo.datautils.SortOnDistance;

import java.util.Optional;

import static be.tomcools.tombot.conversation.flows.BikeRetrieveConversationFlow.State.NEW;
import static be.tomcools.tombot.conversation.flows.BikeRetrieveConversationFlow.State.REQUESTED_COORDINATES;

public class BikeRetrieveConversationFlow extends ConversationFlow {
    private State state = NEW;

    @Override
    String getFlowActivatorMessage() {
        return "Find a Bike";
    }

    @Override
    public String getFlowName() {
        return "BIKE_RETRIEVE";
    }

    @Override
    public HandleResult tryToHandle(FacebookContext fbContext, ConversationContext conversationContext) {
        //getting missing details
        if (state == NEW) {
            requestLocation(fbContext, conversationContext);
            state = REQUESTED_COORDINATES;
        } else if (state == REQUESTED_COORDINATES) {
            if (conversationContext.getLocation() != null) {
                handleBikeRetrieve(fbContext, conversationContext.getLocation().getCoordinates());
                complete();
            } else {
                return HandleResult.builder().isSuccess(false).build();
            }
        }
        return HandleResult.builder().isSuccess(true).build();
    }

    private void handleBikeRetrieve(FacebookContext fbContext, Coordinates coordinates) {
        fbContext.sendReply(Answers.findingPlaceToReturnBike());
        VeloData.openStations(fbContext, stream -> {
            Optional<VeloStation> closestStationWithAvailableBikes = stream
                    .sorted(SortOnDistance.from(coordinates))
                    .limit(10)
                    .filter(s -> s.getAvailableBikes() > 0)
                    .findFirst();
            if (closestStationWithAvailableBikes.isPresent()) {
                VeloStation station = closestStationWithAvailableBikes.get();
                fbContext.sendReply(Answers.closestLocationPickup(station));
                fbContext.sendLocation(station.getCleanName(), station.getCoordinates());
            } else {
                fbContext.sendReply("Damn yo! No bikes available nowhere.");
            }
        });
    }

    enum State {
        NEW, REQUESTED_COORDINATES;
    }
}
