package com.halfwitdevs.countripedia;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

public class CountryListFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_country_list, container, false);

        if(getArguments() != null) {
            final CountryNames[] array = (CountryNames[]) getArguments().getParcelableArray("COUNTRYLIST");
            ListView countryListView = view.findViewById(R.id.country_list_view);
            try {
                final CountrySearchListAdapter adapter = new CountrySearchListAdapter(getContext(), array, true);
                countryListView.setAdapter(adapter);
                countryListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        if(array != null) {
                            CountryNames selected = array[position];
                            Intent intent = new Intent(getContext(), CountryInfoDisplayActivity.class);
                            intent.putExtra("COUNTRYCODE", selected.alpha2Code);
                            startActivity(intent);
                        }
                    }
                });
            } catch(NullPointerException e) {

            }
        }

        return view;
    }
}
