package com.jantonioc.xallyapp.FragmentsComanda;


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
import com.jantonioc.ln.Cliente;
import com.jantonioc.xallyapp.Adaptadores.ClientesAdapter;
import com.jantonioc.xallyapp.FragmentsOrdenes.Categorias;
import com.jantonioc.xallyapp.FragmentsOrdenes.DetalleMenu;
import com.jantonioc.xallyapp.FragmentsOrdenes.Ordenes;
import com.jantonioc.xallyapp.MainActivity;
import com.jantonioc.xallyapp.R;
import com.jantonioc.xallyapp.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.jantonioc.xallyapp.Constans.URLBASE;

/**
 * A simple {@link Fragment} subclass.
 */
public class ClientesComanda extends Fragment {

    private View rootView;
    private RecyclerView lista;
    private List<Cliente> listaclientes;
    private ProgressBar progressBar;
    private ClientesAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;



    public ClientesComanda() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //toolbar
        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle("Huespedes");

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

        return rootView;
    }

    //obtener la lista de clientes del sistema
    private void listaCliente()
    {
        listaclientes = new ArrayList<>();

        String uri = URLBASE+"ClientesWS/ClientesConComanda";
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

                                Fragment fragment = new OrdenComanda();
                                Bundle bundle = new Bundle();
                                MainActivity.comanda.setNombrecompleto(listaclientes.get(lista.getChildAdapterPosition(v)).getNombre() + listaclientes.get(lista.getChildAdapterPosition(v)).getApellido());
                                bundle.putInt("idCliente",listaclientes.get(lista.getChildAdapterPosition(v)).getId());
                                fragment.setArguments(bundle);
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
                        Toast.makeText(rootView.getContext(), "No se encuentran clientes con ordenes sin pagar", Toast.LENGTH_SHORT).show();

                        FragmentManager fm = getActivity().getSupportFragmentManager();
                        for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
                            fm.popBackStack();
                        }

                        //aqui tengo que ver donde lo mando aun
                        Fragment fragment = new ClientesComanda();
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
