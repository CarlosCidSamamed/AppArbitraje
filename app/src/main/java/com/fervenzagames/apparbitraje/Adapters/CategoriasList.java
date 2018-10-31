package com.fervenzagames.apparbitraje.Adapters;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.fervenzagames.apparbitraje.Models.Categorias;
import com.fervenzagames.apparbitraje.R;

import java.util.List;
import java.util.zip.Inflater;

public class CategoriasList extends ArrayAdapter<Categorias> {

    private Activity context;
    private List<Categorias> catList;

    public CategoriasList(Activity context, List<Categorias> catList){
        super(context, R.layout.cat_single_layout, catList);
        this.context = context;
        this.catList = catList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        Categorias cat = getItem(position);

        convertView = LayoutInflater.from(context).inflate(R.layout.cat_single_layout, null);

        TextView nombre = convertView.findViewById(R.id.cat_nombre);
/*        TextView edad = convertView.findViewById(R.id.cat_edad);
        TextView sexo = convertView.findViewById(R.id.cat_sexo);
        TextView peso = convertView.findViewById(R.id.cat_peso);*/

        nombre.setText(cat.getNombre());
/*        edad.setText(cat.getEdad());
        sexo.setText(cat.getSexo());
        peso.setText(cat.getPeso());*/

        return convertView;
    }

    @Override
    public int getCount() { return catList.size(); }

    @Nullable
    @Override
    public Categorias getItem(int position) { return catList.get(position); }

    // Método para actualizar la lista de Categorias una vez que ya se ha creado el Adapter.
    public void actualizarCategorias(List<Categorias> cats){
        this.catList.clear();
        this.catList.addAll(catList);
        notifyDataSetChanged();
    }
}
