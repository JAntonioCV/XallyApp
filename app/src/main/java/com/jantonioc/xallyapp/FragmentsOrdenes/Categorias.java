package com.jantonioc.xallyapp.FragmentsOrdenes;


import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.jantonioc.ln.Categoria;
import com.jantonioc.xallyapp.Adaptadores.CategoriaAdapter;
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
public class Categorias extends Fragment implements CategoriaAdapter.Evento {

    private View rootView;
    private RecyclerView lista;
    private List<Categoria> listacategorias;
    private  ProgressBar progressBar;
    private CategoriaAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private SearchView searchView;


    public Categorias() {
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
        // Inflate the layout for this fragment

        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle("Categoria");

        //Mostrnado el fab
        FloatingActionButton fab = getActivity().findViewById(R.id.fab);
        fab.show();
        //vista
        rootView = inflater.inflate(R.layout.fragment_categorias, container, false);
        //RecyclerView
        lista = rootView.findViewById(R.id.recyclerViewCategoria);
        lista.setHasFixedSize(true);
        lista.setLayoutManager(new LinearLayoutManager(rootView.getContext()));

        //swipe to refresh
        swipeRefreshLayout = rootView.findViewById(R.id.swipe);
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorPrimary));
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                listaCategoria();
                adapter.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);

            }
        });

        progressBar = rootView.findViewById(R.id.progressBar);

        //Haciendo visible el progresbarr
        progressBar.setVisibility(View.VISIBLE);

        //Obteniendo la lista de las categorias
        listaCategoria();

        return rootView;
    }

    //Evento selecionar una categoria
    @Override
    public void selecionar(Categoria obj) {

        //Guardando la id de la categoria
        Bundle bundle = new Bundle();
        bundle.putInt("IdCategoria", obj.getId());

        //Creando la intancia del nuevo fragment
        Fragment fragment = new Menus();
        fragment.setArguments(bundle);

        //Abriendo el nuevo fragment y enviando la categoria en el fragment
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.content, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void listaCategoria() {
        //Instancia de la lista
        listacategorias = new ArrayList<>();

        String uri = URLBASE+"CategoriasWS/Categorias";
        StringRequest request = new StringRequest(Request.Method.GET, uri, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {

                    //obteniendo la respuesta del servidor en un arreglo json
                    JSONArray jsonArray = new JSONArray(response);

                    //Recorriendo el arreglo y obteninendo los objetos de cada posicion
                    for (int i = 0; i < jsonArray.length(); i++) {
                        //obteniendo el objeto actual
                        JSONObject obj = jsonArray.getJSONObject(i);

                        //Agregando la categoria al modelo local
                        Categoria categoria = new Categoria(
                                obj.getInt("id"),
                                obj.getString("codigo"),
                                obj.getString("descripcion"),
                                obj.getBoolean("estado")
                        );

                        //despues agregamos a la lista
                        listacategorias.add(categoria);
                    }

                    //if la lista es mayor que 0 adapta la lista de los contario muestra un mensaje
                    if (listacategorias.size() > 0) {
                        progressBar.setVisibility(View.GONE);
                        adapter = new CategoriaAdapter(listacategorias, Categorias.this);
                        lista.setAdapter(adapter);
                    } else {

                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(rootView.getContext(), "No existen Categorias para mostrar", Toast.LENGTH_SHORT).show();
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
