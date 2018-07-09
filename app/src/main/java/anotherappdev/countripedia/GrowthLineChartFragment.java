package anotherappdev.countripedia;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.MPPointF;

import java.util.ArrayList;

public class GrowthLineChartFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_line_chart, container, false);

        Bundle args = getArguments();
        ArrayList<Entry> growthEntries = args.getParcelableArrayList("ENTRIES");

        final LineChart growthLineChart = view.findViewById(R.id.line_chart);

        LineDataSet dataSet = new LineDataSet(growthEntries, "Population");
        dataSet.setDrawCircles(false);
        dataSet.setDrawValues(false);
        dataSet.setHighlightEnabled(true);
        dataSet.setLineWidth(2.5f);


        LineData lineData = new LineData(dataSet);

        Description description = new Description();
        description.setText("Population of " + args.getString("NAME"));
        growthLineChart.setDrawGridBackground(false);
        growthLineChart.setData(lineData);

        XAxis xAxis = growthLineChart.getXAxis();
        xAxis.setDrawGridLines(false);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        YAxis leftAxis = growthLineChart.getAxisLeft();
        leftAxis.setDrawGridLines(false);

        YAxis rightAxis = growthLineChart.getAxisRight();
        rightAxis.setDrawLabels(false);
        rightAxis.setDrawGridLines(false);

        growthLineChart.setDrawBorders(false);
        growthLineChart.setAutoScaleMinMaxEnabled(true);

        if (PreferenceManager.getDefaultSharedPreferences(getContext()).getBoolean("prefTheme", false)) {
            growthLineChart.setBorderColor(Color.WHITE);
            leftAxis.setTextColor(Color.WHITE);
            xAxis.setTextColor(Color.WHITE);
            description.setTextColor(Color.WHITE);
            growthLineChart.getLegend().setTextColor(Color.WHITE);
        } else {
            dataSet.setColor(Color.BLUE);
        }

        growthLineChart.setDescription(description);
        growthLineChart.setDrawMarkers(true);
        growthLineChart.setTouchEnabled(true);
        growthLineChart.setMarker(new GrowthLineChartFragment.CustomMarkerView(getContext(), R.layout.custom_marker_layout));
        growthLineChart.invalidate();
        growthLineChart.setVisibility(View.VISIBLE);

        growthLineChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                growthLineChart.highlightValue(h);
            }

            @Override
            public void onNothingSelected() {

            }
        });


        return view;
    }

    public class CustomMarkerView extends MarkerView {

        private TextView tvContent;

        public CustomMarkerView (Context context, int layoutResource) {
            super(context, layoutResource);
            tvContent = findViewById(R.id.tvContent);
        }

        @Override
        public void refreshContent(Entry e, Highlight highlight) {
            String x = String.valueOf((long) e.getY()) + ", " + String.valueOf((long) e.getX());
            tvContent.setText(x);
        }

        @Override
        public MPPointF getOffset() {
            return new MPPointF(-(getWidth() / 2), -getHeight());
        }
    }
}
