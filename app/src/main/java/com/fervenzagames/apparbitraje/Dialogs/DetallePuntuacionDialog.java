package com.fervenzagames.apparbitraje.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.fervenzagames.apparbitraje.Models.Arbitros;
import com.fervenzagames.apparbitraje.Models.Competidores;
import com.fervenzagames.apparbitraje.Models.Puntuaciones;
import com.fervenzagames.apparbitraje.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class DetallePuntuacionDialog extends AppCompatDialogFragment {

    private TextView mValor;
    private CircleImageView mFotoJuezView;
    private CircleImageView mFotoCompView;
    private ImageView mTipoAtaque;
    private TextView mAsalto;
    private TextView mTiempo;

    private DatabaseReference mPuntDB;
    private DatabaseReference mCompDB;
    private DatabaseReference mJuezDB;

    private String mFotoJuez;
    private String mFotoComp;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.puntuacion_dialog, null);

        builder.setView(view)
                .setTitle("Detalle Puntuación")
                .setNegativeButton("cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("detalle", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getActivity(), "Se ha pulsado el botón Detalle", Toast.LENGTH_SHORT).show();
                    }
                });


        mValor = view.findViewById(R.id.punt_dialog_valor);
        mFotoJuezView = view.findViewById(R.id.punt_dialog_fotoJuez);
        mFotoCompView = view.findViewById(R.id.punt_dialog_fotoComp);
        mTipoAtaque = view.findViewById(R.id.punt_dialog_tipoAtaque);
        mAsalto = view.findViewById(R.id.punt_dialog_asalto);
        mTiempo = view.findViewById(R.id.punt_dialog_marcaTiempo);

        Bundle extras = getArguments();
        try {
            String idPunt = extras.getString("idPunt");
            String idAsalto = extras.getString("idAsalto");
            cargarDatosPuntuacion(idPunt, idAsalto); // Las puntuaciones dependen de los asaltos.
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        return builder.create();
    }

    private void cargarDatosPuntuacion(String idPunt, String idAsalto){
        mPuntDB = FirebaseDatabase.getInstance().getReference("Arbitraje/Puntuaciones").child(idAsalto).child(idPunt);
        Query consulta = mPuntDB;
        consulta.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()){
                    Toast.makeText(getActivity(), "Error al cargar los datos de la Puntuación en el cuadro de Diálogo...", Toast.LENGTH_SHORT).show();
                } else {
                    Puntuaciones punt = dataSnapshot.getValue(Puntuaciones.class);
                    String valor = "Valor " + String.valueOf(punt.getValor());
                    mValor.setText(valor);
                    String dniJuez = punt.getDniJuez();
                    String idComp = punt.getIdCompetidor();
                    getFotoJuez(dniJuez);
                    getFotoCompetidor(idComp);
                    String tipoAtaque = punt.getTipoAtaque();
                    mostrarTipoAtaque(tipoAtaque);
                    // String zonaContacto = punt.getZonaContacto();
                    // mostrarZonaContacto(zonaContacto);
                    mTiempo.setText(punt.getMarcaTiempo());
                    // Mostrar Info Asalto ¿Necesario en el Dialog?
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    // Método para obtener la foto del Juez a partir de su DNI. Deberemos realizar una búsqueda basada en ese DNI.
    private void getFotoJuez(final String dniJuez){
        mJuezDB = FirebaseDatabase.getInstance().getReference("Arbitraje/Arbitros");
        Query consulta = mJuezDB.orderByChild("dni").equalTo(dniJuez); // Búsqueda por DNI en los Árbitros de la BD.
        consulta.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()){
                    Toast.makeText(getActivity(), "No existen Árbitros en la BD...", Toast.LENGTH_SHORT).show();
                } else {
                    Arbitros arbi = dataSnapshot.getValue(Arbitros.class);
                    try {
                        mFotoJuez = arbi.getFoto();
                        Picasso.get().load(mFotoJuez).into(mFotoJuezView);
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getFotoCompetidor(String idComp){
        mCompDB = FirebaseDatabase.getInstance().getReference("Arbitraje/Competidores").child(idComp);
        Query consulta = mCompDB;
        consulta.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()){
                    Toast.makeText(getActivity(), "Error al cargar la Foto del Competidor...", Toast.LENGTH_SHORT).show();
                } else {
                    Competidores comp = dataSnapshot.getValue(Competidores.class);
                    mFotoComp = comp.getFoto();
                    Picasso.get().load(mFotoComp).into(mFotoCompView);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void mostrarTipoAtaque(String tipoAtaque){
        Resources res = getResources();
        switch (tipoAtaque){
            case "Puñetazo":{
                mTipoAtaque.setImageResource(R.drawable.boxeo);
                break;
            }
            case "Patada":{
                mTipoAtaque.setImageResource(R.drawable.kick);
                break;
            }
            case "Proyeccion":{
                mTipoAtaque.setImageResource(R.drawable.proyeccion);
                break;
            }
        }
    }

    // PENDIENTE de que se incluya la información de la ZonaContacto de un golpe.
    private void mostrarZonaContacto(String zonaContacto){

    }
}
