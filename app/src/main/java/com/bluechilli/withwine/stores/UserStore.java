package com.bluechilli.withwine.stores;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.bluechilli.withwine.events.NotificationSettingChangedEvent;
import com.bluechilli.withwine.events.UserStatusChangedEvent;
import com.bluechilli.withwine.interfaces.IContext;
import com.bluechilli.withwine.jobs.UserLoginJob;
import com.bluechilli.withwine.models.Constants;
import com.bluechilli.withwine.models.CreditCardDetail;
import com.bluechilli.withwine.models.Login;
import com.bluechilli.withwine.models.MessageResult;
import com.bluechilli.withwine.models.PaymentDetail;
import com.bluechilli.withwine.models.SigninStatus;
import com.bluechilli.withwine.models.User;
import com.bluechilli.withwine.services.AccountService;
import com.bluechilli.withwine.utils.ApiAdapter;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;

import de.greenrobot.event.EventBus;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by monishi on 9/01/15.
 */


public final class UserStore extends BaseStore {


    private static UserStore ourInstance = new UserStore();

    public static UserStore getInstance() {
        return ourInstance;
    }

    private User user;
    private AccountService accountService;
    private Object lock = new Object();

    private UserStore() {
        accountService = ApiAdapter.create(Constants.getApiURL(), AccountService.class);

    }

    public User getUser() {

        if(user != null) {
            return user;
        }

        final SharedPreferences preferences = getPreferences();
        return  getUserFromPreferences(preferences);
    }

    private SharedPreferences getPreferences() {
        return getContext().context().getSharedPreferences(Constants.PREFERENCE_DATA, Context.MODE_PRIVATE);
    }

    private void setUser(User user) {
        synchronized (lock) {
            this.user = user;
            final SharedPreferences preferences = getPreferences();
            saveUserToPreferences(preferences, user);
        }
    }



    private User getUserFromPreferences(SharedPreferences preferences) {

        try {
            String s =  preferences.getString(Constants.USER_DATA, null);
            Gson gson = new Gson();

            if(!TextUtils.isEmpty(s)) {
                User u = gson.fromJson(s , User.class);
                return u;
            }
        }
        catch (JsonSyntaxException ex) {

        }


        return null;
    }


    private void saveUserToPreferences(SharedPreferences preferences, User user) {
        Gson gson = new Gson();
        SharedPreferences.Editor edit = preferences.edit();
        edit.putString(Constants.USER_DATA, gson.toJson(user));
        edit.commit();
    }

    public static void loginInBackground(User user, Callback<Login> callback) {
        AccountService service = ApiAdapter.create(Constants.getApiURL(), AccountService.class);
        Login login = user.getLogin();
        service.login(login, callback);
    }

    public void login(User user, final Callback<User> callback) {

        User user1 = this.getUser();

        if(user1 == null) {
            AccountService service = ApiAdapter.create(Constants.getApiURL(), AccountService.class);
            Login login = user.getLogin();

            final User user2 = user;

            service.login(login , new Callback<Login>() {
                @Override
                public void success(Login login, Response response) {
                    user2.updateFromLogin(login);
                    EventBus.getDefault().post(new UserStatusChangedEvent(User.UserStatus.LoggedIn, user2));
                    callback.success(user2, response);
                }

                @Override
                public void failure(RetrofitError error) {
                    callback.failure(error);
                }
            });
        }
        else {
            getContext().jobManager().addJobInBackground(new UserLoginJob(user));
            EventBus.getDefault().post(new UserStatusChangedEvent(User.UserStatus.Authenticated, user));
            callback.success(user, null);
        }
    }

    public void saveNotificationSettings(boolean enableNotification , final Callback<MessageResult> callback) {
        accountService.saveNotificationSettings(user.enablePushNotification, new Callback<MessageResult>() {
            @Override
            public void success(MessageResult messageResult, Response response) {
                EventBus.getDefault().post(new NotificationSettingChangedEvent(user));
                callback.success(messageResult, response);
            }

            @Override
            public void failure(RetrofitError error) {
                callback.failure(error);
            }
        });
    }


    public void onEvent(UserStatusChangedEvent event) {
        this.user = event.getUser();
        switch(event.getStatus()) {
            case Authenticated:
                setUser(user);
            case LoggedIn:
                User user = getUserFromPreferences(getPreferences());
                if(user != null) {
                    user.updateFromLogin(event.getUser().getLogin());
                    setUser(user);
                }
                else {
                   setUser(event.getUser());
                }
                EventBus.getDefault().post(new UserStatusChangedEvent(User.UserStatus.LoggedInCompleted, user));
                break;
            case Deleted:
                removeAccount();
                break;
            case LoggedOut:
                break;
            default:
                break;
        }

    }

    private void removeAccount() {
        AsyncTask<Void,Void,Void> task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                SharedPreferences.Editor edit = getPreferences().edit();
                edit.clear();
                edit.apply();
                return null;
            }
        };

        task.execute();
        user = null;
    }

    public void onEvent(NotificationSettingChangedEvent event) {
        SharedPreferences p = getContext().context().getSharedPreferences(Constants.USER_DATA, Context.MODE_PRIVATE);
        User user = getUserFromPreferences(p);
        user.enablePushNotification = event.getUser().enablePushNotification;
        saveUserToPreferences(p, user);
        this.user = user;
    }

    @Override
    public void start(IContext context) {
        super.start(context);
        EventBus.getDefault().register(this);
    }

    @Override
    public void stop() {
        EventBus.getDefault().unregister(this);
        super.stop();
    }


}
