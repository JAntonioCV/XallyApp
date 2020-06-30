package com.jantonioc.xallyapp;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.jantonioc.xallyapp.FragmentsOrdenes.AddCategoria;
import com.jantonioc.xallyapp.FragmentsOrdenes.Categorias;
import com.jantonioc.xallyapp.FragmentsOrdenes.SelectCategoria;

import static com.google.android.material.bottomnavigation.BottomNavigationView.OnNavigationItemSelectedListener;

public class Main2Activity extends AppCompatActivity {

    public Toolbar toolbar;
    private BottomNavigationView navigation;
    Fragment fragment = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTheme(R.style.AppTheme);

        setContentView(R.layout.activity_main2);


        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Categorias");
        setSupportActionBar(toolbar);
        navigation = findViewById(R.id.bottom_navigation);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.BLACK);
            getWindow().setNavigationBarColor(Color.BLACK);
        }

        fragment = new Categorias();

        cargarFragment(fragment);

        navigation.setOnNavigationItemSelectedListener(new OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {


                fragment = null;

                switch (menuItem.getItemId()) {
                    case R.id.nav_menu:
                        fragment = new Categorias();
                        break;
                    case R.id.nav_orden:
                        fragment = new AddCategoria();
                        break;
                    case R.id.nav_pedidos:
                        fragment = new SelectCategoria();
                        break;
                }

                if (fragment == null)
                    return false;
                else {
                    cargarFragment(fragment);
                    return true;
                }
            }
        });

    }


    private void cargarFragment(Fragment fragment) {

        FragmentManager fm = Main2Activity.this.getSupportFragmentManager();
        for(int i = 0; i < fm.getBackStackEntryCount(); ++i) {
            fm.popBackStack();
        }


        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_layout, fragment);
        transaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

}
