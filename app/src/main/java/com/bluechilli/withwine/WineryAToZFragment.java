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
import com.bluechilli.withwine.models.Brand;
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
public class WineryAToZFragment extends BaseListFragment implements AdapterViewCompat.OnItemClickListener, SwipeRefreshLayout.OnRefreshListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String PAGE_NUMBER = "PAGE_NUMBER";
    public static final String TAG = "WineryAToZFragment";
    // TODO: Rename and change types of parameters
    private int pageNumber = 0;
    private BaseAdapter adapter;
    private Location currentLocation;
    private Collection<Brand> brands;
    private SwipeRefreshLayout swipeRefreshLayout;
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param pageNumber Parameter 1.
     * @param currentLocation
     * @return A new instance of fragment WineryNearMeListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static Fragment newInstance(int pageNumber) {
        Fragment fragment = new WineryAToZFragment();
        Bundle args = new Bundle();
        args.putInt(PAGE_NUMBER, pageNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public WineryAToZFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            pageNumber = getArguments().getInt(PAGE_NUMBER);
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
        WineryStore.getInstance().getBrands(new Callback<Collection<Brand>>() {
            @Override
            public void success(Collection<Brand> brands, Response response) {
                WineryAToZFragment.this.brands = brands;
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

    private void updateUI() {

        if(adapter == null) {
            adapter = new WineryAToZAdapter(getActivity(), R.layout.winery_a_to_z_list_item, brands);
            setListAdapter(adapter);
        }
        else {
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onRefresh() {
        setupListDataSource();
    }


    class WineryAToZAdapter extends BaseArrayAdapter<Brand> {
        WineryAToZViewHolder viewHolder;

        public WineryAToZAdapter(Context context, int resourceId, Collection<Brand> data) {
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
                viewHolder = new WineryAToZViewHolder();
                viewHolder.wineryName = (TextView) convertView.findViewById(R.id.winery_name);
                convertView.setTag(viewHolder);
            }
            else {
                viewHolder = (WineryAToZViewHolder) convertView.getTag();
            }
            Brand dest = getItem(position);

            if(dest != null) {
                viewHolder.bind(dest);
            }
            return convertView;
        }


    }

    static class WineryAToZViewHolder {
        public TextView wineryName;

        public void bind(Brand item) {
            wineryName.setText(item.name);
        }
    }
}
