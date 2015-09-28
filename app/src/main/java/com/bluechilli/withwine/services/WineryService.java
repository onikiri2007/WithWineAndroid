package com.bluechilli.withwine.services;

import com.bluechilli.withwine.models.Brand;
import com.bluechilli.withwine.models.CreditCardDetail;
import com.bluechilli.withwine.models.Destination;
import com.bluechilli.withwine.models.Login;
import com.bluechilli.withwine.models.MessageResult;
import com.bluechilli.withwine.models.PaymentDetail;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Query;

/**
 * Created by monishi on 16/06/15.
 */
public interface WineryService {

    @GET("/brand/destinations")
    void getDesinations(@Query("lastUpdated") String lastUpdated, Callback<Collection<Destination>> callback);

    @GET("/brand/brands")
    void getBrands(Callback<Collection<Brand>> callback);

}
