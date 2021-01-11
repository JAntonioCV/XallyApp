package com.jantonioc.xalliapp.Reportes;


import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.Toast;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.DefaultValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.jantonioc.ln.ProductoVendido;
import com.jantonioc.xalliapp.R;
import com.jantonioc.xalliapp.Retrofit.NetworkClient;
import com.jantonioc.xalliapp.Retrofit.IWebServicesAPI;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * A simple {@link Fragment} subclass.
 */
public class ReportePieChart extends Fragment {

    private View rootView;
    private TextInputEditText fechatxt;
    private int mYear, mMonth, mDay;
    private List<ProductoVendido> lista;
    PieChart pieChart;
    String fecha;
    private Date date = new Date();
    private DateFormat hourFormat = new SimpleDateFormat("HH:mm:ss");
    private boolean abierto = false;

    public ReportePieChart() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Cambiando el toolbar
        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle("Productos mas vendidos");

        //ocualtando el fab
        FloatingActionButton fab = getActivity().findViewById(R.id.fab);
        fab.hide();

        rootView = inflater.inflate(R.layout.fragment_reporte_pie_chart, container, false);

        pieChart = rootView.findViewById(R.id.piechart);
        fechatxt = rootView.findViewById(R.id.fechatxt);

        initpiechart();
        initdate();

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

    private void setData(List<ProductoVendido> lista)
    {
        //arreglo manual de visitantes para pruebas
        ArrayList<PieEntry> entries = new ArrayList<>();

        for (ProductoVendido productoVendido : lista)
        {
            entries.add(new PieEntry(productoVendido.cantidad,productoVendido.nombre));
        }

        //EL que envia los datos al setData
        PieDataSet pieDataSet = new PieDataSet(entries,null);
        //formateador de digitos
        pieDataSet.setValueFormatter(new DefaultValueFormatter(0));
        pieDataSet.setColors(ColorTemplate.JOYFUL_COLORS);
        pieDataSet.setValueTextColor(Color.BLACK);
        pieDataSet.setSliceSpace(1f);
        pieDataSet.setValueTextSize(16f);

        //enviar los datos a la vista
        PieData pieData = new PieData(pieDataSet);
        pieChart.setData(pieData);
        pieChart.animateXY(1400, 1400);
        pieChart.highlightValue(null);
        pieChart.notifyDataSetChanged();
        pieChart.invalidate();
    }

    private void obtenerdatos(String fecha)
    {
        if(!fecha.isEmpty())
        {
            String hora = hourFormat.format(date);
            fecha = fecha +" "+ hora;

            Retrofit retrofit = NetworkClient.getRetrofit();
            IWebServicesAPI iwebServicesAPI = retrofit.create(IWebServicesAPI.class);
            iwebServicesAPI.productosMasVendidos(fecha).enqueue(new Callback<List<ProductoVendido>>() {
                @Override
                public void onResponse(Call<List<ProductoVendido>> call, Response<List<ProductoVendido>> response) {
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
                            pieChart.clear();
                        }
                    }
                    else
                    {
                        Toast.makeText(rootView.getContext(),"Ah ocurrido un error inesperado",Toast.LENGTH_SHORT).show();
                        pieChart.clear();
                    }
                }

                @Override
                public void onFailure(Call<List<ProductoVendido>> call, Throwable t) {
                    Toast.makeText(rootView.getContext(),"Ah ocurrido un error inesperado razón: "+ t.getMessage(),Toast.LENGTH_SHORT).show();
                }
            });

        }
        else
        {
            Toast.makeText(rootView.getContext(),"Seleccione una fecha",Toast.LENGTH_SHORT).show();
            pieChart.clear();
        }
    }

    private void initpiechart()
    {
        //obtener las leyendas
        Legend l = pieChart.getLegend();
        //posicion donde se mostraran las descripciones
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        //como se van a alinear
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        //como se van a alinear
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        //habilitar las legendas sin corte
        l.setWordWrapEnabled(true);

        //si no hay datos
        pieChart.setNoDataText("No hay datos, seleccione una fecha");
        //quitar los textos del setData
        pieChart.setDrawEntryLabels(false);
        //quitar descripcion
        pieChart.getDescription().setEnabled(false);
        //Agregar descripcion en medio
        pieChart.setCenterText("Productos mas vendidos");
    }

    private void initdate()
    {
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        fecha = (mMonth+ 1)+ "-" + mDay + "-" + mYear;
        fechatxt.setText(mDay + "-" + (mMonth+ 1) + "-" + mYear);
        obtenerdatos(fecha);
    }


}
