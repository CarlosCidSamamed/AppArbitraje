package com.fervenzagames.apparbitraje.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.fervenzagames.apparbitraje.Models.Incidencias;
import com.fervenzagames.apparbitraje.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class DetalleIncidenciaDialog extends AppCompatDialogFragment {

    private TextView mValor;
    private TextView mTipo;
    private TextView mDesc;
    private TextView mTiempo;
    
    private DatabaseReference mIncDB;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.incidencia_dialog, null);

        builder.setView(view)
                .setNegativeButton("cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        mValor = view.findViewById(R.id.inc_dialog_valor);
        mTipo = view.findViewById(R.id.inc_dialog_tipo);
        mDesc = view.findViewById(R.id.inc_dialog_desc);
        mTiempo = view.findViewById(R.id.inc_dialog_marcaTiempo);

        Bundle extras = getArguments();
        try {
            String idInc = extras.getString("idInc");
            String idAsalto = extras.getString("idAsalto");
            cargarDatosIncidencia(idInc, idAsalto);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        return builder.create();
    }

    private void cargarDatosIncidencia(String idInc, String idAsalto){
        mIncDB = FirebaseDatabase.getInstance().getReference("Arbitraje/Incidencias").child(idAsalto).child(idInc);
        Query consulta = mIncDB;
        consulta.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()){
                    Toast.makeText(getActivity(), "Error al cargar los datos de la Incidencia en el cuadro de Diálogo...", Toast.LENGTH_SHORT).show();
                } else {
                    Incidencias inc = dataSnapshot.getValue(Incidencias.class);
                    String valor = "Valor  - " + String.valueOf(inc.getValor());
                    mValor.setText(valor);
                    String tipo = "Tipo " + inc.getTipo();
                    mTiempo.setText(tipo);
                    mDesc.setText(inc.getDescripcion());
                    mTiempo.setText(inc.getMarcaTiempo());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
