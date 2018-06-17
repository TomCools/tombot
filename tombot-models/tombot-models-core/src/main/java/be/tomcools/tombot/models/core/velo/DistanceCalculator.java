package be.tomcools.tombot.models.core.velo;

import be.tomcools.tombot.model.facebook.messages.partials.Coordinates;

/**
 * Calculation based on http://www.movable-type.co.uk/scripts/latlong.html
 */
public class DistanceCalculator {
    private static final double MILES_TO_KILOMETERS_MULTIPLIER = 1.609344;
    private static final double DEGREE_TO_NAUTICAL_MILE_MULTIPLIER = 60;
    private static final double NAUTICAL_MILE_TO_MILE_MULTIPLIER = 1.1515;

    public static double calculateDistanceKm(Coordinates a, Coordinates b) {
        return calculateDistanceKm(a.getLatitude(), a.getLongitude(), b.getLatitude(), b.getLongitude());
    }

    private static double calculateDistanceKm(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * DEGREE_TO_NAUTICAL_MILE_MULTIPLIER * NAUTICAL_MILE_TO_MILE_MULTIPLIER * MILES_TO_KILOMETERS_MULTIPLIER;
        return (dist);
    }

    private static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private static double rad2deg(double rad) {
        return (rad * 180 / Math.PI);
    }
}
