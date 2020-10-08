package com.jantonioc.xalliapp.FragmentsOrdenes;


import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Base64;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.jantonioc.ln.Cliente;
import com.jantonioc.xalliapp.Constans;
import com.jantonioc.xalliapp.MainActivity;
import com.jantonioc.xalliapp.VolleySingleton;
import com.jantonioc.xalliapp.Adaptadores.ClientesAdapter;
import com.jantonioc.xalliapp.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class Clientes extends Fragment {

    //interfaz
    private View rootView;
    private RecyclerView lista;
    private List<Cliente> listaclientes;
    private ProgressBar progressBar;
    private ClientesAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private SearchView searchView;


    public Clientes() {
        // Required empty public constructor
    }

    @Override
    public void onCreateOptionsMenu(android.view.Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.search_menu, menu);

        //Menu item para buscar
        MenuItem searchitem = menu.findItem(R.id.action_search);

        //vista del searchView del buscador
        searchView = (SearchView) searchitem.getActionView();

        //evento del cambio de texto en el buscador
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });

        //Cambiar el texto y colores por defecto por otro
        EditText editText = searchView.findViewById(androidx.appcompat.R.id.search_src_text);
        editText.setHint(getResources().getString(R.string.search_hint));
        editText.setHintTextColor(Color.WHITE);
        editText.setTextColor(Color.WHITE);

        super.onCreateOptionsMenu(menu, inflater);
    }

    //crecion del menu barra superior
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
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

        //listar los clientes
        listaCliente();

        // Inflate the layout for this fragment
        return rootView;
    }


    //obtener la lista de clientes del sistema
    private void listaCliente()
    {
        listaclientes = new ArrayList<>();

        String uri = Constans.URLBASE+"ClientesWS/Clientes";
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
        })
        {
            //metodo para la autenficacion basica en el servidor
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

}
