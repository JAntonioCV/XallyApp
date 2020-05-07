package com.jantonioc.xallyapp.Fragments;


import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.jantonioc.xallyapp.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class Orden extends Fragment {


    View rootView;
    Button btnAgregarOrden;

    public Orden() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        //Cambiando el toolbar
        Toolbar toolbar=getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle("Nueva Orden");

        //ocualtando el fab
        FloatingActionButton fab = getActivity().findViewById(R.id.fab);
        fab.hide();

        rootView = inflater.inflate(R.layout.fragment_orden, container, false);

        btnAgregarOrden = rootView.findViewById(R.id.btnagregarpedido);

        btnAgregarOrden.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    Bundle bundle= new Bundle();
                    bundle.putInt("IdOrden",1);

                    Fragment fragment = new Categorias();
                    fragment.setArguments(bundle);

                    FragmentTransaction transaction=getActivity().getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.content,fragment);
                    transaction.addToBackStack(null);
                    transaction.commit();
            }
        });


        return rootView;


    }

}
