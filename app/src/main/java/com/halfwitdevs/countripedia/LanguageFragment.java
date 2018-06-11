package com.halfwitdevs.countripedia;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.GsonBuilder;
import com.google.gson.internal.LinkedTreeMap;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

import javax.net.ssl.HttpsURLConnection;

public class LanguageFragment extends Fragment {
    View view;
    TextView textView;
    LinearLayout languageLayout, progressLayout;
    String lang;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_language, container, false);

        Bundle args = getArguments();


        textView = view.findViewById(R.id.info_language);
        languageLayout = view.findViewById(R.id.language_info_layout);
        progressLayout = view.findViewById(R.id.prog_circle);

        if(args != null) {
            lang = args.getString("LANG");
            new GetLanguageInfo().execute(lang);
        }

        return view;
    }

    class GetLanguageInfo extends AsyncTask<String, Void, String> {
        private String getExtract(String name) {
            StringBuilder summaryString = new StringBuilder();

            HttpsURLConnection connection = null;
            try {
                connection = (HttpsURLConnection)
                        new URL("https://en.wikipedia.org/w/api.php?format=json&action=query&prop=extracts&exintro=&explaintext=&titles="
                                + name.replaceAll("\\(.*\\)", "").replaceAll(" ", "_")
                                + "_language&redirects=1").openConnection();

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
                if (connection != null) {
                    connection.disconnect();
                }
            }

            return summaryString.toString();
        }

        @Override
        protected String doInBackground(String... strings) {
            String summaryString =  getExtract(strings[0]);

            GsonBuilder builder = new GsonBuilder();
            Object countrySummary = builder.create().fromJson(summaryString, Object.class);

            LinkedTreeMap linkedTreeMap = (LinkedTreeMap) ((LinkedTreeMap) ((LinkedTreeMap) countrySummary).get("query")).get("pages");
            LinkedTreeMap page = (LinkedTreeMap) linkedTreeMap.get(linkedTreeMap.keySet().toArray()[0]);

            if (page.get("extract") == null) {
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

                return stringBuilder.toString().replaceAll("\n", "\n\n");
            }
        }

        @Override
        protected void onPostExecute(String langInfo) {
            super.onPostExecute(langInfo);

            if(langInfo.equals("Not Found")) {
                Toast.makeText(getContext(), "Could not find language summary", Toast.LENGTH_SHORT).show();
            } else {
                String displayLang = langInfo + "\n\nTaken from the wikipedia page of " + lang + " at " + Calendar.getInstance().getTime();
                textView.setText(displayLang);
                progressLayout.setVisibility(View.GONE);
                languageLayout.setVisibility(View.VISIBLE);
            }
        }
    }
}
