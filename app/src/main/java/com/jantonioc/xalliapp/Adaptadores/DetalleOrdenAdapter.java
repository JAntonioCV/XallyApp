package com.jantonioc.xalliapp.Adaptadores;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jantonioc.ln.DetalleDeOrden;
import com.jantonioc.xalliapp.R;

import java.text.DecimalFormat;
import java.util.List;

public class DetalleOrdenAdapter extends RecyclerView.Adapter<DetalleOrdenAdapter.holder> implements View.OnClickListener {

    private List<DetalleDeOrden> lista;

    private View.OnClickListener ClickListener;

    public DetalleOrdenAdapter(List<DetalleDeOrden> lista) {
        this.lista = lista;
    }

    @NonNull
    @Override
    public holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.detalle_orden_item, parent, false);

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

            View statusIndicator = itemView.findViewById(R.id.indicator_appointment_status);
            TextView nombrePlatillo = itemView.findViewById(R.id.itemplatillo);
            TextView nota = itemView.findViewById(R.id.itemnota);
            TextView cantidad = itemView.findViewById(R.id.itemcantidad);
            TextView precio = itemView.findViewById(R.id.itemprecio);
            TextView pretotal = itemView.findViewById(R.id.itempretotal);
            TextView estado = itemView.findViewById(R.id.itemestado);

            statusIndicator.setBackgroundColor(estadoindicator(detalleOrden.getEstado(),detalleOrden.getFromservice()));

            nombrePlatillo.setText(detalleOrden.getNombreplatillo());

            nota.setText("Nota: "+obtenerNota(detalleOrden.getNota()));

            estado.setText("Estado: "+obtenerEstado(detalleOrden.getEstado(),detalleOrden.getFromservice()));

            cantidad.setText("Cantidad: "+Integer.valueOf(detalleOrden.getCantidad()).toString());

            precio.setText("Precio: $"+Double.valueOf(detalleOrden.getPrecio()).toString());

            pretotal.setText("Pretotal: $"+ preTotal(detalleOrden.getPrecio(),detalleOrden.getCantidad()));
        }

        private String preTotal(double precio,int cantidad)
        {
            double valor;
            DecimalFormat format = new DecimalFormat();
            format.setMaximumFractionDigits(2);

            valor=precio*cantidad;

            return format.format(valor);
        }

        private String obtenerNota(String nota)
        {
            if(nota.isEmpty() || nota.equals("null"))
            {
                return "Sin nota";
            }

            return nota.equals("Sin nota") ? "Sin nota" : nota;
        }

        private String obtenerEstado(boolean estado,boolean fromservice)
        {
            if(estado == false && fromservice == false)
            {
                return "Sin Enviar";
            }
            else if(estado == false && fromservice == true )
            {
                return "Ordenado";
            }

            return "Atendido";
        }

        private int estadoindicator(boolean estado, boolean fromservice)
        {
            if(estado == false && fromservice == false)
            {
                return Color.YELLOW;
            }
            else if(estado == false && fromservice == true )
            {
                return Color.GREEN;
            }

            return Color.GRAY;
        }


    }


}
