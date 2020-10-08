package com.jantonioc.xalliapp.Adaptadores;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jantonioc.ln.DetalleDeOrden;
import com.jantonioc.xalliapp.R;

import java.text.DecimalFormat;
import java.util.List;

public class DetalleCuentasAdapter extends RecyclerView.Adapter<DetalleCuentasAdapter.holder> implements View.OnClickListener {

    private List<DetalleDeOrden> lista;

    private View.OnClickListener ClickListener;

    public DetalleCuentasAdapter(List<DetalleDeOrden> lista) {
        this.lista = lista;
    }


    @NonNull
    @Override
    public holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.detalle_orden_cuenta, parent, false);

        view.setOnClickListener(this);

        return new holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull holder holder, int position) {
            holder.setDetalleOrden(lista.get(position));
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    public void setClickListener(View.OnClickListener listener) {
        this.ClickListener = listener;
    }

    @Override
    public void onClick(View v) {
        if (ClickListener != null) {
            ClickListener.onClick(v);
        }
    }

    class holder extends RecyclerView.ViewHolder {
        private final View itemView;

        public holder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
        }

        private void setDetalleOrden(final DetalleDeOrden detalleOrden) {


                TextView nombrePlatillo = itemView.findViewById(R.id.itemplatillo);
                TextView cantidad = itemView.findViewById(R.id.itemcantidad);
                TextView precio = itemView.findViewById(R.id.itemprecio);
                TextView pretotal = itemView.findViewById(R.id.itempretotal);

                nombrePlatillo.setText(detalleOrden.getNombreplatillo());

                cantidad.setText("Cantidad: " + Integer.valueOf(detalleOrden.getCantidad()).toString());

                precio.setText("Precio: $" + Double.valueOf(detalleOrden.getPrecio()).toString());

                pretotal.setText("Pretotal: $" + preTotal(detalleOrden.getPrecio(), detalleOrden.getCantidad()));
        }

        private String preTotal(double precio, int cantidad) {
            double valor;
            DecimalFormat format = new DecimalFormat();
            format.setMaximumFractionDigits(2);

            valor = precio * cantidad;

            return format.format(valor);
        }

    }

}