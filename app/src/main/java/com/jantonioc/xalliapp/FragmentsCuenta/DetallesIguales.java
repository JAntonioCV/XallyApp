package com.jantonioc.xalliapp.FragmentsCuenta;


import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.jantonioc.ln.Pago;
import com.jantonioc.xalliapp.Adaptadores.AdapterCuentaIgual;
import com.jantonioc.xalliapp.R;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class DetallesIguales extends Fragment {

    private ListView lista;
    private List<Pago> listaPagos;
    private View rootView;
    private AdapterCuentaIgual adapter;

    //
    String aporte;
    int clientes;



    public DetallesIguales() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //Cambiando el toolbar
        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle("Partes Iguales");

        //ocualtando el fab
        FloatingActionButton fab = getActivity().findViewById(R.id.fab);
        fab.hide();

        //vista
        rootView = inflater.inflate(R.layout.fragment_detalles_iguales, container, false);

        lista = rootView.findViewById(R.id.lisview);

        //obteniendo
        Bundle bundle = getArguments();
        aporte = bundle.getString("aporte","");
        clientes =  bundle.getInt("cantidad",0);

        //crea la lista
        crearLista();

        //adapta la lista
        adapter = new AdapterCuentaIgual(rootView.getContext(),listaPagos);

        //envia el adaptador
        lista.setAdapter(adapter);

        return rootView;
    }

    //crea la lista con los clientes y su aporte
    private void crearLista()
    {
        listaPagos = new ArrayList<>();

        for(int i = 0; i<clientes;i++)
        {
            Pago pago = new Pago();
            pago.setCliente("Cliente: "+(i+1));
            pago.setPago(aporte);

            listaPagos.add(pago);
        }
    }

}
