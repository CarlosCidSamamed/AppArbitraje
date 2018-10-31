package com.fervenzagames.apparbitraje.Dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.fervenzagames.apparbitraje.Models.Competidores;
import com.fervenzagames.apparbitraje.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class AddCompetidorDialog extends AppCompatDialogFragment{

    private ListView listaCompetidoresView;
    private List<Competidores> listaComp;
    public String idCompetidor;

    private DatabaseReference mCompetidoresDB;

    //Interfaz para pasar datos entre el dialog y la activity que lo abrió.
    private AddCompetidorDialogListener listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.add_competidor_dialog, null);

        builder.setView(view)
                .setTitle("Añadir Competidor")
                .setNegativeButton("Cerrar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        listaComp = new ArrayList<>();

        listaCompetidoresView = (ListView) view.findViewById(R.id.add_comp_dialog_listView);

        mCompetidoresDB = FirebaseDatabase.getInstance().getReference("Arbitraje").child("Competidores");

        // Si no hay competidores en la BD...
        if(mCompetidoresDB == null){
            Toast.makeText(getContext(), "No existen Competidores en la BD...", Toast.LENGTH_LONG).show();
        }

        // Cargar datos en la ListView y en la List
        mCompetidoresDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listaComp.clear();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        listaCompetidoresView.setOnItemClickListener(new AdapterView.OnItemClickListener() { // Obtener el ID del Competidor que se pulsa en la ListView
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Competidores comp = listaComp.get(position);
                idCompetidor = listener.getIdCompetidor(comp);
            }
        });

        return builder.create();

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            listener = (AddCompetidorDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " debe implementar AddCompetidorDialogListener.");
        }

    }

    public interface AddCompetidorDialogListener{ // Interfaz que se encarga de pasar los datos del diálogo a la actividad que lo inicia, es decir, AddCombate.
        String getIdCompetidor(Competidores comp); //Método que obtiene y devuelve el ID del Competidor que se ha pulsado en la ListView del Dialog.
    }
}
