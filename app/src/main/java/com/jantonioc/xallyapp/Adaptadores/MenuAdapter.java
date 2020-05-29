package com.jantonioc.xallyapp.Adaptadores;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.Constraints;
import androidx.recyclerview.widget.RecyclerView;


import com.jantonioc.ln.Menu;
import com.jantonioc.xallyapp.R;

import java.util.ArrayList;
import java.util.List;

public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.holder> implements Filterable, View.OnClickListener, View.OnLongClickListener {

    private final List<Menu> lista;
    List<Menu> listafull;

    private View.OnClickListener ClickListener;
    private View.OnLongClickListener LongClickListener;


    public MenuAdapter(List<Menu> lista) {
        this.lista = lista;
        listafull=new ArrayList<>(lista);
    }


    @NonNull
    @Override
    public holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.menu_item,parent,false);

        view.setOnClickListener(this);

        view.setOnLongClickListener(this);

        return new holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull holder holder, int position) {
        holder.setMenu(lista.get(position));

    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    @Override
    public Filter getFilter() {
        return listamenufilter;
    }

    private Filter listamenufilter= new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Menu> filteredList=new ArrayList<>();
            if(constraint== null || constraint.length() == 0)
            {
                filteredList.addAll(listafull);
            }else
            {
                String filterPattern = constraint.toString().toUpperCase().trim();

                for(Menu item : listafull)
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


     public void setClickListener(View.OnClickListener listener)
     {
         this.ClickListener = listener;
     }

     public void setLongClickListener(View.OnLongClickListener listener)
     {
         this.LongClickListener = listener;
     }



    @Override
    public void onClick(View v) {

         if(ClickListener!=null)
         {
             ClickListener.onClick(v);
         }
    }

    @Override
    public boolean onLongClick(View v) {
        if(LongClickListener!=null)
        {
            LongClickListener.onLongClick(v);
            return true;
        }

        return false;
    }


    class holder extends RecyclerView.ViewHolder
    {
        private final View itemView;

        public holder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
        }


        private void setMenu(final Menu obj)
        {

            TextView nombre =itemView.findViewById(R.id.itemnombre);
            TextView precio =itemView.findViewById(R.id.itemprecio);
            nombre.setText(obj.getDescripcion());
            precio.setText("Precio: $"+String.valueOf(obj.getPrecio()));
        }

    }
}
