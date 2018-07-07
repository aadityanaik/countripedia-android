package com.halfwitdevs.countripedia;

import android.content.Context;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.EntryXComparator;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.LinkedTreeMap;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collections;

public class PopulationFragment extends Fragment {
    ImageView errorView;
    String countryCode, countryName;
    ArrayList growthLineEntries;
    ArrayList distributionPieEntries;
    boolean success;
    int date;
    View view;
    Context context;
    static FragmentManager childFragManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_population, container, false);
        childFragManager = getChildFragmentManager();
        context = getContext();

        Bundle args = getArguments();

        if(args != null) {
            errorView = view.findViewById(R.id.error_img);
            if(PreferenceManager.getDefaultSharedPreferences(getContext()).getBoolean("prefTheme", false)) {
                Drawable errImgDrawable = errorView.getDrawable();
                final float[] NEGATIVE = {
                        -1.0f, 0, 0, 0, 255, // red
                        0, -1.0f, 0, 0, 255, // green
                        0, 0, -1.0f, 0, 255, // blue
                        0, 0, 0, 1.0f, 0  // alpha
                };
                errImgDrawable.setColorFilter(new ColorMatrixColorFilter(NEGATIVE));

                errorView.setImageDrawable(errImgDrawable);
            }
            countryCode = args.getString("CODE");
            countryName = args.getString("NAME");

            new GetPopInfo().execute("https://api.worldbank.org/v2/countries/" + args.getString("CODE") + "/indicators/SP.POP.TOTL?format=json&MRV=20",
                    "https://api.worldbank.org/v2/countries/"+ args.getString("CODE") +"/indicators/SP.URB.TOTL.IN.ZS?format=json&MRV=1");

        }

        return view;
    }

    private class GetPopInfo extends AsyncTask<String, Void, String[]> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
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

            success = false;

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

                growthLineEntries = growthEntries;
                distributionPieEntries = distributionEntries;

                GraphCollectionPager graphCollectionPager = new GraphCollectionPager(childFragManager);
                graphCollectionPager.setCountryName(countryName);
                graphCollectionPager.setDate(date);
                graphCollectionPager.setLineChartEntries(growthLineEntries);
                graphCollectionPager.setPieChartEntries(distributionPieEntries);

                ViewPager viewPager = view.findViewById(R.id.graph_pager);
                viewPager.setAdapter(graphCollectionPager);

                PagerTabStrip pagerTabStrip = view.findViewById(R.id.pager_title_strip);

                LinearLayout progressBar = view.findViewById(R.id.prog_circle);
                progressBar.setVisibility(View.GONE);
                LinearLayout linearLayout = view.findViewById(R.id.graph_layout);
                linearLayout.setVisibility(View.VISIBLE);

            } catch (IndexOutOfBoundsException | NullPointerException e) {
                date = 0;
                // TODO
                // Show a toast with a proper error message
                try {
                    ConnectivityManager connectivityManager = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
                    if (connectivityManager != null) {
                        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
                        if (activeNetwork != null && activeNetwork.isConnected()) {
                            Toast.makeText(getContext(), "Unable to retrieve population data: The World Bank may not have information on this country or some network issue may have occurred",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), "Unable to connect to the database: Check your Internet connection",
                                    Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getContext(), "Unable to connect to the database: Check your Internet connection",
                                Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e1) {
                    e1.printStackTrace();
                }

                LinearLayout progressBar = view.findViewById(R.id.prog_circle);
                progressBar.setVisibility(View.GONE);
                errorView.setVisibility(View.VISIBLE);
            }

        }
    }
}

class GraphCollectionPager extends FragmentPagerAdapter {

    private String countryName;
    private int date;

    // for growth
    private ArrayList<Entry> lineChartEntries;

    // for distribution
    private ArrayList<PieEntry> pieChartEntries;

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public void setDate(int date) {
        this.date = date;
    }

    public void setLineChartEntries(ArrayList<Entry> lineChartEntries) {
        this.lineChartEntries = lineChartEntries;
    }

    public void setPieChartEntries(ArrayList<PieEntry> pieChartEntries) {
        this.pieChartEntries = pieChartEntries;
    }

    public GraphCollectionPager(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        Bundle args = new Bundle();

        if(position == 0) {
            fragment = new GrowthLineChartFragment();
            args.putString("NAME", countryName);
            args.putParcelableArrayList("ENTRIES", lineChartEntries);
            fragment.setArguments(args);
        } else if(position == 1) {
            fragment = new DistributionPieChartFragment();
            args.putString("NAME", countryName);
            args.putInt("DATE", date);
            args.putParcelableArrayList("ENTRIES", pieChartEntries);
            fragment.setArguments(args);
        }

        return fragment;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return position == 0 ? "Population" : "Rural-Urban Distribution";
    }
}