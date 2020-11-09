package com.jantonioc.xalliapp.Reportes;


import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.jantonioc.xalliapp.R;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class ReportePieChart extends Fragment {

    private View rootView;

    public ReportePieChart() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_reporte_pie_chart, container, false);

        PieChart pieChart = rootView.findViewById(R.id.piechart);

        ArrayList<PieEntry> visitors = new ArrayList<>();

        visitors.add(new PieEntry(2014,"1000"));
        visitors.add(new PieEntry(2015,"2000"));
        visitors.add(new PieEntry(2016,"3000"));
        visitors.add(new PieEntry(2017,"4000"));
        visitors.add(new PieEntry(2018,"5000"));
        visitors.add(new PieEntry(2019,"6000"));
        visitors.add(new PieEntry(2020,"7000"));

        PieDataSet pieDataSet = new PieDataSet(visitors,"Visitantes");
        pieDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        pieDataSet.setValueTextColor(Color.BLACK);
        pieDataSet.setValueTextSize(16f);

        PieData pieData = new PieData(pieDataSet);

        pieChart.setData(pieData);
        pieChart.getDescription().setEnabled(false);
        pieChart.setCenterText("Visitantes");
        pieChart.animate();

        return rootView;
    }

}
