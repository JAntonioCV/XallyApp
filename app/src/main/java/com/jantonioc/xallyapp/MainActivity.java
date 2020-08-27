package com.jantonioc.xallyapp;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.jantonioc.ln.DetalleDeOrden;
import com.jantonioc.ln.Orden;
import com.jantonioc.xallyapp.FragmentsCuenta.PedidosCuenta;
import com.jantonioc.xallyapp.FragmentsOrdenes.AddCategoria;
import com.jantonioc.xallyapp.FragmentsOrdenes.DetalleOrden;
import com.jantonioc.xallyapp.FragmentsOrdenes.Ordenes;
import com.jantonioc.xallyapp.FragmentsOrdenes.SelectCategoria;
import com.jantonioc.xallyapp.FragmentsPedidos.DetallesDeOrden;
import com.jantonioc.xallyapp.FragmentsPedidos.Pedidos;

import java.nio.charset.MalformedInputException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    //Fragmento para ser acceddido desde cualquier lugar de la clase
    Fragment fragment = null;

    //Instancia de la clase Orden, Detalle Orden
    public static List<DetalleDeOrden> listadetalle = new ArrayList<>();
    public static Orden orden = new Orden();
    public static boolean modpedidos;
    public static List<String> listaClientes;
    public static List<List<DetalleDeOrden>> listadetalles;


    private TextInputLayout txtcantidad;
    private TextInputEditText cantidadtxt;

    private Button calcular;

    private Integer cantidadClientes;


    private static final int INTERVALO = 2000;
    private long tiempoPrimerClick;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Cambiar el texto del toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        //Floating boton
        FloatingActionButton fab = findViewById(R.id.fab);

        //Evento click Floating boton
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Validar si la lista es vacia o no

                if (listadetalle.isEmpty()) {
                    //Si es vacia muestra un toast
                    Toast.makeText(MainActivity.this, "No se han agregdo detalles a la orden", Toast.LENGTH_SHORT).show();

                } else if (MainActivity.modpedidos == false) {
                    //abriendo el fragment del detalle si tiene datos
                    fragment = null;
                    fragment = new DetalleOrden();
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.content, fragment);
                    transaction.addToBackStack(null);
                    transaction.commit();

                } else {

                    //si mod pedidos es true abrimos detalle de orden
                    FragmentManager fm = getSupportFragmentManager();
                    for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
                        fm.popBackStack();
                    }

                    //Abrir el fragmento del detalle de Orden
                    Fragment fragment = new DetallesDeOrden();
                    //Pasar parametros entre fragment
                    Bundle bundle = new Bundle();
                    //mandar el objeto serializado
                    bundle.putInt("idorden",orden.getId());
                    fragment.setArguments(bundle);
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.content, fragment);
                    transaction.addToBackStack(null);
                    transaction.commit();

                }

            }
        });


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        //Abrimos por defecto como primer interfaz el fragment ordenes
        fragment = new Ordenes();

        //Cargamos el fragment
        cargarFragment(fragment);


    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (getSupportFragmentManager().getBackStackEntryCount() > 0)
            {
               super.onBackPressed();
            }
            else
            {
                if (tiempoPrimerClick + INTERVALO > System.currentTimeMillis()){
                    super.onBackPressed();
                    return;
                }else {
                    Toast.makeText(this, "Vuelve a presionar para salir", Toast.LENGTH_SHORT).show();
                }
                tiempoPrimerClick = System.currentTimeMillis();
            }

        }
    }

    //Opciones del menu lateral
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    //Opciones del menu de los 3 puntos
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        //Cargando los fragment dependiendo de su id y la seleccion
        fragment = null;

        switch (item.getItemId()) {
            case R.id.nav_menu:
                //limpiamos los auxiliares
                fragment = new Ordenes();
                MainActivity.listadetalle.clear();
                MainActivity.orden = new Orden();
                MainActivity.modpedidos = false;
                break;

            case R.id.nav_orden:
                fragment = new AddCategoria();
                break;

            case R.id.nav_pedidos:
                //limpiamos los auxiliares
                fragment = new Pedidos();
                MainActivity.listadetalle.clear();
                MainActivity.orden = new Orden();
                MainActivity.modpedidos = false;
                break;

            case R.id.nav_dividir:
                dialogoCantidad();
                fragment = new PedidosCuenta();
                MainActivity.listadetalle.clear();
                MainActivity.orden = new Orden();
                MainActivity.modpedidos = false;
                break;
        }

        if (fragment == null)
            return false;
        else {
            cargarFragment(fragment);
            DrawerLayout drawer = findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
            return true;
        }
    }


    //Cargar el fragment
    private void cargarFragment(Fragment fragment) {

        //sacamos de la pila o cola por que si no se montan unas vistas con otras
        FragmentManager fm = getSupportFragmentManager();
        for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
            fm.popBackStack();
        }

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.content, fragment);
        transaction.commit();
    }


    public static void crearClientes(int cantidad)
    {
        listaClientes = new ArrayList<>();

        for(int i=0; i < cantidad; i++)
        {
            String cliente="Cliente " + (i+1);
            listaClientes.add(cliente);
        }
    }

    public static void crearListas(int cantidad)
    {
        listadetalles = new ArrayList<>();
        for(int i=0; i < cantidad; i++)
        {
            List<DetalleDeOrden> cliente = new ArrayList<>();
            listadetalles.add(cliente);
        }
    }

    public static void limpiarListas()
    {
        for(int i=0; i < MainActivity.listadetalles.size(); i++)
        {
                MainActivity.listadetalles.get(i).clear();
        }


    }


    private void dialogoCantidad()
    {
        //Abrimos la modal agregar el nuevo detalle de orden
        final AlertDialog builder = new AlertDialog.Builder(MainActivity.this).create();

        View view = getLayoutInflater().inflate(R.layout.cantidad_persona, null);

        txtcantidad = view.findViewById(R.id.cantidad);
        cantidadtxt = view.findViewById(R.id.cantidadtxt);
        calcular = view.findViewById(R.id.btncalcular);

        calcular.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!validarCantidad())
                {
                    return;
                }else
                {
                    cantidadClientes = Integer.valueOf(txtcantidad.getEditText().getText().toString().trim());
                    //Creando la lista de clientes y la lista de lista
                    MainActivity.crearClientes(cantidadClientes);
                    MainActivity.crearListas(cantidadClientes);

                    builder.cancel();
                }


            }
        });

        builder.setView(view);
        builder.setCancelable(false);
        builder.setCanceledOnTouchOutside(false);
        builder.create();
        builder.show();
    }

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


}
