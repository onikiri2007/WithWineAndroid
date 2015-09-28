package com.bluechilli.withwine.models;

import org.parceler.Parcel;

/**
 * Created by monishi on 16/06/15.
 */
@Parcel(analyze = GeoCoordinate.class)
public class GeoCoordinate {

    public double latitude;
    public double longitude;
}
