package com.bluechilli.withwine;

import android.location.Location;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.bluechilli.withwine.events.OnLocationUpdated;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import de.greenrobot.event.EventBus;


public class WineryActivity extends BaseActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    public static final String LAST_CURRENT_LOCATION = "CURRENT_LOCATION";

    private TabLayout tabLayout;
    private ViewPager pager;
    private PagerAdapter adapter;
    private GoogleApiClient client;
    private Location currentLocation;
    private LocationRequest locationRequest;

    @Override
    protected int baseLayout() {

        return R.layout.activity_winaries;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        updateLocationFromSavedState(savedInstanceState);
        this.getSupportActionBar().setDisplayShowTitleEnabled(false);
        setupTabs();
        setupLocationManager();
    }

    private void updateLocationFromSavedState(Bundle savedInstanceState) {
        if(savedInstanceState != null) {
            currentLocation = savedInstanceState.getParcelable(LAST_CURRENT_LOCATION);
        }
    }

    private void setupLocationManager() {
        client = new GoogleApiClient
                .Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    private void setupPager(Location currentLocation) {
        pager = (ViewPager) findViewById(R.id.pager);
        adapter = new WineryPager(getSupportFragmentManager(), currentLocation);
        pager.setAdapter(adapter);
        tabLayout.setupWithViewPager(pager);
    }

    private void setupTabs() {
        tabLayout = (TabLayout) findViewById(R.id.tabs);

        tabLayout.addTab(tabLayout.newTab().setText("NEAR ME"), true);
        tabLayout.addTab(tabLayout.newTab().setText("A-Z"));
        tabLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(client.isConnected()) {
            stopLocationUpdate();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        if(client.isConnected()) {
            startLocationUpdate();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        client.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        client.disconnect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        if(currentLocation == null) {
            currentLocation =  LocationServices.FusedLocationApi.getLastLocation(client);
        }

        setupPager(currentLocation);

        createLocationRequest();
    }

    private void startLocationUpdate() {
        LocationServices.FusedLocationApi.requestLocationUpdates(client, locationRequest, this);
    }

    private void stopLocationUpdate() {
        LocationServices.FusedLocationApi.removeLocationUpdates(client, this);
    }

    private void createLocationRequest() {
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        locationRequest.setInterval(1000);
        startLocationUpdate();
    }

    @Override
    public void onConnectionSuspended(int i) {
        client.connect();
    }

    @Override
    public void onLocationChanged(Location location) {
        currentLocation = location;
        EventBus.getDefault().post(new OnLocationUpdated(currentLocation));
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        setupPager(null);

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(currentLocation != null) {
            outState.putParcelable(LAST_CURRENT_LOCATION, currentLocation);
        }
    }

    public class WineryPager extends FragmentPagerAdapter {

        private final Location currentLocation;

        public WineryPager(FragmentManager fm, Location currentLocation) {
            super(fm);
            this.currentLocation = currentLocation;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 1:
                    return WineryAToZFragment.newInstance(position);
                default:
                    return WineryNearMeListFragment.newInstance(position, currentLocation);

            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 1:
                    return WineryActivity.this.getString(R.string.winery_tab_title_2);
                default:
                    return WineryActivity.this.getString(R.string.winery_tab_title_1);
            }
        }

        @Override
        public int getCount() {
            return 2;
        }
    }

}
