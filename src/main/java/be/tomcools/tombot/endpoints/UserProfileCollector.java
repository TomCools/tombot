package be.tomcools.tombot.endpoints;

import be.tomcools.tombot.model.EventBusConstants;
import be.tomcools.tombot.model.FacebookIdentifier;
import be.tomcools.tombot.model.user.UserDetails;
import com.google.gson.Gson;
import io.vertx.core.eventbus.EventBus;

public class UserProfileCollector {
    EventBus eventBus;

    public UserProfileCollector(EventBus eventbus) {
        this.eventBus = eventbus;
    }

    public UserDetails findUserProfile(FacebookIdentifier sender) {



        return UserDetails.builder().build();
    }
}
