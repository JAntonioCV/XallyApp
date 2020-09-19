package com.jantonioc.xallyapp.FragmentsCuenta;


import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.jantonioc.ln.DetalleDeOrden;
import com.jantonioc.xallyapp.Adaptadores.CuentasAdapter;
import com.jantonioc.xallyapp.MainActivity;
import com.jantonioc.xallyapp.R;

import java.util.HashMap;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ListaCuentas extends Fragment {

    //interfaz
    private ExpandableListView expandableListView;
    private CuentasAdapter cuentasAdapter;
    private HashMap<String, List<DetalleDeOrden>> listaclientedetalle;
    private View rootView;


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

        //vista
        rootView = inflater.inflate(R.layout.fragment_lista_cuentas, container, false);

        expandableListView = rootView.findViewById(R.id.expandiblelist);

        //obtiene los clientes
        this.listaclientedetalle = getclientes();

        //pasamos la lita de clientes y la lista de detalles
        cuentasAdapter = new CuentasAdapter(getActivity(),MainActivity.listaClientes,listaclientedetalle);

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

}
