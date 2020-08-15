package com.jantonioc.xallyapp.Adaptadores;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jantonioc.ln.Cliente;
import com.jantonioc.xallyapp.R;

import java.util.List;

public class ClientesAdapter extends RecyclerView.Adapter<ClientesAdapter.holder> implements View.OnClickListener{

    private List<Cliente> lista;

    private View.OnClickListener ClickListener;

    public ClientesAdapter(List<Cliente> lista) {
        this.lista = lista;
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
