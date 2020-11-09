package com.jantonioc.xalliapp.FragmentsPedidos;


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

import android.util.Base64;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.jantonioc.ln.DetalleDeOrden;
import com.jantonioc.ln.Orden;
import com.jantonioc.xalliapp.Constans;
import com.jantonioc.xalliapp.FragmentsOrdenes.Categorias;
import com.jantonioc.xalliapp.MainActivity;
import com.jantonioc.xalliapp.VolleySingleton;
import com.jantonioc.xalliapp.Adaptadores.DetalleOrdenAdapter;
import com.jantonioc.xalliapp.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class DetallesDeOrden extends Fragment {

    //Variables del fragment
    private View rootView;
    private RecyclerView lista;
    private DetalleOrdenAdapter adapter;
    private List<DetalleDeOrden> listadetalle;
    private ProgressBar progressBar;
    private LinearLayout linearLayout;


    //boton y texto enviar
    private Button btnenviar;
    private TextView total;

    //variables nesesarias
    private int idorden;
    private int cantidad;

    //dialog
    private TextInputLayout txtcantidad;
    private TextInputLayout txtnota;

    private TextInputEditText cantidadtxt;
    private TextInputEditText notatxt;

    private Button ordenar;

    private TextView txtplatillo;
    private TextView txtexistencia;


    //enviar la hora de la modificacion
    private Date date = new Date();
    private DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");



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

        //metodo click para agregar detalle de orden
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

        //linear layout
        linearLayout = rootView.findViewById(R.id.linearlayout);
        linearLayout.setVisibility(View.GONE);

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

        //obtener los detalles de ese id de orden
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

                        //eliminar de la lista principal para que no se adapte de nuevo
                        for (final Iterator<DetalleDeOrden> iterator = MainActivity.listadetalle.iterator(); iterator.hasNext();)
                        {
                            final DetalleDeOrden detalleActual = iterator.next();

                            if (listadetalle.get(position).getMenuid() == detalleActual.getMenuid()) {
                                iterator.remove();
                                break;
                            }

                        }

                        //remover de la lista y notofcar al adaptador para que actualize
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
        linearLayout.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);

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
                    detallesordenes.put("notaorden", detalleActual.getNota().isEmpty() ? "" : detalleActual.getNota());
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
            nuevosDetallesObject.put("fechaOrden",dateFormat.format(date));
            nuevosDetallesObject.put("nuevoDetallesWS", detallesordenesArray);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        //Enviar la orden al server
        String uri = Constans.URLBASE+"DetallesDeOrdenWS/NuevosDetalle";
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
                        progressBar.setVisibility(View.GONE);
                        linearLayout.setVisibility(View.VISIBLE);
                        Toast.makeText(rootView.getContext(), mensaje, Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException ex) {
                    progressBar.setVisibility(View.GONE);
                    linearLayout.setVisibility(View.VISIBLE);
                    Toast.makeText(rootView.getContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.GONE);
                linearLayout.setVisibility(View.VISIBLE);
                Toast.makeText(rootView.getContext(), error.getMessage(), Toast.LENGTH_LONG).show();
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

        String uri = Constans.URLBASE+"DetallesDeOrdenWS/DetalleDeOrden/" + idOrden;
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

                        adapter = new DetalleOrdenAdapter(listadetalle);

                        progressBar.setVisibility(View.GONE);
                        linearLayout.setVisibility(View.VISIBLE);

                        adapter.setClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //metodo para modificar
                                //solo se pueden modificar los fromservice = false;
                                if(!listadetalle.get(lista.getChildAdapterPosition(v)).getFromservice())
                                {
                                    //obtenemos la exitencia del producto
                                    Obtenerexitencia(listadetalle.get(lista.getChildAdapterPosition(v)), lista.getChildAdapterPosition(v));
                                }

                            }
                        });

                        //actualizamos el totl con la lista y apdatamos
                        total.setText("$" + calcularTotal(listadetalle));
                        lista.setAdapter(adapter);

                    }
                    //Si no es mayor regresamos al fragmento anterior y sacamos el fragment actual de la pila
                    else {
                        progressBar.setVisibility(View.GONE);
                        linearLayout.setVisibility(View.VISIBLE);
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
                    linearLayout.setVisibility(View.VISIBLE);
                    Toast.makeText(rootView.getContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
                    ex.printStackTrace();
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.GONE);
                linearLayout.setVisibility(View.VISIBLE);
                Toast.makeText(rootView.getContext(),Constans.errorVolley(error), Toast.LENGTH_SHORT).show();

            }
        })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return Constans.getToken();
            }
        };


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

    //Obtener la existencia de un producto de el bar
    private void Obtenerexitencia(final DetalleDeOrden detalleDeOrden, final int position) {
        String uri = Constans.URLBASE+"InventarioWS/Existencia/" + detalleDeOrden.getMenuid();
        StringRequest request = new StringRequest(Request.Method.GET, uri, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                cantidad = Integer.valueOf(response);
                modificardetalle(detalleDeOrden, cantidad, position);
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(rootView.getContext(), error.getMessage(), Toast.LENGTH_LONG).show();
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

    //Validando si se modifica la orden o se agrega una nueva || aqui deberia mostrar lo que ya tengo que podria ser modificado
    private void modificardetalle(final DetalleDeOrden detalleDeOrden, final int cantidad, final int position) {
        //recorremos la lista en busca del detlle de orden especifico
        for (final DetalleDeOrden detalleActual : listadetalle) {

            //si el selecionado se encuentra en la lista entra y botiene los datos
            if (detalleDeOrden.getMenuid() == detalleActual.getMenuid() && detalleActual.getFromservice()==false) {

                //si la cantidad es 0 quiere decir que no hay existencia
                if (cantidad == 0) {

                    //Mostrar un dialog
                    final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                    builder.setTitle("Eliminar Detalle");
                    builder.setMessage("¿La existencia ha cambiado a 0 desea eliminar el detalle?");

                    builder.setPositiveButton("Eliminar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //Si confirma se borra de la lista del detalle y se calcula el total
                            Toast.makeText(rootView.getContext(), "Eliminado de la Orden", Toast.LENGTH_SHORT).show();

                            //eliminar de la lista principal para que no se adapte de nuevo
                            for (final Iterator<DetalleDeOrden> iterator = MainActivity.listadetalle.iterator(); iterator.hasNext();)
                            {
                                final DetalleDeOrden detalleActual = iterator.next();

                                if (listadetalle.get(position).getMenuid() == detalleActual.getMenuid()) {
                                    iterator.remove();
                                    break;
                                }
                            }

                            //eliminar y actualizar el adapter
                            listadetalle.remove(position);
                            total.setText("$" + calcularTotal(listadetalle));
                            adapter.notifyDataSetChanged();
                        }
                    });

                    //si lo cancela lo cerramos
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
                //si la cantidad es menor a lo pedido y diferente de menos 2 la exitencia se redujo
                else if(cantidad < detalleActual.getCantidad() && cantidad != -2)
                {
                    //Mostrar un dialog
                    final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                    builder.setTitle("Actualizar Detalle");
                    builder.setMessage("La existencia ah cambiado,¿Desea actualizar la cantidad del detalle?");

                    builder.setPositiveButton("Actualizar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //Si confirma actualiza la cantidad
                            detalleActual.setCantidad(cantidad);

                            //recorremos para actualizar el campo del platillo selecionado
                            for(DetalleDeOrden detalleDeOrdenMain: MainActivity.listadetalle)
                            {
                                if(detalleDeOrdenMain.getMenuid() == detalleActual.getMenuid())
                                {
                                    detalleDeOrdenMain.setCantidad(detalleActual.getCantidad());
                                    break;
                                }
                            }

                            //actualizar el total y adaptar
                            total.setText("$" + calcularTotal(listadetalle));
                            adapter.notifyDataSetChanged();
                            Toast.makeText(rootView.getContext(), "La cantidad fue cambiada para: " + detalleActual.getNombreplatillo(), Toast.LENGTH_LONG).show();
                        }
                    });

                    //si cancela cerramos el dialog
                    builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

                    builder.create();
                    builder.show();
                    break;
                } else
                    {
                        //de lo contrario podemos modificar
                    final AlertDialog builder = new AlertDialog.Builder(rootView.getContext()).create();

                    View view = getLayoutInflater().inflate(R.layout.detalle_orden, null);
                    txtplatillo = view.findViewById(R.id.nombreplatillo);
                    txtexistencia = view.findViewById(R.id.existencia);
                    txtcantidad = view.findViewById(R.id.cantidad);
                    txtnota = view.findViewById(R.id.notaopcional);

                    //restar del campo cantidad
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

                    //sumar del campo cantidad
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

                    //le pone la exitencia
                    if (cantidad == -2) {
                        txtexistencia.setText("Existencia: No inventariado");
                    } else {
                        txtexistencia.setText("Existencia: " + String.valueOf(cantidad));
                    }

                    //obtenemos la info
                    txtplatillo.setText(detalleActual.getNombreplatillo());
                    txtcantidad.getEditText().setText(String.valueOf(detalleActual.getCantidad()));
                    txtnota.getEditText().setText(detalleActual.getNota());
                    ordenar = view.findViewById(R.id.btnordenar);

                    //itemclic modificar
                    ordenar.setText("MODIFICAR");
                    ordenar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //si son validos los campos o eror
                            if (!validarCampos(cantidad)) {
                                return;
                            } else {
                                //si son validos ver si realmente hay cambios sino toas de no hay cambios
                                if (detalleActual.getCantidad() == Integer.valueOf(txtcantidad.getEditText().getText().toString()) && detalleActual.getNota().equals(txtnota.getEditText().getText().toString())) {
                                    Toast.makeText(rootView.getContext(), "No existen cambios para guardar", Toast.LENGTH_SHORT).show();
                                }
                                else
                                {
                                    //aceptar los cambios esto tambien modifica la  lista del main por que las variables apuntan al mismo hashcode
                                    detalleActual.setCantidad(Integer.valueOf(txtcantidad.getEditText().getText().toString()));
                                    detalleActual.setNota(txtnota.getEditText().getText().toString());
                                    total.setText("$" + calcularTotal(listadetalle));
                                    adapter.notifyDataSetChanged();
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


}

