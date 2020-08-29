package com.jantonioc.xallyapp.FragmentsPedidos;


import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.jantonioc.ln.DetalleDeOrden;
import com.jantonioc.ln.Orden;
import com.jantonioc.xallyapp.Adaptadores.DetalleOrdenAdapter;
import com.jantonioc.xallyapp.Adaptadores.MenuAdapter;
import com.jantonioc.xallyapp.FragmentsOrdenes.Categorias;
import com.jantonioc.xallyapp.FragmentsOrdenes.Ordenes;
import com.jantonioc.xallyapp.MainActivity;
import com.jantonioc.xallyapp.R;
import com.jantonioc.xallyapp.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import static com.jantonioc.xallyapp.Constans.URLBASE;

/**
 * A simple {@link Fragment} subclass.
 */
public class DetallesDeOrden extends Fragment {

    private View rootView;
    private RecyclerView lista;
    private DetalleOrdenAdapter adapter;

    private List<DetalleDeOrden> listadetalle;

    private ProgressBar progressBar;

    private Button btnenviar;
    private TextView total;

    private int idorden;



    public DetallesDeOrden() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull final Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.add_new_detail_order, menu);

        //Menu item para agregar un detalle
        MenuItem add_detail = menu.findItem(R.id.add_detail);

        add_detail.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                //auxiliares para agregar nuevos detalles a una orden
                MainActivity.modpedidos = true;
                MainActivity.orden.setId(idorden);
                //Al agregar detalle abrir el fragmento categoria
                Fragment fragment = new Categorias();
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.content, fragment);
                transaction.addToBackStack(null);
                transaction.commit();

                return false;
            }
        });

        super.onCreateOptionsMenu(menu, inflater);
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

        //progressbar
        progressBar = rootView.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        //tex total y enviar modificaciones
        total = rootView.findViewById(R.id.total);
        btnenviar = rootView.findViewById(R.id.btnenviar);

        btnenviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //si hay cambios enviar si no mostrar toast
                if(haycambios())
                {
                    enviarModificacion(listadetalle);
                }else
                {
                    Toast.makeText(rootView.getContext(),"No hay cambios para enviar",Toast.LENGTH_SHORT).show();
                }

            }
        });

        //obtenemos el idoden seleccionado
        idorden = getArguments().getInt("idorden", 0);

        //helper para el reciclerview
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(lista);

        ObtenerDetalles(idorden);

        return rootView;
    }

    //swipe to delete

    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull final RecyclerView.ViewHolder viewHolder, int direction) {

            //si es del servicio ya esta en la db y no se debe borrar
            if(listadetalle.get(viewHolder.getAdapterPosition()).getFromservice()==true)
            {
                Toast.makeText(rootView.getContext(), "No se puede eliminar", Toast.LENGTH_SHORT).show();
                adapter.notifyItemChanged(viewHolder.getAdapterPosition());
            }
            else
            {
                //Mostrar un dialog
                final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                builder.setTitle("Eliminar Detalle");
                builder.setMessage("¿Desea eliminar el detalle para esta orden?");

                builder.setPositiveButton("Eliminar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Si confirma se borra de la lista del detalle y se calcula el total
                        Toast.makeText(rootView.getContext(), "Eliminado de la Orden", Toast.LENGTH_SHORT).show();
                        int position = viewHolder.getAdapterPosition();
                        listadetalle.remove(position);
                        total.setText("$"+ calcularTotal(listadetalle));
                        adapter.notifyDataSetChanged();
                    }
                });

                builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //si lo cancela se cierra y vuelve el detalle eliminado por el swipe
                        adapter.notifyItemChanged(viewHolder.getAdapterPosition());
                    }
                });

                builder.create();
                builder.show();
            }
        }
    };

    private void enviarModificacion(List<DetalleDeOrden> detallenuevo)
    {
        //nuevo array para los nuevos detalles
        JSONArray detallesordenesArray = new JSONArray();

        //sacando solo los nuevos detalles y agregandolas al nuevo arreglo
        for(DetalleDeOrden detalleActual :detallenuevo )
        {
            if(detalleActual.getFromservice() == false)
            {
                try {
                    //objeto donde se guardara el detalle
                    JSONObject detallesordenes = new JSONObject();

                    //Guardando datos del detalle de orden
                    detallesordenes.put("cantidadorden", detalleActual.getCantidad());
                    detallesordenes.put("notaorden", detalleActual.getNota().isEmpty() ? "Sin nota" : detalleActual.getNota());
                    detallesordenes.put("nombreplatillo", detalleActual.getNombreplatillo());
                    detallesordenes.put("preciounitario", detalleActual.getPrecio());
                    detallesordenes.put("estado", detalleActual.getEstado());
                    detallesordenes.put("menuid", detalleActual.getMenuid());
                    detallesordenes.put("ordenid",idorden);

                    //ingresamos el objeto al array
                    detallesordenesArray.put(detallesordenes);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }

        //Creando un nuevo objeto para guardar el array
        JSONObject nuevosDetallesObject = new JSONObject();

        try {
            //les asginamos un nombre para que los pueda reconocer la api
            nuevosDetallesObject.put("nuevoDetallesWS", detallesordenesArray);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        //Enviar la orden al server
        String uri = URLBASE+"DetallesDeOrdenWS/NuevosDetalle";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, uri, nuevosDetallesObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {

                    //respuesta de parte del servidor
                    String mensaje = response.getString("Mensaje");
                    Boolean resultado = response.getBoolean("Resultado");

                    if (resultado) {
                        //segun yo abre el fragmento de las Pedidos
                        Toast.makeText(rootView.getContext(), mensaje, Toast.LENGTH_SHORT).show();

                        //Cambiar auxiliares si salio bien
                        MainActivity.orden = new Orden();
                        MainActivity.listadetalle.clear();
                        MainActivity.modpedidos=false;

                        //abrir fragmento pedidos
                        Fragment fragment = new Pedidos();
                        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.content, fragment);
                        transaction.commit();

                    } else {
                        Toast.makeText(rootView.getContext(), mensaje, Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException ex) {
                    Toast.makeText(rootView.getContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(rootView.getContext(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        VolleySingleton.getInstance(rootView.getContext()).addToRequestQueue(request);
    }

    //si hay cambios en la lista por medio del campo que solo tienen true los consultados
    private boolean haycambios()
    {
        for(DetalleDeOrden detalleactual :listadetalle)
        {
            if(detalleactual.getFromservice()==false)
            {
                return true;
            }
        }

        return false;
    }

    //obtener las ordenes de un detalle
    private void ObtenerDetalles(final int idOrden) {
        listadetalle = new ArrayList<>();

        String uri = URLBASE+"DetallesDeOrdenWS/DetalleDeOrden/" + idOrden;
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

                            if (MainActivity.modpedidos == true && idOrden==MainActivity.orden.getId()) {

                                listadetalle.addAll(MainActivity.listadetalle);
                            }

                        progressBar.setVisibility(View.GONE);

                        adapter = new DetalleOrdenAdapter(listadetalle);

                        total.setText("$" + calcularTotal(listadetalle));

                        lista.setAdapter(adapter);

                    }
                    //Si no es mayor regresamos al fragmento anterior y sacamos el fragment actual de la pila
                    else {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(rootView.getContext(), "Esta Orden no tiene detalles", Toast.LENGTH_SHORT).show();

                        FragmentManager fm = getActivity().getSupportFragmentManager();
                        for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
                            fm.popBackStack();
                        }

                        Fragment fragment = new Pedidos();
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

