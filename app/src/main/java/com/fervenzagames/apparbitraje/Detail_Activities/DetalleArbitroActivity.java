package com.fervenzagames.apparbitraje.Detail_Activities;

import android.content.Intent;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.fervenzagames.apparbitraje.Edit_Activities.EditArbitroActivity;
import com.fervenzagames.apparbitraje.Models.Arbitros;
import com.fervenzagames.apparbitraje.Models.Campeonatos;
import com.fervenzagames.apparbitraje.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class DetalleArbitroActivity extends AppCompatActivity {

    private String mIdArbi;

    private Toolbar mToolbar;

    private CircleImageView mFoto;
    private Button mEditarBtn;

    private TextView mNombre;
    private TextView mDNI;
    private TextView mEmail;
    private TextView mNivel;
    private TextView mCargo;
    private TextView midCamp;
    private TextView mNombreCamp;
    private TextView mZona;
    private ImageView mConectado;

    private DatabaseReference mArbitroDB;
    private DatabaseReference mCampDB;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_arbitro);

        mToolbar = findViewById(R.id.arb_detalle_app_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Detalle Árbitro");

        Intent intent = getIntent();
        mIdArbi = intent.getStringExtra("idArbitro");

        mArbitroDB = FirebaseDatabase.getInstance().getReference("Arbitraje").child("Arbitros").child(mIdArbi); // Referencia al árbitro deseado.
        mCampDB = FirebaseDatabase.getInstance().getReference("Arbitraje").child("Campeonatos"); // Lista de Campeonatos.

        mFoto = findViewById(R.id.arb_detalle_foto);
        mNombre = findViewById(R.id.arb_detalle_nombre);
        mDNI = findViewById(R.id.arb_detalle_dni);
        mEmail = findViewById(R.id.arb_detalle_email);
        mNivel = findViewById(R.id.arb_detalle_nivel);
        mCargo = findViewById(R.id.arb_detalle_cargo);
        mZona = findViewById(R.id.arb_detalle_zonaCombate);
        mConectado = findViewById(R.id.arb_detalle_conectado);
        midCamp = findViewById(R.id.arb_detalle_idCamp);
        mNombreCamp = findViewById(R.id.arb_detalle_nombreCamp);

        mArbitroDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Arbitros arbi = dataSnapshot.getValue(Arbitros.class);
                try {

                    String nombre = arbi.getNombre();
                    String dni = arbi.getDni();
                    String email = arbi.getEmail();
                    String foto = arbi.getFoto();
                    String nivel = arbi.getNivel();
                    String cargo = arbi.getCargo();
                    String idCamp = arbi.getIdCamp();
                    String zona = String.valueOf(arbi.getZonaCombate());
                    String conectado = String.valueOf(arbi.isConectado());

                    mNombre.setText(nombre);
                    if(foto.equals("default")){
                        Picasso.get().load(R.drawable.default_avatar).into(mFoto); // Por defecto default_avatar.
                    } else {
                        Picasso.get().load(foto).into(mFoto);
                    }
                    mDNI.setText(dni);
                    mEmail.setText(email);
                    mNivel.setText(nivel);
                    mCargo.setText(cargo);
                    midCamp.setText(idCamp);


                    // Zona de combate
                    mZona.setText("Zona de Combate : " + zona);

                    Toast.makeText(DetalleArbitroActivity.this, "Valor de CONECTADO --> " + conectado, Toast.LENGTH_SHORT).show();

                    // Estado de conexión
                    if(conectado.equals("true")){
                        mConectado.setImageResource(R.drawable.punto_verde);
                    } else if(conectado.equals("false")){
                        mConectado.setImageResource(R.drawable.punto_rojo);
                    }

                    // Buscar el nombre del Campeonato a partir de su idCamp.

                    Query consulta = mCampDB.child(idCamp);

                    consulta.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            Campeonatos camp = dataSnapshot.getValue(Campeonatos.class);

                            try {
                                String nombreCamp = camp.getNombre();
                                if(nombreCamp != null) mNombreCamp.setText(nombreCamp);
                            } catch (NullPointerException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });


                } catch (NullPointerException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        mEditarBtn = findViewById(R.id.arb_detalle_editarBtn);
        mEditarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent editarArbIntent = new Intent(DetalleArbitroActivity.this, EditArbitroActivity.class);
                startActivity(editarArbIntent);
            }
        });

    }
}
