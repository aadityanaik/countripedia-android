package com.halfwitdevs.countripedia;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HelpFragment extends Fragment{

    View view;
    HelpDataAdapter helpDataAdapter;
    ExpandableListView expandableListView;
    List<String> listGroupHeader;
    HashMap<String, List<String>> listChildData;
    TextView infoText;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_help_layout, container, false);

        infoText = view.findViewById(R.id.help_header);
        infoText.setText("Welcome to help manual. Tap on the following topic sections to expand them and know more about countripedia!");
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
        listGroupHeader.add("Using Search");
        listGroupHeader.add("Using the Navigation Drawer");
        listGroupHeader.add("Home: Go back to start page");
        listGroupHeader.add("Settings: Switch Theme");
        listGroupHeader.add("Bookmarks");
        listGroupHeader.add("Help");
        listGroupHeader.add("About");

        List<String> firstLookData = new ArrayList<String>();
        firstLookData.add("\tList  of Countries\n\n" +
                "When Countripedia starts up, a list of all countries shows up in alphabetical order. You can" +
                " tap on any country of your choice to select it. You also have the ability to scroll through the list of countries to find" +
                " and select the country of  your choice. \nAnother wa of searching for any country is to use the search button available on" +
                " the toolbar.\n\n" +
                "\tToolbar\n\n" +
                "Three icons occupy the toolbar. They are the:" +
                "\n\tThe Search Icon on the right most side of the screen" +
                "\n\tThe Refresh Icon to the immediate left of the Search Icon, which on tapping refreshes the list." +
                "\n\tThe Hamburger Icon(Navigation Drawer) to the leftmost side of the screen" +
                "\n\nUsing the search icon and the navigation drawer is further described in their respective sections.");

        List<String> searchingCountriesData = new ArrayList<String>();
        searchingCountriesData.add("Pressing the search icon on the toolbar brigs up search." +
                " Type in the country name in the search area. Even entering a single character" +
                " will start filtering out countries that do not start with that letter. Entering more" +
                " characters wll further filter out the list.");

        List<String> navDrawerData = new ArrayList<String>();
        navDrawerData.add("Tapping on the haburger icon opens up the navigation drawer. The navigation drawer presents a lot of options," +
                " which are: Home, Settings, Bookmarks, References, Help, About.\nTapping on any of these will open up that" +
                " particular window.");

        List<String> homeData = new ArrayList<String>();
        homeData.add("Pressing home on the navigation drawer takes you back to the initial start page. Pressing the back button on your phone" +
                " also takes you back to the home page.");

        List<String> settingsData = new ArrayList<String>();
        settingsData.add("Pressing the settings option on the navigation drawer takes you to the settings mennu. In the settings menu" +
                " you get the option to choose between a light or a dark theme. You can do this by tapping on the switch theme switch.");

        List<String> navDrawerBookmarksData = new ArrayList<String>();
        navDrawerBookmarksData.add("Pressing bookmarks on the navigation drawer takes you to the page where your bookmarks are stored." +
                " You can tap on the countries in the bookmarks list and know all about them.");

        List<String> helpData = new ArrayList<String>();
        helpData.add("Help is the silent guide of this app, always here to help. Pressing help on the navigation drawer takes you to the" +
                " help page.");

        List<String> aboutData = new ArrayList<String>();
        aboutData.add("Pressing about on the navigation drawer takes you to the About page of the app." +
                " Here we tell you about the app. And us. We love sharing.");

        listChildData.put(listGroupHeader.get(0), firstLookData);
        listChildData.put(listGroupHeader.get(1), searchingCountriesData);
        listChildData.put(listGroupHeader.get(2), navDrawerData);
        listChildData.put(listGroupHeader.get(3), homeData);
        listChildData.put(listGroupHeader.get(4), settingsData);
        listChildData.put(listGroupHeader.get(5), navDrawerBookmarksData);
        listChildData.put(listGroupHeader.get(6), helpData);
        listChildData.put(listGroupHeader.get(7), aboutData);
    }
}