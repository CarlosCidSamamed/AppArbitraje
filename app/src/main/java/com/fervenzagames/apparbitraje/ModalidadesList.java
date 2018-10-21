package com.fervenzagames.apparbitraje;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.fervenzagames.apparbitraje.Models.Modalidades;

import java.util.List;

public class ModalidadesList extends ArrayAdapter<Modalidades> {

    private Activity context;
    private List<Modalidades> modList;

    public ModalidadesList(Activity context, List<Modalidades> modList){
        super(context, R.layout.mod_single_layout, modList);
        this.context = context;
        this.modList = modList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        Modalidades mod = getItem(position);

        convertView = LayoutInflater.from(context).inflate(R.layout.mod_single_layout, null);

        TextView nombre = convertView.findViewById(R.id.mod_nombre);
        //TextView desc = convertView.findViewById(R.id.mod_descripcion);

        nombre.setText(mod.getNombre());
        //desc.setText(mod.getDescripcion());

        return convertView;
    }

    @Override
    public int getCount() {
        return modList.size();
    }

    @Nullable
    @Override
    public Modalidades getItem(int position) {
        return modList.get(position);
    }
}
