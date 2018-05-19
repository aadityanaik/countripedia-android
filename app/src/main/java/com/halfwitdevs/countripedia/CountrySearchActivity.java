package com.halfwitdevs.countripedia;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.net.MalformedURLException;
import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;

public class CountrySearchActivity extends AppCompatActivity {

    final String TAG_RETAINED_FRAGMENT = "RetainedFragment";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_country_search);

        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
            if (activeNetwork.isConnected()) {
                new GetCountryList().
                        execute("https://restcountries.eu/rest/v2/all/?fields=name;alpha2Code;flag");
            }
        } catch (Exception e) {
            AlertDialog.Builder builder = new AlertDialog.Builder(CountrySearchActivity.this);
            builder.setTitle("Error")
                    .setMessage("Could not retrieve data\nCheck your Internet connection");
            builder.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    recreate();
                }
            });

            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });

            AlertDialog errorDialog = builder.create();
            errorDialog.show();
        }
    }

    private class GetCountryList extends AsyncTask<String, Void, String> {
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
            transaction.replace(R.id.list_fragment_container, progressFragment);
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
                Gson parser = new Gson();
                CountryNames[] countryNamesArray = parser.fromJson(s, CountryNames[].class);
                CountryListFragment countryListFragment = new CountryListFragment();
                Bundle args = new Bundle();
                args.putParcelableArray("COUNTRYLIST", countryNamesArray);
                countryListFragment.setArguments(args);

                transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.list_fragment_container, countryListFragment);
                transaction.commit();
            }
        }
    }

    public static class ViewHolder {
        public TextView countryTextView;
        public ImageView flagIcon;
    }
}
