package com.halfwitdevs.countripedia;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
        if (PreferenceManager.getDefaultSharedPreferences(getBaseContext()).getBoolean("prefTheme", false)) {
            setTheme(R.style.DarkAppTheme);
        } else {
            setTheme(R.style.LightAppTheme);
        }
        setContentView(R.layout.activity_more_info);

        Bundle args = getIntent().getExtras();

        switch (args.getString("ACTION")) {
            case "MAP":
                MapFragment mapFragment = new MapFragment();
                mapFragment.setArguments(args);

                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.info_container, mapFragment)
                        .commit();

                break;

            case "POP":
                PopulationFragment populationFragment = new PopulationFragment();
                populationFragment.setArguments(args);

                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.info_container, populationFragment)
                        .commit();
                break;

            case "RATE":
                CurrencyFragment currencyFragment = new CurrencyFragment();
                currencyFragment.setArguments(args);

                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.info_container, currencyFragment)
                        .commit();

                break;

            case "LANG":
                LanguageFragment languageFragment = new LanguageFragment();
                languageFragment.setArguments(args);

                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.info_container, languageFragment)
                        .commit();

                break;
        }

    }
}
