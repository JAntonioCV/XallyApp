package com.jantonioc.xallyapp.Fragments;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.jantonioc.ln.DetalleDeOrden;
import com.jantonioc.ln.Menu;
import com.jantonioc.xallyapp.Adaptadores.DetalleOrdenAdapter;
import com.jantonioc.xallyapp.MainActivity;
import com.jantonioc.xallyapp.R;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class DetalleOrden extends Fragment {

    View rootView;
    RecyclerView lista;
    DetalleOrdenAdapter adapter;

    TextInputLayout txtcantidad;
    TextInputLayout txtnota;
    TextView txtplatillo;

    //FloatingActionButton fabenviar;
    Button btnenviar;
    TextView total;



    public DetalleOrden() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle("Detalle Orden");

        FloatingActionButton fab = getActivity().findViewById(R.id.fab);
        fab.hide();

        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_detalle_orden, container, false);

        lista = rootView.findViewById(R.id.recyclerViewDetalleOrden);
        lista.setHasFixedSize(true);
        lista.setLayoutManager(new LinearLayoutManager(rootView.getContext()));

        total = rootView.findViewById(R.id.total);

        total.setText("$"+Double.valueOf(calcularTotal(MainActivity.listadetalle)).toString());

        btnenviar= rootView.findViewById(R.id.btnenviar);

        btnenviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(rootView.getContext(),"Enviando",Toast.LENGTH_SHORT).show();
            }
        });

        //swipe to delete

        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                Toast.makeText(rootView.getContext(), "on Delete", Toast.LENGTH_SHORT).show();
                int position = viewHolder.getAdapterPosition();
                MainActivity.listadetalle.remove(position);
                adapter.notifyDataSetChanged();

            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(lista);

        listaDetalleDeOrden();

        return rootView;
    }

    private void listaDetalleDeOrden()
    {
        DetalleOrdenAdapter adapter = new DetalleOrdenAdapter(MainActivity.listadetalle);
        adapter.setClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                modificardetalle(MainActivity.listadetalle.get(lista.getChildAdapterPosition(v)));
            }
        });

        lista.setAdapter(adapter);
    }

    //Validando si se modifica la orden o se agrega una nueva || aqui deberia mostrar lo que ya tengo que podria ser modificado
    private void modificardetalle(final DetalleDeOrden detalleDeOrden)
    {
        for(final DetalleDeOrden detalleActual : MainActivity.listadetalle )
        {
            if(detalleDeOrden.getMenuid() == detalleActual.getMenuid())
            {
                final AlertDialog builder = new AlertDialog.Builder(rootView.getContext()).create();

                View view = getLayoutInflater().inflate(R.layout.detalle_orden,null);
                txtplatillo = view.findViewById(R.id.nombreplatillo);
                txtplatillo.setText(detalleActual.getNombreplatillo());
                txtcantidad = view.findViewById(R.id.cantidad);
                txtnota = view.findViewById(R.id.notaopcional);

                txtcantidad.getEditText().setText(String.valueOf(detalleActual.getCantidad()));
                txtnota.getEditText().setText(detalleActual.getNota());

                Button ordenar = view.findViewById(R.id.btnordenar);
                ordenar.setText("MODIFICAR");

                ordenar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        detalleActual.setCantidad(Integer.valueOf(txtcantidad.getEditText().getText().toString()));
                        detalleActual.setNota(txtnota.getEditText().getText().toString());
                        listaDetalleDeOrden();
                        total.setText("$"+Double.valueOf(calcularTotal(MainActivity.listadetalle)).toString());
                        builder.cancel();
                    }
                });

                builder.setView(view);
                builder.create();
                builder.show();
            }
        }
    }

    private double calcularTotal(List<DetalleDeOrden> detalleDeOrdens)
    {
        double total=0;

        for (DetalleDeOrden detalleActual : detalleDeOrdens)
        {
            total=total + detalleActual.getCantidad()*detalleActual.getPrecio();
        }
        return total;
    }


}
