package com.fervenzagames.apparbitraje.Adapters;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.fervenzagames.apparbitraje.Models.Arbitros;
import com.fervenzagames.apparbitraje.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ArbitrosList extends ArrayAdapter<Arbitros> {

    private Activity context;
    private List<Arbitros> listArbis;

    public ArbitrosList(Activity context, List<Arbitros> lista){
        super(context, R.layout.arbitro_single_layout, lista);
        this.context = context;
        this.listArbis = lista;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        Arbitros arbi = getItem(position);

        convertView = LayoutInflater.from(context).inflate(R.layout.arbitro_single_layout, null);

        ImageView foto = convertView.findViewById(R.id.arbitro_single_foto);
        ImageView estado = convertView.findViewById(R.id.arbitro_single_conectado);
        TextView nombre = convertView.findViewById(R.id.arbitro_single_nombre);
        TextView nivel = convertView.findViewById(R.id.arbitro_single_nivel);
        TextView cargo = convertView.findViewById(R.id.arbitro_single_cargo);
        TextView zona = convertView.findViewById(R.id.arbitro_single_zonaCombate);

        if(arbi.getFoto().equals("default")){
            Picasso.get().load(R.drawable.default_avatar).into(foto);
        } else {
            Picasso.get().load(arbi.getFoto()).into(foto);
        }

        if(arbi.getConectado() == true){
            // Picasso.get().load(R.drawable.punto_verde).into(estado);
            estado.setImageResource(R.drawable.punto_verde);
        } else {
            //Picasso.get().load(R.drawable.punto_rojo).into(estado);
            estado.setImageResource(R.drawable.punto_rojo);
        }

        nombre.setText(arbi.getNombre());
        nivel.setText(arbi.getNivel());
        cargo.setText(arbi.getCargo());
        String z = String.valueOf(arbi.getZonaCombate());
        zona.setText("Zona de Combate:  " + z);

        return convertView;

    }

    @Override
    public int getCount() {
        return listArbis.size();
    }

    @Nullable
    @Override
    public Arbitros getItem(int position) {
        return listArbis.get(position);
    }
}
