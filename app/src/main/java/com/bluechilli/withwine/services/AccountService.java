package com.bluechilli.withwine.services;

import com.bluechilli.withwine.models.CreditCardDetail;
import com.bluechilli.withwine.models.Login;
import com.bluechilli.withwine.models.MessageResult;
import com.bluechilli.withwine.models.PaymentDetail;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;

/**
 * Created by monishi on 13/01/15.
 */
public interface AccountService {

    @POST("/accounts/login")
    void login(@Body Login user, Callback<Login> callback);

    @FormUrlEncoded()
    @POST("/accounts/registerToken")
    void registerNotification(@Field("token") String token, @Field("deviceId") String deviceId, @Field("notificationProvider") String notificationProvider, Callback<MessageResult> callback);

    @POST("/accounts/logout")
    void logout(Callback<MessageResult> callback);

    @POST("/accounts/account/delete")
    void deleteAccount(Callback<MessageResult> callback);

    @POST("/accounts/payment_detail")
    void savePaymentDetail(@Body CreditCardDetail cardDetail, Callback<PaymentDetail> detail);

    @FormUrlEncoded()
    @POST("/accounts/notification")
    void saveNotificationSettings(@Field("isNotificationEnabled") boolean isNotificationEnabled, Callback<MessageResult> detail);

}

