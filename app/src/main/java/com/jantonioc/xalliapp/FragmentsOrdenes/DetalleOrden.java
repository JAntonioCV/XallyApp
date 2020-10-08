package com.jantonioc.xalliapp.FragmentsOrdenes;


import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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
import com.jantonioc.xalliapp.Adaptadores.DetalleOrdenAdapter;
import com.jantonioc.xalliapp.Constans;
import com.jantonioc.xalliapp.MainActivity;
import com.jantonioc.xalliapp.R;
import com.jantonioc.xalliapp.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.jantonioc.xalliapp.Constans.URLBASE;

/**
 * A simple {@link Fragment} subclass.
 */
public class DetalleOrden extends Fragment {

    //interfazz
    private View rootView;
    private RecyclerView lista;
    private DetalleOrdenAdapter adapter;
    private ProgressBar progressBar;
    private LinearLayout linearLayout;

    //dialog
    private TextInputLayout txtcantidad;
    private TextInputLayout txtnota;
    private TextView txtplatillo;
    private TextView txtexistencia;

    private TextInputEditText cantidadtxt;
    private TextInputEditText notatxt;

    private Button ordenar;

    int cantidad;

    private Button btnenviar;
    private TextView total;


    public DetalleOrden() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //toolbar
        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle("Detalle Ordenes");

        //fab botton
        FloatingActionButton fab = getActivity().findViewById(R.id.fab);
        fab.hide();

        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_detalle_orden, container, false);

        lista = rootView.findViewById(R.id.recyclerViewDetalleOrden);
        lista.setHasFixedSize(true);
        lista.setLayoutManager(new LinearLayoutManager(rootView.getContext()));

        linearLayout = rootView.findViewById(R.id.linearlayout);

        progressBar = rootView.findViewById(R.id.progressBar);
        //progressBar.setVisibility(View.GONE);

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
                        //envia la orden
                        enviarOrden(MainActivity.listadetalle, MainActivity.orden);

                    } else {
                        //eror por que el detalle esta vacio
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

                //si lo cancela se vuelve el cardview
                builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //si lo cancela se cierra y vuelve el detalle eliminado por el swipe
                        adapter.notifyItemChanged(viewHolder.getAdapterPosition());
                    }
                });

                builder.setCancelable(false);
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
                Obtenerexitencia(MainActivity.listadetalle.get(lista.getChildAdapterPosition(v)), lista.getChildAdapterPosition(v));
            }
        });

        //adaptamos la lista
        lista.setAdapter(adapter);
        progressBar.setVisibility(View.GONE);
    }

    //Validando si se modifica la orden o se agrega una nueva || aqui deberia mostrar lo que ya tengo que podria ser modificado
    private void modificardetalle(final DetalleDeOrden detalleDeOrden, final int cantidad, final int position) {

        //recorre la lista para modificare
        for (final DetalleDeOrden detalleActual : MainActivity.listadetalle) {

            //if los id del selelecionado y el de la lista son iguales
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
                    break;

                }
                //si es menor a lo pedido y diferente de -2
                else if(cantidad < detalleActual.getCantidad() && cantidad != -2)
                {
                    //Mostrar un dialog
                    final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                    builder.setTitle("Actualizar Detalle");
                    builder.setMessage("La existencia ah cambiado,¿Desea actualizar la cantidad del detalle?");

                    builder.setPositiveButton("Actualizar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //Si confirma actualiza y actualiza el total
                            detalleActual.setCantidad(cantidad);
                            listaDetalleDeOrden();
                            total.setText("$" + calcularTotal(MainActivity.listadetalle));
                            Toast.makeText(rootView.getContext(), "La cantidad fue cambiada para: " + detalleActual.getNombreplatillo(), Toast.LENGTH_LONG).show();
                        }
                    });

                    //si lo cambia se cierra
                    builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

                    builder.create();
                    builder.show();
                    break;
                } else {
                    // de lo contrario podemos agregar o disminuir o modificar
                    final AlertDialog builder = new AlertDialog.Builder(rootView.getContext()).create();

                    //dialog
                    View view = getLayoutInflater().inflate(R.layout.detalle_orden, null);
                    txtplatillo = view.findViewById(R.id.nombreplatillo);
                    txtexistencia = view.findViewById(R.id.existencia);
                    txtcantidad = view.findViewById(R.id.cantidad);
                    txtnota = view.findViewById(R.id.notaopcional);
                    ordenar = view.findViewById(R.id.btnordenar);

                    //restar la cantidad
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

                    //sumar la cantidad
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

                    //si el inventario o no inventriado
                    if (cantidad == -2) {
                        txtexistencia.setText("Existencia: No inventariado");
                    } else {
                        txtexistencia.setText("Existencia: " + String.valueOf(cantidad));
                    }

                    //obteniendo la info
                    txtcantidad.getEditText().setText(String.valueOf(detalleActual.getCantidad()));
                    txtnota.getEditText().setText(detalleActual.getNota());
                    txtplatillo.setText(detalleActual.getNombreplatillo());
                    ordenar.setText("MODIFICAR");

                    //evento click
                    ordenar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //si los campos son validos
                            if (!validarCampos(cantidad)) {
                                return;
                            } else {
                                //si no hay cambios toast
                                if (detalleActual.getCantidad() == Integer.valueOf(txtcantidad.getEditText().getText().toString()) && detalleActual.getNota().equals(txtnota.getEditText().getText().toString())) {
                                    Toast.makeText(rootView.getContext(), "No existen cambios para guardar", Toast.LENGTH_SHORT).show();
                                }
                                else
                                {
                                    //guardamos las modificaciones
                                    detalleActual.setCantidad(Integer.valueOf(txtcantidad.getEditText().getText().toString()));
                                    detalleActual.setNota(txtnota.getEditText().getText().toString());
                                    total.setText("$" + calcularTotal(MainActivity.listadetalle));
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

        linearLayout.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);

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

        String uri = URLBASE+"DetallesDeOrdenWS/OrdenesDetalle";
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
                        linearLayout.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(rootView.getContext(), mensaje, Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException ex) {
                    linearLayout.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(rootView.getContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                linearLayout.setVisibility(View.VISIBLE);
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
        String uri = URLBASE+"InventarioWS/Existencia/" + detalleDeOrden.getMenuid();
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
