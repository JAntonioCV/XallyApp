package com.jantonioc.xalliapp.Reportes;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.text.DecimalFormat;

public class DollarFormatter extends ValueFormatter {

    private DecimalFormat format = new DecimalFormat("###,###,##0.0");

    @Override
    public String getAxisLabel(float value, AxisBase axis) {
        return format.format(value) + " $";
    }
}
