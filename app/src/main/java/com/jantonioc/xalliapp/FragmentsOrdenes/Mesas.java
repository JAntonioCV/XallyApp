package com.jantonioc.xalliapp.FragmentsOrdenes;


import android.os.Bundle;

import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.jantonioc.ln.Cliente;
import com.jantonioc.ln.Menu;
import com.jantonioc.ln.Mesa;
import com.jantonioc.xalliapp.Adaptadores.ClientesAdapter;
import com.jantonioc.xalliapp.Adaptadores.MenuAdapter;
import com.jantonioc.xalliapp.Adaptadores.MesaAdapter;
import com.jantonioc.xalliapp.Constans;
import com.jantonioc.xalliapp.MainActivity;
import com.jantonioc.xalliapp.R;
import com.jantonioc.xalliapp.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class Mesas extends Fragment implements SwipeRefreshLayout.OnRefreshListener  {

    //interfaz
    private View rootView;
    private RecyclerView lista;
    private List<Mesa> listamesas;
    private MesaAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;

    private RelativeLayout relativeLayout;
    private RelativeLayout noconection;
    private FloatingActionButton fab;

    private Button btnreintentar;


    public Mesas() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        //toolbar
        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle("Mesas");

        //ocultando el fab
        fab = getActivity().findViewById(R.id.fab);
        fab.hide();
        //vista
        rootView = inflater.inflate(R.layout.fragment_mesas, container, false);

        relativeLayout = rootView.findViewById(R.id.relativeMesas);
        noconection = rootView.findViewById(R.id.noconection);
        relativeLayout.setVisibility(View.GONE);

        //de la vista de no conexion
        btnreintentar = rootView.findViewById(R.id.btnrein);

        //Recyclerview
        lista = rootView.findViewById(R.id.recyclerViewMesas);
        lista.setHasFixedSize(true);
        lista.setLayoutManager(new LinearLayoutManager(rootView.getContext()));

        //swipe to refresh
        swipeRefreshLayout = rootView.findViewById(R.id.swipe);
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorPrimary));
        swipeRefreshLayout.setOnRefreshListener(this);

        btnreintentar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                swipeRefreshLayout.post(new Runnable() {
                    @Override
                    public void run() {

                        Mesas.this.listaMesas();
                    }
                });
            }
        });

        //listar los clientes
        listaMesas();

        // Inflate the layout for this fragment
        return rootView;

    }

    //obteniendo la lista del menu desde el servidor
    private void listaMesas() {

        swipeRefreshLayout.setRefreshing(true);
        fab.hide();
        relativeLayout.setVisibility(View.GONE);
        noconection.setVisibility(View.GONE);

        listamesas = new ArrayList<>();

        String uri = Constans.URLBASE+"MesasWS/Mesas";
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

                        //Obteniendo los datos y convirtiendolos a menu
                        Mesa mesa = new Mesa(
                                obj.getInt("id"),
                                obj.getString("codigo"),
                                obj.getString("descripcion"),
                                obj.getBoolean("estado")
                        );

                        //Agregando a la lista del menu
                        listamesas.add(mesa);
                    }

                    //Si la lista es mayor que 0 adaptamos y hacemos el evento on click y long Click del la lista
                    if (listamesas.size() > 0) {

                        swipeRefreshLayout.setRefreshing(false);
                        relativeLayout.setVisibility(View.VISIBLE);
                        //adaptar la lista
                        adapter = new MesaAdapter(listamesas);

                        //evento click para la existencia
                        adapter.setClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //agregar la mesa a la orden y abrir las categorias
                                MainActivity.orden.setIdmesa(listamesas.get(lista.getChildAdapterPosition(v)).getId());
                                MainActivity.orden.setMesa(listamesas.get(lista.getChildAdapterPosition(v)).getDescripcion());
                                Fragment fragment = new Categorias();
                                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                                transaction.replace(R.id.content, fragment);
                                transaction.addToBackStack(null);
                                transaction.commit();

                            }
                        });

                        lista.setAdapter(adapter);

                    }
                    //Si no es mayor regresamos al fragmento anterior y sacamos el fragment actual de la pila
                    else {

                        swipeRefreshLayout.setRefreshing(false);
                        Toast.makeText(rootView.getContext(), "No se encuentran mesas disponibles", Toast.LENGTH_SHORT).show();

                        FragmentManager fm = getActivity().getSupportFragmentManager();
                        for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
                            fm.popBackStack();
                        }

                        Fragment fragment = new Ordenes();
                        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.content, fragment);
                        transaction.addToBackStack(null);
                        transaction.commit();
                    }

                } catch (JSONException ex) {

                    //excepcion json
                    swipeRefreshLayout.setRefreshing(false);
                    noconection.setVisibility(View.VISIBLE);
                    Toast.makeText(rootView.getContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                swipeRefreshLayout.setRefreshing(false);
                noconection.setVisibility(View.VISIBLE);
                Toast.makeText(rootView.getContext(), Constans.errorVolley(error), Toast.LENGTH_SHORT).show();
            }
        }){
            //metodo para la autenficacion basica en el servidor
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return MainActivity.getToken();
            }
        };


        VolleySingleton.getInstance(rootView.getContext()).addToRequestQueue(request);
    }


    @Override
    public void onRefresh() {
        listaMesas();
    }
}
