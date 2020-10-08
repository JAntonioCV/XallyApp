package com.jantonioc.xalliapp.Adaptadores;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jantonioc.ln.Cliente;
import com.jantonioc.xalliapp.R;

import java.util.ArrayList;
import java.util.List;

public class ClientesAdapter extends RecyclerView.Adapter<ClientesAdapter.holder> implements View.OnClickListener, Filterable {

    private List<Cliente> lista;
    private List<Cliente> listafull;


    private View.OnClickListener ClickListener;

    public ClientesAdapter(List<Cliente> lista) {
        this.lista = lista;
        listafull=new ArrayList<>(lista);
    }

    @NonNull
    @Override
    public holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cliente_item,parent,false);
        view.setOnClickListener(this);
        return new holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull holder holder, int position) {
        holder.setcliente(lista.get(position));

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

    @Override
    public Filter getFilter() {
        return listaclientefilter;
    }

    private Filter listaclientefilter= new Filter()
    {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Cliente> filteredList=new ArrayList<>();
            if(constraint== null || constraint.length() == 0)
            {
                filteredList.addAll(listafull);
            }else
            {
                String filterPattern = constraint.toString().toUpperCase().trim();

                for(Cliente item : listafull)
                {
                    if(item.getNombre().toUpperCase().startsWith(filterPattern.toUpperCase()))
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

    class holder extends RecyclerView.ViewHolder
    {
        private final View itemView;

        public holder(@NonNull View itemView) {
            super(itemView);
            this.itemView=itemView;
        }

        private void setcliente(final Cliente obj)
        {
            TextView nombreapellido = itemView.findViewById(R.id.itemcliente);
            TextView identificacion = itemView.findViewById(R.id.itemidentificacion);

            identificacion.setText(obj.getIdentificacion());
            nombreapellido.setText(obj.getNombre() +" "+ obj.getApellido());
        }

    }

}
