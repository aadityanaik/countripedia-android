package anotherappdev.countripedia;

import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;

public class DistributionPieChartFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pie_chart, container, false);

        Bundle args = getArguments();

        PieChart pieChart = view.findViewById(R.id.pie_chart);

        ArrayList<PieEntry> distributionPieEntries = args.getParcelableArrayList("ENTRIES");

        PieDataSet dataSet = new PieDataSet(distributionPieEntries, "");
        dataSet.setValueTextSize(15f);
        dataSet.setValueTextColor(Color.WHITE);
        dataSet.setColors(Color.BLUE, Color.rgb(0, 100, 0));
        PieData data = new PieData(dataSet);
        pieChart.setData(data);
        Description description = new Description();
        description.setText("Population Distribution of " + args.getString("NAME") + " in " + args.getInt("DATE"));
        description.setTextSize(10);

        if (PreferenceManager.getDefaultSharedPreferences(getContext()).getBoolean("prefTheme", false)) {
            description.setTextColor(Color.WHITE);
            pieChart.getLegend().setTextColor(Color.WHITE);
            pieChart.setHoleColor(getResources().getColor(R.color.primary_dark_material_dark_dark));
        }

        pieChart.setDescription(description);

        pieChart.invalidate();

        return view;
    }
}
