package anotherappdev.countripedia;

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
        infoText.setText("Welcome to help manual. Tap on the following topic sections to expand them and know more about Countripedia!");
        expandableListView = view.findViewById(R.id.help_list);
        prepareList();
        helpDataAdapter = new HelpDataAdapter(getActivity(), listGroupHeader, listChildData);
        expandableListView.setAdapter(helpDataAdapter);
        return view;
    }

    public void prepareList(){
        listGroupHeader = new ArrayList<>();
        listChildData = new HashMap<>();

        listGroupHeader.add("First Look/Home Screen");
        listGroupHeader.add("Looking up all information");
        listGroupHeader.add("Using Search");
        listGroupHeader.add("Using the Navigation Drawer");
        listGroupHeader.add("Home: Go back to start page");
        listGroupHeader.add("Settings: Switch Theme");
        listGroupHeader.add("Bookmarks");
        listGroupHeader.add("Help");
        listGroupHeader.add("About");

        List<String> firstLookData = new ArrayList<>();
        firstLookData.add("List  of Countries\n\n" +
                "\tWhen Countripedia starts up, a list of all countries shows up in alphabetical order. You can" +
                " tap on any country of your choice to select it. You also have the ability to scroll through the list of countries to find" +
                " and select the country of  your choice. \n\tAnother way of searching for any country is to use the search button available on" +
                " the toolbar.\n\n\n" +
                "Toolbar\n\n" +
                "Three icons occupy the toolbar. They are the:" +
                "\n\tThe Search Icon on the right most side of the screen" +
                "\n\tThe Refresh Icon to the immediate left of the Search Icon, which on tapping refreshes the list." +
                "\n\tThe Hamburger Icon(Navigation Drawer) to the leftmost side of the screen" +
                "\n\nUsing the search icon and the navigation drawer is further described in their respective sections.");

        List<String> infoDisplayData = new ArrayList<>();
        infoDisplayData.add("Upon tapping on a country, you are presented with a page displaying information." +
                "\nYou have 9 sections:\n\nGeneral Info" +
                "\nPressing on general info will show some information about the country. The sections that can display more information" +
                " have an icon indicating the same, on the right hand side." +
                "\n\nSummary" +
                "\nTapping on summary will give you a detailed summary of the country." +
                "\n\nCodes" +
                "\nTapping on codes will give you information about the country codes." +
                "\n\nCalling Codes" +
                "\nTapping on calling codes will give you the international calling codes to that country." +
                " Tapping on the code will open the dialer app with the code already entered." +
                "\n\nBorders" +
                "\nTapping on borders will show a list of neighbouring countries. Tapping on those will take you to a page which will" +
                " display information for that country." +
                "\n\nLanguages" +
                "\nTapping on languages will show the list of official languages of that country. Tapping on any of those languages will" +
                " display information about that language." +
                "\n\nCurrencies" +
                "\nTapping on currencies will show the list of currencies of that country. Tapping on any currency will take youto the currency" +
                " converter, which has a graph displaying the values of the currency and the current value as of that day, all with respect to the" +
                " Indian Rupee(INR)." +
                "\n\nAlternate Names" +
                "\nTapping on alternate names will show the list of alternate names of that country." +
                "\n\nInternet Domains" +
                "\nTapping on internet domains will show the list of top level domains of that country.");

        List<String> searchingCountriesData = new ArrayList<>();
        searchingCountriesData.add("Pressing the search icon on the toolbar brings up search." +
                " Type in the country name in the search area. Even entering a single character" +
                " will start filtering out countries that do not contain that letter. Entering more" +
                " characters wll further filter out the list.");

        List<String> navDrawerData = new ArrayList<>();
        navDrawerData.add("Tapping on the hamburger icon(the three horizontal lines stacked on one another) opens up the navigation drawer. The navigation drawer presents a lot of options," +
                " which are: Home, Settings, Bookmarks, References, Help, About.\nTapping on any of these will open up that" +
                " particular window.");

        List<String> homeData = new ArrayList<>();
        homeData.add("Pressing home on the navigation drawer takes you back to the initial start page.");

        List<String> settingsData = new ArrayList<>();
        settingsData.add("Pressing the settings option on the navigation drawer takes you to the settings menu. In the settings menu" +
                " you get the option to choose between a light or a dark theme. You can do this by tapping on the switch theme switch.");

        List<String> navDrawerBookmarksData = new ArrayList<>();
        navDrawerBookmarksData.add("Pressing bookmarks on the navigation drawer takes you to the page where your bookmarks are stored." +
                " You can tap on the countries in the bookmarks list and know all about them.");

        List<String> helpData = new ArrayList<>();
        helpData.add("Help is the silent guide of this app, always here to help. Pressing help on the navigation drawer takes you to the" +
                " help page.");

        List<String> aboutData = new ArrayList<>();
        aboutData.add("Pressing about on the navigation drawer takes you to the About page of the app." +
                " Here we tell you about the app. And us. We love sharing.");

        listChildData.put(listGroupHeader.get(0), firstLookData);
        listChildData.put(listGroupHeader.get(1), infoDisplayData);
        listChildData.put(listGroupHeader.get(2), searchingCountriesData);
        listChildData.put(listGroupHeader.get(3), navDrawerData);
        listChildData.put(listGroupHeader.get(4), homeData);
        listChildData.put(listGroupHeader.get(5), settingsData);
        listChildData.put(listGroupHeader.get(6), navDrawerBookmarksData);
        listChildData.put(listGroupHeader.get(7), helpData);
        listChildData.put(listGroupHeader.get(8), aboutData);
    }
}