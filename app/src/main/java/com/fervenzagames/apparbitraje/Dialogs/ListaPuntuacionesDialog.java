package com.fervenzagames.apparbitraje.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.fervenzagames.apparbitraje.R;
import com.fervenzagames.apparbitraje.Utils.FirebaseRTDB;

public class ListaPuntuacionesDialog extends AppCompatDialogFragment {

    private ImageView fotoArbi;
    private TextView nombreArbi;
    private ListView listView;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.lista_punts_dialog, null);

        builder.setView(view)
                .setTitle("Lista de Puntuaciones")
                .setNegativeButton("cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setPositiveButton("detalle", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(getActivity(), "Se ha pulsado el botón Detalle", Toast.LENGTH_SHORT).show();
                    }
                });

        fotoArbi = view.findViewById(R.id.lista_punts_dialog_fotoArbi);
        nombreArbi = view.findViewById(R.id.lista_punts_dialog_nombreArbi);
        listView = view.findViewById(R.id.lista_punts_dialog_listView);

        Bundle extras = getArguments();
        try {
            String dniJuez = extras.getString("dniJuez");
            String idCompetidor = extras.getString("idCompetidor");
            String idCombate = extras.getString("idCombate");
            String idAsalto = extras.getString("idAsalto");
            String lado = extras.getString("lado");



            FirebaseRTDB.getListaFiltradaPunts(dniJuez, idCompetidor ,idCombate, idAsalto, getActivity(), listView, lado);

        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        return builder.create();

    }
}
