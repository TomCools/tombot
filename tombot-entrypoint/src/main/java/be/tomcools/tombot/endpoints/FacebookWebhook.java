package be.tomcools.tombot.endpoints;

import be.tomcools.tombot.conversation.ConversationContext;
import be.tomcools.tombot.conversation.quickreplies.QuickReplies;
import be.tomcools.tombot.model.core.EventBusConstants;
import be.tomcools.tombot.model.facebook.*;
import be.tomcools.tombot.model.facebook.settings.SettingConstants;
import be.tomcools.tombot.tools.JSON;
import be.tomcools.tombot.velo.VeloData;
import be.tomcools.tombot.velo.VeloStation;
import com.google.gson.Gson;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.RoutingContext;
import lombok.Builder;

import java.util.List;

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
                FacebookMessage message = JSON.fromJson(b.toJsonObject().toString(), FacebookMessage.class);
                LOG.info("Got Message: {}", message);
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
            context.senderAction(SenderAction.TYPING_ON);
            handleFacebookMessage(context);
        } else if (context.isDelivery()) {
            System.out.println(entryMessage.getDelivery().getSeq() + " delivered");
        } else if (context.isPostback()) {
            if (SettingConstants.GET_STARTED.equalsIgnoreCase(entryMessage.getPostback().getPayload())) {
                handleGettingStarted(context);
            } else {
                context.sendReply("Oh look, a postback" + entryMessage.getPostback().getPayload());
            }
            LOG.debug("POSTBACK :-)");
        } else if (context.isReadConfirmation()) {
            LOG.debug("ReadConfirmation");
        }
    }

    private void handleGettingStarted(FacebookContext context) {
        context.sendReply("Hi! I am Avelo. I can help you find a bike station for Velo Antwerp!");
    }

    private void handleFacebookMessage(FacebookContext facebookContext) {
        eventbus.send(EventBusConstants.GET_CONVERSATION_CONTEXT, facebookContext.getSender().getId(), msg -> {
          if(msg.succeeded()) {
              ConversationContext conversationContext = (ConversationContext) msg.result().body();
              this.handleConversation(facebookContext, conversationContext);
          } else {
              facebookContext.sendReply("Failed to get your previous conversation context :-(. " + msg.cause());
          }
        });
    }

    private void handleConversation(FacebookContext fbContext, ConversationContext conversationContext) {
        if(conversationContext.isFirstContact()) {
            fbContext.sendReply("So happy to have you talking to me! :-)");
            //Send quickActions
            fbContext.sendReply("What do you want to do?", BIKE_RETRIEVE, BIKE_RETURN);
        }

        sendVeloAnalytics(fbContext);
    }

    private void sendVeloAnalytics(FacebookContext context) {
        VeloData.getData().setHandler(handler  -> {
            if(handler.succeeded()) {
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
