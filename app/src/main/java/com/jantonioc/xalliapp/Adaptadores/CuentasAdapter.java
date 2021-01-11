package com.jantonioc.xalliapp.Adaptadores;

import android.content.Context;
import android.content.DialogInterface;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.textfield.TextInputLayout;
import com.jantonioc.ln.DetalleDeOrden;
import com.jantonioc.xalliapp.FragmentsCuenta.DetallesCuenta;
import com.jantonioc.xalliapp.FragmentsCuenta.IObserverItem;
import com.jantonioc.xalliapp.MainActivity;
import com.jantonioc.xalliapp.R;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;

public class CuentasAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List<String> listaclientes;
    private HashMap<String,List<DetalleDeOrden>> detallesDeOrden;

    private TextInputLayout txtcantidad;

    private TextView platillotxt;
    private TextView cantidadtxt;
    private TextView titulotxt;

    private Button btneliminar;

    public static IObserverItem itemTamaño;

    double total=0;

    private boolean abierto = false;

    public CuentasAdapter(Context context, List<String> listaclientes, HashMap<String, List<DetalleDeOrden>> detallesDeOrden) {
        this.context = context;
        this.listaclientes = listaclientes;
        this.detallesDeOrden = detallesDeOrden;
    }

    @Override
    public int getGroupCount() {
        return this.listaclientes.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this.detallesDeOrden.get(this.listaclientes.get(groupPosition))==null ? 0 : this.detallesDeOrden.get(this.listaclientes.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.listaclientes.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return this.detallesDeOrden.get(this.listaclientes.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(final int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

        String cliente = (String)  getGroup(groupPosition);

        if(convertView == null)
        {
            LayoutInflater layoutInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.listacliente,null);
        }

        TextView clientetxt = convertView.findViewById(R.id.cliente);
        TextView total = convertView.findViewById(R.id.total);

        clientetxt.setText(cliente);
        total.setText("Total: $"+total(groupPosition));

        ImageButton agregar = convertView.findViewById(R.id.agregar);

        agregar.setFocusable(false);

        agregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Abrir el fragmento del detalle de los platillos
                Fragment fragment = new DetallesCuenta();
                //Pasar parametros entre fragment
                Bundle bundle = new Bundle();
                bundle.putInt("groupPosition",groupPosition);
                fragment.setArguments(bundle);
                FragmentTransaction transaction = ((MainActivity)context).getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.content, fragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        return convertView;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        final DetalleDeOrden detalleDeOrden = (DetalleDeOrden) getChild(groupPosition,childPosition);

        if(convertView == null)
        {
            LayoutInflater layoutInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.listaproducto,null);
        }

        TextView nombreplatillo = convertView.findViewById(R.id.itemplatillo);
        TextView cantidad = convertView.findViewById(R.id.itemcantidad);
        TextView precio = convertView.findViewById(R.id.itemprecio);
        TextView total = convertView.findViewById(R.id.itempretotal);
        ImageButton borrar = convertView.findViewById(R.id.borrar);

        nombreplatillo.setText(detalleDeOrden.getNombreplatillo());
        cantidad.setText("Cantidad: "+String.valueOf(detalleDeOrden.getCantidad()));
        precio.setText("Precio: $"+String.valueOf(detalleDeOrden.getPrecio()));
        total.setText("Pretotal: $"+preTotal(detalleDeOrden.getPrecio(),detalleDeOrden.getCantidad()));

        borrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                eliminar(detalleDeOrden,groupPosition,childPosition);

            }
        });

        return convertView;

    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    private String preTotal(double precio,int cantidad)
    {
        double valor;
        DecimalFormat format = new DecimalFormat();
        format.setMaximumFractionDigits(2);

        valor=precio*cantidad;

        return format.format(valor);
    }

    private String total(int position)
    {
        total = 0;

        DecimalFormat format = new DecimalFormat();
        format.setMaximumFractionDigits(2);

        for (DetalleDeOrden detalleActual : MainActivity.listadetalles.get(position))
        {
            total = total + (detalleActual.getCantidad() * detalleActual.getPrecio());
        }

        return format.format(total);
    }


    private void eliminar(final DetalleDeOrden obj, final int gposition, final int cposition)
    {
        if(!abierto)
        {
            abierto = true;
            //Abrimos la modal agregar el nuevo detalle de orden
            final AlertDialog builder = new AlertDialog.Builder(context).create();

            LayoutInflater layoutInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View view =  layoutInflater.inflate(R.layout.detalle_cuenta, null);

            platillotxt = view.findViewById(R.id.nombreplatillo);
            cantidadtxt = view.findViewById(R.id.cantidadpedido);
            txtcantidad = view.findViewById(R.id.cantidad);
            btneliminar = view.findViewById(R.id.btnagregar);
            titulotxt = view.findViewById(R.id.textView2);


            titulotxt.setText("Eliminar del cliente");
            platillotxt.setText(obj.getNombreplatillo());
            cantidadtxt.setText(String.valueOf(obj.getCantidad()));
            txtcantidad.getEditText().setText("1");
            btneliminar.setText("Eliminar");

            //restar cantidad
            txtcantidad.setStartIconOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(!txtcantidad.getEditText().getText().toString().isEmpty())
                    {
                        int numero = Integer.valueOf(txtcantidad.getEditText().getText().toString());

                        if(numero<=1)
                        {
                            txtcantidad.setError("La cantidad no puede ser menor a 1");
                            return;
                        }else
                        {
                            numero--;
                            txtcantidad.getEditText().setText(String.valueOf(numero));
                            txtcantidad.setError(null);
                        }
                    }
                    else
                    {
                        txtcantidad.setError("Ingrese una cantidad");
                    }
                }
            });

            //sumar cantidad
            txtcantidad.setEndIconOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(!txtcantidad.getEditText().getText().toString().isEmpty())
                    {
                        int numero = Integer.valueOf(txtcantidad.getEditText().getText().toString());

                        if(numero<obj.getCantidad())
                        {
                            numero++;
                            txtcantidad.getEditText().setText(String.valueOf(numero));
                            txtcantidad.setError(null);
                        }
                        else
                        {
                            txtcantidad.setError("La cantidad no puede ser mayor a la exitencia");
                        }
                    }
                    else
                    {
                        txtcantidad.setError("Ingrese una cantidad");
                    }
                }
            });

            btneliminar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(!validarCampos())
                    {
                        return;
                    }
                    else
                    {
                        DetalleDeOrden detalleDeOrden= new DetalleDeOrden();

                        detalleDeOrden.setMenuid(MainActivity.listadetalles.get(gposition).get(cposition).getMenuid());
                        detalleDeOrden.setPrecio(MainActivity.listadetalles.get(gposition).get(cposition).getPrecio());
                        detalleDeOrden.setNombreplatillo(MainActivity.listadetalles.get(gposition).get(cposition).getNombreplatillo());
                        detalleDeOrden.setCantidad(Integer.valueOf(txtcantidad.getEditText().getText().toString()));

                        if(!yaExiste(detalleDeOrden,gposition))
                        {
                            MainActivity.listadetalle.add(detalleDeOrden);
                        }

                        MainActivity.listadetalles.get(gposition).get(cposition).setCantidad(MainActivity.listadetalles.get(gposition).get(cposition).getCantidad()-Integer.valueOf(txtcantidad.getEditText().getText().toString()));

                        if(MainActivity.listadetalles.get(gposition).get(cposition).getCantidad()==0)
                        {
                            //remover el item de la lista
                            Toast.makeText(context, "Eliminado del cliente", Toast.LENGTH_SHORT).show();
                            MainActivity.listadetalles.get(gposition).remove(cposition);
                            itemTamaño.tamaño(MainActivity.listadetalle.size());
                        }
                        else
                        {
                            Toast.makeText(context, "Eliminado del cliente", Toast.LENGTH_SHORT).show();
                        }

                        notifyDataSetChanged();

                        builder.cancel();
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

    //Validando que no esten los campos vacios
    private boolean validarCampos() {
        boolean isValidate = true;

        String cantidadInput = txtcantidad.getEditText().getText().toString().trim();

        if (cantidadInput.isEmpty()) {
            isValidate = false;
            txtcantidad.setError("Cantidad no puede estar vacio");

        } else if (Integer.valueOf(cantidadInput) <= 0) {
            isValidate = false;
            txtcantidad.setError("La cantidad no puede ser menor a 1");

        } else if (Integer.valueOf(cantidadInput) > Integer.valueOf(cantidadtxt.getText().toString())) {
            isValidate = false;
            txtcantidad.setError("La cantidad no puede ser mayor a lo selecionado");
        } else {
            txtcantidad.setError(null);
        }

        return isValidate;
    }

    //Validar el tipo de orden modificar || nueva
    private boolean yaExiste( DetalleDeOrden obj, int gposition) {

        for (DetalleDeOrden detalleActual : MainActivity.listadetalle) {
            if (obj.getMenuid() == detalleActual.getMenuid()) {
                detalleActual.setCantidad(obj.getCantidad()+detalleActual.getCantidad());
                return true;
            }
        }
        return false;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
        super.registerDataSetObserver(observer);
    }


}
