package com.halfwitdevs.countripedia;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HelpFragment extends Fragment{

    View view;
    HelpDataAdapter helpDataAdapter;
    ExpandableListView expandableListView;
    List<String> listGroupHeader;
    HashMap<String, List<String>> listChildData;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_help_layout, container, false);

        expandableListView = (ExpandableListView)view.findViewById(R.id.help_list);
        prepareList();
        helpDataAdapter = new HelpDataAdapter(getActivity(), listGroupHeader, listChildData);
        expandableListView.setAdapter(helpDataAdapter);
        return view;
    }

    public void prepareList(){
        listGroupHeader = new ArrayList<String>();
        listChildData = new HashMap<String, List<String>>();

        listGroupHeader.add("First Look/Home Screen");
        listGroupHeader.add("Selecting a Country");
        listGroupHeader.add("Settings");
        listGroupHeader.add("Help");
        listGroupHeader.add("About");

        List<String> firstLookData = new ArrayList<String>();
        firstLookData.add("List  of Countries");
        firstLookData.add("Toolbar");

        List<String> selectingCountriesData = new ArrayList<String>();
        selectingCountriesData.add("Country");

        List<String> settingsData = new ArrayList<String>();
        settingsData.add("Switch Theme");

        List<String> helpData = new ArrayList<String>();
        helpData.add("Help is the silent guide of this app, and is a helpful one at that.");

        List<String> aboutData = new ArrayList<String>();
        aboutData.add("We tell you about the app here. And us. We love sharing.");

        listChildData.put(listGroupHeader.get(0), firstLookData);
        listChildData.put(listGroupHeader.get(1), selectingCountriesData);
        listChildData.put(listGroupHeader.get(2), settingsData);
        listChildData.put(listGroupHeader.get(3), helpData);
        listChildData.put(listGroupHeader.get(4), aboutData);
    }
}