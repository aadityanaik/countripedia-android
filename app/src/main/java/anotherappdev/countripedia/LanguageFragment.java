package anotherappdev.countripedia;

import android.content.Context;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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

import javax.net.ssl.HttpsURLConnection;

public class LanguageFragment extends Fragment {
    View view;
    TextView textView;
    LinearLayout languageLayout, progressLayout;
    ImageView errorView;
    String lang;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_language, container, false);

        Bundle args = getArguments();


        textView = view.findViewById(R.id.info_language);
        languageLayout = view.findViewById(R.id.language_info_layout);
        progressLayout = view.findViewById(R.id.prog_circle);
        errorView = view.findViewById(R.id.error_img);
        if (PreferenceManager.getDefaultSharedPreferences(getContext()).getBoolean("prefTheme", false)) {
            Drawable errImgDrawable = errorView.getDrawable();
            final float[] NEGATIVE = {
                    -1.0f, 0, 0, 0, 255, // red
                    0, -1.0f, 0, 0, 255, // green
                    0, 0, -1.0f, 0, 255, // blue
                    0, 0, 0, 1.0f, 0  // alpha
            };
            errImgDrawable.setColorFilter(new ColorMatrixColorFilter(NEGATIVE));

            errorView.setImageDrawable(errImgDrawable);
        }
        errorView.setVisibility(View.GONE);

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
            try {
                String summaryString = getExtract(strings[0]);

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
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String langInfo) {
            super.onPostExecute(langInfo);
            if (langInfo != null) {
                if (langInfo.equals("Not Found")) {
                    Toast.makeText(getContext(), "Could not find language summary", Toast.LENGTH_SHORT).show();
                } else {
                    String displayLang = langInfo + "\n\nTaken from the wikipedia page of " + lang + " at " + Calendar.getInstance().getTime();
                    textView.setText(displayLang);
                    progressLayout.setVisibility(View.GONE);
                    languageLayout.setVisibility(View.VISIBLE);
                }
            } else {
                ConnectivityManager manager = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
                if (manager != null) {
                    NetworkInfo info = manager.getActiveNetworkInfo();
                    if (info != null && info.isConnected()) {
                        Toast.makeText(getContext(), "Could not connect to Wikipedia: Some network error may have occurred", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "Could not connect to Wikipedia: Check your Internet connection", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "Could not connect to Wikipedia: Check your Internet connection", Toast.LENGTH_SHORT).show();
                }

                progressLayout.setVisibility(View.GONE);
                errorView.setVisibility(View.VISIBLE);
            }
        }
    }
}
