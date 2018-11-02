package com.fervenzagames.apparbitraje.Adapters;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.fervenzagames.apparbitraje.Models.Competidores;
import com.fervenzagames.apparbitraje.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CompetidoresList extends ArrayAdapter<Competidores> {

    private Activity context;
    private List<Competidores> competidoresList;

    public CompetidoresList(Activity context, List<Competidores> lista){
        super(context, R.layout.competidor_single_layout, lista);
        this.context = context;
        this.competidoresList = lista;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        Competidores comp = getItem(position);

        convertView = LayoutInflater.from(context).inflate(R.layout.competidor_single_layout, null);

        CircleImageView foto = convertView.findViewById(R.id.comp_single_foto);
        TextView nombre = convertView.findViewById(R.id.comp_single_nombre);

        if(comp.getFoto() == "default"){
            Picasso.get().load(R.drawable.default_avatar).into(foto);
        }
        Picasso.get().load(comp.getFoto()).into(foto);
        nombre.setText(comp.getNombre() + " " + comp.getApellido1() + " " + comp.getApellido2());

        return convertView;

    }

    @Override
    public int getCount() {
        return competidoresList.size();
    }

    @Nullable
    @Override
    public Competidores getItem(int position) {
        return competidoresList.get(position);
    }
}
