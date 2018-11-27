package com.fervenzagames.apparbitraje.Adapters;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.fervenzagames.apparbitraje.R;

import java.util.List;

public class ZonasCombateList extends ArrayAdapter<String> {

    private Activity context;
    private String[] listaZonas;

    public ZonasCombateList(Activity context, String[] listaZonas){
        super(context, R.layout.zona_combate_single_layout, listaZonas);
        this.context = context;
        this.listaZonas = listaZonas;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        String zona = getItem(position);

        convertView = LayoutInflater.from(context).inflate(R.layout.zona_combate_single_layout, null);

        TextView numZona = convertView.findViewById(R.id.zonaCombate_numero);
        numZona.setText(zona);

        return convertView;
    }

    @Override
    public int getCount() {
        return listaZonas.length;
    }

    @Nullable
    @Override
    public String getItem(int position) {
        return listaZonas[position];
    }
}
