package com.jantonioc.xallyapp.Fragments;


import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.jantonioc.ln.Menu;
import com.jantonioc.ln.Receta;
import com.jantonioc.xallyapp.R;
import com.jantonioc.xallyapp.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 */
public class DetalleMenu extends Fragment {

    private View rootView;
    private ListView lista;
    private TextView nombre, precio, tiempo;
    private ProgressBar progressBar;
    private CardView cardinfo,cardingre;


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
        FloatingActionButton fab = getActivity().findViewById(R.id.fab);
        fab.hide();


        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_detalle_menu, container, false);

        cardinfo = rootView.findViewById(R.id.cardinformacion);
        cardingre = rootView.findViewById(R.id.cardingredientes);

        lista = rootView.findViewById(R.id.lista_ingredientes);
        nombre = rootView.findViewById(R.id.itemnombre);
        precio = rootView.findViewById(R.id.itemprecio);
        tiempo = rootView.findViewById(R.id.itemtiempo);
        progressBar = rootView.findViewById(R.id.progressBar);

        //Ocultando las csrd para que se muestren hasta que se cargien los datos
        cardinfo.setVisibility(View.GONE);
        cardingre.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);


        //obtenemos el objeto menu serializado
        Bundle bundle = getArguments();
        Menu menu = (Menu) bundle.getSerializable("Menu");


        //por el id del menu
        detalleMenu(menu);

        return rootView;
    }

    //obtener informacion de los platillos
    private void detalleMenu(final Menu menu) {

        String uri = "http://xally.somee.com/Xally/API/RecetasWS/RecetaPorPlatillo/" + menu.getId();
        StringRequest request = new StringRequest(Request.Method.GET, uri, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {

                    //Obteniendo la informacion del array de los platillos
                    JSONArray jsonArray = new JSONArray(response);

                    if(jsonArray.length() != 0)
                    {
                        //Obteniendo la informacion del objeto
                        JSONObject obj = jsonArray.getJSONObject(0);

                        //Agregando el objeto a la clase
                        Receta receta = new Receta(
                                obj.getInt("id"),
                                obj.getString("descripcion"),
                                obj.getString("tiempoEstimado"),
                                obj.getBoolean("estado"),
                                obj.getString("ingrediente")
                        );

                        //Hacemos visibles las cards
                        cardinfo.setVisibility(View.VISIBLE);
                        cardingre.setVisibility(View.VISIBLE);

                        progressBar.setVisibility(View.GONE);

                        //Mandamos la informacion
                        nombre.setText(menu.getDescripcion());
                        precio.setText("Precio: " + Double.valueOf(menu.getPrecio()).toString() + " $");
                        tiempo.setText("Tiempo estimado: " + receta.getTiempoEstimado() + " minutos");

                        //Creamos un arreglo de los ingredientes y los adaptamos a una lista
                        String[] ingredientes = receta.getIngrediente().split(";");

                        ArrayAdapter adapter = new ArrayAdapter<String>(rootView.getContext(), android.R.layout.simple_list_item_1, ingredientes);

                        //Hacerlo con un RecyclerView
                        //List<String> ingredientes;
                        //ingredientes = Arrays.asList(arreglostring);
                        //DetalleAdapter adapter = new DetalleAdapter(ingredientes);

                        lista.setAdapter(adapter);

                    }
                    else {
                        //Si el objteo es null
                        progressBar.setVisibility(View.GONE);
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


}
