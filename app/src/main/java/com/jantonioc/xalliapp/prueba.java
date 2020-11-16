package com.jantonioc.xalliapp;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.jantonioc.ln.Categoria;
import com.jantonioc.xalliapp.Adaptadores.CategoriaAdapter;
import com.jantonioc.xalliapp.FragmentsOrdenes.Categorias;
import com.jantonioc.xalliapp.FragmentsOrdenes.Clientes;
import com.jantonioc.xalliapp.FragmentsOrdenes.Menus;
import com.jantonioc.xalliapp.FragmentsOrdenes.Ordenes;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.jantonioc.xalliapp.Constans.URLBASE;


/**
 * A simple {@link Fragment} subclass.
 */
public class prueba extends Fragment implements SwipeRefreshLayout.OnRefreshListener, CategoriaAdapter.Evento {

    private View rootView;
    private RecyclerView lista;
    private List<Categoria> listacategorias;
    private CategoriaAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;

    RelativeLayout relativeLayout;
    RelativeLayout noconection;
    FloatingActionButton fab;

    Button btnreintentar;
    TextView txterror;

    public prueba() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment

        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle("Categoria");

        //Mostrnado el fab
        fab = getActivity().findViewById(R.id.fab);
        fab.show();
        //vista
        rootView = inflater.inflate(R.layout.fragment_prueba, container, false);

        relativeLayout = rootView.findViewById(R.id.relativeCategoria);
        noconection = rootView.findViewById(R.id.noconection);
        relativeLayout.setVisibility(View.GONE);

        //de la vista de no conexion
        btnreintentar = rootView.findViewById(R.id.btnrein);
        txterror = rootView.findViewById(R.id.errorTitle);

        //RecyclerView
        lista = rootView.findViewById(R.id.recyclerViewCategoria);
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
                        prueba.this.listaCategoria();
                    }
                });
            }
        });

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

        swipeRefreshLayout.setRefreshing(true);
        fab.hide();
        relativeLayout.setVisibility(View.GONE);
        noconection.setVisibility(View.GONE);

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

                        swipeRefreshLayout.setRefreshing(false);
                        relativeLayout.setVisibility(View.VISIBLE);
                        adapter = new CategoriaAdapter(listacategorias, prueba.this);
                        lista.setAdapter(adapter);
                        fab.show();

                    } else {
                        //abrir la pantalla principal home
                        swipeRefreshLayout.setRefreshing(false);
                        Toast.makeText(rootView.getContext(), "Esta categoria no posee productos", Toast.LENGTH_SHORT).show();

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
                    txterror.setText("Se ha producido una excepcion");
                    Toast.makeText(rootView.getContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();

                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                swipeRefreshLayout.setRefreshing(false);
                noconection.setVisibility(View.VISIBLE);
                txterror.setText(Constans.errorVolley(error));
                Snackbar.make(rootView,Constans.errorVolley(error), Snackbar.LENGTH_SHORT).show();

            }
        })
        {
            //metodo para la autenficacion basica en el servidor
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return Constans.getToken();
            }
        };

        VolleySingleton.getInstance(rootView.getContext()).addToRequestQueue(request);
    }


    @Override
    public void onRefresh() {
        listaCategoria();
    }
}
