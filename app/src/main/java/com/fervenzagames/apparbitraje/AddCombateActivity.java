package com.fervenzagames.apparbitraje;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import de.hdodenhof.circleimageview.CircleImageView;

public class AddCombateActivity extends AppCompatActivity {

    private Toolbar mToolbar;

    private TextView mNombreCamp;
    private TextView mNombreMod;
    private TextView mNombreCat;

    private Spinner mEstadoSpinner;

    // Competidor Rojo
    private CircleImageView mFotoRojo;
    private TextView mNombreRojo;

    // Competidor Azul
    private CircleImageView mFotoAzul;
    private TextView mNombreAzul;

    private Button mGuardarBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_combate);

        mToolbar = (Toolbar) findViewById(R.id.add_comb_app_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Añadir Combate");

        mNombreCamp = (TextView) findViewById(R.id.add_comb_nombreCampeonato);
        mNombreMod = (TextView) findViewById(R.id.add_comb_modNombre);
        mNombreCat = (TextView) findViewById(R.id.add_comb_catNombre);

        mEstadoSpinner = (Spinner) findViewById(R.id.add_comb_estadoSpinner);

        mFotoRojo = (CircleImageView) findViewById(R.id.add_comb_Rojo);
        mNombreRojo = (TextView) findViewById(R.id.add_comb_Rojo_nombre);

        mFotoAzul = (CircleImageView) findViewById(R.id.add_comb_Azul);
        mNombreAzul = (TextView) findViewById(R.id.add_comb_Azul_nombre);

        mGuardarBtn = (Button) findViewById(R.id.add_comb_guardar_btn);

    }
}
