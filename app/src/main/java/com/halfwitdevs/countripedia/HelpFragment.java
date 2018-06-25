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

        listGroupHeader.add("First Look");
        listGroupHeader.add("Selecting countries");

        List<String> firstLookData = new ArrayList<String>();
        firstLookData.add("Welcome");

        List<String> selectingCountriesData = new ArrayList<String>();
        selectingCountriesData.add("Country");

        listChildData.put(listGroupHeader.get(0), firstLookData);
        listChildData.put(listGroupHeader.get(1), selectingCountriesData);
    }
}