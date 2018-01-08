package be.tomcools.tombot.conversation.flows.implementations;

import be.tomcools.tombot.conversation.context.ConversationContext;
import be.tomcools.tombot.conversation.flows.ConversationFlow;
import be.tomcools.tombot.conversation.flows.HandleResult;
import be.tomcools.tombot.conversation.replies.text.Answers;
import be.tomcools.tombot.facebook.FacebookContext;
import be.tomcools.tombot.model.facebook.messages.partials.Coordinates;
import be.tomcools.tombot.velo.VeloData;
import be.tomcools.tombot.velo.VeloStation;
import be.tomcools.tombot.velo.datautils.SortOnDistance;

import java.time.temporal.ChronoUnit;
import java.util.Optional;


public class BikeReturnConversationFlow extends ConversationFlow {

    @Override
    public String getFlowActivatorMessage() {
        return "Return a Bike";
    }

    @Override
    public String getFlowName() {
        return "BIKE_RETURN";
    }

    @Override
    public HandleResult tryToHandle(FacebookContext fbContext, ConversationContext convo) {
        if (convo.locationIsNewerThan(1, ChronoUnit.MINUTES) &&
                convo.previousFlowWasNot(this)) {
            //if a location was presented in the last 60 seconds in a different flow, just use that one.
            handleBikeReturn(fbContext, convo.getLocation().getCoordinates());
            complete();
        } else {
            requestLocation(fbContext, convo);
        }
        return HandleResult.builder().isSuccess(true).build();
    }

    private void handleBikeReturn(FacebookContext fbContext, Coordinates coordinates) {
        fbContext.sendReply(Answers.findingPlaceToReturnBike());
        VeloData.openStations(fbContext, (veloStationStream -> {
            Optional<VeloStation> closestStationWithAvailableBikes = veloStationStream
                    .sorted(SortOnDistance.from(coordinates))
                    .limit(10)
                    .filter(s -> s.getAvailableSlots() > 0)
                    .findFirst();
            if (closestStationWithAvailableBikes.isPresent()) {
                VeloStation station = closestStationWithAvailableBikes.get();
                fbContext.sendReply(Answers.closestLocationDropoff(station));
                fbContext.sendLocation(station.getCleanName(), station.getCoordinates());
            } else {
                fbContext.sendReply("Damn yo! All nearby station are full.");
            }
        }));
    }
}
