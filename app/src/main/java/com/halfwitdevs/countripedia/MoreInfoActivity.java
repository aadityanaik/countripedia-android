package com.halfwitdevs.countripedia;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.EntryXComparator;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.LinkedTreeMap;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collections;

public class MoreInfoActivity extends AppCompatActivity {

    final ProgressFragment progress = new ProgressFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_info);

        Bundle args = getIntent().getExtras();

        switch (args.getString("ACTION")) {
            case "MAP":
                MapFragment mapFragment = new MapFragment();
                mapFragment.setArguments(args);

                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.info_container, mapFragment).commit();

                break;

            case "POP":
                // first get the data
                // then pass it to population fragment
                // till then the fragment displayed will be progress fragment

                new GetPopInfo().execute("https://api.worldbank.org/v2/countries/" + args.getString("CODE") + "/indicators/SP.POP.TOTL?format=json&MRV=20",
                        "https://api.worldbank.org/v2/countries/"+ args.getString("CODE") +"/indicators/SP.URB.TOTL.IN.ZS?format=json&MRV=1");
        }

    }

    private class GetPopInfo extends AsyncTask<String, Void, String[]> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.info_container, progress)
                    .commit();
        }

        @Override
        protected String[] doInBackground(String... strings) {
            String responseGrowth = "", responseDistribution = "";
            try {
                responseGrowth = new URLHandler(strings[0]).getResponse();
                responseDistribution = new URLHandler(strings[1]).getResponse();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            return new String[]{responseGrowth, responseDistribution};
        }

        @Override
        protected void onPostExecute(String[] strings) {
            super.onPostExecute(strings);

            GsonBuilder builder = new GsonBuilder();
            // Gson builder because the object may not have fixed values

            Object growth = builder.create().fromJson(strings[0], Object.class);
            Object distribution = builder.create().fromJson(strings[1], Object.class);

            // some weird shit
            ArrayList growthList, distributionList;

            ArrayList<Entry> growthEntries = new ArrayList<>();
            ArrayList<PieEntry> distributionEntries = new ArrayList<>();

            boolean success = false;
            int date;

            try {
                growthList = (ArrayList) ((ArrayList) growth).get(1);

                for(Object popLvl : growthList) {
                    LinkedTreeMap value = (LinkedTreeMap) popLvl;
                    growthEntries.add(new Entry(Float.parseFloat(value.get("date").toString()),
                            Float.parseFloat(value.get("value").toString())));
                }

                // ascending order of dates
                Collections.sort(growthEntries, new EntryXComparator());

                distributionList = (ArrayList) ((ArrayList) distribution).get(1);
                LinkedTreeMap value = (LinkedTreeMap) distributionList.get(0);
                distributionEntries.add(new PieEntry(Float.parseFloat(String.valueOf(value.get("value"))), "Urban"));
                distributionEntries.add(new PieEntry(100 - Float.parseFloat(String.valueOf(value.get("value"))), "Rural"));
                date = Integer.parseInt(String.valueOf(value.get("date")));

                success = true;
            } catch (ArrayIndexOutOfBoundsException e) {
                growthList = distributionList = null;
                date = 0;
            }

            PopulationFragment populationFragment = new PopulationFragment();
            Bundle popArgs = new Bundle();
            popArgs.putBoolean("SUCCESS", success);
            popArgs.putParcelableArrayList("GROWTH", growthEntries);
            popArgs.putParcelableArrayList("DISTR", distributionEntries);
            popArgs.putInt("DATE", date);
            popArgs.putString("NAME", getIntent().getExtras().getString("NAME"));
            populationFragment.setArguments(popArgs);

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.info_container, populationFragment)
                    .commit();
        }
    }
}
