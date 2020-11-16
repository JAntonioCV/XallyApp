package com.jantonioc.xalliapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.jantonioc.ln.Comanda;
import com.jantonioc.ln.DetalleDeOrden;
import com.jantonioc.ln.Orden;
import com.jantonioc.xalliapp.FragmentsCarnet.OrdenCarnet;
import com.jantonioc.xalliapp.FragmentsFinalizar.OrdenFinalizar;
import com.jantonioc.xalliapp.FragmentsOrdenes.DetalleOrden;
import com.jantonioc.xalliapp.FragmentsOrdenes.Ordenes;
import com.jantonioc.xalliapp.FragmentsComanda.ClientesComanda;
import com.jantonioc.xalliapp.FragmentsCuenta.PedidosCuenta;
import com.jantonioc.xalliapp.FragmentsPedidos.DetallesDeOrden;
import com.jantonioc.xalliapp.FragmentsPedidos.Pedidos;
import com.jantonioc.xalliapp.Reportes.ReporteBarChart;
import com.jantonioc.xalliapp.Reportes.ReportePieChart;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    //Fragmento para ser acceddido desde cualquier lugar de la clase
    Fragment fragment = null;

    //lista para lod detallse de nueva orden y agregar orden
    public static List<DetalleDeOrden> listadetalle = new ArrayList<>();
    //objeto para almacenar la orden
    public static Orden orden = new Orden();
    //si es modificacion de pepdido
    public static boolean modpedidos;
    //lista de clientes para la cuenta
    public static List<String> listaClientes;
    //lista de lista para la cuenta
    public static List<List<DetalleDeOrden>> listadetalles;
    //informacion de la comanda
    public static Comanda comanda = new Comanda();

    //para que no se cierre al primer atras
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
                    bundle.putInt("idorden", orden.getId());
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

        View headerView = navigationView.getHeaderView(0);
        TextView nombre = headerView.findViewById(R.id.nav_nombre);
        TextView rol = headerView.findViewById(R.id.nav_rol);
        nombre.setText(Constans.nombre);
        rol.setText(Constans.rol);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        //Abrimos por defecto como primer interfaz el fragment ordenes
        fragment = new prueba();

        //Cargamos el fragment
        cargarFragment(fragment);


    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        //sobre escribir boton atras
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                super.onBackPressed();
            } else {
                if (tiempoPrimerClick + INTERVALO > System.currentTimeMillis()) {
                    super.onBackPressed();
                    return;
                } else {
                    Toast.makeText(this, "Vuelve a presionar para salir", Toast.LENGTH_SHORT).show();
                }
                tiempoPrimerClick = System.currentTimeMillis();
            }

        }
    }

//    //Opciones del menu lateral
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.main, menu);
//        return true;
//    }
//
//    //Opciones del menu de los 3 puntos
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        //Cargando los fragment dependiendo de su id y la seleccion
        fragment = null;

        switch (item.getItemId()) {
            case R.id.nav_menu:
                //abrir nueva orden
                fragment = new Ordenes();
                //limpiamos los auxiliares
                MainActivity.listadetalle.clear();
                MainActivity.orden = new Orden();
                MainActivity.modpedidos = false;
                break;

            case R.id.nav_comanda:
                //abrir lista clientes para la comanda
                fragment = new ClientesComanda();
                //limpiamos los auxiliares
                MainActivity.listadetalle.clear();
                MainActivity.orden = new Orden();
                MainActivity.modpedidos = false;
                break;

            case R.id.nav_pedidos:
                //abrir los pedidos
                fragment = new Pedidos();
                //limpiamos los auxiliares
                MainActivity.listadetalle.clear();
                MainActivity.orden = new Orden();
                MainActivity.modpedidos = false;
                break;

            case R.id.nav_dividir:
                //abrimos dividir cuenta
                fragment = new PedidosCuenta();
                MainActivity.listadetalle.clear();
                MainActivity.orden = new Orden();
                MainActivity.modpedidos = false;
                break;

            case R.id.nav_finalizar:
                fragment = new OrdenFinalizar();
                MainActivity.listadetalle.clear();
                MainActivity.orden = new Orden();
                MainActivity.modpedidos = false;
                break;

            case R.id.nav_cdiplomatico:
                fragment = new OrdenCarnet();
                MainActivity.listadetalle.clear();
                MainActivity.orden = new Orden();
                MainActivity.modpedidos = false;
                break;

            case R.id.nav_repor1:
                fragment = new ReportePieChart();
                MainActivity.listadetalle.clear();
                MainActivity.orden = new Orden();
                MainActivity.modpedidos = false;
                break;

            case R.id.nav_repor2:
                fragment = new ReporteBarChart();
                MainActivity.listadetalle.clear();
                MainActivity.orden = new Orden();
                MainActivity.modpedidos = false;
                break;

            case R.id.nav_salir:
                //eliminamos la contraseña y finalizamos la actividad
                if(!Constans.eliminarpass(getApplicationContext()))
                {
                    Toast.makeText(getApplicationContext(),"Error al cerrar la sesión",Toast.LENGTH_SHORT);
                }
                Intent i = new Intent(MainActivity.this,LoginActivity.class);
                MainActivity.this.finish();
                startActivity(i);
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


    //metodo para crear clientes
    public static void crearClientes(int cantidad) {
        listaClientes = new ArrayList<>();

        for (int i = 0; i < cantidad; i++) {
            String cliente = "Cliente " + (i + 1);
            listaClientes.add(cliente);
        }
    }

    //metodo para crear listas
    public static void crearListas(int cantidad) {
        listadetalles = new ArrayList<>();
        for (int i = 0; i < cantidad; i++) {
            List<DetalleDeOrden> cliente = new ArrayList<>();
            listadetalles.add(cliente);
        }
    }

    //metodo para limoiar las listas
    public static void limpiarListas()
    {
        for(int i=0; i < MainActivity.listadetalles.size(); i++)
        {
                MainActivity.listadetalles.get(i).clear();
        }


    }


}
