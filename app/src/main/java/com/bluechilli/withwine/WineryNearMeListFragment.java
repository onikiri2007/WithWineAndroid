package com.bluechilli.withwine;


import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.internal.widget.AdapterViewCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bluechilli.withwine.adapters.BaseArrayAdapter;
import com.bluechilli.withwine.events.OnLocationUpdated;
import com.bluechilli.withwine.interfaces.RetryListener;
import com.bluechilli.withwine.models.Constants;
import com.bluechilli.withwine.models.Destination;
import com.bluechilli.withwine.stores.WineryStore;
import com.bluechilli.withwine.utils.DateUtils;

import java.util.Collection;
import java.util.Date;

import de.greenrobot.event.EventBus;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link WineryNearMeListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WineryNearMeListFragment extends BaseListFragment implements AdapterViewCompat.OnItemClickListener, SwipeRefreshLayout.OnRefreshListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String PAGE_NUMBER = "PAGE_NUMBER";
    private static final String CURRENT_LOCATION = "CURRENT_LOCATION";
    public static final String TAG = "WineryNearMeListFragment";
    // TODO: Rename and change types of parameters
    private int pageNumber = 0;
    private BaseAdapter adapter;
    private Location currentLocation;
    private Collection<Destination> destinations;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Date lastUpdated = null;
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param pageNumber Parameter 1.
     * @param currentLocation
     * @return A new instance of fragment WineryNearMeListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static Fragment newInstance(int pageNumber, Location currentLocation) {
        Fragment fragment = new WineryNearMeListFragment();
        Bundle args = new Bundle();
        args.putInt(PAGE_NUMBER, pageNumber);
        args.putParcelable(CURRENT_LOCATION, currentLocation);
        fragment.setArguments(args);
        return fragment;
    }

    public WineryNearMeListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            pageNumber = getArguments().getInt(PAGE_NUMBER);
            currentLocation = getArguments().getParcelable(CURRENT_LOCATION);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_winery_list, container, false);

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.winery_swipe_to_refresh_layout);
        swipeRefreshLayout.setColorSchemeResources(R.color._accent);
        swipeRefreshLayout.setOnRefreshListener(this);

        setupListDataSource();

        return view;
    }

    private void setupListDataSource() {
        WineryStore.getInstance().getDestinations(currentLocation, lastUpdated,  new Callback<Collection<Destination>>() {
            @Override
            public void success(Collection<Destination> destinations, Response response) {

                WineryNearMeListFragment.this.destinations = destinations;
                lastUpdated = DateUtils.getUTCDateTimeAsDate();
                updateUI();
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d(error.getMessage(), Constants.TAG);
                showRetry(getString(R.string.network_error), new RetryListener() {
                    @Override
                    public void retry() {
                        setupListDataSource();
                    }
                });
            }
        });

    }

    @Override
    public void onItemClick(AdapterViewCompat<?> parent, View view, int position, long id) {

    }

    public void onEventMainThread(OnLocationUpdated event) {
        currentLocation = event.getLocation();
        updateUI();
    }

    private void updateUI() {

        if(destinations != null) {
            WineryStore.getInstance().determineDistanceFromCurrentLocation(destinations, currentLocation);
        }

        if(adapter == null) {
            adapter = new WineryNearMeAdapter(getActivity(), R.layout.winery_near_me_list_item, destinations);
            setListAdapter(adapter);
        }
        else {
            adapter.notifyDataSetChanged();
        }

    }

    @Override
    public void onPause() {
        EventBus.getDefault().unregister(this);
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onRefresh() {
        setupListDataSource();
    }


    class WineryNearMeAdapter extends BaseArrayAdapter<Destination> {
        WineryNearMeViewHolder viewHolder;

        public WineryNearMeAdapter(Context context, int resourceId, Collection<Destination> data) {
            super(context, resourceId, data);
        }

        @Override
        public int getCount() {
            if(data != null) {
                return data.size();
            }

            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if(convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = (View) inflater.inflate(this.resourceId, parent, false);
                viewHolder = new WineryNearMeViewHolder();
                viewHolder.wineryName = (TextView) convertView.findViewById(R.id.winery_name);
                viewHolder.locationName = (TextView) convertView.findViewById(R.id.location_name);
                viewHolder.distance = (TextView) convertView.findViewById(R.id.distance);
                convertView.setTag(viewHolder);
            }
            else {
                viewHolder = (WineryNearMeViewHolder) convertView.getTag();
            }
            Destination dest = getItem(position);

            if(dest != null) {
                viewHolder.bind(dest);
            }
            return convertView;
        }


    }

    static class WineryNearMeViewHolder {
        public TextView wineryName;
        public TextView locationName;
        public TextView distance;

        public void bind(Destination item) {
            wineryName.setText(item.brandName);
            locationName.setText(item.locationTitle);
            distance.setText(String.format("%f km", item.distance / 1000));
        }
    }
}
