package com.jantonioc.xalliapp.FragmentsCuenta;


import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.jantonioc.ln.DetalleDeOrden;
import com.jantonioc.xalliapp.Adaptadores.CuentasAdapter;
import com.jantonioc.xalliapp.MainActivity;
import com.jantonioc.xalliapp.R;

import java.util.HashMap;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ListaCuentas extends Fragment implements IObserverItem {

    //interfaz
    private ExpandableListView expandableListView;
    private CuentasAdapter cuentasAdapter;
    private HashMap<String, List<DetalleDeOrden>> listaclientedetalle;
    private View rootView;
    private static TextView faltantes;


    public ListaCuentas() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //cambiar el nombre del toolbar
        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle("Clientes");

        //ocultar el floating boton
        FloatingActionButton fab = getActivity().findViewById(R.id.fab);
        fab.hide();

        MainActivity.cuenta=true;

        //vista
        rootView = inflater.inflate(R.layout.fragment_lista_cuentas, container, false);

        expandableListView = rootView.findViewById(R.id.expandiblelist);

        faltantes = rootView.findViewById(R.id.itemfaltante);

        faltantes.setText("Item faltantes: " + MainActivity.listadetalle.size());

        //obtiene los clientes
        this.listaclientedetalle = getclientes();

        //pasamos la lita de clientes y la lista de detalles

        CuentasAdapter.itemTamaño = this;

        cuentasAdapter = new CuentasAdapter(getActivity(), MainActivity.listaClientes,listaclientedetalle);

        //adaptamos
        expandableListView.setAdapter(cuentasAdapter);

        return rootView;

    }

    //hashmap que trae a los clientes desde un hashmap
    private HashMap<String,List<DetalleDeOrden>> getclientes()
    {
        HashMap<String, List<DetalleDeOrden>> clienteDetalles = new HashMap<>();

        for (int i = 0; i< MainActivity.listaClientes.size();i++)
        {
            clienteDetalles.put(MainActivity.listaClientes.get(i),MainActivity.listadetalles.get(i));
        }

        return clienteDetalles;
    }

    @Override
    public void tamaño(int tamaño) {

        faltantes.setText("Item faltantes: " + tamaño);
    }
}
