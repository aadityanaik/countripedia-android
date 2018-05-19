package com.halfwitdevs.countripedia;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class MoreInfoActivity extends AppCompatActivity {

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


        }

    }
}
