package com.jantonioc.xallyapp.Adaptadores;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jantonioc.ln.Orden;
import com.jantonioc.xallyapp.R;

import java.util.List;

public class PedidosAdapter extends RecyclerView.Adapter<PedidosAdapter.holder> implements View.OnClickListener {

    private List<Orden> lista;

    private View.OnClickListener ClickListener;

    public PedidosAdapter(List<Orden> lista) {
        this.lista = lista;
    }

    @NonNull
    @Override
    public holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pedidos_item,parent,false);

        view.setOnClickListener(this);

        return new holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull holder holder, int position) {
        holder.setOrden(lista.get(position));

    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    public void setClickListener(View.OnClickListener listener)
    {
        this.ClickListener = listener;
    }

    @Override
    public void onClick(View v) {

        if(ClickListener!=null)
        {
            ClickListener.onClick(v);
        }
    }

    class holder extends RecyclerView.ViewHolder
    {
        private final View itemView;

        public holder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
        }

        private void setOrden(final Orden obj)
        {
            TextView codigo = itemView.findViewById(R.id.itemcodigo);
            TextView fecha = itemView.findViewById(R.id.itemfecha);
            TextView hora = itemView.findViewById(R.id.itemhora);
            TextView estado = itemView.findViewById(R.id.itemestado);
            TextView cliente = itemView.findViewById(R.id.itemcliente);
            TextView mesero = itemView.findViewById(R.id.itemmesero);

            codigo.setText("Codigo: " + obj.getCodigo());
            fecha.setText("Fecha: " + obj.getFechaorden());
            hora.setText("Hora: " + obj.getTiempoorden());
            estado.setText("Estado: "+Obtenerestado(obj.getEstado()));

            cliente.setText("Cliente: " + OBtenerTipoCliente(obj.getCliente()) );

            mesero.setText("Mesero:"+ obj.getMesero());

        }

        private String OBtenerTipoCliente(String cliente)
        {
            return cliente.equals("DEFAULT USER") ? "Visitante" : cliente;
        }

        private String Obtenerestado(int estado)
        {
            String estados;

            switch (estado)
            {
                case 1:
                    estados="Ordenado";
                    break;

                case 2:
                    estados="Sin Facturar";
                    break;

                case 3:
                    estados="Facturada";
                    break;

                    default:
                        estados = "sin estado";
                        break;

            }

            return estados;
        }

    }






}
