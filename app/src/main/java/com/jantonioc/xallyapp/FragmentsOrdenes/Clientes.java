package com.jantonioc.xallyapp.FragmentsOrdenes;


import android.os.Bundle;

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
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.jantonioc.ln.Categoria;
import com.jantonioc.ln.Cliente;
import com.jantonioc.ln.Menu;
import com.jantonioc.xallyapp.Adaptadores.CategoriaAdapter;
import com.jantonioc.xallyapp.Adaptadores.ClientesAdapter;
import com.jantonioc.xallyapp.Adaptadores.MenuAdapter;
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
public class Clientes extends Fragment {

    private View rootView;
    private RecyclerView lista;
    private List<Cliente> listaclientes;
    private ProgressBar progressBar;
    private ClientesAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;


    public Clientes() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //toolbar
        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle("Clientes");

        //ocultando el fab
        FloatingActionButton fab = getActivity().findViewById(R.id.fab);
        fab.hide();
        //vista
        rootView = inflater.inflate(R.layout.fragment_clientes, container, false);
        //Recyclerview
        lista = rootView.findViewById(R.id.recyclerViewClientes);
        lista.setHasFixedSize(true);
        lista.setLayoutManager(new LinearLayoutManager(rootView.getContext()));
        //progressbar
        progressBar = rootView.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        //swipe to refresh
        swipeRefreshLayout = rootView.findViewById(R.id.swipe);
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorPrimary));
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                listaCliente();
                adapter.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);

            }
        });

        listaCliente();

        // Inflate the layout for this fragment
        return rootView;
    }


    //obtener la lista de clientes del sistema
    private void listaCliente()
    {
        listaclientes = new ArrayList<>();

        String uri = "http://192.168.1.52/ProyectoXalli_Gentelella/ClientesWS/Clientes";
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
                        Cliente cliente = new Cliente(
                                obj.getInt("id"),
                                obj.getString("identificacion"),
                                obj.getString("nombre"),
                                obj.getString("apellido")
                        );

                        //Agregando a la lista del menu
                        listaclientes.add(cliente);
                    }

                    //Si la lista es mayor que 0 adaptamos y hacemos el evento on click de la lista
                    if (listaclientes.size() > 0) {

                        progressBar.setVisibility(View.GONE);

                        adapter = new ClientesAdapter(listaclientes);

                        adapter.setClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //abrir el fragment de las categorias
                                MainActivity.orden.setIdcliente(listaclientes.get(lista.getChildAdapterPosition(v)).getId());
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
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(rootView.getContext(), "No se encuentran clientes registrados", Toast.LENGTH_SHORT).show();

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
        });


        VolleySingleton.getInstance(rootView.getContext()).addToRequestQueue(request);


    }

}
