package be.tomcools.tombot.models.core;

import be.tomcools.tombot.model.facebook.messages.partials.Coordinates;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
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

    public String getCleanName() {
        return this.name.replaceFirst(this.id, "").replaceFirst("- ", "");
    }

    public Coordinates getCoordinates() {
        return new Coordinates(Double.parseDouble(lat), Double.parseDouble(lon));
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
