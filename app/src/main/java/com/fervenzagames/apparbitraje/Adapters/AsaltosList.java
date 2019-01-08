package com.fervenzagames.apparbitraje.Adapters;

import android.app.Activity;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.fervenzagames.apparbitraje.Models.Asaltos;
import com.fervenzagames.apparbitraje.R;

import java.util.List;

public class AsaltosList extends ArrayAdapter<Asaltos> {

    private Activity context;
    private List<Asaltos> lista;

    public AsaltosList(Activity context, List<Asaltos> lista){
        super(context, R.layout.asalto_single_layout, lista);
        this.context = context;
        this.lista = lista;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        Asaltos asalto = getItem(position);

        convertView = LayoutInflater.from(context).inflate(R.layout.asalto_single_layout, null);

        TextView numero = convertView.findViewById(R.id.asalto_single_numero);
        TextView estado = convertView.findViewById(R.id.asalto_single_estado);
        TextView ganador = convertView.findViewById(R.id.asalto_single_ganador);

        try {
            String num = String.valueOf(asalto.getNumAsalto());
            numero.setText(num);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        String est = asalto.estadoToString(asalto.getEstado());
        estado.setText(est);
        ganador.setText(asalto.getGanador());

        return  convertView;

    }

    @Override
    public int getCount() {
        return lista.size();
    }

    @Nullable
    @Override
    public Asaltos getItem(int position) {
        return lista.get(position);
    }
}
