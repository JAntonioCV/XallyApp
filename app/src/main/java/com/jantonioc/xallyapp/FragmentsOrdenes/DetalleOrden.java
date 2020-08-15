package com.jantonioc.xallyapp.FragmentsOrdenes;


import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.jantonioc.ln.DetalleDeOrden;
import com.jantonioc.ln.Menu;
import com.jantonioc.ln.Orden;
import com.jantonioc.xallyapp.Adaptadores.DetalleOrdenAdapter;
import com.jantonioc.xallyapp.MainActivity;
import com.jantonioc.xallyapp.R;
import com.jantonioc.xallyapp.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class DetalleOrden extends Fragment {

    private View rootView;
    private RecyclerView lista;
    private DetalleOrdenAdapter adapter;

    private TextInputLayout txtcantidad;
    private TextInputLayout txtnota;
    private TextView txtplatillo;
    private TextView txtexistencia;

    private TextInputEditText cantidadtxt;
    private TextInputEditText notatxt;

    private Button ordenar;

    int cantidad;


    //FloatingActionButton fabenviar;
    private Button btnenviar;
    private TextView total;


    public DetalleOrden() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle("Detalle Ordenes");

        FloatingActionButton fab = getActivity().findViewById(R.id.fab);
        fab.hide();

        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_detalle_orden, container, false);

        lista = rootView.findViewById(R.id.recyclerViewDetalleOrden);
        lista.setHasFixedSize(true);
        lista.setLayoutManager(new LinearLayoutManager(rootView.getContext()));

        total = rootView.findViewById(R.id.total);

        //Calcular el total
        total.setText("$" + calcularTotal(MainActivity.listadetalle));

        //Validar si la lista tiene datos envia si no, muestra un mensaje
        btnenviar = rootView.findViewById(R.id.btnenviar);


            btnenviar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //validando que la lista no este vacia al enviar
                    if (MainActivity.listadetalle.size() > 0) {

                        enviarOrden(MainActivity.listadetalle, MainActivity.orden);

                    } else {

                        Toast.makeText(rootView.getContext(), "El Detalle esta vacio", Toast.LENGTH_SHORT).show();

                    }
                }
            });



        //swipe to delete
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull final RecyclerView.ViewHolder viewHolder, int direction) {

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
                        MainActivity.listadetalle.remove(position);

                        total.setText("$"+ calcularTotal(MainActivity.listadetalle));
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
        };

        //helper para el reciclerview
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(lista);

        //lista el detalle de orden en el recyclerview
        listaDetalleDeOrden();

        return rootView;
    }

    //lista y adapta las ordenes
    private void listaDetalleDeOrden() {

        //Adapta las lista de detalles
        adapter = new DetalleOrdenAdapter(MainActivity.listadetalle);
        //evento click cuando se modifica un detalle
        adapter.setClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //metodo para modificar
                //modificardetalle(MainActivity.listadetalle.get(lista.getChildAdapterPosition(v)));
                Obtenerexitencia(MainActivity.listadetalle.get(lista.getChildAdapterPosition(v)), lista.getChildAdapterPosition(v));
            }
        });

        //adaptamos la lista
        lista.setAdapter(adapter);
    }

    //Validando si se modifica la orden o se agrega una nueva || aqui deberia mostrar lo que ya tengo que podria ser modificado
    private void modificardetalle(final DetalleDeOrden detalleDeOrden, final int cantidad, final int position) {

        for (final DetalleDeOrden detalleActual : MainActivity.listadetalle) {

            if (detalleDeOrden.getMenuid() == detalleActual.getMenuid()) {

                //si la cantidad es 0 y la cantidad de orden mayor quiere decir que no hay existencia
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
                            MainActivity.listadetalle.remove(position);
                            total.setText("$" + calcularTotal(MainActivity.listadetalle));
                            adapter.notifyDataSetChanged();
                        }
                    });

                    builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //si lo cancela se cierra y vuelve el detalle eliminado por el swipe
                            adapter.notifyItemChanged(position);
                        }
                    });

                    builder.create();
                    builder.show();

                } else if(cantidad < detalleActual.getCantidad() && cantidad != -2)
                {
                    //Mostrar un dialog
                    final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                    builder.setTitle("Actualizar Detalle");
                    builder.setMessage("La existencia ah cambiado,¿Desea actualizar la cantidad del detalle?");

                    builder.setPositiveButton("Actualizar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //Si confirma
                            detalleActual.setCantidad(cantidad);
                            listaDetalleDeOrden();
                            total.setText("$" + calcularTotal(MainActivity.listadetalle));
                            Toast.makeText(rootView.getContext(), "La cantidad fue cambiada para: " + detalleActual.getNombreplatillo(), Toast.LENGTH_LONG).show();
                        }
                    });

                    builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

                    builder.create();
                    builder.show();
                }

                else {

                    final AlertDialog builder = new AlertDialog.Builder(rootView.getContext()).create();

                    View view = getLayoutInflater().inflate(R.layout.detalle_orden, null);
                    txtplatillo = view.findViewById(R.id.nombreplatillo);
                    txtexistencia = view.findViewById(R.id.existencia);
                    txtcantidad = view.findViewById(R.id.cantidad);
                    txtnota = view.findViewById(R.id.notaopcional);

                    txtplatillo.setText(detalleActual.getNombreplatillo());

                    if (cantidad == -2) {
                        txtexistencia.setText("Existencia: No inventariado");
                    } else {
                        txtexistencia.setText("Existencia: " + String.valueOf(cantidad));
                    }

                    txtcantidad.getEditText().setText(String.valueOf(detalleActual.getCantidad()));
                    txtnota.getEditText().setText(detalleActual.getNota());

                    Button ordenar = view.findViewById(R.id.btnordenar);
                    ordenar.setText("MODIFICAR");

                    ordenar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            if (!validarCampos(cantidad)) {
                                return;
                            } else {
                                detalleActual.setCantidad(Integer.valueOf(txtcantidad.getEditText().getText().toString()));
                                detalleActual.setNota(txtnota.getEditText().getText().toString());
                                listaDetalleDeOrden();
                                total.setText("$" + calcularTotal(MainActivity.listadetalle));
                                builder.cancel();
                            }


                        }
                    });

                    builder.setView(view);
                    builder.create();
                    builder.show();
                }


            }
        }
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

    //enviamos la orden al servidor
    private void enviarOrden(List<DetalleDeOrden> detalleDeOrdenes, Orden orden) {

        //Creamos el objeto de la orden :v
        JSONObject ordenObject = new JSONObject();
        try {

            ordenObject.put("codigo", orden.getCodigo());
            ordenObject.put("fechaorden", orden.getFechaorden());
            ordenObject.put("tiempoorden", orden.getTiempoorden());
            ordenObject.put("estado", orden.getEstado());
            ordenObject.put("meseroid",1);
            ordenObject.put("clienteid",orden.getIdcliente());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        //Creamos el Array de los detalles de ordenes
        JSONArray detallesordenesArray = new JSONArray();

        //Recorremos las lista de detalles y la agregamos al array
        for (DetalleDeOrden detalleActual : detalleDeOrdenes) {
            try {

                //objeto donde se guardara una orden
                JSONObject detallesordenes = new JSONObject();

                //Guardando datos del detalle de orden
                detallesordenes.put("cantidadorden", detalleActual.getCantidad());
                detallesordenes.put("notaorden", detalleActual.getNota().isEmpty() ? "Sin nota" : detalleActual.getNota());
                detallesordenes.put("nombreplatillo", detalleActual.getNombreplatillo());
                detallesordenes.put("preciounitario", detalleActual.getPrecio());
                detallesordenes.put("estado", detalleActual.getEstado());
                detallesordenes.put("menuid", detalleActual.getMenuid());


                //ingresamos el objeto al array
                detallesordenesArray.put(detallesordenes);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        //Creando un nuevo objeto para guardar el array y el object
        JSONObject ordenesObject = new JSONObject();

        try {

            //les asginamos un nombre para que los pueda reconocer la api
            ordenesObject.put("ordenWS", ordenObject);
            ordenesObject.put("detallesWS", detallesordenesArray);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        //Enviar la orden al server

        String uri = "http://192.168.1.52/ProyectoXalli_Gentelella/DetallesDeOrdenWS/OrdenesDetalle";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, uri, ordenesObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {

                    //respuesta de parte del servidor
                    String mensaje = response.getString("Mensaje");
                    Boolean resultado = response.getBoolean("Resultado");

                    if (resultado) {
                        //segun yo abre el fragmento de las ordenes
                        Toast.makeText(rootView.getContext(), mensaje, Toast.LENGTH_SHORT).show();
                        //reiniciando variables nesesarias
                        MainActivity.orden = new Orden();
                        MainActivity.listadetalle.clear();
                        MainActivity.modpedidos=false;

                        Fragment fragment = new Ordenes();
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

    //Obtener la existencia de un producto de el bar
    private void Obtenerexitencia(final DetalleDeOrden detalleDeOrden, final int position) {
        String uri = "http://192.168.1.52/ProyectoXalli_Gentelella/InventarioWS/Existencia/" + detalleDeOrden.getMenuid();
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
        });
        VolleySingleton.getInstance(rootView.getContext()).addToRequestQueue(request);
    }


}
