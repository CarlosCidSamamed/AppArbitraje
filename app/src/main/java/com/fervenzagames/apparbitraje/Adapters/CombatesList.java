package com.fervenzagames.apparbitraje.Adapters;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.fervenzagames.apparbitraje.Models.Combates;
import com.fervenzagames.apparbitraje.R;

import java.util.List;

public class CombatesList extends ArrayAdapter<Combates> {

    private Activity context;
    private List<Combates> combatesList;

    public CombatesList(Activity context, List<Combates> combatesList){
        super(context, R.layout.combate_single_layout, combatesList);
        this.context = context;
        this.combatesList = combatesList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        Combates comb = getItem(position);

        convertView = LayoutInflater.from(context).inflate(R.layout.combate_single_layout, null);

        TextView modalidad = convertView.findViewById(R.id.modalidadCombate);
        TextView numero = convertView.findViewById(R.id.numCombate);
        TextView categoria = convertView.findViewById(R.id.categoriaCombate);
        TextView ganador = convertView.findViewById(R.id.ganadorCombate);
        TextView motivo = convertView.findViewById(R.id.motivoCombate);
        TextView estado = convertView.findViewById(R.id.estadoCombate);

        modalidad.setText(comb.getModalidad());
        numero.setText(comb.getNumCombate());
        categoria.setText(comb.getCategoria());
        ganador.setText(comb.getGanador());
        motivo.setText(comb.getMotivo());
        String est = "";
        if(comb.getEstadoCombate() != null) {
            est = comb.estadoToString(comb.getEstadoCombate());
        } else {
            est = "Estado no especificado...";
        }
        estado.setText(est);

        return convertView;

    }

    @Override
    public int getCount() {
        return combatesList.size();
    }

    @Nullable
    @Override
    public Combates getItem(int position) {
        return combatesList.get(position);
    }
}
