package com.fervenzagames.apparbitraje;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;

public class CampeonatosList extends ArrayAdapter<Campeonatos> {

    private Activity context;
    private List<Campeonatos> campList;

    public CampeonatosList (Activity context, List<Campeonatos> campList){
        super(context, R.layout.camp_single_layout, campList);
        this.context = context;
        this.campList = campList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
/*        LayoutInflater inflater = context.getLayoutInflater();
        View listViewItem = inflater.inflate(R.layout.camp_single_layout, null, true);
        TextView nombre = (TextView) context.findViewById(R.id.camp_nombre);
        TextView lugar = (TextView) context.findViewById(R.id.camp_lugar);
        TextView fecha = (TextView) context.findViewById(R.id.camp_fecha);

        Campeonatos camp = campList.get(position);
        if (camp != null) {
            nombre.setText(camp.getNombre());
            lugar.setText(camp.getLugar());
            fecha.setText(camp.getFecha());
        }

        return listViewItem;*/

        Campeonatos camp = getItem(position);

        convertView = LayoutInflater.from(context).inflate(R.layout.camp_single_layout, null);

        TextView nombre = convertView.findViewById(R.id.camp_nombre);
        TextView lugar = convertView.findViewById(R.id.camp_lugar);
        TextView fecha = convertView.findViewById(R.id.camp_fecha);

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
