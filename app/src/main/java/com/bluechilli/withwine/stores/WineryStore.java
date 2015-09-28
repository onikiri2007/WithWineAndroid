package com.bluechilli.withwine.stores;

import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;

import com.bluechilli.withwine.models.Brand;
import com.bluechilli.withwine.models.Constants;
import com.bluechilli.withwine.models.Destination;
import com.bluechilli.withwine.models.GeoCoordinate;
import com.bluechilli.withwine.models.User;
import com.bluechilli.withwine.services.WineryService;
import com.bluechilli.withwine.utils.ApiAdapter;
import com.bluechilli.withwine.utils.DateUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by monishi on 16/06/15.
 */
public final class WineryStore extends BaseStore {

    private static WineryStore ourInstance = new WineryStore();

    public static WineryStore getInstance() {
        return ourInstance;
    }

    private User user;
    private WineryService wineryService;

    private WineryStore() {
        wineryService = ApiAdapter.create(Constants.getApiURL(), WineryService.class);

    }

    public void getDestinations(Location location, final Callback<Collection<Destination>> callback) {

        getDestinations(location, null, callback);
    }

    private Comparator<Destination> compare = new Comparator<Destination>() {
        @Override
        public int compare(Destination destination, Destination t1) {
            return ((Double) destination.distance).compareTo(t1.distance);
        }
    };

    public void getDestinations(final Location location, Date lastUpdated , final Callback<Collection<Destination>> callback) {

        String lastUpdatedOn = null;

        if(lastUpdated != null) {
            lastUpdatedOn = DateUtils.getUTCDateTimeAsString(lastUpdated, DateUtils.DATE_FORMAT);
        }

        Collection<Destination> dests = Destination.listAll(Destination.class);

        determineDistanceFromCurrentLocation(dests, location);

        if(!dests.isEmpty()) {

            Collections.sort(((List<Destination>)dests), compare);
            callback.success(dests, null);
        }

        wineryService.getDesinations(lastUpdatedOn, new Callback<Collection<Destination>>() {
            @Override
            public void success(Collection<Destination> destinations, Response response) {
                AsyncTask<Collection<Destination>, Void, Void> task = new AsyncTask<Collection<Destination>, Void, Void>() {
                    @Override
                    protected Void doInBackground(Collection<Destination>... items) {

                        Collection<Destination> dests = items[0];

                        for (Destination dest : dests) {
                            List<Destination> fromDb = Destination.find(Destination.class, "dest_id == ?", String.format("%s", dest.destId));

                            if (!fromDb.isEmpty()) {
                                Destination db = fromDb.get(0);
                                dest.setId(db.getId());
                            }

                            if (dest.location != null) {
                                dest.latitude = dest.location.latitude;
                                dest.longitude = dest.location.longitude;
                            }

                            if (dest.brand != null) {
                                dest.brandName = dest.brand.name;
                            }
                        }

                        Destination.saveInTx(dests);

                        return null;
                    }
                };

                task.execute(destinations);
                determineDistanceFromCurrentLocation(destinations, location);
                Collections.sort(((List<Destination>)destinations), compare);
                callback.success(destinations, response);
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d(error.getMessage(), Constants.TAG);
                callback.failure(error);
            }
        });

    }

    public void determineDistanceFromCurrentLocation(Collection<Destination> destinations, Location location) {

        for(Destination dest : destinations) {
            Location loc1 = new Location("");
            loc1.setLatitude(dest.latitude);
            loc1.setLongitude(dest.longitude);

            if(location != null) {
                dest.distance = location.distanceTo(loc1);
            }
        }
    }

    public void getBrands(final Callback<Collection<Brand>> callback) {

        wineryService.getBrands(new Callback<Collection<Brand>>() {
            @Override
            public void success(Collection<Brand> brands, Response response) {
                AsyncTask<Collection<Brand>, Void, Void> task = new AsyncTask<Collection<Brand>, Void, Void>() {
                    @Override
                    protected Void doInBackground(Collection<Brand>... items) {

                        Collection<Brand> brands = items[0];

                        for (Brand brand : brands) {
                            List<Brand> fromDb = Brand.find(Brand.class, "brand_id == ?", String.format("%s", brand.brandId));

                            if (!fromDb.isEmpty()) {

                                brand.setId(fromDb.get(0).getId());
                            }
                        }

                        Destination.saveInTx(brands);

                        return null;
                    }
                };

                task.execute(brands);

                callback.success(brands, response);
            }

            @Override
            public void failure(RetrofitError error) {
                callback.failure(error);
            }
        });
    }

}
