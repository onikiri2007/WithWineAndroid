package com.bluechilli.withwine.events;

import android.location.Location;

/**
 * Created by monishi on 17/06/15.
 */
public class OnLocationUpdated {
    private final Location location;

    public OnLocationUpdated(Location location) {
        this.location = location;
    }

    public Location getLocation() {
        return location;
    }
}
