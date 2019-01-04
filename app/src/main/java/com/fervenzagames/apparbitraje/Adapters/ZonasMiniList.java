package com.fervenzagames.apparbitraje.Adapters;

import android.app.Activity;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.fervenzagames.apparbitraje.Models.ZonasCombate;
import com.fervenzagames.apparbitraje.R;

import java.util.List;

public class ZonasMiniList extends ArrayAdapter {

    private Activity context;
    private List<ZonasCombate> listaZonas;

    public ZonasMiniList(Activity context, List<ZonasCombate> listaZonas){
        super(context, R.layout.zona_layout_mini, listaZonas);
        this.context = context;
        this.listaZonas = listaZonas;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        ZonasCombate zona = getItem(position);

        convertView = LayoutInflater.from(context).inflate(R.layout.zona_layout_mini, null);

        TextView numero = convertView.findViewById(R.id.zona_mini_numero);
        String num = String.valueOf(zona.getNumZona());
        numero.setTypeface(numero.getTypeface(), Typeface.BOLD);
        numero.setText(num);

        TextView numCombates = convertView.findViewById(R.id.zona_mini_numCombates);
        String combates = String.valueOf(zona.getListaDatosExtraCombates().size());
        String comb = " Combates : " + combates;
        numCombates.setText(comb);

        return convertView;
    }

    @Override
    public int getCount() {
        return listaZonas.size();
    }

    @Nullable
    @Override
    public ZonasCombate getItem(int position) {
        return listaZonas.get(position);
    }
}
