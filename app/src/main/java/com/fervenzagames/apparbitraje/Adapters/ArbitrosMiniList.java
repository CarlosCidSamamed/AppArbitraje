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

import de.hdodenhof.circleimageview.CircleImageView;

public class ArbitrosMiniList extends ArrayAdapter<Arbitros> {

    private Activity context;
    private List<Arbitros> listArbis;

    public ArbitrosMiniList(Activity context, List<Arbitros> lista){
        super(context, R.layout.arbitro_single_layout_mini, lista);
        this.context = context;
        this.listArbis = lista;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        Arbitros arbi = getItem(position);

        convertView = LayoutInflater.from(context).inflate(R.layout.arbitro_single_layout_mini, null);

        CircleImageView foto = convertView.findViewById(R.id.arb_single_mini_foto);
        TextView nombre = convertView.findViewById(R.id.arb_single_mini_nombre);
        TextView zona = convertView.findViewById(R.id.arb_single_mini_zona);
        ImageView estado = convertView.findViewById(R.id.arb_single_mini_estado);

        if(arbi.getFoto().equals("default")){
            Picasso.get().load(R.drawable.default_avatar).into(foto);
        } else {
            Picasso.get().load(arbi.getFoto()).into(foto);
        }

        if(arbi.getConectado() == true){
            Picasso.get().load(R.drawable.online).into(estado);
        } else {
            Picasso.get().load(R.drawable.offline).into(estado);
        }

        nombre.setText(arbi.getNombre());
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

