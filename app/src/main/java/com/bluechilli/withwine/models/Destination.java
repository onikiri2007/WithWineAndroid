package com.bluechilli.withwine.models;

import com.google.gson.annotations.SerializedName;
import com.orm.SugarRecord;
import com.orm.dsl.Ignore;

import org.parceler.Parcel;

/**
 * Created by monishi on 16/06/15.
 */
@Parcel(analyze = Destination.class)
public class Destination extends SugarRecord<Destination> {

    @SerializedName("id")
    public int destId;
    public int brandId;
    public String locationTitle;
    public String address;
    public String openingHours;
    public double latitude;
    public double longitude;
    public String facilities;
    public String facilityOther;
    public Brand brand;
    @Ignore
    public GeoCoordinate location;
    public String brandName;
    public double distance;

}
