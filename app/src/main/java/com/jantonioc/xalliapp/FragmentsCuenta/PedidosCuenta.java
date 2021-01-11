package com.jantonioc.xalliapp.FragmentsCuenta;


import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.jantonioc.ln.DetalleDeOrden;
import com.jantonioc.ln.Orden;
import com.jantonioc.xalliapp.Constans;
import com.jantonioc.xalliapp.FragmentsOrdenes.Ordenes;
import com.jantonioc.xalliapp.FragmentsPedidos.Pedidos;
import com.jantonioc.xalliapp.MainActivity;
import com.jantonioc.xalliapp.Principal;
import com.jantonioc.xalliapp.VolleySingleton;
import com.jantonioc.xalliapp.Adaptadores.PedidosAdapter;
import com.jantonioc.xalliapp.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.jantonioc.xalliapp.MainActivity.navigationView;

/**
 * A simple {@link Fragment} subclass.
 */
public class PedidosCuenta extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private View rootView;
    private RecyclerView lista;
    private List<Orden> listaPedidos;

    private PedidosAdapter adapter;

    private SwipeRefreshLayout swipeRefreshLayout;

    private TextInputLayout txtcantidad;
    private TextInputEditText cantidadtxt;
    private Button calcular;
    private Integer cantidadClientes = 0;
    private RadioButton rbiguales,rbindividual;

    private RelativeLayout relativeLayout;
    private RelativeLayout noconection;
    private FloatingActionButton fab;

    private Button btnreintentar;
    private TextView txterror;

    private boolean abierto = false;


    AlertDialog builder;


    public PedidosCuenta() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //cambiar el nombre del toolbar
        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle("Dividir Cuenta");

        //ocultar el floating boton
        fab = getActivity().findViewById(R.id.fab);
        fab.hide();

        //vista del fragment
        rootView = inflater.inflate(R.layout.fragment_pedidos, container, false);

        relativeLayout = rootView.findViewById(R.id.relativePedidos);
        noconection = rootView.findViewById(R.id.noconection);
        relativeLayout.setVisibility(View.GONE);

        //de la vista de no conexion
        btnreintentar = rootView.findViewById(R.id.btnrein);
        txterror = rootView.findViewById(R.id.errorTitle);


        //recycler view
        lista = rootView.findViewById(R.id.recyclerViewPedidos);
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

                        PedidosCuenta.this.listaPedidos();

                    }
                });
            }
        });

        //listar los pedidos
        listaPedidos();

        return rootView;
    }

    private void listaPedidos() {

        swipeRefreshLayout.setRefreshing(true);
        relativeLayout.setVisibility(View.GONE);
        noconection.setVisibility(View.GONE);

        //limpiar los pedidos al consultar al WS
        listaPedidos = new ArrayList<>();

        String uri = Constans.URLBASE + "OrdenesWS/OrdenesCerradas";
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

                        //obteniendo la fecha en Date
                        String jsondate = obj.getString("fechaorden");

                        //Convirtiendo la fecha a legible
                        String fecha = ConvertirJsonFecha(jsondate);

                        //Convirtiendo el tiempo a legible
                        String hora = ConvertirJsonTiempo(jsondate);

                        //Obteniendo los datos y convirtiendolos a Orden
                        Orden orden = new Orden(
                                obj.getInt("id"),
                                obj.getInt("codigo"),
                                fecha,
                                obj.getString("horaorden"),
                                obj.getInt("estado"),
                                obj.getInt("clienteid"),
                                obj.getInt("meseroid"),
                                obj.getInt("mesaid"),
                                obj.getString("cliente"),
                                obj.getString("mesero"),
                                obj.getString("mesa")
                        );

                        //Agregando a la lista de orden
                        listaPedidos.add(orden);
                    }

                    //Si la lista es mayor que 0 adaptamos y hacemos el evento on click
                    if (listaPedidos.size() > 0) {

                        swipeRefreshLayout.setRefreshing(false);
                        relativeLayout.setVisibility(View.VISIBLE);

                        adapter = new PedidosAdapter(listaPedidos);

                        //listener al darle click
                        adapter.setClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //ver el detalle de orden

                                dialogoCantidad(listaPedidos.get(lista.getChildAdapterPosition(v)).getId());

                                //clientes(listaPedidos.get(lista.getChildAdapterPosition(v)).getId());
                            }
                        });

                        lista.setAdapter(adapter);

                    }
                    //Si no es mayor regresamos al fragmento anterior y sacamos el fragment actual de la pila
                    else {

                        navigationView.getMenu().getItem(0).getSubMenu().getItem(3).setChecked(false);
                        swipeRefreshLayout.setRefreshing(false);
                        Toast.makeText(rootView.getContext(), "No se poseen ordenes cerradas para el dia de hoy", Toast.LENGTH_SHORT).show();

                        FragmentManager fm = getActivity().getSupportFragmentManager();
                        for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
                            fm.popBackStack();
                        }

                        Fragment fragment = new Principal();
                        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                        transaction.add(R.id.content, fragment);
                        transaction.commit();
                    }

                } catch (JSONException ex) {

                    swipeRefreshLayout.setRefreshing(false);
                    noconection.setVisibility(View.VISIBLE);
                    Toast.makeText(rootView.getContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                swipeRefreshLayout.setRefreshing(false);
                noconection.setVisibility(View.VISIBLE);
                Toast.makeText(rootView.getContext(), Constans.errorVolley(error), Toast.LENGTH_SHORT).show();

            }
        })
        {
            //metodo para la autenficacion basica en el servidor
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return MainActivity.getToken();
            }
        };


        VolleySingleton.getInstance(rootView.getContext()).addToRequestQueue(request);


    }

    private void detalles(int idorden, int cantidadClientes)
    {
        Fragment fragment = new DetallesOrdenIguales();
        Bundle bundle = new Bundle();
        bundle.putInt("idOrden",idorden);
        bundle.putInt("cantidad",cantidadClientes);
        fragment.setArguments(bundle);
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.content, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void clientes(int idorden) {

        MainActivity.limpiarListas();
        obtenerDetalles(idorden);
    }



    //obtener la fecha en forato legible para el usuario
    private static String ConvertirJsonFecha(String jsonfecha) {

        jsonfecha = jsonfecha.replace("/Date(", "").replace(")/", "");
        long tiempo = Long.parseLong(jsonfecha);
        Date fecha = new Date(tiempo);

        return new SimpleDateFormat("dd/MM/yyyy").format(fecha);
    }

    //obtener el tiempo en formato legible para el usuario
    private static String ConvertirJsonTiempo(String jsonfecha) {
        jsonfecha = jsonfecha.replace("/Date(", "").replace(")/", "");
        long tiempo = Long.parseLong(jsonfecha);
        Date fecha = new Date(tiempo);

        return new SimpleDateFormat("hh:mm a").format(fecha);
    }


    private void obtenerDetalles(int idOrden) {

        //MainActivity.orden.setId(idOrden);
        MainActivity.listadetalle = new ArrayList<>();

        String uri = Constans.URLBASE + "DetallesDeOrdenWS/DetalleDeOrdenCuenta/" + idOrden;
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
                        MainActivity.listadetalle.add(detalleDeOrden);
                    }

                    if(MainActivity.listadetalle.size()>0)
                    {
                        //Abrir el fragmento del detalle de los platillos
                        Fragment fragment = new ListaCuentas();
                        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.content, fragment);
                        transaction.addToBackStack(null);
                        transaction.commit();
                    }
                    else
                    {
                        Toast.makeText(rootView.getContext(), "Esta Orden no tiene detalles", Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException ex) {

                    Toast.makeText(rootView.getContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
                    ex.printStackTrace();
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(rootView.getContext(),Constans.errorVolley(error), Toast.LENGTH_SHORT).show();

            }
        })
        {
            //metodo para la autenficacion basica en el servidor
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return MainActivity.getToken();
            }
        };

        VolleySingleton.getInstance(rootView.getContext()).addToRequestQueue(request);
    }

    private void dialogoCantidad(final int id) {

        if(!abierto)
        {
            abierto = true;

            //Abrimos la modal agregar el nuevo detalle de orden
            builder = new AlertDialog.Builder(rootView.getContext()).create();

            View view = getLayoutInflater().inflate(R.layout.cantidad_persona, null);
            txtcantidad = view.findViewById(R.id.cantidad);
            cantidadtxt = view.findViewById(R.id.cantidadtxt);
            rbiguales = view.findViewById(R.id.rbiguales);
            rbindividual = view.findViewById(R.id.rbindividuales);
            calcular = view.findViewById(R.id.btncalcular);

            rbiguales.setChecked(true);

            //calcula
            calcular.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //si los campos son validos
                    if (!validarCantidad()) {
                        return;
                    } else {
                        if(rbindividual.isChecked())
                        {
                            //pasa la cantidad y crea la lista de clientes y lista de productos por cliente
                            cantidadClientes = Integer.valueOf(txtcantidad.getEditText().getText().toString().trim());
                            //Creando la lista de clientes y la lista de lista
                            MainActivity.crearClientes(cantidadClientes);
                            MainActivity.crearListas(cantidadClientes);
                            //manda el id de la orden
                            clientes(id);
                            builder.cancel();

                        }
                        else
                        {
                            //pasa la cantidad y crea la lista de clientes y lista de productos por cliente
                            cantidadClientes = Integer.valueOf(txtcantidad.getEditText().getText().toString().trim());
                            detalles(id,cantidadClientes);
                            builder.cancel();

                        }

                    }
                }
            });

            builder.setView(view);
            builder.create();
            builder.show();
            builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    abierto = false;
                }
            });
        }


    }

    //valida el campo Cantidad
    private boolean validarCantidad()
    {
        boolean isValidate = true;

        String cantidadInput = txtcantidad.getEditText().getText().toString().trim();

        if (cantidadInput.isEmpty()) {
            isValidate = false;
            txtcantidad.setError("Cantidad no puede estar vacio");

        } else if (Integer.valueOf(cantidadInput) <= 0) {
            isValidate = false;
            txtcantidad.setError("La cantidad no puede ser menor a 1");

        } else {
            txtcantidad.setError(null);
        }

        return isValidate;
    }


    @Override
    public void onRefresh() {
        listaPedidos();
    }
}
