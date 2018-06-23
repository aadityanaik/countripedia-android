package com.halfwitdevs.countripedia;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.MPPointF;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.LinkedTreeMap;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Scanner;
import java.util.Set;

import javax.net.ssl.HttpsURLConnection;

public class CurrencyFragment extends Fragment {
    LinearLayout convLayout, progressLayout;
    ImageView error;
    TextView rate, result;
    EditText input;
    String currencyCode;
    LineChart lineChart;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_currency, container, false);

        Bundle args = getArguments();

        if(args != null) {
            currencyCode = args.getString("CODE");
            convLayout = view.findViewById(R.id.conv_rate_layout);
            progressLayout = view.findViewById(R.id.prog_circle);
            error =view.findViewById(R.id.error_img);
            lineChart = view.findViewById(R.id.rateChart);
            rate = view.findViewById(R.id.currency_rate);
            result = view.findViewById(R.id.currency_cvted);
            result.setHint("INR");
            input = view.findViewById(R.id.currency_input);
            input.setHint(currencyCode);

            new GetConversionRates().execute(currencyCode);
        }

        return view;
    }

    class GetConversionRates extends AsyncTask<String, Void, String> {
        Date date, newDate;
        SimpleDateFormat dateFormat;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                Calendar calendar = Calendar.getInstance();
                date = calendar.getTime();
                calendar.add(Calendar.DAY_OF_YEAR, -6);
                newDate = calendar.getTime();

                dateFormat = new SimpleDateFormat("yyyy-MM-dd");

                String results;
                results = getCurrencyInfo("https://free.currencyconverterapi.com/api/v5/convert?q=" + strings[0] + "_INR&compact=ultra&date=" +
                        dateFormat.format(newDate) + "&endDate=" + dateFormat.format(date));

                return results;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (s != null) {
                convLayout.setVisibility(View.VISIBLE);

                GsonBuilder builder = new GsonBuilder();
                Object cvtRate = builder.create().fromJson(s, Object.class);

                final double rateDouble = Double.parseDouble(((LinkedTreeMap) (((LinkedTreeMap) cvtRate).get(currencyCode + "_INR"))).get(dateFormat.format(date)).toString());

                LinkedTreeMap dateRateMap = (LinkedTreeMap) ((((LinkedTreeMap) cvtRate).get(currencyCode + "_INR")));

                ArrayList<Entry> entries = new ArrayList<>();

                final Set keySet = dateRateMap.keySet();

                for (int i = 0; i < keySet.size(); i++) {
                    entries.add(new Entry(i, Float.parseFloat(dateRateMap.get(keySet.toArray()[i].toString()).toString())));
                }

                LineDataSet dataSet = new LineDataSet(entries, "Exchange Rate");
                dataSet.setDrawCircles(false);
                dataSet.setDrawValues(false);
                dataSet.setHighlightEnabled(true);

                LineData lineData = new LineData(dataSet);

                lineChart.setData(lineData);

                XAxis xAxis = lineChart.getXAxis();
                xAxis.setDrawGridLines(false);
                xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                xAxis.setLabelRotationAngle(-45f);
                xAxis.setValueFormatter(new IAxisValueFormatter() {
                    @Override
                    public String getFormattedValue(float value, AxisBase axis) {
                        return keySet.toArray()[(int) value].toString();
                    }
                });


                YAxis leftAxis = lineChart.getAxisLeft();
                leftAxis.setDrawGridLines(false);

                YAxis rightAxis = lineChart.getAxisRight();
                rightAxis.setDrawLabels(false);
                rightAxis.setDrawGridLines(false);

                lineChart.setDrawBorders(false);
                lineChart.setAutoScaleMinMaxEnabled(true);

                if (PreferenceManager.getDefaultSharedPreferences(getContext()).getBoolean("prefTheme", false)) {
                    lineChart.setBorderColor(Color.WHITE);
                    leftAxis.setTextColor(Color.WHITE);
                    xAxis.setTextColor(Color.WHITE);
                    lineChart.getLegend().setTextColor(Color.WHITE);
                } else {
                    dataSet.setColor(Color.BLUE);
                }


                lineChart.setDrawMarkers(true);
                lineChart.setTouchEnabled(true);
                lineChart.setMarker(new CustomMarkerView(getContext(), R.layout.custom_marker_layout));
                lineChart.invalidate();

                lineChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
                    @Override
                    public void onValueSelected(Entry e, Highlight h) {
                        lineChart.highlightValue(h);
                    }

                    @Override
                    public void onNothingSelected() {

                    }
                });

                rate.setText(String.valueOf(rateDouble));

                input.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        double inputDouble;
                        try {
                            inputDouble = Double.parseDouble(s.toString());
                            result.setText(String.format(Locale.getDefault(), "%.2f", inputDouble * rateDouble));
                        } catch (NumberFormatException e) {
                            result.setText("");
                        }
                    }
                });

                progressLayout.setVisibility(View.GONE);
                convLayout.setVisibility(View.VISIBLE);
            } else {
                //error
                progressLayout.setVisibility(View.GONE);
                error.setVisibility(View.VISIBLE);
            }
        }

        private String getCurrencyInfo(String url) throws IOException {
            HttpsURLConnection connection;
            connection = (HttpsURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/json");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            Scanner input = new Scanner
                    (new InputStreamReader(connection.getInputStream()));

            StringBuilder output = new StringBuilder();

            while (input.hasNextLine()) {
                output.append(input.nextLine());
            }

            connection.disconnect();

            return output.toString();
        }

        class CustomMarkerView extends MarkerView {

            private TextView tvContent;

            public CustomMarkerView(Context context, int layoutResource) {
                super(context, layoutResource);
                tvContent = findViewById(R.id.tvContent);
            }

            @Override
            public void refreshContent(Entry e, Highlight highlight) {
                String x = String.valueOf(e.getY());
                tvContent.setText(x);
            }

            @Override
            public MPPointF getOffset() {
                return new MPPointF(-(getWidth() / 2), -getHeight());
            }
        }
    }
}
