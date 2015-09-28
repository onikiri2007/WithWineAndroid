package com.bluechilli.withwine.models;

import com.bluechilli.withwine.BuildConfig;

/**
 * Created by monishi on 23/12/2014.
 */
public class Constants {

    public static final String PREFERENCE_DATA = "com.bluechilli.withwine.data";
    public static final String USER_DATA = "USER";
    public static final String API_URL = "https://dev.bluechilli.com/lifewithwine/api";
    public static final String API_STAGING_URL = "https://staging.bluechilli.com/lifewithwine/api";
    public static final String API_PRODUCTION_URL = "https://www.lifewithwine.com/api";
    public static final String API_KEY = "AF7EE5F7-5D69-4F11-8947-3237FE67ACCF";
    public static final String API_SALT = "NJrusrzPRkijVS6fz6mIZw";
    public static final String DEFAULT_COUNTRY = "MY";
    public static final long SMS_TIMEOUT_IN_MILLI_SECOND = 10000;
    public static final int DEFAULT_FADE_IN_OUT_ANIMATION_DURATION = 200;
    public static final String PROPERTY_REG_ID = "registration_id";
    public static final String PROPERTY_APP_VERSION = "appVersion";
    public static final String TAG = "WITH_WINE_LOG";
    public final static String PHONE_NUMBER = "phoneNumber";
    public static final String PLAN = "plan" ;
    public static final int CREDIT_CARD_REQUEST_CODE = 90;
    public static final String CREDIR_CARD_FLOW_OPTION = "CREDIT_CARD_FLOW_OPTION";
    public static final String PAYMENT_DETAIL = "PAYMENT_DETAIL" ;
    public static final String ACTION_BAR_TITLE = "ACTION_BAR_TITLE";
    public static final int DEFAULT_COUNTRY_CODE = 61 ;

    public static String getApiURL() {
        if(BuildConfig.BUILD_TYPE.equalsIgnoreCase("debug")) {
            return API_URL;
        }
        else if(BuildConfig.BUILD_TYPE.equalsIgnoreCase("staging")) {
            return API_STAGING_URL;
        }
        else {
            return API_PRODUCTION_URL;
        }
    }
}

