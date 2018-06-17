package be.tomcools.tombot.conversation.flows.implementations;


import be.tomcools.tombot.conversation.ConversationContext;
import be.tomcools.tombot.conversation.FacebookContext;
import be.tomcools.tombot.conversation.flows.ConversationFlow;
import be.tomcools.tombot.conversation.flows.HandleResult;
import be.tomcools.tombot.conversation.replies.quickreplies.QuickReplies;
import be.tomcools.tombot.conversation.replies.text.Answers;
import be.tomcools.tombot.model.facebook.messages.partials.Coordinates;

import java.time.temporal.ChronoUnit;
import java.util.Optional;


public class BikeReturnConversationFlow extends ConversationFlow {

    private boolean locationRequested = false;

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
        if (!locationRequested) {
            return handleInitialTime(fbContext, convo);
        } else {
            if (!fbContext.getMessage().hasLocation()) {
                return HandleResult.builder().
                        isSuccess(false)
                        .backupAction(() -> {
                            fbContext.sendReply("Let's try this again...", QuickReplies.location());
                        }).build();
            } else {
                return executeCorrect(fbContext, convo);
            }
        }
    }

    private HandleResult executeCorrect(FacebookContext fbContext, ConversationContext convo) {
        handleBikeReturn(fbContext, convo.getLocation().getCoordinates());
        return HandleResult.SUCCES;
    }

    private HandleResult handleInitialTime(FacebookContext fbContext, ConversationContext convo) {
        if (convo.previousFlowWas(this) && !fbContext.getMessage().hasLocation()) {
            requestLocation(Answers.askLocationSameFlow(), fbContext, convo);
            this.locationRequested = true;
        } else if (convo.locationIsOlderThan(1, ChronoUnit.MINUTES)) {
            requestLocation(fbContext, convo);
            this.locationRequested = true;
        } else {
            return executeCorrect(fbContext, convo);
        }
        return HandleResult.builder().
                isSuccess(true).
                build();
    }

    private void handleBikeReturn(FacebookContext fbContext, Coordinates coordinates) {
        fbContext.sendReply(Answers.findingPlaceToReturnBike());
        /*VeloData.openStations(fbContext, (veloStationStream -> {
            Optional<VeloStation> closestStationWithAvailableBikes = veloStationStream
                    .sorted(SortOnDistance.from(coordinates))
                    .limit(10)
                    .filter(s -> s.getAvailableSlots() > 0)
                    .findFirst();
            if (closestStationWithAvailableBikes.isPresent()) {
                VeloStation station = closestStationWithAvailableBikes.get();
                fbContext.sendReply(Answers.closestLocationDropoff(station));
                fbContext.sendLocation(station.getCleanName(), station.getCoordinates());

                //Location should reactivate the same flow.
                doDelayed(() -> fbContext.sendReply("Got what you wanted?", QuickReplies.thanks(), QuickReplies.flowActivation("Different location", flowActivation())), 1000);
            } else {
                fbContext.sendReply("Damn yo! All nearby station are full.");
            }
        }));
        complete();*/
    }
}
