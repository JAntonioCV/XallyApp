package com.jantonioc.xallyapp.Adaptadores;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jantonioc.ln.Categoria;
import com.jantonioc.ln.Menu;
import com.jantonioc.xallyapp.R;

import java.util.ArrayList;
import java.util.List;

public class CategoriaAdapter extends RecyclerView.Adapter<CategoriaAdapter.holder> implements Filterable {

    private final List<Categoria> lista;
    private final List<Categoria> listafull;
    private final Evento evt;

    public CategoriaAdapter(List<Categoria> lista, Evento evt) {
        this.lista = lista;
        listafull = new ArrayList<>(lista);
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

    @Override
    public Filter getFilter() {
        return listacategoriafilter;
    }

    private Filter listacategoriafilter= new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Categoria> filteredList=new ArrayList<>();
            if(constraint== null || constraint.length() == 0)
            {
                filteredList.addAll(listafull);
            }else
            {
                String filterPattern = constraint.toString().toUpperCase().trim();

                for(Categoria item : listafull)
                {
                    if(item.getDescripcion().toUpperCase().startsWith(filterPattern.toUpperCase()))
                    {
                        filteredList.add(item);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            lista.clear();
            lista.addAll((List)results.values);
            notifyDataSetChanged();

        }
    };

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
