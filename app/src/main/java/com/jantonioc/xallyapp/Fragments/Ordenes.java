package com.jantonioc.xallyapp.Fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.jantonioc.xallyapp.MainActivity;
import com.jantonioc.xallyapp.R;
import com.jantonioc.xallyapp.VolleySingleton;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 */
public class Ordenes extends Fragment {


    //Variables de la interfaz
    private View rootView;
    private Button btnAgregarOrden;
    private Date date = new Date();

    private TextInputLayout txtcodigo;
    private TextInputLayout txtfecha;
    private TextInputLayout txthora;

    //obtener los formatos de fecha y hora
    private DateFormat hourFormat = new SimpleDateFormat("HH:mm:ss");
    private DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");


    public Ordenes() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        //Cambiando el toolbar
        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle("Nueva Orden");

        //ocualtando el fab
        FloatingActionButton fab = getActivity().findViewById(R.id.fab);
        fab.hide();

        rootView = inflater.inflate(R.layout.fragment_orden, container, false);

        txtcodigo = rootView.findViewById(R.id.codigoorden);
        txtfecha = rootView.findViewById(R.id.fechaorden);
        txthora = rootView.findViewById(R.id.horaorden);

        //Obteniendo el ultimo codigo
        obtenerCodigo();

        btnAgregarOrden = rootView.findViewById(R.id.btnagregarpedido);

        btnAgregarOrden.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
        //Al agregar orden abrir el fragmento categoria
                Fragment fragment = new Categorias();
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.content, fragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });


        return rootView;


    }

    private void obtenerCodigo() {
        String uri = "http://192.168.1.52/MenuAPI/API/OrdenesWS/UltimoCodigo";
        StringRequest request = new StringRequest(Request.Method.GET, uri, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                //Si la respuesta es distinta de nula tenemos codigo de lo contrario no
                if (response != null) {

                    txtcodigo.getEditText().setText(response.replace("\"", ""));
                    txtfecha.getEditText().setText(dateFormat.format(date));
                    txthora.getEditText().setText(hourFormat.format(date));

                    MainActivity.orden.setCodigo(txtcodigo.getEditText().getText().toString());
                    MainActivity.orden.setFechaorden(txtfecha.getEditText().getText().toString());
                    MainActivity.orden.setTiempoorden(txthora.getEditText().getText().toString());
                    MainActivity.orden.setEstado(true);

                } else {
                    Toast.makeText(rootView.getContext(), "Error al obtener el codigo, intente de nuevo", Toast.LENGTH_SHORT).show();
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(rootView.getContext(), "Eror"+error.getMessage()+error, Toast.LENGTH_LONG).show();

            }
        });


        VolleySingleton.getInstance(rootView.getContext()).addToRequestQueue(request);
    }


}
