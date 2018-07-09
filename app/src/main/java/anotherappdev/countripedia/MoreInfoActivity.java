package anotherappdev.countripedia;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

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

        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.toolbar);

        switch (args.getString("ACTION")) {
            case "MAP":
                MapFragment mapFragment = new MapFragment();
                mapFragment.setArguments(args);

                toolbar.setTitle("Maps");

                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.info_container, mapFragment)
                        .commit();

                break;

            case "POP":
                PopulationFragment populationFragment = new PopulationFragment();
                populationFragment.setArguments(args);

                toolbar.setTitle("Population graphs");

                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.info_container, populationFragment)
                        .commit();
                break;

            case "RATE":
                CurrencyFragment currencyFragment = new CurrencyFragment();
                currencyFragment.setArguments(args);

                toolbar.setTitle("Currency Exchange");

                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.info_container, currencyFragment)
                        .commit();

                break;

            case "LANG":
                LanguageFragment languageFragment = new LanguageFragment();
                languageFragment.setArguments(args);

                toolbar.setTitle("Language- " + args.getString("LANG"));

                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.info_container, languageFragment)
                        .commit();

                break;
        }

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
