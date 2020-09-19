package com.jantonioc.xallyapp.FragmentsCuenta;


import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.jantonioc.ln.DetalleDeOrden;
import com.jantonioc.ln.Menu;
import com.jantonioc.xallyapp.Adaptadores.CuentasAdapter;
import com.jantonioc.xallyapp.Adaptadores.DetalleCuentasAdapter;
import com.jantonioc.xallyapp.Adaptadores.DetalleOrdenAdapter;
import com.jantonioc.xallyapp.FragmentsPedidos.Pedidos;
import com.jantonioc.xallyapp.MainActivity;
import com.jantonioc.xallyapp.R;
import com.jantonioc.xallyapp.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class DetallesCuenta extends Fragment {

    //interfaz
    private View rootView;
    private RecyclerView lista;
    private DetalleCuentasAdapter adapter;

    private ProgressBar progressBar;

    //posicon
    Integer groupPosition;

    //dialog
    private TextInputLayout txtcantidad;
    private TextView platillotxt;
    private TextView cantidadtxt;

    Button btnagregar;


    public DetallesCuenta() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //toolbar
        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle("Detalles de Orden");

        //fab botton
        FloatingActionButton fab = getActivity().findViewById(R.id.fab);
        fab.hide();

        //vista
        rootView = inflater.inflate(R.layout.fragment_detalles_cuenta, container, false);

        //la lista
        lista = rootView.findViewById(R.id.recyclerViewDetalleOrdenCuenta);
        lista.setHasFixedSize(true);
        lista.setLayoutManager(new LinearLayoutManager(rootView.getContext()));

        //progressbar
        progressBar = rootView.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        //Obteniendo la id de la categoria selecionada
        groupPosition = getArguments().getInt("groupPosition", 0);

        ObtenerDetalles();

        // Inflate the layout for this fragment
        return rootView;
    }

    //obtener las ordenes de un detalle
    private void ObtenerDetalles() {

                    //Si la lista es mayor que 0 adaptamos y hacemos el evento on click y long Click del la lista
                    if (MainActivity.listadetalle.size() > 0) {

                        progressBar.setVisibility(View.GONE);
                        adapter = new DetalleCuentasAdapter(MainActivity.listadetalle);
                        adapter.setClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                AgregarDetalle(MainActivity.listadetalle.get(lista.getChildAdapterPosition(v)),lista.getChildAdapterPosition(v));
                            }
                        });

                        lista.setAdapter(adapter);

                    }
                    //Si no es mayor regresamos al fragmento anterior y sacamos el fragment actual de la pila
                    else {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(rootView.getContext(), "No hay detalles para dividir", Toast.LENGTH_SHORT).show();

                        FragmentManager fm = getActivity().getSupportFragmentManager();
                        for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
                            fm.popBackStack();
                        }

                        Fragment fragment = new ListaCuentas();
                        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.content, fragment);
                        transaction.addToBackStack(null);
                        transaction.commit();
                    }



    }

    private void AgregarDetalle(final DetalleDeOrden obj, final int position)
    {
            //Abrimos la modal agregar el nuevo detalle de orden
            final AlertDialog builder = new AlertDialog.Builder(rootView.getContext()).create();
            View view = getLayoutInflater().inflate(R.layout.detalle_cuenta, null);

            platillotxt = view.findViewById(R.id.nombreplatillo);
            cantidadtxt = view.findViewById(R.id.cantidadpedido);
            txtcantidad = view.findViewById(R.id.cantidad);
            btnagregar = view.findViewById(R.id.btnagregar);

            platillotxt.setText(obj.getNombreplatillo());
            cantidadtxt.setText(String.valueOf(obj.getCantidad()));
            txtcantidad.getEditText().setText("1");

            btnagregar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //Validamos que los campos no esten vacios o incumplan
                    if (!validarCampos()) {
                        return;
                    } else {

                        //Agregar detalle orden
                        DetalleDeOrden detalleDeOrden = new DetalleDeOrden();
                        detalleDeOrden.setCantidad(Integer.valueOf(txtcantidad.getEditText().getText().toString()));
                        detalleDeOrden.setNombreplatillo(obj.getNombreplatillo());
                        detalleDeOrden.setPrecio(obj.getPrecio());
                        detalleDeOrden.setMenuid(obj.getMenuid());

                        //si ya existe para el cliente actualizar la cantidad
                        if(yaExiste(detalleDeOrden))
                        {
                            Toast.makeText(rootView.getContext(), "Cantidad Actualizada", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            //Agregar a una posicion especifica
                            MainActivity.listadetalles.get(groupPosition).add(detalleDeOrden);
                        }

                        //Actualizar la cantidad en la lista
                        MainActivity.listadetalle.get(position).setCantidad((MainActivity.listadetalle.get(position).getCantidad()-Integer.valueOf(txtcantidad.getEditText().getText().toString())));

                        //si la lista en esa posicion tiene cantidad 0 eliminar
                        if(MainActivity.listadetalle.get(position).getCantidad()==0)
                        {
                            //remover el item de la lista
                            MainActivity.listadetalle.remove(position);
                        }

                        //notificar los cambios
                        adapter.notifyDataSetChanged();

                        //Para que se cierre automaticamente al darla guardar
                        builder.cancel();

                        //abrir la vista del cliente automaticamente cuando la lista este vacia
                        if(MainActivity.listadetalle.size()==0)
                        {
                            FragmentManager fm = getActivity().getSupportFragmentManager();
                            for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
                                fm.popBackStack();
                            }

                            Fragment fragment = new ListaCuentas();
                            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                            transaction.replace(R.id.content, fragment);
                            transaction.addToBackStack(null);
                            transaction.commit();
                        }
                    }

                }
            });

            builder.setView(view);
            builder.create();
            builder.show();
    }


    //Validando que no esten los campos vacios
    private boolean validarCampos() {
        boolean isValidate = true;

        String cantidadInput = txtcantidad.getEditText().getText().toString().trim();

        if (cantidadInput.isEmpty()) {
            isValidate = false;
            txtcantidad.setError("Cantidad no puede estar vacio");

        } else if (Integer.valueOf(cantidadInput) <= 0) {
            isValidate = false;
            txtcantidad.setError("La cantidad no puede ser menor a 1");

        } else if (Integer.valueOf(cantidadInput) > Integer.valueOf(cantidadtxt.getText().toString())) {
            isValidate = false;
            txtcantidad.setError("La cantidad no puede ser mayor a lo ordenado");
        } else {
            txtcantidad.setError(null);
        }

        return isValidate;
    }

    //Validar el tipo de orden modificar || nueva
    private boolean yaExiste(final DetalleDeOrden obj) {

        for (DetalleDeOrden detalleActual : MainActivity.listadetalles.get(groupPosition)) {
            if (obj.getMenuid() == detalleActual.getMenuid()) {
                detalleActual.setCantidad(obj.getCantidad()+detalleActual.getCantidad());
                return true;
            }
        }
        return false;
    }
}
