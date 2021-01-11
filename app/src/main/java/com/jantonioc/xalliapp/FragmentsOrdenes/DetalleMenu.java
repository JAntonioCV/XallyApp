package com.jantonioc.xalliapp.FragmentsOrdenes;


import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.jantonioc.ln.Menu;
import com.jantonioc.xalliapp.Constans;
import com.jantonioc.xalliapp.MainActivity;
import com.jantonioc.xalliapp.R;
import com.jantonioc.xalliapp.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.jantonioc.xalliapp.Constans.URLBASE;

/**
 * A simple {@link Fragment} subclass.
 */
public class DetalleMenu extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    //interfaz
    private View rootView;
    private ListView lista;
    private TextView nombre, precio, tiempo;
    private CardView cardinfo,cardingre;
    private List<String> ingredientes = new ArrayList();
    Menu menu;

    //swipe to refresh
    private SwipeRefreshLayout swipeRefreshLayout;

    private RelativeLayout noconection;
    private FloatingActionButton fab;
    private LinearLayout linearLayout;

    private Button btnreintentar;

    public DetalleMenu() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //Cambiar el toolbar
        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle("Detalle Menu");

        //ocultar el boton flotante
        fab = getActivity().findViewById(R.id.fab);
        fab.hide();


        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_detalle_menu, container, false);

        linearLayout = rootView.findViewById(R.id.linearlayoutDetalleMenu);
        noconection = rootView.findViewById(R.id.noconection);
        //de la vista de no conexion
        btnreintentar = rootView.findViewById(R.id.btnrein);

        cardinfo = rootView.findViewById(R.id.cardinformacion);
        cardingre = rootView.findViewById(R.id.cardingredientes);

        lista = rootView.findViewById(R.id.lista_ingredientes);
        nombre = rootView.findViewById(R.id.itemnombre);
        precio = rootView.findViewById(R.id.itemprecio);
        tiempo = rootView.findViewById(R.id.itemtiempo);

        //swipe to refresh
        swipeRefreshLayout = rootView.findViewById(R.id.swipe);
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorPrimary));
        swipeRefreshLayout.setOnRefreshListener(this);

        //Ocultando las csrd para que se muestren hasta que se cargien los datos
        cardinfo.setVisibility(View.GONE);
        cardingre.setVisibility(View.GONE);

        //obtenemos el objeto menu serializado
        Bundle bundle = getArguments();
        menu = (Menu) bundle.getSerializable("Menu");

        btnreintentar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                swipeRefreshLayout.post(new Runnable() {
                    @Override
                    public void run() {

                        DetalleMenu.this.detalleMenu(menu);

                    }
                });
            }
        });

        //por el id del menu
        detalleMenu(menu);

        return rootView;
    }

    //obtener informacion de los platillos
    private void detalleMenu(final Menu menu) {

        swipeRefreshLayout.setRefreshing(true);
        cardinfo.setVisibility(View.GONE);
        cardingre.setVisibility(View.GONE);
        noconection.setVisibility(View.GONE);
        ingredientes = new ArrayList<>();

        String uri = URLBASE+"IngredientesWS/IngredientesMenu/" + menu.getId();
        StringRequest request = new StringRequest(Request.Method.GET, uri, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {

                   swipeRefreshLayout.setRefreshing(false);

                    //Obteniendo la informacion del array de los platillos
                    JSONArray jsonArray = new JSONArray(response);

                    if(jsonArray.length() != 0)
                    {
                        for(int i=0; i<jsonArray.length();i++)
                        {
                            JSONObject obj = jsonArray.getJSONObject(i);

                            String ingrediente = obj.getString("descripcion");
                            ingredientes.add(ingrediente);
                        }

                        //Hacemos visibles las cards
                        cardinfo.setVisibility(View.VISIBLE);
                        cardingre.setVisibility(View.VISIBLE);

                        //Mandamos la informacion
                        nombre.setText(menu.getDescripcion());
                        precio.setText("Precio: " + Double.valueOf(menu.getPrecio()).toString() + " $");
                        String tiempoestimado = menu.getTiempoestimado().equalsIgnoreCase("null") || menu.getTiempoestimado().isEmpty()  ? "Inmediato" : menu.getTiempoestimado();
                        tiempo.setText("Tiempo estimado: " + tiempoestimado);

                        ArrayAdapter adapter = new ArrayAdapter<>(rootView.getContext(), android.R.layout.simple_list_item_1, ingredientes);

                        lista.setAdapter(adapter);

                    }
                    else {
                        //Si el objteo es null

                        swipeRefreshLayout.setRefreshing(false);

                        Toast.makeText(rootView.getContext(), "Este Menu no posee Detalle", Toast.LENGTH_SHORT).show();

                        FragmentManager fm = getActivity().getSupportFragmentManager();
                        for (int i = 0; i < fm.getBackStackEntryCount()-1; ++i) {
                            fm.popBackStack();
                        }

                        Fragment fragment = new Menus();
                        Bundle bundle = new Bundle();
                        bundle.putInt("IdCategoria", menu.getIdcategoria());
                        fragment.setArguments(bundle);

                        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.content, fragment);
                        transaction.addToBackStack(null);
                        transaction.commit();
                    }

                } catch (JSONException ex) {

                    //excepcion json
                    swipeRefreshLayout.setRefreshing(false);
                    cardinfo.setVisibility(View.GONE);
                    cardingre.setVisibility(View.GONE);
                    noconection.setVisibility(View.VISIBLE);
                    Toast.makeText(rootView.getContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();

                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                swipeRefreshLayout.setRefreshing(false);
                cardinfo.setVisibility(View.GONE);
                cardingre.setVisibility(View.GONE);
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


    @Override
    public void onRefresh() {
        detalleMenu(menu);
    }
}
