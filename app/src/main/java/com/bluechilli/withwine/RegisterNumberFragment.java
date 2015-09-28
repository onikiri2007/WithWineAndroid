package com.bluechilli.withwine;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RegisterNumberFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RegisterNumberFragment extends BaseFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    public static final String TAG = "REGISTER_NUMBER";

    public static RegisterNumberFragment newInstance() {
        RegisterNumberFragment fragment = new RegisterNumberFragment();
        return fragment;
    }

    public RegisterNumberFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.number_entry_layout, container, false);
    }


}
