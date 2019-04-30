package com.fervenzagames.apparbitraje.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatDialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;

import com.fervenzagames.apparbitraje.Adapters.DetallePuntuacionesListaDialogAdapter;
import com.fervenzagames.apparbitraje.Models.Arbitros;
import com.fervenzagames.apparbitraje.Models.Asaltos;
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

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class DetallePuntsDialog extends AppCompatDialogFragment {

    private String mIdCombate;
    private String mIdAsalto;
    private String mIdArbitro;
    private String mIdComp;
    private String mLado;

    private DatabaseReference mAsaltoDB;
    private DatabaseReference mArbiDB;
    private DatabaseReference mCompDB;

    private CircleImageView fotoArbi;
    private CircleImageView fotoComp;
    private ListView mListaPuntsView;

    private static String TAG = "DialogPuntuaciones";

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.detalle_punts_lista_dialog, null);

        builder.setView(view)
                .setTitle("Detalle de las Puntuaciones")
                .setNegativeButton("cerrar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

        Bundle extras = getArguments();
        try {
            mIdCombate = extras.getString("idCombate");
            mIdAsalto = extras.getString("idAsalto");
            mIdArbitro = extras.getString("idArbitro");
            mIdComp = extras.getString("idComp");
            mLado = extras.getString("lado");

            cargarDatosArbitro(mIdArbitro);
            cargarDatosCompetidor(mIdComp);

            if(mLado.equals("Rojo")) {
                cargarListaPunt(mIdAsalto, "Rojo");
            } else if(mLado.equals("Azul")) {
                cargarListaPunt(mIdAsalto, "Azul");
            }
        } catch (NullPointerException e) {
            Log.v(TAG, "Error al cargar el Bundle con los extras");
            e.printStackTrace();
        }

        return builder.create();
    }

    private void cargarListaPunt(final String idAsalto, final String lado) {
        mAsaltoDB = FirebaseDatabase.getInstance().getReference("Arbitraje/Asaltos").child(mIdCombate).child(idAsalto);
        Query consulta = mAsaltoDB;
        consulta.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()){
                    Log.v(TAG, "Error al localizar el Asalto cuyo ID es " + idAsalto);
                } else {
                    // Obtener la lista de Puntuaciones.
                    Asaltos asalto = dataSnapshot.getValue(Asaltos.class);
                    List<Puntuaciones> listaPunt = new ArrayList<>();
                    try {
                        listaPunt = asalto.getListaPuntuaciones();
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                    if(listaPunt.size() == 0){
                        Log.v(TAG, "La lista de Puntuaciones de este Asalto está VACÍA");
                    } else {
                        // Crear el adapter.
                        DetallePuntuacionesListaDialogAdapter adapter = new DetallePuntuacionesListaDialogAdapter(getActivity(), listaPunt, lado);
                        // Asignar el adapter a la ListView para mostrar los datos.
                        mListaPuntsView.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void cargarDatosCompetidor(final String idComp){
        mCompDB = FirebaseDatabase.getInstance().getReference("Arbitraje/Competidores").child(idComp);
        Query consulta = mCompDB;
        consulta.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()){
                    Log.v(TAG, "Error al localizar al Competidor cuyo ID es " + idComp);
                } else {
                    // Obtener la foto del Competidor
                    Competidores comp = dataSnapshot.getValue(Competidores.class);
                    String foto = null;
                    try {
                        foto = comp.getFoto();
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                    if(foto != null){
                        Picasso.get().load(foto).into(fotoComp);
                    } else {
                        Picasso.get().load(R.drawable.default_avatar).into(fotoComp);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void cargarDatosArbitro(final String idArbitro) {
        mArbiDB = FirebaseDatabase.getInstance().getReference("Arbitraje/Arbitros").child(idArbitro);
        Query consulta = mArbiDB;
        consulta.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()){
                    Log.v(TAG, "Error al localizar al Arbitro cuyo ID es " + idArbitro);
                } else {
                    Arbitros arbi = dataSnapshot.getValue(Arbitros.class);
                    String foto = null;
                    try {
                        foto = arbi.getFoto();
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                    if(foto != null){
                        Picasso.get().load(foto).into(fotoArbi);
                    } else {
                        Picasso.get().load(R.drawable.default_avatar).into(fotoArbi);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


}
