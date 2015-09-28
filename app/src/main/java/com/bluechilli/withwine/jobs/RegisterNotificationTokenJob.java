package com.bluechilli.withwine.jobs;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.bluechilli.withwine.WithWineAppApplication;
import com.bluechilli.withwine.models.Constants;
import com.bluechilli.withwine.models.MessageResult;
import com.bluechilli.withwine.services.AccountService;
import com.bluechilli.withwine.utils.ApiAdapter;
import com.bluechilli.withwine.utils.Installation;
import com.path.android.jobqueue.Job;
import com.path.android.jobqueue.Params;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by monishi on 23/02/15.
 */
public class RegisterNotificationTokenJob extends Job {

    private final String registrationId;

    public RegisterNotificationTokenJob(String registrationId) {
        super(new Params(1));
        this.registrationId = registrationId;
    }

    @Override
    public void onAdded() {

    }

    @Override
    public void onRun() throws Throwable {
        AccountService service = ApiAdapter.create(Constants.getApiURL(), AccountService.class);

        service.registerNotification(registrationId , Installation.getUniqueId(WithWineAppApplication.getInstance().getApplicationContext()), "Google", new Callback<MessageResult>() {
            @Override
            public void success(MessageResult result, Response response) {
                final SharedPreferences pref = WithWineAppApplication.getInstance().getSharedPreferences(Constants.USER_DATA, Context.MODE_PRIVATE);

                SharedPreferences.Editor editor = pref.edit();
                editor.putString(Constants.PROPERTY_REG_ID, registrationId);
                editor.putInt(Constants.PROPERTY_APP_VERSION, WithWineAppApplication.getAppVersion(WithWineAppApplication.getInstance().getApplicationContext()));
                editor.commit();
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d(Constants.TAG, error.getMessage());
            }
        });

    }

    @Override
    protected void onCancel() {

    }

    @Override
    protected boolean shouldReRunOnThrowable(Throwable throwable) {
        return true;
    }
}
