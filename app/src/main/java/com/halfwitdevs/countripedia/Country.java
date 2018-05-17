package com.halfwitdevs.countripedia;

import android.annotation.SuppressLint;
import android.os.AsyncTask;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.LinkedTreeMap;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

import javax.net.ssl.HttpsURLConnection;

class Language {
    public String name, nativeName;
    @Override
    public String toString(){
        return name + " (" + nativeName + ")";
    }
}

class Currency {
    public String code, name, symbol;
    @Override
    public String toString(){
        return name + " (" + symbol + ")";
    }
}

class Neighbour {
    public String name;
}

class Summary {
    private String summary;

    public void setSummary(String countryName, String alternateName) throws ExecutionException, InterruptedException {
        summary = new GetSummary().execute(new String[] {countryName, alternateName}).get();
    }

    public String getSummary() {
        return summary;
    }

    static class GetSummary extends AsyncTask<String[], Void, String> {
        private String getExtract(String name) {
            StringBuilder summaryString = new StringBuilder();

            HttpsURLConnection connection = null;
            try {
                switch (name) {
                    case "Congo":
                        name = "Republic of the Congo";
                        break;

                    case "Georgia":
                        name = "Georgia (country)";
                        break;

                    case "Macedonia (the former Yugoslav Republic of)":
                        name = "republic of macedonia";
                        break;
                }

                connection = (HttpsURLConnection) new URL("https://en.wikipedia.org/w/api.php?format=json&action=query&prop=extracts&exintro=&explaintext=&titles=" + name.replaceAll(" ", "_") + "&redirects=1").openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Accept", "application/json");
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);
                Scanner input = new Scanner
                        (new InputStreamReader(connection.getInputStream()));

                while (input.hasNextLine()) {
                    summaryString.append(input.nextLine());
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if(connection != null) {
                    connection.disconnect();
                }
            }

            return summaryString.toString();
        }

        @Override
        protected String doInBackground(String[]... strings) {
            String summaryString = getExtract(strings[0][0]);
            GsonBuilder builder = new GsonBuilder();
            Object countrySummary = builder.create().fromJson(summaryString, Object.class);

            LinkedTreeMap linkedTreeMap = (LinkedTreeMap) ((LinkedTreeMap) ((LinkedTreeMap) countrySummary).get("query")).get("pages");
            LinkedTreeMap page = (LinkedTreeMap) linkedTreeMap.get(linkedTreeMap.keySet().toArray()[0]);

            if(page.get("extract") == null) {
                summaryString = getExtract(strings[0][1]);
                countrySummary = builder.create().fromJson(summaryString, Object.class);
                linkedTreeMap = (LinkedTreeMap) ((LinkedTreeMap) ((LinkedTreeMap) countrySummary).get("query")).get("pages");
                page = (LinkedTreeMap) linkedTreeMap.get(linkedTreeMap.keySet().toArray()[0]);
            }

            if(page.get("extract") == null) {
                return "Not Found";
            } else {
                ArrayList<Character> arrayList = new ArrayList<>();
                for (char a : page.get("extract").toString().toCharArray()) {
                    arrayList.add(a);
                }

                int start = 0, end, nest = 0;

                for (int i = 0; i < arrayList.size(); i++) {
                    char character = arrayList.get(i);
                    if (nest == 0) {
                        if (character == '(') {
                            nest++;
                            start = i - 1;
                        }
                    } else {
                        if (character == ')') {
                            nest--;
                            if (nest == 0) {
                                end = i;
                                while (end != start) {
                                    arrayList.remove(end);
                                    end--;
                                }

                                arrayList.remove(start);
                                i = start;
                            }
                        } else if (character == '(') {
                            nest++;
                        }
                    }
                }

                StringBuilder stringBuilder = new StringBuilder();
                for (char a : arrayList) {
                    stringBuilder.append(a);
                }

                return stringBuilder.toString();
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }
    }
}

public class Country {
    public String unit = "sq. km.";
    String name, capital;
    String[] topLevelDomain;
    String alpha2Code, alpha3Code;
    String[] callingCodes;
    String[] altSpellings;
    String region;
    String subregion;
    String population;
    String demonym;
    String area;
    String[] timezones;
    String[] borders;
    String nativeName;
    Language[] languages;
    Currency[] currencies;
    String numericCode;

    Neighbour[] neighbours;

    float[] latlng;

    String flag;

    String getName() {
        return name;
    }

    public String convertArea(int dest) {
        // 0- sqkm
        // 1- sqmi
        // 2- hect
        double value = Double.parseDouble(area);
        int src = 0;

        switch (unit) {
            case "sq. km.":
                src = 0;
                break;
            case "sq. mi.":
                src = 1;
                break;
            case "hect.":
                src = 2;
                break;
        }

        if(src == dest) {
            return String.valueOf(value);
        } else {
            // convert to sqkm
            switch (src) {
                case 1:
                    value = value / 0.38610215854245;
                    break;

                case 2:
                    value = value / 100;
                    break;
            }

            // convert to dest
            switch (dest) {
                case 0:
                    unit = "sq. km.";
                    area = String.format(Locale.getDefault(), "%f", value);
                    return String.format(Locale.getDefault(), "%.2f", value);

                case 1:
                    unit = "sq. mi.";
                    area = String.format(Locale.getDefault(), "%f",value * 0.38610215854245);
                    return String.format(Locale.getDefault(), "%.2f", value * 0.38610215854245);

                case 2:
                    unit = "hect.";
                    area = String.format(Locale.getDefault(), "%f",value * 100);
                    return String.format(Locale.getDefault(), "%.2f", value * 100);
            }
        }

        return "0";
    }

    public List<String> getGroups() {
        List<String> groupList = new ArrayList<>();
        groupList.add("General Info");
        groupList.add("Summary");
        groupList.add("Codes");
        groupList.add("Calling Codes");
        groupList.add("Borders");
        groupList.add("Languages");
        groupList.add("Currencies");
        groupList.add("Alternate Names");
        groupList.add("Internet Domains");

        return groupList;
    }

    @SuppressLint("StaticFieldLeak")
    public HashMap<String, ArrayList<String>> getGroupsAndItems() throws ExecutionException, InterruptedException {
        HashMap<String, ArrayList<String>> groupItemMap = new HashMap<>();

        ArrayList<String> childItemGeneral = new ArrayList<>();
        childItemGeneral.add("Name:  " + name);

        if(!nativeName.equals(name)) {
            childItemGeneral.add("Native Name:  " + nativeName);
        }

        if (!capital.equals("")) {
            childItemGeneral.add("Capital:  " + capital);
        } else {
            childItemGeneral.add("Capital Not Found");
        }

        if (!region.equals("")) {
            if(!subregion.equals("")) {
                childItemGeneral.add("Region:  " + region + " (" + subregion + ")");
            } else {
                childItemGeneral.add("Region:  " + region);
            }
        } else {
            childItemGeneral.add("Region Not Found");
        }

        if (!population.equals("")) {
            childItemGeneral.add("Population:  " + population);
        } else {
            childItemGeneral.add("Population Not Found");
        }

        if (area != null && !area.equals("")) {
            childItemGeneral.add(String.format(Locale.getDefault(), "Area:  %.2f " + unit, Double.parseDouble(area)));
        } else {
            childItemGeneral.add("Area Not Found");
        }

        if(!demonym.equals("") || demonym != null) {
            childItemGeneral.add("Demonym:  " + demonym);
        }

        groupItemMap.put("General Info", childItemGeneral);

        groupItemMap.put("Codes", new ArrayList<>(Arrays.asList("ISO Alpha-2:  " + alpha2Code, "ISO Alpha-3:  " + alpha3Code, "Numeric:  " + numericCode)));

        groupItemMap.put("Calling Codes", new ArrayList<>(Arrays.asList(callingCodes)));


        if (borders != null) {
            try {
                String[] neighbours = new AsyncTask<Void, Void, String[]>() {
                    @Override
                    protected String[] doInBackground(Void... voids) {
                        Neighbour[] countryNameList;
                        try {
                            StringBuilder borderCodes = new StringBuilder();
                            for (String border : borders) {
                                borderCodes.append(border).append(";");
                            }

                            HttpsURLConnection connection;
                            connection = (HttpsURLConnection) new URL("https://restcountries.eu/rest/v2/alpha?codes=" + borderCodes.toString() + "&fields=name").openConnection();
                            connection.setRequestMethod("GET");
                            connection.setRequestProperty("Accept", "application/json");
                            connection.setConnectTimeout(5000);
                            connection.setReadTimeout(5000);
                            Scanner input = new Scanner
                                    (new InputStreamReader(connection.getInputStream()));

                            StringBuilder jsonString = new StringBuilder();

                            while (input.hasNextLine()) {
                                jsonString.append(input.nextLine());
                            }

                            connection.disconnect();
                            countryNameList = new Gson().fromJson(jsonString.toString(), Neighbour[].class);

                            String[] output = new String[countryNameList.length];

                            for (int i = 0; i < output.length; i++) {
                                output[i] = countryNameList[i].name;
                            }

                            return output;
                        } catch (IOException e) {
                            return null;
                        }
                    }
                }.execute().get();

                groupItemMap.put("Borders", new ArrayList<>(Arrays.asList(neighbours)));

            } catch (Exception e) {
                groupItemMap.put("Borders", new ArrayList<>(Collections.singletonList("None")));
            }
        } else {
            groupItemMap.put("Borders", new ArrayList<>(Collections.singletonList("None")));
        }

        ArrayList<String> languageArray = new ArrayList<>();
        for (Language language : languages) {
            languageArray.add(language.toString());
        }
        groupItemMap.put("Languages", languageArray);

        ArrayList<String> currencyArray = new ArrayList<>();
        for (Currency currency : currencies) {
            if (currency.name != null) {
                currencyArray.add(currency.toString());
            }
        }
        if (currencyArray.size() == 0) {
            currencyArray.add("None");
        }
        groupItemMap.put("Currencies", currencyArray);

        ArrayList<String> alternameArray = new ArrayList<>();
        for (int i = 1; i < altSpellings.length; i++) {
            alternameArray.add(altSpellings[i]);
        }
        if (alternameArray.size() == 0) {
            alternameArray.add("None");
        }
        groupItemMap.put("Alternate Names", alternameArray);

        groupItemMap.put("Internet Domains", new ArrayList<>(Arrays.asList(topLevelDomain)));

        Summary summary = new Summary();
        summary.setSummary(name, nativeName);
        groupItemMap.put("Summary", new ArrayList<>(Collections.singletonList(summary.getSummary())));

        return groupItemMap;
    }

    @Override
    public String toString() {
        StringBuilder info = new StringBuilder();
        info.append("Name: ").append(name).append("\nCapital: ").append(capital).append("\n");
        if (topLevelDomain.length > 0) {
            info.append("Top Level Domain: ");
            for (String x :
                    topLevelDomain) {
                info.append(x).append(" ");
            }
        }
        info.append("\nCodes: ").append(alpha2Code).append(" ").append(alpha3Code).append("\n");
        if (callingCodes.length > 0) {
            info.append("Calling codes: ");
            for (String x :
                    callingCodes) {
                info.append(x).append(" ");
            }
        }

        if (altSpellings.length > 1) {
            info.append("\n\nAlternative names:\n");
            for (int i = 1; i < altSpellings.length; i++) {
                info.append(altSpellings[i]).append("\n");
            }
        }

        info.append("\n\nReigon: ").append(region).append("- ").append(subregion).append("\nPopulation: ").append(population)
                .append("\nDemonym: ").append(demonym).append("\nArea: ").append(area)
                .append("\n\nTimezones:\n");
        for ( Object x :
                timezones ) {
            info.append(x).append("\n");
        }

        if (borders.length > 0) {
            info.append("\n\nBorders\n");
            for (Object border : borders) {
                info.append(border).append(" ");
            }
        }

        info.append("\n\nNative Name: ").append(nativeName).append("\n\nLanguages:\n");
        for ( Object x :
                languages ) {
            info.append(x).append("\n");
        }
        info.append("\n\nCurrencies:\n");
        for ( Object x :
                currencies ) {
            info.append(x).append("\n");
        }

        return info.toString();
    }
}
