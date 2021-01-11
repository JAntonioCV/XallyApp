package com.jantonioc.xalliapp;


import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.jantonioc.ln.Orden;
import com.jantonioc.xalliapp.FragmentsCarnet.OrdenCarnet;
import com.jantonioc.xalliapp.FragmentsComanda.ClientesComanda;
import com.jantonioc.xalliapp.FragmentsCuenta.PedidosCuenta;
import com.jantonioc.xalliapp.FragmentsFinalizar.OrdenFinalizar;
import com.jantonioc.xalliapp.FragmentsOrdenes.Ordenes;
import com.jantonioc.xalliapp.FragmentsPedidos.Pedidos;

import static com.jantonioc.xalliapp.MainActivity.navigationView;


/**
 * A simple {@link Fragment} subclass.
 */
public class Principal extends Fragment {

    View rootView;
    CardView nuevaorden, nuevodetalle,dividir,finalizar, comanda, carnet;
    Fragment fragment;


    public Principal() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        // Inflate the layout for this fragment

        //Cambiando el toolbar
        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle("Acceso Rapido");

        //ocualtando el fab
        FloatingActionButton fab = getActivity().findViewById(R.id.fab);
        fab.hide();

        //vista
        rootView = inflater.inflate(R.layout.fragment_principal, container, false);

        nuevaorden = rootView.findViewById(R.id.cardnuevaorden);
        nuevodetalle = rootView.findViewById(R.id.carddetalles);
        dividir = rootView.findViewById(R.id.carddividir);
        finalizar = rootView.findViewById(R.id.cardfinalizar);
        comanda = rootView.findViewById(R.id.cardcomanda);
        carnet = rootView.findViewById(R.id.cardcarnet);

        nuevaorden.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                fragment = new Ordenes();
                MainActivity.listadetalle.clear();
                MainActivity.orden = new Orden();
                MainActivity.modpedidos = false;
                navigationView.getMenu().getItem(0).getSubMenu().getItem(0).setChecked(true);
                MainActivity.dashboard = true;
                cargarFragment(fragment);
            }
        });

        nuevodetalle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragment = new Pedidos();
                MainActivity.listadetalle.clear();
                MainActivity.orden = new Orden();
                MainActivity.modpedidos = false;
                navigationView.getMenu().getItem(0).getSubMenu().getItem(1).setChecked(true);
                MainActivity.dashboard = true;
                cargarFragment(fragment);
            }
        });

        dividir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragment = new PedidosCuenta();
                MainActivity.listadetalle.clear();
                MainActivity.orden = new Orden();
                MainActivity.modpedidos = false;
                navigationView.getMenu().getItem(0).getSubMenu().getItem(3).setChecked(true);
                MainActivity.dashboard = true;
                cargarFragment(fragment);
            }
        });

        finalizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragment = new OrdenFinalizar();
                MainActivity.listadetalle.clear();
                MainActivity.orden = new Orden();
                MainActivity.modpedidos = false;
                navigationView.getMenu().getItem(0).getSubMenu().getItem(2).setChecked(true);
                MainActivity.dashboard = true;
                cargarFragment(fragment);
            }
        });

        comanda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragment = new ClientesComanda();
                MainActivity.listadetalle.clear();
                MainActivity.orden = new Orden();
                MainActivity.modpedidos = false;
                navigationView.getMenu().getItem(1).getSubMenu().getItem(0).setChecked(true);
                MainActivity.dashboard = true;
                cargarFragment(fragment);

            }
        });

        carnet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragment = new OrdenCarnet();
                MainActivity.listadetalle.clear();
                MainActivity.orden = new Orden();
                MainActivity.modpedidos = false;
                navigationView.getMenu().getItem(1).getSubMenu().getItem(1).setChecked(true);
                MainActivity.dashboard = true;
                cargarFragment(fragment);
            }
        });

        return rootView;
    }


    //Cargar el fragment
    private void cargarFragment(Fragment fragment) {

        //sacamos de la pila o cola por que si no se montan unas vistas con otras
        FragmentManager fm = getActivity().getSupportFragmentManager();
        for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
            fm.popBackStack();
        }

        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.content, fragment);
        transaction.commit();
    }


}
