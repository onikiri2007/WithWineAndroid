package com.bluechilli.withwine.utils;

import com.bluechilli.withwine.WithWineAppApplication;
import com.bluechilli.withwine.models.Constants;
import com.bluechilli.withwine.models.User;
import com.bluechilli.withwine.stores.UserStore;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Date;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;

public final class ApiAdapter {

    public static <T> T create(String endpoint, Class<T> serviceClass ) {
        Gson gson = new GsonBuilder()
                .setExclusionStrategies( new SugarExclusionStrategy() )
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                .registerTypeAdapter(Date.class, new DateDeserializer())
                .create();
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setRequestInterceptor(new RequestInterceptor() {
                    @Override
                    public void intercept(RequestFacade request) {
                        request.addHeader("ApiKey", Constants.API_KEY);
                        request.addQueryParam("ApiKey", Constants.API_KEY);

                        User user = UserStore.getInstance().getUser();

                        if (user != null && user.userKey != null) {
                            request.addHeader("UserKey", String.format("%s", user.userKey));
                            request.addQueryParam("UserKey", String.format("%s", user.userKey));
                        }

//                        Date d = new Date();
  //                      request.addHeader("AccessDate", DateUtils.getUTCDateTimeAsString(d, DateUtils.DATE_FORMAT));
    //                    request.addHeader("Secret", CryptoUtils.get(String.format("%s%s", Constants.API_KEY, DateUtils.getUTCDateTimeAsString(d, "yyyyMMddHHmmss")), Constants.API_SALT));
                    }
                })
                .setEndpoint(endpoint).setConverter(new GsonConverter(gson))
                .setErrorHandler(new ApiErrorHandler(WithWineAppApplication.getInstance().getApplicationContext()))
                .build();
        return restAdapter.create( serviceClass );
    }
}
