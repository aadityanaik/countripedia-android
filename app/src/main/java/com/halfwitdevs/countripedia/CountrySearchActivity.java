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
import android.preference.PreferenceFragment;
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
    public static boolean startInSettings = false;

    DrawerLayout drawerLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;
    Toolbar toolbar;
    NavigationView navigationView;
    public Boolean selectedFragmentFlag;
    MaterialSearchView materialSearchView;
    MenuItem searchItem;
    MenuItem refreshItem;
    //boolean doubleBackToExitPressedOnce = false;        //for Press Back Button Twice

    final String TAG_RETAINED_FRAGMENT = "RetainedFragment";
    final String TAG_LIST = "CountryList";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(null);
        selectedFragmentFlag = false;
        if (PreferenceManager.getDefaultSharedPreferences(getBaseContext()).getBoolean("prefTheme", false)) {
            setTheme(R.style.DarkAppTheme);
        } else {
            setTheme(R.style.LightAppTheme);
        }
        setContentView(R.layout.activity_country_search);

        navgationDrawerAndToolbar();
        navigationView.getMenu().getItem(1).setChecked(true);

        drawerLayout.closeDrawers();

        if(startInSettings) {
            startInSettings = false;
            android.support.v4.app.Fragment fragment = new SettingsFragment();
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.list_fragment_container, fragment)
                    .commit();
            setTitle("Settings");
        } else {
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
        actionBarDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle().onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
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
        android.support.v4.app.FragmentManager manager = getSupportFragmentManager();
        Class FragmentClass = null;
        String tag = null;
        switch (menuItem.getItemId()) {
            case R.id.home:
                selectedFragmentFlag = false;

                RetainedFragment retainedFragment = (RetainedFragment) manager.findFragmentByTag(TAG_RETAINED_FRAGMENT);
                if(retainedFragment != null && retainedFragment.getData() != null) {
                    String s = retainedFragment.getData();
                    Gson parser = new Gson();
                    CountryNames[] countryNamesArray = parser.fromJson(s, CountryNames[].class);
                    fragment = new CountryListFragment();
                    Bundle args = new Bundle();
                    args.putParcelableArray("COUNTRYLIST", countryNamesArray);
                    fragment.setArguments(args);
                    tag = TAG_LIST;
                } else {
                    recreate();
                    drawerLayout.closeDrawers();
                    return;
                }
                break;
            case R.id.settings:
                selectedFragmentFlag = true;
                FragmentClass = SettingsPreference.class;
                break;
            case R.id.bookmarks:
                selectedFragmentFlag = true;
                FragmentClass = BookmarkFragment.class;
                break;
            case R.id.references:
                selectedFragmentFlag = true;
                FragmentClass = ReferencesFragment.class;
                break;
            case R.id.help:
                selectedFragmentFlag = true;
                FragmentClass = HelpFragment.class;
                break;
            case R.id.about:
                selectedFragmentFlag = true;
                FragmentClass = AboutFragment.class;
                break;

            default:
                return;
        }

        try{
            if(fragment == null)
                fragment = (android.support.v4.app.Fragment)FragmentClass.newInstance();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        if(tag != null) {
            android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
            android.support.v4.app.Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.list_fragment_container);
            if (currentFragment == null) {
                fragmentManager.beginTransaction().add(R.id.list_fragment_container, fragment).commit();
            } else {
                fragmentManager.beginTransaction().replace(R.id.list_fragment_container, fragment).commit();
            }
        } else {
            android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
            android.support.v4.app.Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.list_fragment_container);
            if (currentFragment == null) {
                fragmentManager.beginTransaction().add(R.id.list_fragment_container, fragment, tag).commit();
            } else {
                fragmentManager.beginTransaction().replace(R.id.list_fragment_container, fragment, tag).commit();
            }
        }
        menuItem.setChecked(true);
        String title = menuItem.getTitle().toString();
        if(title.equals("Home")) {
            setTitle("Countripedia");
        } else {
            setTitle(title);
        }
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

                args.putParcelableArray("COUNTRYLIST", countryNamesArray);
                countryListFragment.setArguments(args);

                transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.list_fragment_container, countryListFragment, TAG_LIST);
                transaction.commit();
            }
        }
    }

    @Override
    public void onBackPressed() {
        if(!selectedFragmentFlag) {
            super.onBackPressed();
        }
        else {
            int size = navigationView.getMenu().size();
            for (int i = 0; i < size; i++) {
                navigationView.getMenu().getItem(i).setChecked(false);
            }
            navigationView.getMenu().getItem(1).setChecked(true);
            selectedFragmentFlag = false;

            android.support.v4.app.Fragment fragment;
            android.support.v4.app.FragmentManager manager = getSupportFragmentManager();
            String tag = null;

            RetainedFragment retainedFragment = (RetainedFragment) manager.findFragmentByTag(TAG_RETAINED_FRAGMENT);
            if(retainedFragment != null && retainedFragment.getData() != null) {
                String s = retainedFragment.getData();
                Gson parser = new Gson();
                CountryNames[] countryNamesArray = parser.fromJson(s, CountryNames[].class);
                fragment = new CountryListFragment();
                Bundle args = new Bundle();
                args.putParcelableArray("COUNTRYLIST", countryNamesArray);
                fragment.setArguments(args);
                tag = TAG_LIST;
                if(tag != null) {
                    manager.beginTransaction().replace(R.id.list_fragment_container, fragment).commit();
                }
                setTitle("Countripedia");
            } else {
                recreate();
            }
        }
    }

    public static class ViewHolder {
        public TextView countryTextView;
        public ImageView flagIcon;
    }
}
