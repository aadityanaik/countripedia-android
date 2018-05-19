package com.halfwitdevs.countripedia;

import android.os.Bundle;
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
import android.widget.Toast;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;

public class PopulationFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_population, container, false);

        Bundle args = getArguments();

        if(args != null) {
            if(args.getBoolean("SUCCESS")) {
                ArrayList growthLineEntries = args.getParcelableArrayList("GROWTH");
                ArrayList distributionPieEntries = args.getParcelableArrayList("DISTR");

                GraphCollectionPager graphCollectionPager = new GraphCollectionPager(getChildFragmentManager());
                graphCollectionPager.setCountryName(args.getString("NAME"));
                graphCollectionPager.setDate(args.getInt("DATE"));
                graphCollectionPager.setLineChartEntries(growthLineEntries);
                graphCollectionPager.setPieChartEntries(distributionPieEntries);

                ViewPager viewPager = view.findViewById(R.id.graph_pager);
                viewPager.setAdapter(graphCollectionPager);

                PagerTabStrip pagerTabStrip = view.findViewById(R.id.pager_title_strip);

            } else {
                Toast.makeText(getContext(), "SHIT", Toast.LENGTH_SHORT).show();
            }
        }

        return view;
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