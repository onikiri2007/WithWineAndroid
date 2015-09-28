package com.bluechilli.withwine.jobs;

import com.bluechilli.withwine.events.UserStatusChangedEvent;
import com.bluechilli.withwine.models.Login;
import com.bluechilli.withwine.models.User;
import com.bluechilli.withwine.stores.UserStore;
import com.path.android.jobqueue.Job;
import com.path.android.jobqueue.Params;

import de.greenrobot.event.EventBus;
import retrofit.Callback;
import retrofit.RetrofitError;

/**
 * Created by monishi on 13/01/15.
 */
public class UserLoginJob extends Job {

    private User user;

    public UserLoginJob(User user) {
        super(new Params(1)
                .requireNetwork());

        this.user = user;
    }
    @Override
    public void onAdded() {

    }

    @Override
    public void onRun() throws Throwable {
        UserStore.loginInBackground(user, new Callback<Login>() {
            @Override
            public void success(Login login1, retrofit.client.Response response) {
                User user = new User();
                user.updateFromLogin(login1);
                EventBus.getDefault().post(new UserStatusChangedEvent(User.UserStatus.LoggedIn, user));
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }

    @Override
    protected void onCancel() {

    }

    @Override
    protected boolean shouldReRunOnThrowable(Throwable throwable) {
        return false;
    }
}
