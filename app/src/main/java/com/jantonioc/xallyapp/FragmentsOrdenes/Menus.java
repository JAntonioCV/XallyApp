package com.jantonioc.xallyapp.FragmentsOrdenes;


import android.content.DialogInterface;
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
import android.widget.TextView;
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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.jantonioc.ln.DetalleDeOrden;
import com.jantonioc.ln.Menu;
import com.jantonioc.xallyapp.Adaptadores.MenuAdapter;
import com.jantonioc.xallyapp.MainActivity;
import com.jantonioc.xallyapp.R;
import com.jantonioc.xallyapp.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.jantonioc.xallyapp.Constans.URLBASE;

/**
 * A simple {@link Fragment} subclass.
 */
public class Menus extends Fragment {

    //interfaz
    private View rootView;
    private RecyclerView lista;
    private List<Menu> listamenu;
    private SearchView searchView;
    private MenuAdapter adapter;
    private ProgressBar progressBar;
    private int idcategoria;
    private int cantidad;

    //dialog
    private TextInputLayout txtcantidad;
    private TextInputLayout txtnota;

    private TextInputEditText cantidadtxt;
    private TextInputEditText notatxt;

    private Button ordenar;

    private TextView txtplatillo;
    private TextView txtexistencia;

    //swipe to refresh
    private SwipeRefreshLayout swipeRefreshLayout;


    public Menus() {
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
        toolbar.setTitle("Menu");

        //boton fab
        FloatingActionButton fab = getActivity().findViewById(R.id.fab);
        fab.show();

        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_menu, container, false);
        lista = rootView.findViewById(R.id.recyclerViewMenu);
        lista.setHasFixedSize(true);
        lista.setLayoutManager(new LinearLayoutManager(rootView.getContext()));
        progressBar = rootView.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        //Obteniendo la id de la categoria selecionada
        idcategoria = getArguments().getInt("IdCategoria", 0);

        //swipe to refresh
        swipeRefreshLayout = rootView.findViewById(R.id.swipe);
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorPrimary));
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                listaMenu(idcategoria);
                adapter.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);

            }
        });

        //Listando el menu por id de la cstegoria
        listaMenu(idcategoria);

        return rootView;
    }

    //obteniendo la lista del menu desde el servidor
    private void listaMenu(final int idcategoria) {
        listamenu = new ArrayList<>();

        String uri = URLBASE+"MenusWS/MenusCategoria/" + idcategoria;
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
                        Menu menu = new Menu(
                                obj.getInt("id"),
                                obj.getString("codigo"),
                                obj.getString("descripcion"),
                                obj.getString("tiempoestimado"),
                                obj.getDouble("precio"),
                                obj.getBoolean("estado"),
                                obj.getString("ruta"),
                                obj.getInt("idcategoria")
                        );

                        //Agregando a la lista del menu
                        listamenu.add(menu);
                    }

                    //Si la lista es mayor que 0 adaptamos y hacemos el evento on click y long Click del la lista
                    if (listamenu.size() > 0) {
                        //ocultar el progressbar
                        progressBar.setVisibility(View.GONE);

                        //adaptar la lista
                        adapter = new MenuAdapter(listamenu);

                        //evento click para la existencia
                        adapter.setClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                Obtenerexitencia(listamenu.get(lista.getChildAdapterPosition(v)));
                            }
                        });

                        //longclick para el detalle del menu
                        adapter.setLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View v) {
                                detalleMenu(listamenu.get(lista.getChildAdapterPosition(v)));
                                return true;
                            }
                        });

                        lista.setAdapter(adapter);

                    }
                    //Si no es mayor regresamos al fragmento anterior y sacamos el fragment actual de la pila
                    else {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(rootView.getContext(), "Esta categoria no posee productos", Toast.LENGTH_SHORT).show();

                        FragmentManager fm = getActivity().getSupportFragmentManager();
                        for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
                            fm.popBackStack();
                        }

                        Fragment fragment = new Categorias();
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


    //Detalle de orden pasamos el objeto

    private void detalleOrden(final Menu obj, final int cantidad) {

        //Consultamos si es una nueva o una existente
        if (!esNuevaOrden(obj)) {
            //Abrimos la modal para modificar la orden para evitar que se agregen 2 productos del mismo en la lista
            modificarOrden(obj, cantidad);
        } else {
            //Abrimos la modal agregar el nuevo detalle de orden
            final AlertDialog builder = new AlertDialog.Builder(rootView.getContext()).create();

            View view = getLayoutInflater().inflate(R.layout.detalle_orden, null);

            txtplatillo = view.findViewById(R.id.nombreplatillo);
            txtexistencia = view.findViewById(R.id.existencia);
            txtcantidad = view.findViewById(R.id.cantidad);
            txtnota = view.findViewById(R.id.notaopcional);
            cantidadtxt = view.findViewById(R.id.cantidadtxt);
            notatxt = view.findViewById(R.id.notatxt);
            ordenar = view.findViewById(R.id.btnordenar);

            //restar cantidad
            txtcantidad.setStartIconOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(!txtcantidad.getEditText().getText().toString().isEmpty())
                    {
                        int numero = Integer.valueOf(txtcantidad.getEditText().getText().toString());

                        if(numero<=1)
                        {
                            txtcantidad.setError("La cantidad no puede ser menor a 1");
                            return;
                        }else
                        {
                            numero--;
                            txtcantidad.getEditText().setText(String.valueOf(numero));
                            txtcantidad.setError(null);
                        }
                    }
                    else
                    {
                        txtcantidad.setError("Ingrese una cantidad");
                    }
                }
            });

            //sumar cantidad
            txtcantidad.setEndIconOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(!txtcantidad.getEditText().getText().toString().isEmpty())
                    {
                        int numero = Integer.valueOf(txtcantidad.getEditText().getText().toString());

                        if(cantidad == -2)
                        {
                            numero++;
                            txtcantidad.getEditText().setText(String.valueOf(numero));
                            txtcantidad.setError(null);
                        }
                        else if(numero<cantidad)
                        {
                            numero++;
                            txtcantidad.getEditText().setText(String.valueOf(numero));
                            txtcantidad.setError(null);
                        }
                        else
                        {
                            txtcantidad.setError("La cantidad no puede ser mayor a la exitencia");
                        }
                    }
                    else
                    {
                        txtcantidad.setError("Ingrese una cantidad");
                    }
                }
            });

            //enviar datos por default si no se cumplen los de abajo
            txtplatillo.setText(obj.getDescripcion());
            txtcantidad.getEditText().setText("1");

            //si no hay en exitencia no puede agregar
            if(cantidad == 0)
            {
                txtexistencia.setText("Existencia: 0");
                txtcantidad.setEnabled(false);
                txtnota.setEnabled(false);
                txtcantidad.getEndIconDrawable().setAlpha(130);
                txtcantidad.getStartIconDrawable().setAlpha(130);
                ordenar.setEnabled(false);
                txtcantidad.getEditText().setText("");
            }
            //no es inventariado 0 la existencia
            else if (cantidad == -2) {
                txtexistencia.setText("Existencia: No inventariado");
            } else {
                txtexistencia.setText("Existencia: " + String.valueOf(cantidad));
            }

            ordenar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //Validamos que los campos no esten vacios o incumplan
                    if (!validarCampos(cantidad)) {
                        return;
                    } else {
                        //Agregar Ordenes
                        DetalleDeOrden detalleDeOrden = new DetalleDeOrden();
                        detalleDeOrden.setCantidad(Integer.valueOf(txtcantidad.getEditText().getText().toString()));
                        detalleDeOrden.setNota(txtnota.getEditText().getText().toString());
                        detalleDeOrden.setNombreplatillo(obj.getDescripcion());
                        detalleDeOrden.setPrecio(obj.getPrecio());
                        detalleDeOrden.setMenuid(obj.getId());
                        detalleDeOrden.setEstado(false);
                        detalleDeOrden.setFromservice(false);

                        //agregamos el detalle y mostramos un toast
                        MainActivity.listadetalle.add(detalleDeOrden);
                        Toast.makeText(rootView.getContext(), "Agregado al detalle de orden", Toast.LENGTH_SHORT).show();

                        //Para que se cierre automaticamente al darla guardar
                        builder.cancel();
                    }

                }
            });

            builder.setView(view);
            builder.create();
            builder.show();

        }
    }

    //Obtener la existencia de un producto de el bar
    private void Obtenerexitencia(final Menu menu) {
        String uri = URLBASE+"InventarioWS/Existencia/" + menu.getId();
        StringRequest request = new StringRequest(Request.Method.GET, uri, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                cantidad = Integer.valueOf(response);
                detalleOrden(menu, cantidad);

            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(rootView.getContext(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        VolleySingleton.getInstance(rootView.getContext()).addToRequestQueue(request);
    }


    //detalle del platillo
    private void detalleMenu(final Menu menu) {
        //Abrir el fragmento del detalle de los platillos
        Fragment fragment = new DetalleMenu();
        //Pasar parametros entre fragment
        Bundle bundle = new Bundle();
        //mandar el objeto serializado
        bundle.putSerializable("Menu", menu);
        fragment.setArguments(bundle);
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.content, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }


    //Validando que no esten los campos vacios
    private boolean validarCampos(int cantidad) {
        boolean isValidate = true;

        String cantidadInput = txtcantidad.getEditText().getText().toString().trim();

        if (cantidadInput.isEmpty()) {
            isValidate = false;
            txtcantidad.setError("Cantidad no puede estar vacio");

        } else if (Integer.valueOf(cantidadInput) <= 0) {
            isValidate = false;
            txtcantidad.setError("La cantidad no puede ser menor a 1");

        } else if (Integer.valueOf(cantidadInput) > cantidad && cantidad != -2) {
            isValidate = false;
            txtcantidad.setError("La cantidad no puede ser mayor a la existencia");
        } else {
            txtcantidad.setError(null);
        }

        return isValidate;
    }

    //Validar el tipo de orden modificar || nueva
    private boolean esNuevaOrden(final Menu obj) {

        for (final DetalleDeOrden detalleActual : MainActivity.listadetalle) {

            if (obj.getId() == detalleActual.getMenuid()) {
                return false;
            }
        }
        return true;
    }


    //Validando si se modifica la orden o se agrega una nueva || aqui deberia mostrar lo que ya tengo que podria ser modificado
    private void modificarOrden(final Menu obj, final int cantidad)
    {
        //for sobre la lista temporal para encontrar el seleccionado con un iterator ya que se puede eliminar en tiempo real
        for (final Iterator<DetalleDeOrden> iterator = MainActivity.listadetalle.iterator(); iterator.hasNext();) {

            final DetalleDeOrden detalleActual = iterator.next();

            //si coninicden los id del menu
            if (obj.getId()==detalleActual.getMenuid()) {

                //si la exitencia es cero se debe borrar
                if(cantidad==0)
                {
                    //Mostrar un dialog
                    final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                    //si elimina
                    builder.setTitle("Eliminar Detalle");
                    builder.setMessage("¿La existencia ha cambiado a 0 desea eliminar el detalle?");

                    builder.setPositiveButton("Eliminar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //Si confirma se borra de la lista del detalle y se calcula el total
                            Toast.makeText(rootView.getContext(), "Eliminado de la Orden", Toast.LENGTH_SHORT).show();
                            iterator.remove();

                        }
                    });

                    //si cancela
                    builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //si lo cancela se cierra y vuelve el detalle eliminado por el swipe
                            dialog.cancel();
                            return;
                        }
                    });

                    builder.create();
                    builder.show();
                    break;
                }
                else if(cantidad < detalleActual.getCantidad() && cantidad != -2)
                {
                    //Mostrar un dialog
                    final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                    //si actualiza
                    builder.setTitle("Actualizar Detalle");
                    builder.setMessage("La existencia ah cambiado,¿Desea actualizar la cantidad del detalle?");

                    builder.setPositiveButton("Actualizar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //Si confirma
                            detalleActual.setCantidad(cantidad);
                            Toast.makeText(rootView.getContext(), "La cantidad fue cambiada para: " + detalleActual.getNombreplatillo(), Toast.LENGTH_LONG).show();
                        }
                    });

                    //si cancela
                    builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

                    builder.create();
                    builder.show();
                    break;
                }
                else
                {
                    //por ultimo si puede sumar  o restar  o modificar
                    final AlertDialog builder = new AlertDialog.Builder(rootView.getContext()).create();

                    //abrimos el detalle de la orden
                    View view = getLayoutInflater().inflate(R.layout.detalle_orden, null);
                    txtcantidad = view.findViewById(R.id.cantidad);

                    //restar de cantidad
                    txtcantidad.setStartIconOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            if(!txtcantidad.getEditText().getText().toString().isEmpty())
                            {
                                int numero = Integer.valueOf(txtcantidad.getEditText().getText().toString());

                                if(numero<=1)
                                {
                                    txtcantidad.setError("La cantidad no puede ser menor a 1");
                                    return;
                                }else
                                {
                                    numero--;
                                    txtcantidad.getEditText().setText(String.valueOf(numero));
                                    txtcantidad.setError(null);
                                }
                            }
                            else
                            {
                                txtcantidad.setError("Ingrese una cantidad");
                            }

                        }
                    });

                    //sumar de cantidad
                    txtcantidad.setEndIconOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            if(!txtcantidad.getEditText().getText().toString().isEmpty())
                            {
                                int numero = Integer.valueOf(txtcantidad.getEditText().getText().toString());
                                if(cantidad==-2)
                                {
                                    numero++;
                                    txtcantidad.getEditText().setText(String.valueOf(numero));
                                    txtcantidad.setError(null);
                                }
                                else if(numero < cantidad)
                                {
                                    numero++;
                                    txtcantidad.getEditText().setText(String.valueOf(numero));
                                    txtcantidad.setError(null);
                                }
                                else
                                {
                                    txtcantidad.setError("La cantidad no puede ser mayor a la exitencia");
                                }
                            }
                            else
                            {
                                txtcantidad.setError("Ingrese una cantidad");
                            }
                        }
                    });

                    txtnota = view.findViewById(R.id.notaopcional);
                    txtplatillo = view.findViewById(R.id.nombreplatillo);
                    txtexistencia = view.findViewById(R.id.existencia);
                    ordenar = view.findViewById(R.id.btnordenar);

                    txtplatillo.setText(obj.getDescripcion());
                    txtcantidad.getEditText().setText(String.valueOf(detalleActual.getCantidad()));
                    txtnota.getEditText().setText(detalleActual.getNota());
                    ordenar.setText("MODIFICAR");

                    //existencia si no es inventariado o la exitencia
                    if (cantidad == -2) {
                        txtexistencia.setText("Existencia: No inventariado");
                    } else {
                        txtexistencia.setText("Existencia: " + String.valueOf(cantidad));
                    }

                    //evento click del ordenar
                    ordenar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            //Validamos que los campos no esten vacios o incumplan
                            if (!validarCampos(cantidad)) {
                                return;
                            } else {

                                //si no han exitido cambios toas de lo contario cambia
                                if(detalleActual.getCantidad() == Integer.valueOf(txtcantidad.getEditText().getText().toString()) && detalleActual.getNota().equals(txtnota.getEditText().getText().toString()))
                                {
                                    Toast.makeText(rootView.getContext(), "No existen cambios para guardar", Toast.LENGTH_SHORT).show();
                                }
                                else
                                {
                                    //modifiamos el detalle para que se guarden los nuevos datos
                                    detalleActual.setCantidad(Integer.valueOf(txtcantidad.getEditText().getText().toString()));
                                    detalleActual.setNota(txtnota.getEditText().getText().toString());
                                    Toast.makeText(rootView.getContext(), "Detalle de orden modificado", Toast.LENGTH_SHORT).show();
                                    builder.cancel();
                                }

                            }
                        }
                    });

                    builder.setView(view);
                    builder.create();
                    builder.show();
                    break;
                }

            }

        }
    }

}
