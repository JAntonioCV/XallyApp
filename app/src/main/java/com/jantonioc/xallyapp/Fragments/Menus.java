package com.jantonioc.xallyapp.Fragments;


import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.jantonioc.ln.Categoria;
import com.jantonioc.ln.DetalleDeOrden;
import com.jantonioc.ln.Menu;
import com.jantonioc.ln.Receta;
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
public class Menus extends Fragment {

    View rootView;
    RecyclerView lista;
    List<Menu> listamenu;
    SearchView searchView;
    MenuAdapter adapter;
    ProgressBar progressBar;
    int idcategoria;

    TextInputLayout txtcantidad;
    TextInputLayout txtnota;



    public Menus() {
        // Required empty public constructor
    }


    @Override
    public void onCreateOptionsMenu(android.view.Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.toolbar_menu,menu);

        MenuItem searchitem=menu.findItem(R.id.action_search);
        SearchView searchView= (SearchView) searchitem.getActionView();

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

      EditText editText=searchView.findViewById(androidx.appcompat.R.id.search_src_text);
        editText.setHint(getResources().getString(R.string.search_hint));
        editText.setHintTextColor(Color.WHITE);
        editText.setTextColor(Color.WHITE);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Toolbar toolbar=getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle("Menu");

        FloatingActionButton fab = getActivity().findViewById(R.id.fab);
        fab.show();

        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_menu, container, false);
        lista = rootView.findViewById(R.id.recyclerViewMenu);
        lista.setHasFixedSize(true);
        lista.setLayoutManager(new LinearLayoutManager(rootView.getContext()));
        progressBar = rootView.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        idcategoria= getArguments().getInt("IdCategoria", 0);

        listaMenu(idcategoria);

        return rootView;
    }

    public void listaMenu(final int idcategoria)
    {
        listamenu=new ArrayList<>();

        String uri="http://xally.somee.com/Xally/API/MenusWS/MenusCategoria/"+idcategoria;
        StringRequest request= new StringRequest(Request.Method.GET, uri, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {

                    JSONArray jsonArray= new JSONArray(response);

                    for (int i=0;i<jsonArray.length();i++)
                    {
                        JSONObject obj= jsonArray.getJSONObject(i);

                        Menu menu = new Menu(
                                obj.getInt("id"),
                                obj.getString("codigo"),
                                obj.getString("descripcion"),
                                obj.getDouble("precio"),
                                obj.getBoolean("estado")
                        );

                        listamenu.add(menu);
                    }

                    if(listamenu.size()>0) {

                        progressBar.setVisibility(View.GONE);

                        adapter = new MenuAdapter(listamenu);

                        adapter.setClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                detalleOrden(listamenu.get(lista.getChildAdapterPosition(v)));
                            }
                        });


                        adapter.setLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View v) {
                               detalleMenu(listamenu.get(lista.getChildAdapterPosition(v)));
                                return true;
                            }
                        });

                        lista.setAdapter(adapter);
                    }
                    else
                    {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(rootView.getContext(), "Esta categoria no posee productos", Toast.LENGTH_SHORT).show();

                        FragmentManager fm = getActivity().getSupportFragmentManager();
                        for(int i = 0; i < fm.getBackStackEntryCount(); ++i) {
                            fm.popBackStack();
                        }

                        Fragment fragment = new Categorias();
                        FragmentTransaction transaction=getActivity().getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.content,fragment);
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
                Toast.makeText(rootView.getContext(), error.getMessage(), Toast.LENGTH_LONG ).show();

            }
        });


        VolleySingleton.getInstance(rootView.getContext()).addToRequestQueue(request);
    }


    //Detalle de orden
    private void detalleOrden(final Menu obj) {

        if(!esNuevaOrden(obj))
        {
            modificarOrden(obj);
        }
        else
        {
            final AlertDialog builder = new AlertDialog.Builder(rootView.getContext()).create();

            View view = getLayoutInflater().inflate(R.layout.detalle_orden,null);
            txtcantidad = view.findViewById(R.id.cantidad);
            txtnota = view.findViewById(R.id.notaopcional);

            Button ordenar = view.findViewById(R.id.btnordenar);

            ordenar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(!validarCampos())
                    {
                        return;
                    }
                    else
                    {
                        //Agregar Orden
                        DetalleDeOrden detalleDeOrden = new DetalleDeOrden();

                        detalleDeOrden.setCantidad(Integer.valueOf(txtcantidad.getEditText().getText().toString()));
                        detalleDeOrden.setNota(txtnota.getEditText().getText().toString());
                        detalleDeOrden.setMenuid(obj.getId());
                        detalleDeOrden.setOrdenid(1);

                        MainActivity.listadetalle.add(detalleDeOrden);

                        builder.cancel();
                    }

                }
            });

            builder.setView(view);
            builder.create();
            builder.show();

        }
    }



    //detalle del platillo
    private void detalleMenu(final Menu menu)
    {
        Fragment fragment = new DetalleMenu();
        Bundle bundle = new Bundle();
        bundle.putSerializable("Menu",menu);
        fragment.setArguments(bundle);
        FragmentTransaction transaction=getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.content,fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }


    //Validando que no esten los campos vacios
    private boolean validarCampos()
    {
        boolean isValidate=true;

        String cantidadInput = txtcantidad.getEditText().getText().toString().trim();

        if(cantidadInput.isEmpty())
        {
            isValidate=false;
            txtcantidad.setError("Cantidad no puede estar vacio");

        }else if(Integer.valueOf(cantidadInput)<=0)
        {
            isValidate=false;
            txtcantidad.setError("La cantidad no puede ser menor a 1");

        }else
        {
            txtcantidad.setError(null);
        }


        return isValidate;
    }

    private boolean esNuevaOrden(final Menu obj) {

        for (final DetalleDeOrden detalleActual : MainActivity.listadetalle) {

            if (obj.getId() == detalleActual.getMenuid()) {
                return false;
            }
        }
        return true;
    }


    //Validando si se modifica la orden o se agrega una nueva || aqui deberia mostrar lo que ya tengo que podria ser modificado
    private void modificarOrden(final Menu obj)
    {
        for(final DetalleDeOrden detalleActual : MainActivity.listadetalle )
        {
            if(obj.getId() == detalleActual.getMenuid())
            {
                final AlertDialog builder = new AlertDialog.Builder(rootView.getContext()).create();

                View view = getLayoutInflater().inflate(R.layout.detalle_orden,null);
                txtcantidad = view.findViewById(R.id.cantidad);
                txtnota = view.findViewById(R.id.notaopcional);

                txtcantidad.getEditText().setText(String.valueOf(detalleActual.getCantidad()));
                txtnota.getEditText().setText(detalleActual.getNota());


                Button ordenar = view.findViewById(R.id.btnordenar);
                ordenar.setText("MODIFICAR");

                ordenar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        detalleActual.setCantidad(Integer.valueOf(txtcantidad.getEditText().getText().toString()));
                        detalleActual.setNota(txtnota.getEditText().getText().toString());
                        builder.cancel();
                    }
                });

                builder.setView(view);
                builder.create();
                builder.show();
            }
        }
    }

}
