package com.fervenzagames.apparbitraje;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.fervenzagames.apparbitraje.Add_Activities.AddCompetidorActivity;
import com.fervenzagames.apparbitraje.Models.Arbitros;
import com.fervenzagames.apparbitraje.User_Activities.LoginActivity;
import com.fervenzagames.apparbitraje.User_Activities.SettingsActivity;
import com.fervenzagames.apparbitraje.Utils.Login_Logout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private Toolbar mToolbar;

    private ViewPager mViewPager;
    private SectionsPagerAdapter mSectionsPagerAdapter;

    private TabLayout mTabLayout;

    private int tipo;

    private String mUid;
    private DatabaseReference mArbitrosDB;
    private DatabaseReference mRootDB;
    private DatabaseReference mUsuariosDB;

    private AlertDialog.Builder alertDialogBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        // comprobarEstadoUsuarioActual();

        mToolbar = findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("AppArbitraje");


        // Tabs
        mViewPager = findViewById(R.id.main_tabPager);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager.setAdapter(mSectionsPagerAdapter);

        mTabLayout = findViewById(R.id.main_tabs);
        mTabLayout.setupWithViewPager(mViewPager);

        alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder
                .setMessage("Ya se ha iniciado sesión con este usuario en otro dispositivo.")
                .setCancelable(false)
                .setPositiveButton("aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        sendToStart(null);
                    }
                })
                .setNegativeButton("cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        sendToStart(null);
                    }
                });

        mUid = null;

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
        //mAuth.addAuthStateListener(mAuthListener);
/*
        uid = currentUser.getUid();
*/
        //updateUI(currentUser);

/*        mArbitrosDB = FirebaseDatabase.getInstance().getReference("Arbitraje").child("Arbitros").child(uid);
        // Referencia a los datos del Árbitro correspondiente al usuario que ha iniciado sesión en este dispositivo.*/

        // Si el usuario no ha iniciado sesión deberemos abrir la pantalla de LOGIN
        if(currentUser == null){
            sendToStart(null);
        } else {
            mUid = currentUser.getUid();
            comprobarEstadoUsuario(mUid);
        }
        /*else {
            Toast.makeText(this, "Current User UID ----> " + currentUser.getUid(), Toast.LENGTH_SHORT).show();
            //mUid = currentUser.getUid();
            try {
                //mUid = getIntent().getExtras().getString("uid");
                mUid = currentUser.getUid();
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
            if(mUid != null){
                actualizarEstadoArbitro(mUid, true);
            }
        }*/
    }

/*

    @Override
    protected void onResume() {
        super.onResume();

        try {
            mUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        if(mUid != null){
            actualizarEstadoArbitro(mUid, true);
        } else {
            mUid = getIntent().getExtras().getString("uid");
            actualizarEstadoArbitro(mUid, false);
        }

    }
*/

    private void sendToStart(String uid) {
        /*if(uid != null){
            actualizarEstadoArbitro(uid, false);
        }*/
        /* Se crea un Intent para la página de LOGIN */
        Intent startIntent = new Intent(MainActivity.this, StartActivity.class);
        /* Se envía como extra el ID del Usuario para modificar el campo conectado del árbitro */
        Bundle extras = new Bundle();
        extras.putString("uid", mUid);
        //Toast.makeText(this, "(Main) sendToStart mUid : " + mUid, Toast.LENGTH_SHORT).show();
        startIntent.putExtras(extras);
        /* Se inicia ese Intent */
        startActivity(startIntent);
        // Toast.makeText(this, "Estado de la Activity MainActivity antes del FINISH --> " + this.getLifecycle().getCurrentState(), Toast.LENGTH_SHORT).show(); --> CREATED
        /* Con esta línea evitamos que al pulsar el botón para retroceder se vuelva a esta actividad. */
        finish();
        // Toast.makeText(this, "Estado de la Activity MainActivity después del FINISH --> " + this.getLifecycle().getCurrentState(), Toast.LENGTH_SHORT).show(); // --> RESUMED
    }

    // Selecionar el menú principal que deseamos y mostrarlo.
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        if(tipo == 0) { // MÓVIL
            getMenuInflater().inflate(R.menu.phone_main_menu, menu);
        } else if (tipo == 1){ // TABLET
            getMenuInflater().inflate(R.menu.main_menu, menu);
        }


        return true;
    }

    // ¿Qué ocurre si se pulsa algún botón en el menú principal?
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        if(tipo == 0) { // MÓVIL
            switch (item.getItemId()){
                case R.id.phone_cerrar_sesion_btn:{

                    // Obtener el UID antes de cerrar la sesión para poder actualizar el campo conectado del Árbitro
                    try {
                        mUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        //String id_1 = mUid;
                        // verValorConectado(mUid);
                        //verValorConectado(id_1);
                        //Toast.makeText(this, "Iniciando LOGOUT --- UID : " + mUid, Toast.LENGTH_SHORT).show();
                        // Cerrar Sesión
                        //FirebaseAuth.getInstance().signOut(); // Cerrar sesión con Firebase Auth.
                        //logoutUser(mUid);
                        Login_Logout.actualizarEstadoUsuario(mUid, "false", getBaseContext()); // Incluye el cambio de valor y el logout
                        /*if(FirebaseAuth.getInstance().getCurrentUser() == null) {
                            Toast.makeText(this, "Finalizando LOGOUT --- User == NULL", Toast.LENGTH_SHORT).show();
                        }*/
                        sendToStart(mUid); // Redirigir al inicio de la app.
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                    break;
                }
                case R.id.phone_user_settings_btn:{
                    Intent settingsIntent = new Intent(MainActivity.this, SettingsActivity.class);
                    startActivity(settingsIntent);
                    break;
                }
            }
        } else if(tipo == 1) { // TABLET
            switch(item.getItemId())
            {
                case R.id.main_logout_btn:{
                    // Vamos a incluir el código para cerrar la sesión.

                    // Modificar el campo conectado del Árbitro en la BD. CONECTADO --> FALSE
                    /*try {
                        mUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        Toast.makeText(this, "Current User UID -----> " + mUid, Toast.LENGTH_SHORT).show();
                        actualizarEstadoArbitro(mUid, false);
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }*/

                    // Obtener el UID antes de cerrar la sesión para poder actualizar el campo conectado del Árbitro
                    try {
                        mUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        // Cerrar Sesión
                        // FirebaseAuth.getInstance().signOut(); // Cerrar sesión con Firebase Auth.
                        Login_Logout.actualizarEstadoUsuario(mUid, "false", getBaseContext()); // Incluye el cambio de valor y el logout

                        sendToStart(mUid); // Redirigir al inicio de la app.
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }

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
            /*case R.id.main_arbitrar_combate:{
                if (tipo == 0) // MÓVIL
                {
                    Intent arbitrarIntent = new Intent(MainActivity.this, SillaArbitrajeActivity.class);
                    startActivity(arbitrarIntent);
                } else if (tipo == 1){ // TABLET
                    Intent arbitrarIntent = new Intent(MainActivity.this, MesaArbitrajeActivity.class);
                    startActivity(arbitrarIntent);
                }
                break;
            }*/
                case R.id.main_competidor:{ // Prueba de la Actividad para Añadir un Competidor a la BD.
                    Intent competidorIntent = new Intent(MainActivity.this, AddCompetidorActivity.class);
                    startActivity(competidorIntent);
                    break;
                }
                case R.id.main_lista_arbitros:{
                    Intent listArbitrosIntent = new Intent(MainActivity.this, ArbitrosActivity.class);
                    startActivity(listArbitrosIntent);
                    break;
                }
                case R.id.main_combates:{
                    Intent listaCombatesIntent = new Intent(MainActivity.this, CombatesActivity.class);
                    startActivity(listaCombatesIntent);
                    break;
                }
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

    public void actualizarEstadoArbitro(String uid, final boolean estado){
        // Como parámetros se pasan:
        // 1. el UID que permite identificar al usuario y al árbitro correspondiente.
        // 2. el nuevo estado del árbitro.
        final String id = uid;

        // El proceso de actualizar usando updateChildren lo he cogido de la documentación de Firebase: https://firebase.google.com/docs/database/android/read-and-write?hl=es-419

        mRootDB = FirebaseDatabase.getInstance().getReference("Arbitraje");
        mArbitrosDB = FirebaseDatabase.getInstance().getReference("Arbitraje/Arbitros").child(uid);

        final Query consulta = mArbitrosDB;

        consulta.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    // Comprobación de la ruta de la consulta
                    Toast.makeText(MainActivity.this, "Ruta de la consulta (conectado = FALSE) --> " + consulta.getRef().toString(), Toast.LENGTH_SHORT).show();
                    // Recuperamos los datos del árbitro
                    Arbitros arbi = dataSnapshot.getValue(Arbitros.class);
                    // Modificamos el estado
                    arbi.setConectado(estado);
                    Toast.makeText(MainActivity.this, "(MAIN) Actualizando estado Árbitro --> " + estado, Toast.LENGTH_SHORT).show();
                    // Actualizamos los datos del Árbitro en la BD con el nuevo estado de conexión.
                    Map<String, Object> nuevoArbi = arbi.toMap();
                    Map<String, Object> updates = new HashMap<>();
                    updates.put("/Arbitros/" + id, nuevoArbi);
                    mRootDB.updateChildren(updates);
                    Toast.makeText(MainActivity.this, "(MAIN) Nuevo estado Arbitro en BD --> " + arbi.getConectado(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Error al localizar al Árbitro cuyo ID es " + id, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    // Método que usa un objeto de la clase Firebase.AuthStateListener para asegurarse de que los tokens de login y logout se actaulizan correctamete.
    // Así pdoremos asegurarnos de que el usuario está o no logueado.

    public void comprobarEstadoUsuarioActual(){
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        if(user != null){ // User is signed in
            //actualizarEstadoArbitro(user.getUid(), true);
        } else { // No user is signed in
            //actualizarEstadoArbitro(mCurrentUser.getUid(), false);
            Toast.makeText(this, "No existe ningún usuario LOGUEADO (MAIN)...", Toast.LENGTH_SHORT).show();
        }
        /*mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if((user == null) && (mUid != null)){
                    actualizarEstadoArbitro(mUid, false);
                    Toast.makeText(MainActivity.this, "LOGOUT", Toast.LENGTH_SHORT).show();
                }
            }
        };*/
    }

    public void logoutUser(String uid){
        //actualizarEstadoArbitro(uid, false);
        Login_Logout.actualizarEstadoUsuario(uid, "false", getBaseContext());
        mAuth.signOut();
    }

    public void verValorConectado(final String uid){
        mArbitrosDB = FirebaseDatabase.getInstance().getReference("Arbitraje/Arbitros").child(uid);
        Query consulta = mArbitrosDB;
        consulta.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()){
                    Toast.makeText(MainActivity.this, "Error al recuperar los datos del árbitro cuyo id es " + uid, Toast.LENGTH_SHORT).show();
                } else {
                    Arbitros arbi = dataSnapshot.getValue(Arbitros.class);
                    Toast.makeText(MainActivity.this, "  El árbitro " + arbi.getNombre() + " tiene el estado de " + arbi.getConectado(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void comprobarEstadoUsuario(final String uid){
        if(uid == null){
            sendToStart(uid);
        } else {
            mUsuariosDB = FirebaseDatabase.getInstance().getReference("Usuarios").child(uid);
            Query usuarioQuery = mUsuariosDB;
            usuarioQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (!dataSnapshot.exists()) {
                        Toast.makeText(MainActivity.this, "(LOGIN) No existe ese usuario en la BD...", Toast.LENGTH_SHORT).show();
                    } else {
                        try {
                            String estado = dataSnapshot.child("conectado").getValue().toString();
                            //Toast.makeText(MainActivity.this, "Estado del Usuario --> " + estado, Toast.LENGTH_SHORT).show();
                            if(estado.equals("true")){
                                // Aviso
                                alertDialogBuilder.show();
                                Login_Logout.logoutUser();
                                //sendToStart(uid);
                            }
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

}
