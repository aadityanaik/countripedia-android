package com.halfwitdevs.countripedia;

import android.app.Activity;
import android.os.Bundle;
import android.os.Process;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.Toast;

import com.ahmadrosid.svgloader.SvgLoader;
import com.google.gson.Gson;

import java.util.concurrent.ExecutionException;

public class CountryInfoDisplayFragment extends Fragment {
    Country country;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        String countryInfoJson = null;
        View view = inflater.inflate(R.layout.fragment_country_info_display, container, false);

        if(getArguments() != null) {
            countryInfoJson = getArguments().getString("COUNTRYINFO");
        }

        if(countryInfoJson != null) {
            country = new Gson().fromJson(countryInfoJson, Country.class);
            try {
                new ImageLoader((ImageView) view.findViewById(R.id.country_flag_image)).run();
                ExpandableInfoListAdapter adapter = new ExpandableInfoListAdapter(getContext(), country.getGroups(), country.getGroupsAndItems());
                ExpandableListView infoView = view.findViewById(R.id.country_info_exp_list);
                infoView.setAdapter(adapter);
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }

        return view;
    }

    private class ImageLoader implements Runnable {
        ImageView imageView;

        ImageLoader(ImageView flagView) {
            imageView = flagView;
        }

        @Override
        public void run() {
            android.os.Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);

            // wifi is selected
            SvgLoader.pluck()
                    .with((Activity) getContext())
                    .setPlaceHolder(R.drawable.progress_animation, R.drawable.progress_animation)
                    .load(country.flag, imageView);


        }
    }
}
