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

public class BikeRetrieveConversationFlow extends ConversationFlow {

    @Override
    public String getFlowActivatorMessage() {
        return "Find a Bike";
    }

    @Override
    public String getFlowName() {
        return "BIKE_RETRIEVE";
    }

    private boolean locationRequested = false;

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
                handleBikeRetrieve(fbContext, convo.getLocation().getCoordinates());
                return HandleResult.SUCCES;
            }
        }

    }

    private HandleResult handleInitialTime(FacebookContext fbContext, ConversationContext convo) {
        if (convo.previousFlowWas(this) && !fbContext.getMessage().hasLocation()) {
            requestLocation(Answers.askLocationSameFlow(), fbContext, convo);
            this.locationRequested = true;
        } else if (convo.locationIsOlderThan(1, ChronoUnit.MINUTES)) {
            requestLocation(fbContext, convo);
            this.locationRequested = true;
        } else {
            //if a location was presented in the last 60 seconds, just use that one.
            handleBikeRetrieve(fbContext, convo.getLocation().getCoordinates());
        }
        return HandleResult.builder().
                isSuccess(true).
                build();
    }

    private void handleBikeRetrieve(FacebookContext fbContext, Coordinates coordinates) {
        fbContext.sendReply(Answers.findingPlaceToRetrieveBike());
        /*
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

                doDelayed(() -> fbContext.sendReply("Got what you wanted?", QuickReplies.thanks(), QuickReplies.flowActivation("Different location", flowActivation())), 1000);
            } else {
                fbContext.sendReply("Damn yo! No bikes available nowhere.");
            }
        });
        complete();
        */
    }
}
