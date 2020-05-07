package com.jantonioc.xallyapp.Fragments;


import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
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
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.jantonioc.ln.Categoria;
import com.jantonioc.ln.DetalleDeOrden;
import com.jantonioc.xallyapp.Adaptadores.CategoriaAdapter;
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
public class Categorias extends Fragment implements CategoriaAdapter.Evento {

     View rootView;
     RecyclerView lista;
     List<Categoria>  listacategorias;
     ProgressBar progressBar;

    TextInputLayout txtcodigo;
    TextInputLayout txtfecha;
    TextInputLayout txthora;


    public Categorias() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        Toolbar toolbar=getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle("Categoria");

        //Mostrnado el fab
        FloatingActionButton fab = getActivity().findViewById(R.id.fab);
        fab.show();


        rootView = inflater.inflate(R.layout.fragment_categorias, container, false);

        lista = rootView.findViewById(R.id.recyclerViewCategoria);
        lista.setHasFixedSize(true);
        lista.setLayoutManager(new LinearLayoutManager(rootView.getContext()));

        progressBar= rootView.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        listaCategoria();

        return rootView;
    }

    @Override
    public void selecionar(Categoria obj) {

        Bundle bundle= new Bundle();
        bundle.putInt("IdCategoria",obj.getId());

        Fragment fragment = new Menus();
        fragment.setArguments(bundle);

        FragmentTransaction transaction=getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.content,fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void listaCategoria()
    {
        listacategorias=new ArrayList<>();

        String uri="http://xally.somee.com/Xally/API/CategoriasWS/Categorias";
        StringRequest request= new StringRequest(Request.Method.GET, uri, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {

                    JSONArray jsonArray= new JSONArray(response);

                    for (int i=0;i<jsonArray.length();i++)
                    {
                        JSONObject obj= jsonArray.getJSONObject(i);

                        Categoria categoria = new Categoria(
                                obj.getInt("id"),
                                obj.getString("codigo"),
                                obj.getString("descripcion"),
                                obj.getBoolean("estado")
                        );

                        listacategorias.add(categoria);
                    }

                    if(listacategorias.size()>0) {
                        progressBar.setVisibility(View.GONE);
                        CategoriaAdapter adapter= new CategoriaAdapter(listacategorias, Categorias.this);
                        lista.setAdapter(adapter);
                    }
                    else
                    {
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
                Toast.makeText(rootView.getContext(), error.getMessage(), Toast.LENGTH_LONG ).show();

            }
        });

        VolleySingleton.getInstance(rootView.getContext()).addToRequestQueue(request);
    }








}
