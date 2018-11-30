package com.fervenzagames.apparbitraje.Adapters;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.fervenzagames.apparbitraje.Models.Campeonatos;
import com.fervenzagames.apparbitraje.R;

import java.util.List;

public class CampeonatosMiniList extends ArrayAdapter<Campeonatos> {

    private Activity context;
    private List<Campeonatos> campList;

    public CampeonatosMiniList (Activity context, List<Campeonatos> campList){
        super(context, R.layout.camp_single_layout, campList);
        this.context = context;
        this.campList = campList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        Campeonatos camp = getItem(position);

        convertView = LayoutInflater.from(context).inflate(R.layout.camp_single_layout_mini, null);

        TextView nombre = convertView.findViewById(R.id.camp_nombre_mini);
        TextView lugar = convertView.findViewById(R.id.camp_lugar_mini);
        TextView fecha = convertView.findViewById(R.id.camp_fecha_mini);

        nombre.setText(camp.getNombre());
        lugar.setText(camp.getLugar());
        fecha.setText(camp.getFecha());

        return convertView;
    }

    @Override
    public int getCount() {
        return campList.size();
    }

    @Nullable
    @Override
    public Campeonatos getItem(int position) {
        return campList.get(position);
    }
}
