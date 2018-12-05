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

public class MiSpinnerAdapter extends ArrayAdapter<String> {

    private Activity context;
    private List<String> lista;

    public MiSpinnerAdapter(Activity context, List<String> lista){
        super(context, R.layout.spinner_single_textview_dropdown_item, lista);
        this.context = context;
        this.lista = lista;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        String texto = getItem(position);

        convertView = LayoutInflater.from(context).inflate(R.layout.spinner_single_textview_dropdown_item, null);

        TextView textView = convertView.findViewById(R.id.texto);
        textView.setText(texto);

        return convertView;

    }

    @Override
    public int getCount() {
        return lista.size();
    }

    @Nullable
    @Override
    public String getItem(int position) {
        return lista.get(position);
    }
}
