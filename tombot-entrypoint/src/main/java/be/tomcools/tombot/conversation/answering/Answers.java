package be.tomcools.tombot.conversation.answering;

import be.tomcools.tombot.velo.VeloStation;

import java.util.Random;

import static be.tomcools.tombot.conversation.Emoticons.*;

public class Answers {
    private static final Random RAND = new Random();

    public static String couldNotRetrieveVeloData() {
        return random("Oh sorry, we couldn't get the Velodata " + SCREAMING_IN_FEAR + " something is going on...",
                "Hmmm.. " + FEARFULL + " can't get station information. Please try again later.");
    }

    public static String findingPlaceToReturnBike() {
        return random("Finding you a place to return your bike... " + BIKE_DRIVING,
                "Hold on, checking now! " + BIKE_DRIVING);
    }

    public static String findingPlaceToRetrieveBike() {
        return random("Finding you a place to get a bike... " + BIKE_DRIVING,
                "Hold on, checking now! " + BIKE_DRIVING);
    }

    public static String closestLocationPickup(VeloStation station) {
        return random(
                "Closest station for pickup is: " + station.getName(),
                station.getCleanName() + " still has " + station.getAvailableBikes() + " bikes available for you.");
    }

    public static String closestLocationDropoff(VeloStation station) {
        return random("Closest station for dropoff is: " + station.getName(),
                station.getCleanName() + " still has " + station.getAvailableSlots() + " slots available for leaving your bike.");
    }

    public static String askForLocation() {
        return random("Where are you?",
                "Where are you at?");
    }

    private static String random(String... possibleAnswers) {
        return possibleAnswers[RAND.nextInt(possibleAnswers.length)];
    }


}
