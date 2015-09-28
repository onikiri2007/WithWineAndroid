package com.bluechilli.withwine.models;

import com.google.gson.annotations.SerializedName;
import com.orm.SugarRecord;

import org.parceler.Parcel;

import java.util.Date;
import java.util.List;

/**
 * Created by monishi on 16/06/15.
 */
@Parcel(analyze = Brand.class)
public class Brand extends SugarRecord<Brand> {

    @SerializedName("id")
    public int brandId;

    public String name;

    public String logoPath;

    public String description;

    public int enterpriseId;

    public String coverPhotoPath;

    public Date lastUpdated;

    public String isFollowed;

    public boolean isVerified;

    public boolean isFreshShipping;

    public int act;

    public int nsw;

    public int nt;

    public int qld;

    public int sa;

    public int tas;

    public int vic;

    public int wa;

    public String policy;

    public List<Destination> Destinations;
}
