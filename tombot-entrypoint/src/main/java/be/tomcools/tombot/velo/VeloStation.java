package be.tomcools.tombot.velo;

import be.tomcools.tombot.model.facebook.Coordinates;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Setter
@Getter
public class VeloStation {
    private String id;
    private String lon;
    private String lat;
    private String bikes;
    private String slots;
    private String zip;
    private String address;
    private String status;
    private String name;
    private String stationType;

    public Coordinates getCoordinates() {
        return new Coordinates(Double.parseDouble(lat),Double.parseDouble(lon));
    }

    public boolean isOpen() {
        return "OPN".equalsIgnoreCase(status);
    }

    public int getAvailableBikes() {
        return Integer.valueOf(bikes);
    }

    public int getAvailableSlots() {
        return Integer.valueOf(slots);
    }
}
