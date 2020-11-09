package com.jantonioc.xalliapp.FragmentsCarnet;


import android.os.Bundle;

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
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.jantonioc.ln.DetalleDeOrden;
import com.jantonioc.xalliapp.Adaptadores.DetalleOrdenAdapter;
import com.jantonioc.xalliapp.Constans;
import com.jantonioc.xalliapp.FragmentsComanda.AgregarComanda;
import com.jantonioc.xalliapp.FragmentsFinalizar.OrdenFinalizar;
import com.jantonioc.xalliapp.R;
import com.jantonioc.xalliapp.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class DetallesDeOrdenCarnet extends Fragment {

    //Variables del fragment
    private View rootView;
    private RecyclerView lista;
    private DetalleOrdenAdapter adapter;
    private List<DetalleDeOrden> listadetalle;
    private ProgressBar progressBar;
    private LinearLayout linearLayout;


    //boton y texto enviar
    private Button btncerrar;
    private TextView total;

    //variables nesesarias
    private int idorden;


    //enviar la hora de la modificacion
    private Date date = new Date();
    private DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");


    public DetallesDeOrdenCarnet() {
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
        rootView = inflater.inflate(R.layout.fragment_detalles_de_orden, container, false);

        //la lista
        lista = rootView.findViewById(R.id.recyclerViewDetalleOrden);
        lista.setHasFixedSize(true);
        lista.setLayoutManager(new LinearLayoutManager(rootView.getContext()));

        //linear layout
        linearLayout = rootView.findViewById(R.id.linearlayout);
        linearLayout.setVisibility(View.GONE);

        //progressbar
        progressBar = rootView.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        //tex total y enviar modificaciones
        total = rootView.findViewById(R.id.total);
        btncerrar = rootView.findViewById(R.id.btnenviar);
        btncerrar.setText("Guardar Carnet");

        //obtenemos el idoden seleccionado
        idorden = getArguments().getInt("idorden", 0);

        btncerrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new AgregarCarnet();
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.content, fragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        //obtener los detalles de ese id de orden
        ObtenerDetalles(idorden);

        return rootView;
    }

    //obtener las ordenes de un detalle
    private void ObtenerDetalles(final int idOrden) {

        listadetalle = new ArrayList<>();

        String uri = Constans.URLBASE+"DetallesDeOrdenWS/DetalleDeOrdenCuenta/" + idOrden;
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
                        linearLayout.setVisibility(View.VISIBLE);
                        adapter = new DetalleOrdenAdapter(listadetalle);
                        //actualizamos el totl con la lista y apdatamos
                        total.setText("$" + calcularTotal(listadetalle));
                        lista.setAdapter(adapter);

                    }
                    //Si no es mayor regresamos al fragmento anterior y sacamos el fragment actual de la pila
                    else {
                        progressBar.setVisibility(View.GONE);
                        linearLayout.setVisibility(View.VISIBLE);
                        Toast.makeText(rootView.getContext(), "Esta Orden no tiene detalles", Toast.LENGTH_SHORT).show();

                        FragmentManager fm = getActivity().getSupportFragmentManager();
                        for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
                            fm.popBackStack();
                        }

                        Fragment fragment = new OrdenFinalizar();
                        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.content, fragment);
                        transaction.addToBackStack(null);
                        transaction.commit();
                    }

                } catch (JSONException ex) {

                    progressBar.setVisibility(View.GONE);
                    linearLayout.setVisibility(View.VISIBLE);
                    Toast.makeText(rootView.getContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
                    ex.printStackTrace();
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.GONE);
                linearLayout.setVisibility(View.VISIBLE);
                Toast.makeText(rootView.getContext(),Constans.errorVolley(error), Toast.LENGTH_SHORT).show();

            }
        })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return Constans.getToken();
            }
        };


        VolleySingleton.getInstance(rootView.getContext()).addToRequestQueue(request);

    }

    //Calclulamos el total del detalle para la orden
    private String calcularTotal(List<DetalleDeOrden> detalleDeOrdens) {

        DecimalFormat format = new DecimalFormat();
        format.setMaximumFractionDigits(2);

        double total = 0;

        for (DetalleDeOrden detalleActual : detalleDeOrdens) {
            total = total + detalleActual.getCantidad() * detalleActual.getPrecio();
        }
        return format.format(total);
    }

}
