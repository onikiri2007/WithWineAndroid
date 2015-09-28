package com.bluechilli.withwine;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bluechilli.withwine.commands.BackMenuCommand;
import com.bluechilli.withwine.commands.ChangeActionBarTitleCommand;
import com.bluechilli.withwine.commands.DrawerMenuCommand;
import com.bluechilli.withwine.commands.LoadingCommand;
import com.bluechilli.withwine.commands.MenuCommand;
import com.bluechilli.withwine.commands.RetryCommand;
import com.bluechilli.withwine.commands.RetryRequestCommand;
import com.bluechilli.withwine.events.UserStatusChangedEvent;
import com.bluechilli.withwine.interfaces.IHandleAction;
import com.bluechilli.withwine.interfaces.IHandleLoadingIndicator;
import com.bluechilli.withwine.interfaces.IHandleMenu;
import com.bluechilli.withwine.interfaces.IHandleRetry;
import com.bluechilli.withwine.interfaces.RetryListener;
import com.bluechilli.withwine.jobs.RegisterNotificationTokenJob;
import com.bluechilli.withwine.managers.NetworkManager;
import com.bluechilli.withwine.models.Constants;
import com.bluechilli.withwine.models.MenuItems;
import com.bluechilli.withwine.models.User;
import com.bluechilli.withwine.stores.UserStore;
import com.bluechilli.withwine.utils.GooglePlayApi;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.gson.Gson;

import java.io.IOException;

import de.greenrobot.event.EventBus;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class BaseActivity extends AppCompatActivity implements IHandleLoadingIndicator, IHandleMenu, IHandleRetry, NavigationView.OnNavigationItemSelectedListener {

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    protected DrawerLayout drawerLayout;
    private User user;
    private String name = "";
    private View loadingView;
    protected Menu menu;
    protected boolean enableMenuItem;
    private View retryView;
   // private TextView retryTextView;
  //  private ImageButton retryButtonView;
    private RetryListener retryListener;
    private Toolbar toolbar;
    private NavigationView drawerView;
    private ImageButton retryButton;
    private CoordinatorLayout coordinatorLayout;
    GoogleCloudMessaging gcm;

    protected int baseLayout() {
        return R.layout.app_layout;
    }

    protected int getDrawerMenuLayout() {
        if(isApp()) {
            return R.menu.drawer_app;
        }

        return R.menu.drawer;
    }

    protected boolean isApp() {
        User user = UserStore.getInstance().getUser();

        if(user == null) {
            return false;
        }

        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(baseLayout());
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawerView = (NavigationView) findViewById(R.id.drawer_view);
        drawerView.inflateMenu(getDrawerMenuLayout());
        setupDrawerHeader(UserStore.getInstance().getUser(), drawerView);
        drawerView.setNavigationItemSelectedListener(this);
        final ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
            actionBar.setHomeButtonEnabled(true);
        }
        loadingView = findViewById(R.id.loadingView);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.main_frame);
        //retryView = findViewById(R.id.retry_view);
       // retryTextView = (TextView) findViewById(R.id.retryTextView);
       // retryButtonView = (ImageButton) findViewById(R.id.retry_button);
       /* retryButtonView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(NetworkManager.getInstance().hasNetworkConnection()) {
                    if(retryListener != null) {
                        retryView.animate().alpha(0f).setDuration(Constants.DEFAULT_FADE_IN_OUT_ANIMATION_DURATION).withEndAction(new Runnable() {
                            @Override
                            public void run() {
                                retryView.setVisibility(View.GONE);
                            }
                        });
                        retryListener.retry();
                        retryListener = null;
                    }
                    else {
                        EventBus.getDefault().post(new RetryCommand());
                    }
                }
                else {
                    Snackbar.make(coordinatorLayout, getString(R.string.network_error), Snackbar.LENGTH_LONG).show();
                    hideLoading();
                }
            }
        });*/
    }

    private void setupDrawerHeader(User user, NavigationView drawerView) {

        if(user != null && !user.isEmpty()) {
            TextView txtUserName = (TextView) drawerView.findViewById(R.id.user_name);
            txtUserName.setText(user.getFullName());
            TextView txtPhoneNumber = (TextView) drawerView.findViewById(R.id.phone_number);
            txtPhoneNumber.setText(user.getFullPhoneNumber());
        }
    }
/*
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }*/

    public boolean isDrawerOpen() {
        return drawerLayout.isDrawerOpen(GravityCompat.START);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch(item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void Logout() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    @Override
    public void showLoading(int colorResourceId) {

        final int c = colorResourceId;

        loadingView.animate().alpha(1f).setDuration(Constants.DEFAULT_FADE_IN_OUT_ANIMATION_DURATION).withStartAction(new Runnable() {
            @Override
            public void run() {
                loadingView.setVisibility(View.VISIBLE);
                loadingView.setBackgroundResource(c);
            }
        });

    }

    @Override
    public void hideLoading() {

        loadingView.animate().alpha(0f).setDuration(Constants.DEFAULT_FADE_IN_OUT_ANIMATION_DURATION).withEndAction(new Runnable() {
            @Override
            public void run() {
                loadingView.setVisibility(View.GONE);
            }
        });
    }


    @Override
    public void enableBack(boolean enabled)
    {
        getSupportActionBar().setDisplayHomeAsUpEnabled(enabled);
        getSupportActionBar().setHomeButtonEnabled(enabled);
    }

    @Override
    public void enableMenu(boolean enabled) {
        enableMenuItem = enabled;
        this.invalidateOptionsMenu();
    }

    @Override
    public void enableDrawerMenu(boolean enabled) {
        getSupportActionBar().setDisplayHomeAsUpEnabled(enabled);
        getSupportActionBar().setHomeButtonEnabled(enabled);
    }

    @Override
    public void setTitle(String title) {
        this.getSupportActionBar().setTitle(title);
    }

    @Override
    public void showRetry(String message) {
        loadingView.setVisibility(View.GONE);
        final Snackbar bar = Snackbar.make(coordinatorLayout, message, Snackbar.LENGTH_LONG);
        bar.setActionTextColor(getResources().getColor(R.color._text_color));
        bar.setAction("Retry", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (NetworkManager.getInstance().hasNetworkConnection()) {
                    if (retryListener != null) {
                        retryListener.retry();
                        retryListener = null;
                        bar.dismiss();
                    } else {
                        EventBus.getDefault().post(new RetryCommand());
                    }
                } else {
                    hideLoading();
                    bar.dismiss();
                }
            }
        });

        bar.show();

//        retryTextView.setText(message);
  /*      retryView.animate().alpha(1f).setDuration(Constants.DEFAULT_FADE_IN_OUT_ANIMATION_DURATION).withStartAction(new Runnable() {
            @Override
            public void run() {
                retryView.setVisibility(View.VISIBLE);
            }
        });*/
    }

    @Override
    public void showRetry(String message, RetryListener listener) {
       retryListener = listener;
       showRetry(message);
    }

    @Override
    public void hideRetry() {

        retryView.animate().alpha(0f).setDuration(Constants.DEFAULT_FADE_IN_OUT_ANIMATION_DURATION).withEndAction(new Runnable() {
            @Override
            public void run() {
                retryView.setVisibility(View.GONE);
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onPause() {
        EventBus.getDefault().unregister(this);
        super.onPause();
    }

    //Event Handlers

    public void onEventMainThread(MenuCommand event) {
        enableMenu(event.isEnabled());
    }

    public void onEventMainThread(DrawerMenuCommand event) {
        enableDrawerMenu(event.isEnabled());
    }

    public void onEventMainThread(BackMenuCommand event) {
        enableBack(event.isEnabled());
    }

    public void onEventMainThread(RetryRequestCommand event) {

        if(event.isShow()) {
            retryListener = event.getListener();
            showRetry(event.getMessage());
        }
        else {
            hideRetry();
        }
    }

    public void onEventMainThread(LoadingCommand event) {

        if(event.isShow()) {
            showLoading(event.getBackgroundColorResourceId());
        }
        else {
            hideLoading();
        }
    }
    public void onEventMainThread(ChangeActionBarTitleCommand cmd) {
        this.setTitle(cmd.getTitle());
    }

    public void registerForPushNotification() {

        GooglePlayApi api = new GooglePlayApi();

        if(api.servicesConnected(this)) {
            String registrationId = getRegistrationId();

            if(registrationId.isEmpty()) {
                gcm = GoogleCloudMessaging.getInstance(this);
                registerNotificationInBackground();
            }
        }
    }

    private void registerNotificationInBackground() {

        final AsyncTask<Void, Void, String> registrationTask = new AsyncTask<Void, Void, String>() {


            @Override
            protected String doInBackground(Void... params) {
                String registrationId = "";
                try {
                    registrationId = gcm.register(WithWineAppApplication.getInstance().getApplicationContext().getString(R.string.sender_id));
                    WithWineAppApplication.getInstance().jobManager().addJobInBackground(new RegisterNotificationTokenJob(registrationId));
                }
                catch(IOException ex) {
                    Log.d(Constants.TAG, ex.getLocalizedMessage());
                }

                return registrationId;
            }
        };

        registrationTask.execute();
    }

    private String getRegistrationId() {
        final SharedPreferences preferences = getPreferences();
        String registrationId = preferences.getString(Constants.PROPERTY_REG_ID, "");

        if(TextUtils.isEmpty(registrationId)) {
            Log.d(Constants.TAG, "registrationId was not found");
            return "";
        }
        else {
            if(isAppVersionChanged()) {
                Log.d(Constants.TAG, "App version has changed");
                return "";
            }
        }

        return registrationId;
    }

    private boolean isAppVersionChanged() {
        boolean result = false;
        final SharedPreferences pref = getPreferences();
        int version = pref.getInt(Constants.PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = WithWineAppApplication.getAppVersion(this);

        if(currentVersion != version) {
            result = true;
        }

        return result;
    }


    private SharedPreferences getPreferences() {
        return getSharedPreferences(Constants.USER_DATA,MODE_PRIVATE);
    }


    public void onEventMainThread(UserStatusChangedEvent event) {
        if(event.getStatus() == User.UserStatus.LoggedOut || event.getStatus() == User.UserStatus.Deleted) {
            this.Logout();
        }
    }

    protected boolean canGoBack(MenuItem item) {
        int id = item.getItemId();
        return id == R.id.home || !(drawerView.isEnabled() && item.getTitle() == null);
    }

    protected boolean popback(MenuItem item) {
        boolean canGoBack = false;
        if(this.canGoBack(item)) {
            getSupportFragmentManager().popBackStack();
            canGoBack = true;
        }

        return canGoBack;
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        return false;
    }
}
