package com.jantonioc.xallyapp.Fragments;


import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.jantonioc.ln.Menu;
import com.jantonioc.xallyapp.Adaptadores.MenuAdapter;
import com.jantonioc.xallyapp.MainActivity;
import com.jantonioc.xallyapp.R;
import com.jantonioc.xallyapp.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 */
public class Ordenes extends Fragment {


    View rootView;
    Button btnAgregarOrden;
    Date date = new Date();

    TextInputLayout txtcodigo;
    TextInputLayout txtfecha;
    TextInputLayout txthora;


    DateFormat hourFormat = new SimpleDateFormat("HH:mm:ss");
    DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");


    public Ordenes() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        //Cambiando el toolbar
        Toolbar toolbar=getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle("Nueva Orden");

        //ocualtando el fab
        FloatingActionButton fab = getActivity().findViewById(R.id.fab);
        fab.hide();

        rootView = inflater.inflate(R.layout.fragment_orden, container, false);

        txtcodigo = rootView.findViewById(R.id.codigoorden);
        txtfecha = rootView.findViewById(R.id.fechaorden);
        txthora = rootView.findViewById(R.id.horaorden);

        txtcodigo.getEditText().setText("003");
        txtfecha.getEditText().setText(dateFormat.format(date));
        txthora.getEditText().setText(hourFormat.format(date));

        MainActivity.orden.setCodigo(txtcodigo.getEditText().getText().toString());
        MainActivity.orden.setFechaorden(txtfecha.getEditText().getText().toString());
        MainActivity.orden.setTiempoorden(txthora.getEditText().getText().toString());
        MainActivity.orden.setEstado(true);


        //obtenerCodigo();

        btnAgregarOrden = rootView.findViewById(R.id.btnagregarpedido);

        btnAgregarOrden.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    Fragment fragment = new Categorias();
                    FragmentTransaction transaction=getActivity().getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.content,fragment);
                    transaction.addToBackStack(null);
                    transaction.commit();
            }
        });


        return rootView;


    }

    public void obtenerCodigo()
    {
        String uri="http://xally.somee.com/Xally/API/OrdenesWS/UltimoCodigo";
        StringRequest request= new StringRequest(Request.Method.GET, uri, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {

                    String codigo=null;

                    JSONObject jsonObject = new JSONObject(response);

                    codigo = jsonObject.getString("codigo");


                    if(codigo != null) {

                        txtcodigo.getEditText().setText(txtcodigo.getEditText().getText().toString());
                        txtfecha.getEditText().setText(dateFormat.format(date));
                        txthora.getEditText().setText(hourFormat.format(date));

                        MainActivity.orden.setCodigo(codigo);
                        MainActivity.orden.setCodigo(txtfecha.getEditText().getText().toString());
                        MainActivity.orden.setCodigo(txthora.getEditText().getText().toString());

                    }
                    else
                    {
                        Toast.makeText(rootView.getContext(), "Error al obtener el codigo, intente de nuevo", Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException ex) {
                    Toast.makeText(rootView.getContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
                    ex.printStackTrace();
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(rootView.getContext(), error.getMessage(), Toast.LENGTH_LONG ).show();

            }
        });


        VolleySingleton.getInstance(rootView.getContext()).addToRequestQueue(request);
    }


}
