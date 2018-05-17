package com.halfwitdevs.countripedia;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.gson.Gson;

import java.net.MalformedURLException;

public class CountryInfoDisplayActivity extends AppCompatActivity {

    private static final String TAG_RETAINED_FRAGMENT = "RetainedFragment";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_country_info_display);

        Intent intent = getIntent();
        String countryCode = intent.getStringExtra("COUNTRYCODE");

        Toast.makeText(this, countryCode, Toast.LENGTH_SHORT).show();

        new GetCountryInfo().execute("https://restcountries.eu/rest/v2/alpha/" + countryCode);
    }

    private class GetCountryInfo extends AsyncTask<String, Void, String> {
        // using v4 cuz reasons
        RetainedFragment retainedFragment;
        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction transaction = fragmentManager.beginTransaction();
        ProgressFragment progressFragment;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            // setting up the progress bar
            progressFragment = new ProgressFragment();
            transaction.replace(R.id.fragment_container, progressFragment);
            transaction.commit();
        }

        @Override
        protected String doInBackground(String... strings) {
            String jsonString = null;
            retainedFragment = (RetainedFragment) fragmentManager.findFragmentByTag(TAG_RETAINED_FRAGMENT);
            if(retainedFragment == null) {
                try {
                    jsonString =  new URLHandler(strings[0]).getResponse();
                    retainedFragment = new RetainedFragment();
                    retainedFragment.setData(jsonString);
                    fragmentManager.beginTransaction().add(retainedFragment, TAG_RETAINED_FRAGMENT).commit();
                } catch (MalformedURLException e) {
                    return null;
                }
            } else {
                jsonString = retainedFragment.getData();
            }

            return jsonString;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if(s != null) {
                Bundle args = new Bundle();
                args.putString("COUNTRYINFO", s);
                CountryInfoDisplayFragment infoDisplayFragment = new CountryInfoDisplayFragment();
                infoDisplayFragment.setArguments(args);

                transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.fragment_container, infoDisplayFragment);
                transaction.commit();
            }
        }
    }
}
