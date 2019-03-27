package com.fervenzagames.apparbitraje.Utils;


import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.fervenzagames.apparbitraje.User_Activities.LoginActivity;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

// En esta clase nos ocuparemos de la modificación del valor de Usuarios.conectado ("true" y "false")
public class Login_Logout {

    private static DatabaseReference mUsuarioDB;
    private static DatabaseReference mArbitroDB;

    public static void actualizarEstadoUsuario(final String uid, final String estado, final Context context){
        mUsuarioDB = FirebaseDatabase.getInstance().getReference("Usuarios").child(uid);
        Query usuario = mUsuarioDB;
        usuario.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUsuarioDB.child("conectado").setValue(estado).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            //Toast.makeText(context, "(Login_Logout) Estado --> " + estado, Toast.LENGTH_SHORT).show();
                            // si el estado es false es porque deberemos realizar el logout después de haber modificado el valor en la BD
                            if(estado.equals("false")){
                                //Toast.makeText(context, "(Login Logout) Cerrar Sesión tras modificar conectado a false...", Toast.LENGTH_SHORT).show();
                                logoutUser(uid, estado);
                            }
                        } else {
                            Toast.makeText(context, "(Login_Logout) Error al modificar el valor de ESTADO...", Toast.LENGTH_SHORT).show();
                            Toast.makeText(context, task.getException().toString(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        // Modificar el valor de CONECTADO del Árbitro
        mArbitroDB = FirebaseDatabase.getInstance().getReference("Arbitraje/Arbitros").child(uid);
        Query arbi = mArbitroDB;
        arbi.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(estado.equals("true")){
                    mArbitroDB.child("conectado").setValue(true);
                } else if(estado.equals("false")){
                    mArbitroDB.child("conectado").setValue(false);
                    mArbitroDB.child("listo").setValue(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    public static void logoutUser(String uid, final String estado){
        // Modificar el valor de CONECTADO del Árbitro
        mArbitroDB = FirebaseDatabase.getInstance().getReference("Arbitraje/Arbitros").child(uid);
        Query arbi = mArbitroDB;
        arbi.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(estado.equals("true")){
                    mArbitroDB.child("conectado").setValue(true);
                } else if(estado.equals("false")){
                    mArbitroDB.child("conectado").setValue(false);
                    mArbitroDB.child("listo").setValue(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.signOut();
    }

}
