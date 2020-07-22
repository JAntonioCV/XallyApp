package com.jantonioc.xallyapp.FragmentsPedidos;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jantonioc.xallyapp.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class Pedidos extends Fragment {


    public Pedidos() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_pedidos, container, false);
    }

}
