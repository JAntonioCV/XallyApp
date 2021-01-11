package com.jantonioc.xalliapp.Reportes;


import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.RectF;
import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.DefaultValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.MPPointF;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.jantonioc.ln.ProductoVendido;
import com.jantonioc.ln.VentasMes;
import com.jantonioc.xalliapp.Constans;
import com.jantonioc.xalliapp.R;
import com.jantonioc.xalliapp.Retrofit.NetworkClient;
import com.jantonioc.xalliapp.Retrofit.IWebServicesAPI;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeoutException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * A simple {@link Fragment} subclass.
 */
public class ReporteBarChart extends Fragment {

    private View rootView;
    private TextInputEditText fechatxt;
    private int mYear, mMonth, mDay;
    private List<VentasMes> lista;
    List<String> xAxisValues = new ArrayList<>(Arrays.asList("Ene", "Feb", "Mar", "Abr", "May", "Jun","Jul", "Aug", "Sep", "Oct", "Nov", "Dec"));
    BarChart barChart;
    String fecha;
    private boolean abierto = false;


    public ReporteBarChart() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //Cambiando el toolbar
        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle("Ventas mensuales por año");

        //ocualtando el fab
        FloatingActionButton fab = getActivity().findViewById(R.id.fab);
        fab.hide();

        rootView = inflater.inflate(R.layout.fragment_reporte_bar_chart, container, false);

        fechatxt = rootView.findViewById(R.id.fechatxt);
        barChart = rootView.findViewById(R.id.barchart);

        initBarchart();
        initDate();

        fechatxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!abierto)
                {
                    abierto = true;
                    DatePickerDialog datePickerDialog = new DatePickerDialog(rootView.getContext(),
                            new DatePickerDialog.OnDateSetListener() {

                                @Override
                                public void onDateSet(DatePicker view, int year,
                                                      int monthOfYear, int dayOfMonth) {

                                    fechatxt.setText( dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                                    fecha = (monthOfYear + 1)+ "-" + dayOfMonth + "-" + year;
                                    obtenerdatos(fecha);
                                }
                            }, mYear, mMonth, mDay);
                    datePickerDialog.show();
                    datePickerDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            abierto = false;
                        }
                    });
                }
            }
        });

        return rootView;

    }

    private void obtenerdatos(String fecha)
    {
        if(!fecha.isEmpty())
        {
            Retrofit retrofit = NetworkClient.getRetrofit();
            IWebServicesAPI iwebServicesAPI = retrofit.create(IWebServicesAPI.class);
            iwebServicesAPI.ventasPorAnio(fecha).enqueue(new Callback<List<VentasMes>>() {
                @Override
                public void onResponse(Call<List<VentasMes>> call, Response<List<VentasMes>> response) {
                    //si la peticion es exitosa
                    if(response.isSuccessful())
                    {
                        lista = new ArrayList<>();
                        lista = response.body();

                        if(lista.size()>0)
                        {
                            setData(lista);
                        }
                        else
                        {
                            Toast.makeText(rootView.getContext(),"No hay datos de venta, seleccione otra fecha",Toast.LENGTH_SHORT).show();
                            barChart.clear();
                        }
                    }
                    else
                    {
                        Toast.makeText(rootView.getContext(),"Error en el servidor razón :" + response.message(),Toast.LENGTH_SHORT).show();
                        barChart.clear();
                    }
                }

                @Override
                public void onFailure(Call<List<VentasMes>> call, Throwable t) {
                    Toast.makeText(rootView.getContext(), Constans.errorRetrofit(t),Toast.LENGTH_SHORT).show();
                }
            });
        }
        else
        {
            Toast.makeText(rootView.getContext(),"Seleccione una fecha",Toast.LENGTH_SHORT).show();
            barChart.clear();
        }
    }


    private void initBarchart()
    {
        //si se expande vertical o horizontalmente
        barChart.setPinchZoom(false);
        //enviar el menor numero por el que inicia el grafico en Y
        barChart.getAxisLeft().setAxisMinimum(0);
        //deshabilitar descripcion
        barChart.getDescription().setEnabled(false);
        //deshabilitar y en la derecha
        barChart.getAxisRight().setEnabled(false);
        barChart.setDrawValueAboveBar(true);
        barChart.setNoDataText("No hay Datos, Seleccione una fecha");


        Legend l = barChart.getLegend();
        l.setEnabled(false);


        XAxis xAxis = barChart.getXAxis();
        //posicion
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        //lineas de grid de fondo
        xAxis.setDrawGridLines(false);
        //granularidad en 1 no se hacen mas labels de x
        xAxis.setGranularity(1f);
        //angulo de rotacion
        xAxis.setLabelRotationAngle(-45);
        //enviar un array o lista para X
        xAxis.setValueFormatter(new IndexAxisValueFormatter(xAxisValues));


        YAxis leftyAxis = barChart.getAxisLeft();
        leftyAxis.setDrawGridLines(false);
        leftyAxis.setValueFormatter(new DollarFormatter());
    }

    private void initDate()
    {
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        fecha = (mMonth+ 1)+ "-" + mDay + "-" + mYear;
        fechatxt.setText(mDay + "-" + (mMonth+ 1) + "-" + mYear);
        obtenerdatos(fecha);

    }

    private void setData(List<VentasMes> lista)
    {
        ArrayList<BarEntry> barEntries = new ArrayList<>();

        float max = 0;
        for (VentasMes ventasMes : lista)
        {
            if(ventasMes.getTotalVentas()>max)
            {
                max = (float) ventasMes.getTotalVentas();
            }

            barEntries.add(new BarEntry(ventasMes.getMes()-1,(float)ventasMes.getTotalVentas()));
        }

        barChart.getAxisLeft().setAxisMaximum(max + 10);

        BarDataSet barDataSet = new BarDataSet(barEntries, "Meses");

        barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        barDataSet.setValueTextColor(Color.BLACK);
        barDataSet.setValueTextSize(16f);

        BarData barData = new BarData(barDataSet);

        barChart.setFitBars(true);
        barChart.setData(barData);
        barChart.animateY(2000);
        barChart.notifyDataSetChanged();
        barChart.invalidate();
    }
}
