package com.halfwitdevs.countripedia;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.net.MalformedURLException;
import java.sql.Ref;
import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;

public class CountrySearchActivity extends AppCompatActivity {

    DrawerLayout drawerLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;
    Toolbar toolbar;
    NavigationView navigationView;
    MaterialSearchView materialSearchView;
    MenuItem searchItem;
    MenuItem refreshItem;

    final String TAG_RETAINED_FRAGMENT = "RetainedFragment";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_country_search);

        navgationDrawerAndToolbar();

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

    private void navgationDrawerAndToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        drawerLayout = findViewById(R.id.drawer_layout);
        actionBarDrawerToggle = drawerToggle();
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        navigationView = findViewById(R.id.navigation_menu);
        setDrawerContent(navigationView);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private ActionBarDrawerToggle drawerToggle() {
        return new ActionBarDrawerToggle(CountrySearchActivity.this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        //drawerToggle().syncState();
        actionBarDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle().onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        /*
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        */
        if(drawerToggle().onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setDrawerContent(NavigationView navigationView) {
            navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                selectDrawerItem(item);
                return true;
            }
        });
    }

    private void selectDrawerItem(MenuItem menuItem) {
        android.support.v4.app.Fragment fragment = null;
        Class FragmentClass = null;
        switch (menuItem.getItemId()) {
            case R.id.home:
                FragmentClass = CountryListFragment.class;
                break;
            case R.id.settings:
                break;
            case R.id.bookmarks:
                FragmentClass = BookmarkFragment.class;
                break;
            case R.id.references:
                FragmentClass = ReferencesFragment.class;
                break;
            case R.id.help:
                FragmentClass = HelpFragment.class;
                break;
            case R.id.about:
                FragmentClass = AboutFragment.class;
                break;
            default:
                FragmentClass = CountryListFragment.class;
                break;
        }

        try{
            fragment = (android.support.v4.app.Fragment)FragmentClass.newInstance();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
        android.support.v4.app.Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.list_fragment_container);
        if (currentFragment == null){
            fragmentManager.beginTransaction().add(R.id.list_fragment_container, fragment).commit();
        }
        else {
            fragmentManager.beginTransaction().replace(R.id.list_fragment_container, fragment).commit();
        }
        menuItem.setChecked(true);
        setTitle(menuItem.getTitle());
        drawerLayout.closeDrawers();
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
                });*/

                args.putParcelableArray("COUNTRYLIST", countryNamesArray);
                countryListFragment.setArguments(args);

                transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.list_fragment_container, countryListFragment);
                transaction.commit();
            }
        }
    }

    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.search_menu, menu);
        searchItem = menu.findItem(R.id.filter_search);
        //materialSearchView.setMenuItem(searchItem);       this one is giving a null pointer exception
        refreshItem = menu.findItem(R.id.referesh_button);
        return true;
    }
    */

    public static class ViewHolder {
        public TextView countryTextView;
        public ImageView flagIcon;
    }
}
