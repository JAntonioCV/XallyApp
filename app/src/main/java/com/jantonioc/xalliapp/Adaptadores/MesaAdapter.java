package com.jantonioc.xalliapp.Adaptadores;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jantonioc.ln.Cliente;
import com.jantonioc.ln.Mesa;
import com.jantonioc.xalliapp.FragmentsOrdenes.Mesas;
import com.jantonioc.xalliapp.R;

import java.util.List;

public class MesaAdapter extends RecyclerView.Adapter<MesaAdapter.holder> implements View.OnClickListener {

    private List<Mesa> lista;
    private View.OnClickListener ClickListener;

    public MesaAdapter(List<Mesa> lista) {
        this.lista = lista;
    }

    @NonNull
    @Override
    public holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.mesa_item,parent,false);
        view.setOnClickListener(this);
        return new MesaAdapter.holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull holder holder, int position) {
        holder.setmesa(lista.get(position));

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
            this.itemView=itemView;
        }

        private void setmesa(final Mesa obj)
        {
            TextView descripcionmesa = itemView.findViewById(R.id.itemmesa);
            descripcionmesa.setText(obj.getDescripcion());
        }

    }

}
