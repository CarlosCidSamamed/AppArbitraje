package com.fervenzagames.apparbitraje.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatDialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.fervenzagames.apparbitraje.Models.Arbitros;
import com.fervenzagames.apparbitraje.Models.Puntuaciones;
import com.fervenzagames.apparbitraje.R;
import com.fervenzagames.apparbitraje.Utils.FirebaseRTDB;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ListaPuntuacionesDialog extends AppCompatDialogFragment {

    private ImageView fotoArbi;
    private TextView nombreArbi;
    private ListView listView;

    private DatabaseReference mArbiDB;
    //private String mFotoArbi;
    //private String mNombreArbi;
    private List<String> datosArbi;

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
        /*mFotoArbi = "";
        mNombreArbi = "";*/

        datosArbi = new ArrayList<>();

        Bundle extras = getArguments();
        try {
            String dniJuez = extras.getString("dniJuez");
            Log.v("ListaPunt Dialog", "dniJuez --> " + dniJuez);
            String idCompetidor = extras.getString("idCompetidor");
            String idCombate = extras.getString("idCombate");
            String idAsalto = extras.getString("idAsalto");
            String lado = extras.getString("lado");

            getDatosArbi(dniJuez);

            FirebaseRTDB.getListaFiltradaPunts(dniJuez, idCompetidor ,idCombate, idAsalto, getActivity(), listView, lado);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    List<Puntuaciones> lista = FirebaseRTDB.getmListaFiltrada();
                    Puntuaciones p = lista.get(i);
                    abrirDialogPunt(p.getId(), p.getIdAsalto());
                }
            });

        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        return builder.create();

    }

    private void abrirDialogPunt(String idPunt, String idAsalto){
        DetallePuntuacionDialog dialog = new DetallePuntuacionDialog();
        Bundle extras = new Bundle();
        extras.putString("idPunt", idPunt);
        extras.putString("idAsalto", idAsalto);
        dialog.setArguments(extras);
        dialog.show(getActivity().getSupportFragmentManager(), "detalle puntuacion dialog");
    }

    private void getDatosArbi (final String dniArbi) {
        mArbiDB = FirebaseDatabase.getInstance().getReference("Arbitraje/Arbitros");
        Query consulta = mArbiDB;
        datosArbi.clear();
        consulta.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()){
                    Log.v("getfotoArbi Dialog Punt", "Error al localizar los Arbitros en la BD");
                } else {
                    for(DataSnapshot arbiSnap: dataSnapshot.getChildren()){
                        Arbitros arbi = arbiSnap.getValue(Arbitros.class);
                        try {
                            if(arbi.getDni().equals(dniArbi)){
                                /*mFotoArbi = arbi.getFoto();
                                mNombreArbi = arbi.getNombre();*/
                                datosArbi.add(arbi.getFoto());
                                datosArbi.add(arbi.getNombre());
                            }

                            if(datosArbi.size() > 0) {

                                if (!datosArbi.get(0).equals("default")) {
                                    Picasso.get().load(datosArbi.get(0)).into(fotoArbi);
                                } else {
                                    Picasso.get().load(R.drawable.default_avatar).into(fotoArbi);
                                }

                                if (!datosArbi.get(1).equals("")) {
                                    nombreArbi.setText(datosArbi.get(1));
                                }
                            }
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        }
                    }
                    Log.v("getDatosArbi", "URL a la foto del Juez : " + datosArbi.get(0));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }}
