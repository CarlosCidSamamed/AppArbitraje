package com.fervenzagames.apparbitraje;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.time.chrono.MinguoChronology;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private Toolbar mToolbar;

    private ViewPager mViewPager;
    private SectionsPagerAdapter mSectionsPagerAdapter;

    private TabLayout mTabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        mToolbar = (Toolbar) findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("AppArbitraje");

        // Tabs
        mViewPager = (ViewPager) findViewById(R.id.main_tabPager);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager.setAdapter(mSectionsPagerAdapter);

        mTabLayout = (TabLayout) findViewById(R.id.main_tabs);
        mTabLayout.setupWithViewPager(mViewPager);
    }

    @Override
    protected void onStart() {
        super.onStart();
        //Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        //updateUI(currentUser);

        // Si el usuario no ha iniciado sesión deberemos abrir la pantalla de LOGIN
        if(currentUser == null){
            sendToStart();
        }
    }


    private void sendToStart() {
        /* Se crea un Intent para la página de LOGIN */
        Intent startIntent = new Intent(MainActivity.this, StartActivity.class);
        /* Se inicia ese Intent */
        startActivity(startIntent);
        /* Con esta línea evitamos que al pulsar el botón para retroceder se vuelva a esta actividad. */
        finish();
    }

    // Selecionar el menú principal que deseamos y mostrarlo.
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.main_menu, menu);

        return true;
    }

    // ¿Qué ocurre si se pulsa algún botón en el menú principal?
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        switch(item.getItemId())
        {
            case R.id.main_logout_btn:{
                // Vamos a incluir el código para cerrar la sesión.
                FirebaseAuth.getInstance().signOut(); // Cerrar sesión con Firebase Auth.
                sendToStart(); // Redirigir al inicio de la app.
                break;
            }
            case R.id.main_settings_btn:{
                Toast.makeText(MainActivity.this, "Ha pulsado el botón Opciones", Toast.LENGTH_SHORT).show();
                break;
            }
            case R.id.main_account_settings:{
                // Toast.makeText(MainActivity.this, "Ha pulsado el botón de Cuenta de Usuario", Toast.LENGTH_SHORT).show();
                // Crear el Intent de la página de Settings
                Intent sett_intent = new Intent(MainActivity.this, SettingsActivity.class);
                // E iniciarlo
                startActivity(sett_intent);
                break;
            }
            case R.id.main_app_type:{
                Toast.makeText(MainActivity.this, "Ha pulsado el botón de Tipo de App", Toast.LENGTH_SHORT).show();
                break;
            }
            case R.id.main_ver_campeonatos:{
                Toast.makeText(MainActivity.this, "Ver los Campeonatos de la BD", Toast.LENGTH_SHORT).show();
                Intent campIntent = new Intent(MainActivity.this, CampeonatosActivity.class);
                startActivity(campIntent);
                break;
            }
        }

        return true;
    }
}
