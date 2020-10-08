package com.jantonioc.xalliapp.FragmentsCuenta;


import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.jantonioc.ln.DetalleDeOrden;
import com.jantonioc.xalliapp.Constans;
import com.jantonioc.xalliapp.VolleySingleton;
import com.jantonioc.xalliapp.Adaptadores.DetalleOrdenAdapter;
import com.jantonioc.xalliapp.FragmentsComanda.OrdenComanda;
import com.jantonioc.xalliapp.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class DetallesOrdenIguales extends Fragment {

    //interfaz
    private View rootView;
    private RecyclerView lista;
    private DetalleOrdenAdapter adapter;

    private List<DetalleDeOrden> listadetalle;

    private ProgressBar progressBar;

    private Button btnenviar;
    private TextView total;

    private int idorden;
    private int cantidad;


    public DetallesOrdenIguales() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //toolbar
        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle("Detalle Ordenes");

        //fab boton
        FloatingActionButton fab = getActivity().findViewById(R.id.fab);
        fab.hide();

        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_detalles_de_orden, container, false);

        lista = rootView.findViewById(R.id.recyclerViewDetalleOrden);
        lista.setHasFixedSize(true);
        lista.setLayoutManager(new LinearLayoutManager(rootView.getContext()));

        //progressbar
        progressBar = rootView.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        total = rootView.findViewById(R.id.total);

        //Validar si la lista tiene datos envia si no, muestra un mensaje
        btnenviar = rootView.findViewById(R.id.btnenviar);
        btnenviar.setText("Dividir");

        //abrir el agregar comanda
        btnenviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String aporte = calcularAporte();

                Fragment fragment = new DetallesIguales();
                Bundle bundle = new Bundle();
                bundle.putString("aporte",aporte);
                bundle.putInt("cantidad",cantidad);
                fragment.setArguments(bundle);
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.content, fragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        //obteniendo el id de la orden selecionada
        Bundle bundle = getArguments();
        int idorden = bundle.getInt("idOrden",0);
        cantidad = bundle.getInt("cantidad",0);

        //obtenemos los detalles
        obtenerDetalles(idorden);

        return rootView;

    }

    private void obtenerDetalles(int idorden) {

        listadetalle = new ArrayList<>();

        String uri = Constans.URLBASE+"DetallesDeOrdenWS/DetalleDeOrdenCuenta/" + idorden;
        StringRequest request = new StringRequest(Request.Method.GET, uri, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {

                    //obteniendo el arreglo desde la respuesta
                    JSONArray jsonArray = new JSONArray(response);

                    //Recorriendo el arreglo paara obtener los objetos
                    for (int i = 0; i < jsonArray.length(); i++) {
                        //Obteniendo los objetos
                        JSONObject obj = jsonArray.getJSONObject(i);

                        //Obteniendo los datos y convirtiendolos a Detalle orden
                        DetalleDeOrden detalleDeOrden = new DetalleDeOrden(
                                obj.getInt("id"),
                                obj.getInt("cantidadorden"),
                                obj.getString("notaorden"),
                                obj.getString("nombreplatillo"),
                                obj.getBoolean("estado"),
                                obj.getDouble("preciounitario"),
                                obj.getInt("menuid"),
                                obj.getBoolean("fromservice")
                        );

                        //Agregando a la lista del menu
                        listadetalle.add(detalleDeOrden);
                    }

                    //Si la lista es mayor que 0 adaptamos y hacemos el evento on click y long Click del la lista
                    if (listadetalle.size() > 0) {

                        progressBar.setVisibility(View.GONE);

                        //Calcular el total
                        total.setText("$" + calcularTotal(listadetalle));

                        adapter = new DetalleOrdenAdapter(listadetalle);

                        lista.setAdapter(adapter);

                    }
                    //Si no es mayor regresamos al fragmento anterior y sacamos el fragment actual de la pila
                    else {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(rootView.getContext(), "Esta Orden no tiene detalles", Toast.LENGTH_SHORT).show();

                        FragmentManager fm = getActivity().getSupportFragmentManager();
                        for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
                            fm.popBackStack();
                        }

                        //acordarse de abrir la vista anterior
                        Fragment fragment = new OrdenComanda();
                        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.content, fragment);
                        transaction.addToBackStack(null);
                        transaction.commit();
                    }

                } catch (JSONException ex) {

                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(rootView.getContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
                    ex.printStackTrace();
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(rootView.getContext(), error.getMessage(), Toast.LENGTH_LONG).show();

            }
        })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                //authorizacion basica con las credenciales del usuario en la db del sistema
                String [] cred  = Constans.obtenerDatos(rootView.getContext());
                HashMap<String, String> params = new HashMap<String, String>();
                String creds = String.format("%s:%s",cred[0],cred[1]);
                String auth = "Basic " + Base64.encodeToString(creds.getBytes(), Base64.NO_WRAP);
                params.put("Authorization", auth);
                return params;
            }
        };

        VolleySingleton.getInstance(rootView.getContext()).addToRequestQueue(request);
    }

    //Calclulamos el total del detalle para la orden
    private String calcularTotal(List<DetalleDeOrden> detalleDeOrdens) {

        DecimalFormat format = new DecimalFormat();
        format.setMaximumFractionDigits(2);

        Double total = 0.0;

        for (DetalleDeOrden detalleActual : detalleDeOrdens) {
            total = total + detalleActual.getCantidad() * detalleActual.getPrecio();
        }

        return format.format(total);
    }

    //calcula el total y devuelve un doble
    private double calcularTotaldouble(List<DetalleDeOrden> detalleDeOrdens) {

        double total = 0;

        for (DetalleDeOrden detalleActual : detalleDeOrdens) {
            total = total + detalleActual.getCantidad() * detalleActual.getPrecio();
        }
        return total;
    }

    private String calcularAporte()
    {
        //para que retorne la cadena como punto y no como una coma;
        //NumberFormat nFormat = NumberFormat.getInstance(Locale.ENGLISH);
        //DecimalFormat format = (DecimalFormat) nFormat;
        DecimalFormat format = new DecimalFormat();
        format.setMaximumFractionDigits(2);

        double aporte = calcularTotaldouble(listadetalle)/cantidad;

        return format.format(aporte);
    }

}
