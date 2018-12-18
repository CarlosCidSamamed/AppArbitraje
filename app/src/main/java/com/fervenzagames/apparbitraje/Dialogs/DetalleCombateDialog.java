package com.fervenzagames.apparbitraje.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.fervenzagames.apparbitraje.Models.Competidores;
import com.fervenzagames.apparbitraje.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class DetalleCombateDialog extends AppCompatDialogFragment {

    private TextView mNombreRojo;
    private TextView mNombreAzul;
    private CircleImageView mFotoRojo;
    private CircleImageView mFotoAzul;

    private DatabaseReference mCompetidorDB;
    private DatabaseReference mCombateDB;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.combate_dialog, null);

        builder.setView(view)
        .setTitle("Detalle Combate")
        .setNegativeButton("cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        mNombreRojo = view.findViewById(R.id.combate_dialog_nombreRojo);
        mNombreAzul = view.findViewById(R.id.combate_dialog_nombreAzul);
        mFotoRojo = view.findViewById(R.id.combate_dialog_fotoRojo);
        mFotoAzul = view.findViewById(R.id.combate_dialog_fotoAzul);

        Bundle extras = getArguments();
        String idCombate = extras.getString("idCombate");
        //Toast.makeText(getActivity(), "ID Combate Dialog --> " + idCombate, Toast.LENGTH_SHORT).show();
        String idCat = extras.getString("idCategoria");
        //Toast.makeText(getActivity(), "ID Categoría Dialog --> " + idCat, Toast.LENGTH_SHORT).show();

        // ROJO
        cargarDatosCompetidor(idCombate, idCat, true);
        // AZUL
        cargarDatosCompetidor(idCombate, idCat, false);

        return builder.create();
    }

    // Método para buscar y cargar los datos de un competidor en la sección correspondiente del dialog.
    // Se pasan como parámetros el id del Combate, el id de la Categoría y si el competidor a cargar es el rojo o el azul.
    private void cargarDatosCompetidor(String idCombate, String idCategoria, final boolean rojo){

        mCompetidorDB = FirebaseDatabase.getInstance().getReference("Arbitraje/Competidores");
        mCombateDB = FirebaseDatabase.getInstance().getReference("Arbitraje/Combates").child(idCategoria).child(idCombate);
        Query consulta = mCombateDB;
        consulta.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()){
                    Toast.makeText(getContext(), "No existen datos en la BD relacionados con el combate indicado...", Toast.LENGTH_SHORT).show();
                } else {
                    String idCompetidor = "";
                    if(rojo == true) {
                        idCompetidor = dataSnapshot.child("idRojo").getValue().toString();
                        //Toast.makeText(getContext(), "ID Competidor ROJO " + idCompetidor, Toast.LENGTH_SHORT).show();
                    } else {
                        idCompetidor = dataSnapshot.child("idAzul").getValue().toString();
                        //Toast.makeText(getContext(), "ID Competidor AZUL " + idCompetidor, Toast.LENGTH_SHORT).show();
                    }

                    // Localizar al competidor y buscar su nombre y su foto
                    final Query consulta = mCompetidorDB.child(idCompetidor);
                    consulta.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists()){

                                // Toast.makeText(getContext(), "URL consulta Competidor " + consulta.getRef(), Toast.LENGTH_SHORT).show();

                                Competidores comp = dataSnapshot.getValue(Competidores.class);

                                String nombre = comp.getnombreCompleto();
                                // Toast.makeText(getContext(), "Nombre Completo --> " + nombre, Toast.LENGTH_SHORT).show();
                                String foto = comp.getFoto();

                                if(rojo == true){ // ROJO
                                    mNombreRojo.setText(nombre);
                                    Picasso.get().load(foto).into(mFotoRojo);
                                } else { // AZUL
                                    mNombreAzul.setText(nombre);
                                    Picasso.get().load(foto).into(mFotoAzul);
                                }

                        /*try {
                            String nombre = dataSnapshot.child("nombre").getValue().toString();
                            Toast.makeText(getContext(), "Nombre : " + nombre , Toast.LENGTH_SHORT).show();

                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        }*/

                        /*String ap1 = dataSnapshot.child("apellido1").getValue().toString();
                        String ap2 = dataSnapshot.child("apellido2").getValue().toString();
                        String nombreCompleto = nombre + " " + ap1 + " " + ap2;

                        String foto = dataSnapshot.child("foto").getValue().toString();
                        try {
                            if(rojo == true){ // ROJO
                                mNombreRojo.setText(nombreCompleto);
                                Picasso.get().load(foto).into(mFotoRojo);
                            } else { // AZUL
                                mNombreAzul.setText(nombreCompleto);
                                Picasso.get().load(foto).into(mFotoAzul);
                            }
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        }*/
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }
}
