package com.jantonioc.xalliapp.Reportes;


import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.jantonioc.xalliapp.R;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class ReporteBarChart extends Fragment {

    private View rootView;


    public ReporteBarChart() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        rootView = inflater.inflate(R.layout.fragment_reporte_bar_chart, container, false);

        BarChart barChart = rootView.findViewById(R.id.barchart);

        ArrayList<BarEntry> visitors = new ArrayList<>();

        visitors.add(new BarEntry(2014,1000));
        visitors.add(new BarEntry(2015,2000));
        visitors.add(new BarEntry(2016,3000));
        visitors.add(new BarEntry(2017,4000));
        visitors.add(new BarEntry(2018,5000));
        visitors.add(new BarEntry(2019,6000));
        visitors.add(new BarEntry(2020,7000));

        BarDataSet barDataSet = new BarDataSet(visitors,"Visitantes");
        barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        barDataSet.setValueTextColor(Color.BLACK);
        barDataSet.setValueTextSize(16f);

        BarData barData = new BarData(barDataSet);

        barChart.setFitBars(true);
        barChart.setData(barData);
        barChart.getDescription().setText("Visitas por año");
        barChart.animateY(2000);


        return rootView;

    }

}
