package com.halfwitdevs.countripedia;

import android.content.Intent;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;

public class CountryListFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_country_list, container, false);

        setHasOptionsMenu(true);

        if(getArguments() != null) {
            final Parcelable[] parcelableArray = getArguments().getParcelableArray("COUNTRYLIST");

            ListView countryListView = view.findViewById(R.id.country_list_view);
            try {
                final CountryNames[] array =  Arrays.copyOf(parcelableArray, parcelableArray.length, CountryNames[].class);
                final CountrySearchListAdapter adapter = new CountrySearchListAdapter(getContext(), array, true);
                countryListView.setAdapter(adapter);
                countryListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        if(array != null) {
                            CountryNames selected = array[position];
                            Intent intent = new Intent(getContext(), CountryInfoDisplayActivity.class);

                            //search stuff
                /*
                materialSearchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
                    @Override
                    public void onSearchViewShown() {
                        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                    }

                    @Override
                    public void onSearchViewClosed() {

                    }
                });

                materialSearchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
                    private String searchText = null;
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        Vector<CountryNames> filtered = new Vector<>();
                        if (newText != null) {
                            for (CountryNames name : nameList) {
                                if (name.name.toLowerCase().contains(newText.toLowerCase())) {
                                    filtered.add(name);
                                }
                            }
                            searchText = newText.toLowerCase();
                        }

                        Collections.sort(filtered, new Comparator<CountryNames>() {
                            @Override
                            public int compare(CountryNames o1, CountryNames o2) {
                                return Integer.valueOf(o1.name.indexOf(searchText)).compareTo(o2.name.indexOf(searchText));
                            }
                        });

                        adapter =
                                new CountryListAdapter(CountrySearchActivity.this,
                                        filtered.toArray(new CountryNames[filtered.size()]), getFlags);
                        countryList.setAdapter(adapter);

                        return true;
                    }
                });
                */

                            intent.putExtra("COUNTRYCODE", selected.alpha2Code);
                            intent.putExtra("COUNTRYNAME", selected.name);
                            startActivity(intent);
                        }
                    }
                });
            } catch(NullPointerException e) {
                e.printStackTrace();

            }
        }

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.search_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }
}
