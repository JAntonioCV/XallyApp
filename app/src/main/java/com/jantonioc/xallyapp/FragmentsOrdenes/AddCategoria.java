package com.jantonioc.xallyapp.FragmentsOrdenes;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.material.textfield.TextInputLayout;
import com.jantonioc.ln.Categoria;
import com.jantonioc.xallyapp.R;
import com.jantonioc.xallyapp.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddCategoria extends Fragment {

    private TextInputLayout txtcodigo;
    private TextInputLayout txtdescricion;
    private RadioButton activo;
    private RadioButton inactivo;
    private View rootview;
    private Button btnguardar;
    private Button btncancelar;

    boolean guardado = false;

    public AddCategoria() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Toolbar toolbar=getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle("Agregar Categoria");

        // Inflate the layout for this fragment
        rootview = inflater.inflate(R.layout.fragment_add_categoria, container, false);

        txtcodigo = rootview.findViewById(R.id.codigo);
        txtdescricion = rootview.findViewById(R.id.descripcion);
        activo = rootview.findViewById(R.id.activo);
        inactivo = rootview.findViewById(R.id.inactivo);
        btnguardar = rootview.findViewById(R.id.btnguardar);
        btncancelar = rootview.findViewById(R.id.btncancelar);
        activo.setChecked(true);

        btnguardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!validarCampos())
                {
                    return;
                }
                else
                {

                    Categoria categoria = new Categoria();
                    categoria.setCodigo(txtcodigo.getEditText().getText().toString());
                    categoria.setDescripcion(txtdescricion.getEditText().getText().toString());
                    categoria.setEstado(getEstado());

                    agregarCategoria(categoria);
                }
            }
        });

        btncancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                limpiarControles();
            }
        });


        return rootview;

    }

    private void agregarCategoria(Categoria categoria)
    {


        Map<String,String> parametros= new HashMap<>();
        parametros.put("codigo",categoria.getCodigo());
        parametros.put("descripcion",categoria.getDescripcion());
        parametros.put("estado",String.valueOf(categoria.isEstado()));

        JSONObject parametroJson= new JSONObject(parametros);


        String uri="http://xally.somee.com/Xally/API/CategoriasWS/AddCategoria";
        JsonObjectRequest request= new JsonObjectRequest(Request.Method.POST, uri,parametroJson, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response)
            {
                try {

                    String mensaje = response.getString("Mensaje");
                    Boolean resultado = response.getBoolean("Resultado");

                    if(resultado)
                    {
                        Toast.makeText(rootview.getContext(),mensaje,Toast.LENGTH_SHORT).show();
                        limpiarControles();

                    }
                    else
                    {
                        Toast.makeText(rootview.getContext(),mensaje,Toast.LENGTH_SHORT).show();

                    }

                }catch (JSONException ex)
                {
                    Toast.makeText(rootview.getContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(rootview.getContext(), error.getMessage(), Toast.LENGTH_LONG ).show();
            }
        });

        VolleySingleton.getInstance(rootview.getContext()).addToRequestQueue(request);
    }


    private boolean getEstado()
    {

        if(activo.isChecked())
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    private boolean validarCampos()
    {
        boolean isValidate=true;

        String codigoInput = txtcodigo.getEditText().getText().toString().trim();
        String descripcionInput = txtdescricion.getEditText().getText().toString().trim();

        if(codigoInput.isEmpty())
        {
            isValidate=false;
            txtcodigo.setError("Código no puede estar vacio");

        }else if(codigoInput.length()>3)
        {
            isValidate=false;
            txtcodigo.setError("El codigo debe ser menor a 3 caracteres");

        }else
        {
            txtcodigo.setError(null);
        }

        if(descripcionInput.isEmpty())
        {
            isValidate=false;
            txtdescricion.setError("Descripción no puede estar vacia");

        }else
        {
            txtdescricion.setError(null);
        }

        return isValidate;
    }


    private void limpiarControles()
    {
        txtcodigo.getEditText().setText("");
        txtdescricion.getEditText().setText("");
        txtcodigo.requestFocus();
        activo.setChecked(true);
    }



}
