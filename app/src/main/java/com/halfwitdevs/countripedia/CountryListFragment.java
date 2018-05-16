package com.halfwitdevs.countripedia;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;

public class CountryListFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_country_list, container, false);

        if(getArguments() != null) {
            CountryNames[] array = (CountryNames[]) getArguments().getParcelableArray("COUNTRYLIST");
            ListView countryListView = view.findViewById(R.id.country_list_view);
            try {
                countryListView.setAdapter(new CountrySearchListAdapter(getContext(), array, true));
            } catch(NullPointerException e) {

            }
        }

        return view;
    }
}
