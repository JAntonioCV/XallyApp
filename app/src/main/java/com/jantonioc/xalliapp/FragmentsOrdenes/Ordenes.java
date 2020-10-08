package com.jantonioc.xalliapp.FragmentsOrdenes;


import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.jantonioc.xalliapp.Constans;
import com.jantonioc.xalliapp.MainActivity;
import com.jantonioc.xalliapp.R;
import com.jantonioc.xalliapp.VolleySingleton;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.jantonioc.xalliapp.Constans.URLBASE;

/**
 * A simple {@link Fragment} subclass.
 */
public class Ordenes extends Fragment {


    //Variables de la interfaz
    private View rootView;
    private Button btnAgregarOrden;
    private Date date = new Date();

    private TextInputLayout txtcodigo;
    private TextInputLayout txtfecha;
    private TextInputLayout txthora;

    //obtener los formatos de fecha y hora
    private DateFormat hourFormat = new SimpleDateFormat("HH:mm:ss");
    private DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

    private RadioButton rbHuesped;
    private RadioButton rbvisitante;



    public Ordenes() {
        // Required empty public constructor
    }

    //creacion del menu
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    //opcion del menu y listener al darle click recargar el WS
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.reload_fragment, menu);

        //Menu item para buscar el codigo
        MenuItem reload = menu.findItem(R.id.action_reload);

        reload.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                obtenerCodigo();
                return false;
            }
        });

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        //Cambiando el toolbar
        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle("Nueva Orden");

        //ocualtando el fab
        FloatingActionButton fab = getActivity().findViewById(R.id.fab);
        fab.hide();

        //vista
        rootView = inflater.inflate(R.layout.fragment_orden, container, false);

        //campos
        txtcodigo = rootView.findViewById(R.id.codigoorden);
        txtfecha = rootView.findViewById(R.id.fechaorden);
        txthora = rootView.findViewById(R.id.horaorden);

        rbHuesped = rootView.findViewById(R.id.rbhuesped);
        rbvisitante = rootView.findViewById(R.id.rbvisitante);

        //poner el visitante seleccionado por defecto
        rbvisitante.setChecked(true);

        //Obteniendo el ultimo codigo
        obtenerCodigo();

        btnAgregarOrden = rootView.findViewById(R.id.btnagregarpedido);

        btnAgregarOrden.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

        //Al agregar orden abrir el fragmento categoria
                if(txtcodigo.getEditText().getText().toString().isEmpty())
                {
                    //si el codigo es vacion mensaje de actualize el codigo
                    Toast.makeText(rootView.getContext(),"Sin codigo actualize, por favor",Toast.LENGTH_LONG).show();
                }else
                {
                    //si el huesped es seleccionado
                    if(rbHuesped.isChecked())
                    {
                        Fragment fragment = new Clientes();
                        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.content, fragment);
                        transaction.addToBackStack(null);
                        transaction.commit();

                    }
                    //si es visitante el selecionado
                    else if(rbvisitante.isChecked())
                    {
                        MainActivity.orden.setIdcliente(-1);
                        Fragment fragment = new Categorias();
                        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.content, fragment);
                        transaction.addToBackStack(null);
                        transaction.commit();
                    }
                    else
                    {
                        //que seleecione uno
                        Toast.makeText(rootView.getContext(),"Seleccione el tipo de cliente",Toast.LENGTH_LONG).show();
                    }

                }
            }
        });

        return rootView;


    }

    //obtiene el codigo
    private void obtenerCodigo() {
        String uri = URLBASE+"OrdenesWS/UltimoCodigo";
        StringRequest request = new StringRequest(Request.Method.GET, uri, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                //Si la respuesta es distinta de nula tenemos codigo de lo contrario no
                if (response != null) {

                    txtcodigo.getEditText().setText(response);
                    txtfecha.getEditText().setText(dateFormat.format(date));
                    txthora.getEditText().setText(hourFormat.format(date));

                    MainActivity.orden.setCodigo(Integer.valueOf(txtcodigo.getEditText().getText().toString()));
                    MainActivity.orden.setFechaorden(txtfecha.getEditText().getText().toString());
                    MainActivity.orden.setTiempoorden(txthora.getEditText().getText().toString());
                    MainActivity.orden.setEstado(1);


                } else {
                    Toast.makeText(rootView.getContext(), "Error al obtener el codigo, intente de nuevo", Toast.LENGTH_SHORT).show();
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //mostrar ciertos errores
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    Toast.makeText(rootView.getContext(), "Error de comunicacion", Toast.LENGTH_SHORT).show();
                } else if (error instanceof AuthFailureError) {
                    Toast.makeText(rootView.getContext(), "Error de Autentificación", Toast.LENGTH_SHORT).show();
                } else if (error instanceof ServerError) {
                    Toast.makeText(rootView.getContext(), "Error del Servidor", Toast.LENGTH_SHORT).show();
                } else if (error instanceof NetworkError) {
                    Toast.makeText(rootView.getContext(), "Error de conexion de red o wifi", Toast.LENGTH_SHORT).show();
                } else if (error instanceof ParseError) {
                    Toast.makeText(rootView.getContext(), "Error de analisis", Toast.LENGTH_SHORT).show();
                }
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

        //politica de reintentos para obtener el codigo
        request.setRetryPolicy(new DefaultRetryPolicy(5000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance(rootView.getContext()).addToRequestQueue(request);
    }


}