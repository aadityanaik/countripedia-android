package com.halfwitdevs.countripedia;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Process;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.Toast;

import com.ahmadrosid.svgloader.SvgLoader;
import com.google.gson.Gson;

import java.util.concurrent.ExecutionException;

public class CountryInfoDisplayFragment extends Fragment {
    Country country;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        String countryInfoJson = null;
        View view = inflater.inflate(R.layout.fragment_country_info_display, container, false);

        if(getArguments() != null) {
            countryInfoJson = getArguments().getString("COUNTRYINFO");
        }

        if(countryInfoJson != null) {
            country = new Gson().fromJson(countryInfoJson, Country.class);
            try {
                new ImageLoader((ImageView) view.findViewById(R.id.country_flag_image)).run();
                final ExpandableInfoListAdapter adapter = new ExpandableInfoListAdapter(getContext(), country.getGroups(), country.getGroupsAndItems());
                ExpandableListView infoView = view.findViewById(R.id.country_info_exp_list);
                infoView.setAdapter(adapter);
                infoView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
                    @Override
                    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                        String groupName = adapter.getGroup(groupPosition).toString();
                        String childName = adapter.getChild(groupPosition, childPosition).toString();
                        Intent intent = new Intent(getContext(), MoreInfoActivity.class);
                        Bundle args = new Bundle();

                        switch (groupName) {
                            case "General Info":
                                switch ((childName.indexOf(':') != -1) ? childName.substring(0, childName.indexOf(':')) : childName) {
                                    case "Name":

                                        // action defines what the more info activity will show
                                        args.putString("ACTION", "MAP");

                                        // category is for the map... the Map fragment will accept a category of either the country or the capital
                                        // here country
                                        args.putString("CATEGORY", "COUNTRY");
                                        args.putString("LOC", country.name);
                                        args.putFloat("LAT", country.latlng[0]);
                                        args.putFloat("LNG", country.latlng[1]);
                                        args.putString("CODE", country.alpha2Code);

                                        intent.putExtras(args);


                                        startActivity(intent);

                                        break;

                                    case "Capital":
                                        // similar system to previous
                                        // however here we don't have the lat lng for capital
                                        // so we pass capital name & country name
                                        // and get the lat lng using geocoder

                                        args.putString("ACTION", "MAP");
                                        args.putString("CATEGORY", "CAPITAL");
                                        args.putString("CAPNAME", country.capital);
                                        args.putString("COUNTRYNAME", country.name);

                                        intent.putExtras(args);

                                        startActivity(intent);

                                        break;

                                    case "Region":
                                        // Quite similar to previous
                                        // passing subregion (if available) and region

                                        String rgn = "";

                                        if(country.subregion != null && !country.subregion.equals("")) {
                                            rgn += country.subregion + ",";
                                        }

                                        rgn += country.region;

                                        args.putString("ACTION", "MAP");
                                        args.putString("CATEGORY", "REGION");
                                        args.putString("RGN", rgn);

                                        intent.putExtras(args);

                                        startActivity(intent);

                                        break;

                                    case "Population":
                                        args.putString("ACTION", "POP");
                                        args.putString("CODE", country.alpha2Code);
                                        args.putString("NAME", country.name);
                                        intent.putExtras(args);
                                        startActivity(intent);

                                        break;

                                    case "Area":

                                        break;
                                }

                                break;

                            case "Calling Codes":
                                intent = new Intent(Intent.ACTION_DIAL);
                                intent.setData(Uri.parse("tel:+" + childName));
                                startActivity(intent);

                                break;

                            case "Borders":
                                String countryCode = country.borders[childPosition];
                                intent = new Intent(getContext(), CountryInfoDisplayActivity.class);
                                intent.putExtra("COUNTRYCODE", countryCode);
                                startActivity(intent);
                                break;

                            case "Languages":
                                args.putString("ACTION", "LANG");
                                args.putString("LANG", country.languages[childPosition].name);

                                intent.putExtras(args);
                                startActivity(intent);

                                break;

                            case "Currencies":
                                // Just send the currency code over
                                String currencyCode = country.currencies[childPosition].code;

                                args.putString("ACTION", "RATE");
                                args.putString("CODE", currencyCode);

                                intent.putExtras(args);
                                startActivity(intent);
                                break;
                        }
                        return true;
                    }
                });
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }

        return view;
    }

    private class ImageLoader implements Runnable {
        ImageView imageView;

        ImageLoader(ImageView flagView) {
            imageView = flagView;
        }

        @Override
        public void run() {
            android.os.Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);

            // wifi is selected
            SvgLoader.pluck()
                    .with((Activity) getContext())
                    .setPlaceHolder(R.drawable.progress_animation, R.drawable.progress_animation)
                    .load(country.flag, imageView);


        }
    }
}
