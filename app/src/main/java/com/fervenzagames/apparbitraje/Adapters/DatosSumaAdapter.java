package com.fervenzagames.apparbitraje.Adapters;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.fervenzagames.apparbitraje.Models.DatosSuma;
import com.fervenzagames.apparbitraje.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class DatosSumaAdapter extends ArrayAdapter {

    private Activity context;
    private List<DatosSuma> listaDatos;
    private String lado;

    public DatosSumaAdapter(Activity context, List<DatosSuma> listaDatos, String lado){
        super(context, R.layout.total_punt_arbi_mini_list, listaDatos);
        this.context = context;
        this.listaDatos = listaDatos;
        this.lado = lado;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        DatosSuma datos = getItem(position);

        convertView = LayoutInflater.from(context).inflate(R.layout.total_punt_arbi_mini_list, null);

        ImageView foto = convertView.findViewById(R.id.total_punt_arbi_foto);
        TextView suma = convertView.findViewById(R.id.total_punt_arbi_num);
        Button btn = convertView.findViewById(R.id.total_punt_arbi_detalleBtn);

        TextView dniText = convertView.findViewById(R.id.total_punt_arbi_dni);

        if(lado.equals("Rojo")){
            suma.setTextColor(context.getResources().getColor(R.color.colorRojo));
        } else if(lado.equals("Azul")){
            suma.setTextColor(context.getResources().getColor(R.color.colorAccent2));
        }

        Bundle extras = context.getIntent().getExtras();
        String dni = null;
        String idComp = null;
        try {
            //dni = extras.getString("dni");
            dni = datos.getDniJuez();
            idComp = extras.getString("idComp");
        } catch (NullPointerException e) {
            Log.v("DatosSumaAdapter", "Error al obtener los Extras");
            e.printStackTrace();
        }
        if(dni != null){
            try {
                dniText.setText(dni);
                if(!datos.getFotoJuez(dni).equals("default")) {
                    Picasso.get().load(datos.getFotoJuez(dni)).into(foto);
                }
            } catch (NullPointerException e) {
                Log.v("DatosSumaAdapter", "Error al obtener la Foto del Juez");
                e.printStackTrace();
            }
        }
        /*Picasso.get().load("https://firebasestorage.googleapis.com/v0/b/apparbitraje.appspot.com/o/profileImages%2FJSkce72ytyVGLV8mMI1j1SYTtVx1.jpg?alt=media&token=5128aa32-8c28-428a-9b24-ab07fc20f507")
                .into(foto);*/
        int s = datos.getSumaPuntos();
        suma.setText(String.valueOf(s));

        return convertView;
    }

    @Override
    public int getCount() {
        return listaDatos.size();
    }

    @Nullable
    @Override
    public DatosSuma getItem(int position) {
        return listaDatos.get(position);
    }
}
