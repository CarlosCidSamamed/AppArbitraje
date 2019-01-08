package com.fervenzagames.apparbitraje.Detail_Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.fervenzagames.apparbitraje.R;

public class DetalleAsaltoActivity extends AppCompatActivity {

    private Toolbar mToolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_asalto);

        mToolbar = findViewById(R.id.detalle_asalto_app_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Detalle del Asalto");
    }
}
