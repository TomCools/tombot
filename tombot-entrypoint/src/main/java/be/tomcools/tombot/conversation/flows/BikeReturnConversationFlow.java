package be.tomcools.tombot.conversation.flows;

import be.tomcools.tombot.conversation.answering.Answers;
import be.tomcools.tombot.conversation.context.ConversationContext;
import be.tomcools.tombot.facebook.FacebookContext;
import be.tomcools.tombot.model.facebook.messages.partials.Coordinates;
import be.tomcools.tombot.velo.VeloData;
import be.tomcools.tombot.velo.VeloStation;
import be.tomcools.tombot.velo.datautils.SortOnDistance;

import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static be.tomcools.tombot.conversation.flows.BikeReturnConversationFlow.State.NEW;

public class BikeReturnConversationFlow extends ConversationFlow {
    private State state = NEW;

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

    enum State {
        NEW, REQUESTED_COORDINATES;
    }
}
