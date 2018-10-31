package com.fervenzagames.apparbitraje;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.fervenzagames.apparbitraje.Add_Activities.AddCompetidorActivity;
import com.fervenzagames.apparbitraje.Arbitraje_Activities.MesaArbitrajeActivity;
import com.fervenzagames.apparbitraje.Arbitraje_Activities.SillaArbitrajeActivity;
import com.fervenzagames.apparbitraje.User_Activities.SettingsActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private Toolbar mToolbar;

    private ViewPager mViewPager;
    private SectionsPagerAdapter mSectionsPagerAdapter;

    private TabLayout mTabLayout;

    private int tipo;

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

        tipo = detectarTipoDispostivo();
        switch (tipo){
            case 0:
            {
                Toast.makeText(this, "MÓVIL", Toast.LENGTH_LONG).show();
                break;
            }
            case 1:{
                Toast.makeText(this, "TABLET", Toast.LENGTH_LONG).show();
                break;
            }
        }
        // Voy a usar el valor de tipo para delimitar las activities que se pueden ver y cargar desde un móvil o desde una tablet.
        // Esto será así porque los árbitros de SILLA usarán móviles y los de MESA usarán TABLETS.

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
            case R.id.main_arbitrar_combate:{
                if (tipo == 0) // MÓVIL
                {
                    Intent arbitrarIntent = new Intent(MainActivity.this, SillaArbitrajeActivity.class);
                    startActivity(arbitrarIntent);
                } else if (tipo == 1){ // TABLET
                    Intent arbitrarIntent = new Intent(MainActivity.this, MesaArbitrajeActivity.class);
                    startActivity(arbitrarIntent);
                }
            }
            case R.id.main_competidor:{ // Prueba de la Actividad para Añadir un Competidor a la BD.
                Intent competidorIntent = new Intent(MainActivity.this, AddCompetidorActivity.class);
                startActivity(competidorIntent);
            }
        }

        return true;
    }


    // Este método se encarga de detectar el tamaño de la pantalla para saber si el dispostivo es un móvil o una tablet.
    // MÓVIL <7 pulgadas TABLET 7 o más.
    // MÓVIL  --> 0
    // TABLET --> 1
    public int detectarTipoDispostivo(){

        int tipo = 0;
        int screenWidth = 0;
        int screenHeight = 0;

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        screenWidth = dm.widthPixels;
        screenHeight = dm.heightPixels;

        double x = Math.pow(screenWidth / dm.xdpi, 2);
        double y = Math.pow(screenHeight / dm.xdpi, 2);

        double screenInches = Math.sqrt(x + y);
        screenInches = (double) Math.round(screenInches * 10) / 10;

        Toast.makeText(this, "Tamaño en Pulgadas de la Pantalla --> " + screenInches, Toast.LENGTH_LONG).show();

        if(screenInches < 7.0f)
        {
            tipo = 0; // MÓVIL
        } else if (screenInches >= 7.0f){
            tipo = 1; // TABLET
        }

        return tipo;
    }
}
