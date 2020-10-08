package com.jantonioc.xalliapp.Adaptadores;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.jantonioc.ln.Pago;

import java.util.List;

public class AdapterCuentaIgual extends BaseAdapter {

    private Context context;
    private List<Pago> pagos;

    public AdapterCuentaIgual(Context context, List<Pago> pagos) {
        this.context = context;
        this.pagos = pagos;
    }

    @Override
    public int getCount() {
        return pagos.size();
    }

    @Override
    public Object getItem(int position) {
        return pagos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = convertView;

        if (view == null)
        {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(android.R.layout.simple_list_item_2,null);
        }

        TextView txt1 = view.findViewById(android.R.id.text1);
        TextView txt2 = view.findViewById(android.R.id.text2);

        txt1.setText(pagos.get(position).getCliente());
        txt2.setText("$:"+pagos.get(position).getPago());

        return view;
    }
}
