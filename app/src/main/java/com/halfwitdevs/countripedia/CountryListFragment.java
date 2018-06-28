package com.halfwitdevs.countripedia;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;

public class CountryListFragment extends Fragment {
    public MaterialSearchView materialSearchView;
    CountrySearchListAdapter adapter;
    MenuItem refresh;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_country_list, container, false);

        materialSearchView = getActivity().findViewById(R.id.search_view);
        materialSearchView.setHint("SEARCH");

        if (PreferenceManager.getDefaultSharedPreferences(getContext()).getBoolean("prefTheme", false)) {
            materialSearchView.setBackgroundColor(getResources().getColor(R.color.primary_dark_material_dark_dark));
            materialSearchView.setTextColor(Color.WHITE);
            materialSearchView.setHintTextColor(Color.WHITE);
            materialSearchView.setBackIcon(getResources().getDrawable(R.drawable.ic_action_navigation_arrow_back_inverted));
        }

        setHasOptionsMenu(true);

        if(getArguments() != null) {
            final Parcelable[] parcelableArray = getArguments().getParcelableArray("COUNTRYLIST");

            final ListView countryListView = view.findViewById(R.id.country_list_view);
            try {
                final CountryNames[] array =  Arrays.copyOf(parcelableArray, parcelableArray.length, CountryNames[].class);
                adapter = new CountrySearchListAdapter(getContext(), array, true);
                countryListView.setAdapter(adapter);
                countryListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        CountryNames[] nameArray = adapter.getNameArray();
                        if (nameArray != null) {
                            CountryNames selected = nameArray[position];
                            Intent intent = new Intent(getContext(), CountryInfoDisplayActivity.class);

                            intent.putExtra("COUNTRYCODE", selected.alpha2Code);
                            intent.putExtra("COUNTRYNAME", selected.name);
                            startActivity(intent);
                        }
                    }
                });

                            //search stuff

                materialSearchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
                    @Override
                    public void onSearchViewShown() {
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
                            for (CountryNames name : array) {
                                if (name.name.toLowerCase().contains(newText.toLowerCase())) {
                                    filtered.add(name);
                                }
                            }
                            searchText = newText.toLowerCase();
                        }

                        Collections.sort(filtered, new Comparator<CountryNames>() {
                            @Override
                            public int compare(CountryNames o1, CountryNames o2) {
                                return Integer.valueOf(o1.name.toLowerCase().indexOf(searchText)).compareTo(o2.name.toLowerCase().indexOf(searchText));
                            }
                        });

                        adapter =
                                new CountrySearchListAdapter(getContext(),
                                        filtered.toArray(new CountryNames[filtered.size()]), true);
                        countryListView.setAdapter(adapter);

                        return true;
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
        final MenuItem menuItem = menu.findItem(R.id.filter_search);
        materialSearchView.setMenuItem(menuItem);
        refresh = menu.findItem(R.id.refresh_button);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.refresh_button) {
            getActivity().recreate();
        }
        return super.onOptionsItemSelected(item);
    }


}
