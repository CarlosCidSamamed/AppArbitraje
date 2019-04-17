package com.fervenzagames.apparbitraje.Adapters;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.fervenzagames.apparbitraje.Models.Puntuaciones;
import com.fervenzagames.apparbitraje.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class PuntuacionesAdapter extends ArrayAdapter {

    private Activity context;
    private List<Puntuaciones> listaPunts;
    private String lado;
    private DatabaseReference mConsultaDB;

    public PuntuacionesAdapter(Activity context, List<Puntuaciones> listaPunts, String lado){
        super(context, R.layout.puntuacion_single_layout_mini, listaPunts);
        this.context = context;
        this.listaPunts = listaPunts;
        this.lado = lado;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Puntuaciones punt = getItem(position);

        convertView = LayoutInflater.from(context).inflate(R.layout.puntuacion_single_layout_mini, null);

        TextView tipoAtaqueText = convertView.findViewById(R.id.punt_single_mini_tipoAtaqueText);
        TextView valorText = convertView.findViewById(R.id.punt_single_mini_valorText);

        // Modificar el color de la letra según sea el color del competidor que suma puntos
        if(lado.equals("Rojo")){

            valorText.setTextColor(context.getResources().getColor(R.color.colorRojo));
        } else if(lado.equals("Azul")){
            valorText.setTextColor(context.getResources().getColor(R.color.colorAccent2));
        }


        tipoAtaqueText.setText(punt.getTipoAtaque());
        String v = String.valueOf(punt.getValor());
        valorText.setText(v);

        return convertView;
    }

    @Override
    public int getCount() {
        return listaPunts.size();
    }

    @Nullable
    @Override
    public Puntuaciones getItem(int position) {
        return listaPunts.get(position);
    }
}
