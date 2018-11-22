package com.fervenzagames.apparbitraje.Edit_Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.fervenzagames.apparbitraje.R;

public class EditArbitroActivity extends AppCompatActivity {

    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_arbitro);

        mToolbar = findViewById(R.id.edit_arbitro_app_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Editar Árbitro");
    }
}
