package com.jantonioc.xallyapp.Adaptadores;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jantonioc.ln.Categoria;
import com.jantonioc.xallyapp.R;

import java.util.List;

public class CategoriaAdapter extends RecyclerView.Adapter<CategoriaAdapter.holder> {

    private final List<Categoria> lista;
    private final Evento evt;

    public CategoriaAdapter(List<Categoria> lista, Evento evt) {
        this.lista = lista;
        this.evt = evt;
    }


    @NonNull
    @Override
    public holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new holder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.categoria_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull holder holder, int position) {
        holder.setcategoria(lista.get(position),evt);

    }


    @Override
    public int getItemCount() {
        return lista.size();
    }

    public interface Evento
    {
        void selecionar(Categoria obj);

    }

    class holder extends RecyclerView.ViewHolder
    {
        private final View itemView;

        public holder(@NonNull View itemView) {
            super(itemView);
            this.itemView=itemView;
        }

        private void setcategoria(final Categoria obj, final Evento evt)
        {
            TextView textView=itemView.findViewById(R.id.itemcategoria);
            textView.setText(obj.getDescripcion());
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    evt.selecionar(obj);
                }
            });

        }

    }
}
