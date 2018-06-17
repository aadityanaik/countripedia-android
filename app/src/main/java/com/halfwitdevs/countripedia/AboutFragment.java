package com.halfwitdevs.countripedia;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class AboutFragment extends Fragment {
    ListView listView;
    TextView messageTV;
    View view;

    final String MESSAGE = "Countripedia is an app made to bring an online atlas into your hands. " +
            "Using Countripedia, you can easily get all the vital information regarding a country. " +
            "You can look up countries, see their nicknames and explore the spoken languages. " +
            "Learn about the population growth and  distribution through an interactive graph " +
            "or the exchange rate of a country's currency.";

    final String[] CREATORLIST = {"Aaditya Naik", "Aditya Chakraborti"};

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_about_layout, container, false);
        text();
        listView = view.findViewById(R.id.creator_list);
        return view;
    }

    public void text() {
        messageTV = (TextView)view.findViewById(R.id.message);
        messageTV.setText(MESSAGE);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, CREATORLIST);
        listView.setAdapter(arrayAdapter);
    }
}

