package be.tomcools.tombot.velo.datautils;

import be.tomcools.tombot.model.facebook.Coordinates;
import be.tomcools.tombot.velo.DistanceCalculator;
import be.tomcools.tombot.velo.VeloStation;

import java.util.Comparator;

/**
 * Created by tomco on 6/01/2018.
 */
public class SortOnDistance implements Comparator<VeloStation> {

    private Coordinates from;

    private SortOnDistance(Coordinates from) {
        this.from = from;
    }

    public static SortOnDistance from(Coordinates from) {
        return new SortOnDistance(from);
    }

    @Override
    public int compare(VeloStation s1, VeloStation s2) {
        double distanceTo1 = DistanceCalculator.calculateDistanceKm(from, s1.getCoordinates());
        double distanceTo2 = DistanceCalculator.calculateDistanceKm(from, s2.getCoordinates());
        return Double.compare(distanceTo1, distanceTo2);
    }
}
