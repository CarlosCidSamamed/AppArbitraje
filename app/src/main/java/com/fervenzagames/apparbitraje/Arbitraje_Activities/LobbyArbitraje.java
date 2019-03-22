package com.fervenzagames.apparbitraje.Arbitraje_Activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.fervenzagames.apparbitraje.Adapters.ArbitrosMiniList;
import com.fervenzagames.apparbitraje.Models.Arbitros;
import com.fervenzagames.apparbitraje.Models.Combates;
import com.fervenzagames.apparbitraje.Models.DatosExtraZonasCombate;
import com.fervenzagames.apparbitraje.Models.Mensajes;
import com.fervenzagames.apparbitraje.Models.ZonasCombate;
import com.fervenzagames.apparbitraje.Notifications.MyFirebaseMessagingService;
import com.fervenzagames.apparbitraje.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.GsonBuilder;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.FirebaseFunctionsException;
import com.google.firebase.functions.HttpsCallableResult;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class LobbyArbitraje extends AppCompatActivity {

    private static final String TAG = "LobbyArbitraje";

    private Toolbar mToolbar;
    private ListView mListView;
    private Button mEnviarBtn;
    private Button mIniciarBtn;

    private ImageView mConectado;
    private CircleImageView mFoto;
    private TextView mNombre;

    private DatabaseReference mCombateDB;

    private DatabaseReference mRootDB;
    private DatabaseReference mArbisDB;
    private DatabaseReference mZonaDB;
    private List<Arbitros> mLista;
    private List<String> mListaIDsArbis;
    private List<DatosExtraZonasCombate> mListaDatosExtraZona;
    private String mIdCamp;
    private String mIdCat;
    private String mIdCombate;
    private String mIdZona;
    private String mIdAsalto;
    private DatosExtraZonasCombate mDatosCombate;

    private String idRojo;
    private String idAzul;

    private int mNumArbisConfirmados;
    private int mNumArbisMinimo;
    private String mNombreMod;

    private FirebaseFunctions mFunctions;
    private DatabaseReference mMensajesDB;

    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby_arbitraje);

        mFunctions = FirebaseFunctions.getInstance(); // Inicializar la instancia para poder usar Firebase Cloud Functions
        mMensajesDB = FirebaseDatabase.getInstance().getReference("Arbitraje/Mensajes");

        mAuth = FirebaseAuth.getInstance();

        mToolbar = findViewById(R.id.lobby_app_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Lobby Arbitraje");
        mListView = findViewById(R.id.lobby_arbitraje_lista);
        mEnviarBtn = findViewById(R.id.lobyy_arbitraje_enviarBtn);
        mIniciarBtn = findViewById(R.id.lobby_arbitraje_iniciarBtn);

        mConectado = findViewById(R.id.arb_single_mini_estado);
        mFoto = findViewById(R.id.arb_single_mini_foto);
        mNombre = findViewById(R.id.arb_single_mini_nombre);

        mRootDB = FirebaseDatabase.getInstance().getReference("Arbitraje");
        mArbisDB = FirebaseDatabase.getInstance().getReference("Arbitraje/Arbitros");
        mZonaDB = FirebaseDatabase.getInstance().getReference("Arbitraje/ZonasCombate");
        mLista = new ArrayList<>();
        mListaIDsArbis = new ArrayList<>();
        mListaDatosExtraZona = new ArrayList<>();

        Bundle extras = getIntent().getExtras();
        mIdCamp = extras.getString("idCamp");
        mIdCat = extras.getString("idCat");
        mIdCombate = extras.getString("idComb");
        mIdZona = extras.getString("idZona");
        mIdAsalto = extras.getString("idAsalto");

        idRojo = extras.getString("idRojo");
        idAzul = extras.getString("idAzul");


        recuperarDispArbitros(mIdCamp, mIdZona, mIdCombate);
        mNumArbisConfirmados = mListaIDsArbis.size();

        // Obtener el NOMBRE de la MODALIDAD
        mNombreMod = extras.getString("nombreMod");
        switch(mNombreMod){
            case "Sanda SD":
            case "Qingda QD":
            case "Kungfu Combat KC":{
                mNumArbisMinimo = 3;
                Toast.makeText(this, "(LobbyArbitraje) El número mínimo de árbitros para esta modalidad " +  mNombreMod + " es de " + mNumArbisMinimo, Toast.LENGTH_SHORT).show();
                break;
            }
            case "Shuai Jiao SJ":{
                mNumArbisMinimo = 1;
                Toast.makeText(this, "(LobbyArbitraje) El número mínimo de árbitros para esta modalidad " +  mNombreMod + " es de " + mNumArbisMinimo, Toast.LENGTH_SHORT).show();
                break;
            }
        }
        if(mNumArbisConfirmados < mNumArbisMinimo){ // Si el número de arbitros que han confirmado su disponiblidad se informa de ello y se oculta el botón para iniciar el asalto.
            //Toast.makeText(this, "(LobbyArbitraje) El número de árbitros preparados para arbitrar el combate es inferior al mínimo permitido.", Toast.LENGTH_SHORT).show();
            mIniciarBtn.setVisibility(View.INVISIBLE);
        } else {
            mIniciarBtn.setVisibility(View.VISIBLE);
        }

        mIniciarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle extras = new Bundle();
                extras.putString("idCamp", mIdCamp);
                extras.putString("idCat", mIdCat);
                extras.putString("idCombate", mIdCombate);
                extras.putString("idZona", mIdZona);
                extras.putString("idAsalto", mIdAsalto);
                extras.putString("idRojo", idRojo);
                extras.putString("idAzul", idAzul);
                Intent mesaArbiIntent = new Intent(LobbyArbitraje.this, MesaArbitrajeActivity.class);
                mesaArbiIntent.putExtras(extras);
                startActivity(mesaArbiIntent);
            }
        });

        mEnviarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activarTriggerNotificaciones(null); // Enviar las notificaciones a todos los arbitros de la ZONA
            }
        });

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Arbitros arbi = mLista.get(i);
                activarTriggerNotificaciones(arbi.getIdArbitro()); // Enviar la notificación al árbitro que se ha pulsado.
            }
        });


    }

    // Recuperar el estado de disponibilidad de los árbitros de la zona de combate indicada para el combate correcto.
    private void recuperarDispArbitros(String idCamp, final String idZona, final String idCombate){
        // Obtener el id del campeonato al que pertenece la zona indicada
        Query consultaZona = mZonaDB.child(idCamp).child(idZona);
        consultaZona.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()){
                    Toast.makeText(LobbyArbitraje.this, "(LobbyArbitraje) Error al buscar la Zona de Combate indicada. " + idZona, Toast.LENGTH_SHORT).show();
                    Toast.makeText(LobbyArbitraje.this, "(LobbyArbitraje) ID ZONA --> " + mIdZona, Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        //mLista.clear();
                        ZonasCombate zona = dataSnapshot.getValue(ZonasCombate.class);
                        //mIdCamp = zona.getIdCamp();
                        mListaDatosExtraZona = zona.getListaDatosExtraCombates();
                        // Debemos localizar el combate correcto dentro de esa lista
                        for(int i = 0; i < mListaDatosExtraZona.size(); i++){
                            DatosExtraZonasCombate datos = mListaDatosExtraZona.get(i);
                            /*Toast.makeText(LobbyArbitraje.this, "(LobbyArbitraje) Leyendo datos EXTRA i = " + i, Toast.LENGTH_SHORT).show();
                            Toast.makeText(LobbyArbitraje.this, "(LobbyArbitraje) datos.getIdCombate " + datos.getIdCombate(), Toast.LENGTH_SHORT).show();
                            Toast.makeText(LobbyArbitraje.this, "(Lobbyrbitraje) idCombate (parámetro) " + idCombate, Toast.LENGTH_SHORT).show();*/
                            if(datos.getIdCombate().equals(idCombate)){
                                mDatosCombate = datos;
                            }
                        }
                        int numArbisAsignados = mDatosCombate.getNumArbisAsignados();
                        if( numArbisAsignados > 0 ){
                            // Una vez localizados los datos del combate deseado deberemos recorrer la lista de árbitros asignados,
                            // si los hubiera, para poder comprobar su estado de disponiblidad
                            mListaIDsArbis = mDatosCombate.getListaIDsArbis();
                            for(int i = 0; i < mListaIDsArbis.size(); i++){
                                //Toast.makeText(LobbyArbitraje.this, "mListaIDsArbis( " + i + " ) " + mListaIDsArbis.get(i), Toast.LENGTH_SHORT).show();
                                recuperarDisponibilidadArbitro(mListaIDsArbis.get(i));
                            }
                            /*// Mostrar lista con los datos.
                            ArbitrosMiniList adapter = new ArbitrosMiniList(LobbyArbitraje.this, mLista);
                            adapter.setDropDownViewResource(R.layout.arbitro_single_layout_mini);
                            mListView.setAdapter(adapter);
                            adapter.notifyDataSetChanged();*/

                        } else {
                            Toast.makeText(LobbyArbitraje.this, "(LobbyArbitraje) No existen Árbitros asignados a la zona y combate indicados.", Toast.LENGTH_SHORT).show();
                        }

                    } catch (NullPointerException e) {
                        Toast.makeText(LobbyArbitraje.this, "(LobbyArbitraje) Error al cargar los Datos Extra de la Zona de Combate", Toast.LENGTH_SHORT).show();
                        Toast.makeText(LobbyArbitraje.this, "(LobbyArbitraje) Tamaño lista datos extra -->> " + mListaDatosExtraZona.size(), Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    // Recuperar el estado de disponibilidad del árbitro indicado
    private void recuperarDisponibilidadArbitro(final String idArbitro){
        Query consultaArbis = mArbisDB.child(idArbitro);
        consultaArbis.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mLista.clear();
                if(!dataSnapshot.exists()){
                    Toast.makeText(LobbyArbitraje.this, "(LobbyArbitraje) Error al localizar al Árbitro en la BD. (idArbitro => " + idArbitro + " )", Toast.LENGTH_SHORT).show();
                } else {
                    //Toast.makeText(LobbyArbitraje.this, "(LobbyArbitraje) Ruta consulta Árbitro --> " + dataSnapshot.getRef(), Toast.LENGTH_SHORT).show();
                    Arbitros arbi = dataSnapshot.getValue(Arbitros.class);
                    //Toast.makeText(LobbyArbitraje.this, "(LobbyArbitraje) Nombre Árbitro --> " + arbi.getNombre(), Toast.LENGTH_SHORT).show();
                    /*// Comprobar si el árbitro está conectado o no
                    if(arbi.getListo()) { // Se muestra si ha confirmado o no su disponibilidad.
                        mLista.add(arbi); // Añadir el Árbitro a la lista que se mostrará en pantalla solo si el valor de conectado es true.
                    }*/
                    mLista.add(arbi); // Se añade a todos los árbitros de la zona para mostrar si han confirmado o no su disponibilidad.
                    // Mostrar lista con los datos.
                    ArbitrosMiniList adapter = new ArbitrosMiniList(LobbyArbitraje.this, mLista);
                    adapter.setDropDownViewResource(R.layout.arbitro_single_layout_mini);
                    mListView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    // Este método se encarga de enviar las notificaciones a los árbitros de la zona de combate para que estos confirmen su disponibilidad para comenzar a arbitrar.
    // Si se indica un idArbi solo se enviará un mensaje a ese Árbitro. En caso contrario se enviará una notificación a cada uno de los árbitros asignados a la zona de combate actual.
    private void activarTriggerNotificaciones(final String idArbi){
        if(idArbi!=null){ // Una notificación
            //modificarIdZonaArbi(idArbi);
            enviarMensaje(idArbi).addOnCompleteListener(new OnCompleteListener<String>() {
                @Override
                public void onComplete(@NonNull Task<String> task) {
                    if(!task.isSuccessful()){
                        Exception e = task.getException();
                        if(e instanceof FirebaseFunctionsException){
                            FirebaseFunctionsException ffe = (FirebaseFunctionsException) e;
                            FirebaseFunctionsException.Code code = ffe.getCode();
                            Object details = ffe.getDetails();
                            Log.w(TAG, "enviarMensaje:FirebaseFunctionsException // Code : " + code + " // Details : "+ details);
                        }
                        Log.w(TAG, "enviarMensaje:onFailure", e);
                        Toast.makeText(LobbyArbitraje.this, "(LobbyArbitraje) Error al enviar el mensaje al Árbitro cuyo ID es " + idArbi, Toast.LENGTH_SHORT).show();
                        Toast.makeText(LobbyArbitraje.this, "(LobbyArbitraje) Excepción enviarMensaje --> " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    String result = task.getResult();
                    Toast.makeText(LobbyArbitraje.this, "(LobbyArbitraje) Result --> " + result, Toast.LENGTH_SHORT).show();
                }
            });
        } else { // Notificaciones a todos los árbitros de la zona de combate
            // Recorreremos la lista de arbitros de la zona para modificar sus idZona
            for(int i = 0; i < mListaIDsArbis.size(); i++){
                //modificarIdZonaArbi(mListaIDsArbis.get(i));
                final String id = mListaIDsArbis.get(i);
                enviarMensaje(mListaIDsArbis.get(i)).addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if(!task.isSuccessful()){
                            Exception e = task.getException();
                            if(e instanceof FirebaseFunctionsException){
                                FirebaseFunctionsException ffe = (FirebaseFunctionsException) e;
                                FirebaseFunctionsException.Code code = ffe.getCode();
                                Object details = ffe.getDetails();
                                Log.w(TAG, "enviarMensaje:FirebaseFunctionsException // Code : " + code + " // Details : "+ details);
                            }
                            Log.w(TAG, "enviarMensaje:onFailure", e);
                            Toast.makeText(LobbyArbitraje.this, "(LobbyArbitraje) Error al enviar el mensaje al Árbitro cuyo ID es " + id, Toast.LENGTH_SHORT).show();
                            Toast.makeText(LobbyArbitraje.this, "(LobbyArbitraje) Excepción enviarMensaje --> " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            return;
                        }
                        String result = task.getResult();
                        Toast.makeText(LobbyArbitraje.this, "(LobbyArbitraje) Result --> " + result, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }

    public void modificarIdZonaArbi (final String idArbi){
        Query consulta = mArbisDB.child(idArbi);
        consulta.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()){
                    Toast.makeText(LobbyArbitraje.this, "(LobbyArbitraje) Error al localizar al Árbitro en la BD. (Activar trigger)", Toast.LENGTH_SHORT).show();
                } else {
                    Arbitros arbi = dataSnapshot.getValue(Arbitros.class);
                    // Almacenar el idZona correcto
                    String idZonaViejo = null;
                    try {
                        idZonaViejo = arbi.getIdZona();
                        arbi.setIdZona(""); // Se borra el IdZona que tuviese asignado. Si era null pasa a ser una cadena vacía.
                        Map<String, Object> nuevoArbi = arbi.toMap();
                        HashMap<String, Object> updates = new HashMap<>();
                        updates.put("/Arbitros/" + idArbi, nuevoArbi);
                        mRootDB.updateChildren(updates);

                        // Volver a modificarlo al idZona correcto para que se envíe la notificación a su dispositivo.
                        HashMap<String, Object> updates2 = new HashMap<>();
                        if (idZonaViejo != null) {
                            arbi.setIdZona(idZonaViejo);
                        }
                        Map<String, Object> nuevoArbi2 = arbi.toMap();
                        updates2.put("/Arbitros/" + idArbi, nuevoArbi2);
                        mRootDB.updateChildren(updates2);
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

    // Este método realizará la llamada https a la cloud function que enviará los mensajes del tipo correcto a los árbitros correspondientes.
    public Task<String> enviarMensaje(final String idArbitro){
        // Crear los argumentos para la función (Callable Function)
        final Map<String, Object> data = new HashMap<>();
        // Enviaré un map cuya String será el tipo de mensaje y el Object será el mensaje a enviar.

        // Para determinar el tipo de mensaje y los datos a enviar en el mensaje deberemos evaluar:
        // 1. El estado de Conexión
        // 2. La disponibilidad para arbitrar.

        // Primero deberemos localizar al Árbitro en la BD
        Query consulta = mArbisDB.child(idArbitro);
        // Si no se encuentra el Árbitro --> AVISO DE ERROR
        consulta.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()){
                    Toast.makeText(LobbyArbitraje.this, "(LobbyArbitraje) Error al localizar al Árbitro receptor del mensaje...", Toast.LENGTH_SHORT).show();
                } else {
                    // Si se encuentra el Árbitro deberemos confeccionar el mensaje evaluando arbi.conectado y arbi.listo

                    // Crear Mensaje
                    Mensajes mensaje = new Mensajes();
                    // Añadir datos
                    mensaje.setEmisor(mAuth.getCurrentUser().getUid()); // ID del usuario que ha iniciado sesión en la tablet (MESA)
                    mensaje.setReceptor(idArbitro);
                    Calendar cal = Calendar.getInstance();
                    DateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss"); // Fecha y hora actual con el formato indicado.
                    mensaje.setFechaHora(sdf.format(cal.getTime()));

                    data.put("emisor", mensaje.getEmisor());
                    data.put("receptor", mensaje.getReceptor());
                    data.put("fechaHora", mensaje.getFechaHora());

                    Arbitros arbi = dataSnapshot.getValue(Arbitros.class);
                    if(!arbi.getConectado()){
                        // Enviar mensaje de Inicio de Sesión
                        // Título, cuerpo y tipo para Inicio de Sesión
                        mensaje.setTitulo(Mensajes.TITULO_LOGIN);
                        mensaje.setCuerpo(Mensajes.CUERPO_LOGIN);
                        mensaje.setTipo(Mensajes.TIPO_LOGIN);
                        String json = mensaje.serializeToJson(mensaje);
                        Toast.makeText(LobbyArbitraje.this, "(Mensajes.serializetoJson) --> " + json, Toast.LENGTH_SHORT).show();
                        // Una vez definido el mensaje a enviar se procede a preparar el objeto data para invocar la Cloud Function
                        data.put("tipo", "login");
                        data.put("mensaje", mensaje.getCuerpo());
                        data.put("titulo", mensaje.getTitulo());

                    } else if (!arbi.getListo()){ // Conectado == TRUE ... Listo == FALSE --> Mensaje de Confirmación
                        // Título, cuerpo y tipo para Confirmación
                        mensaje.setTitulo(Mensajes.TITULO_CONF);
                        mensaje.setCuerpo(Mensajes.CUERPO_CONF);
                        mensaje.setTipo(Mensajes.TIPO_CONF);
                        // Una vez definido el mensaje a enviar se procede a preparar el objeto data para invocar la Cloud Function
                        data.put("tipo", "confirmacion");
                        //Gson gson = new GsonBuilder().create();
                        // Obtener el tipo del HashMap para poder serializarlo correctamente.
                        //Type type = new TypeToken<HashMap<String, String>>(){}.getType();
                        String m = mensaje.serializeToJson(mensaje); // Mi método para serializar un Mensaje
                        //String json = gson.toJson(mensaje);//, type); // Serializar el Map<String, String>
                        Toast.makeText(LobbyArbitraje.this, "(Mensajes.serializetoJson) --> " + m, Toast.LENGTH_SHORT).show();
                        data.put("mensaje", mensaje.getCuerpo());
                        data.put("titulo", mensaje.getTitulo());
                    } else { // Conectado == TRUE ... Listo == TRUE --> Mensaje de Inicio de Asalto
                        // Título, cuerpo y tipo para Inicio de Asalto
                        mensaje.setTitulo(Mensajes.TITULO_ASALTO);
                        mensaje.setCuerpo(Mensajes.CUERPO_ASALTO);
                        mensaje.setTipo(Mensajes.TIPO_ASALTO);
                        // Si el mensaje es para el Inicio de Sesión deberemos añadir los datos extra necesarios
                        // para poder cargar la activity SillaArbitraje al pulsar sobre la notificación
                        mensaje.setIdCamp(mIdCamp);
                        mensaje.setIdCat(mIdCat);
                        mensaje.setIdCombateActual(mIdCombate);
                        mensaje.setIdAsaltoActual(mIdAsalto);
                        mensaje.setIdZona(mIdZona);
                        mensaje.setIdRojo(idRojo);
                        mensaje.setIdAzul(idAzul);
                        String json = mensaje.serializeToJson(mensaje);
                        Toast.makeText(LobbyArbitraje.this, "(Mensajes.serializetoJson) --> " + json, Toast.LENGTH_SHORT).show();
                        // Una vez definido el mensaje a enviar se procede a preparar el objeto data para invocar la Cloud Function
                        data.put("tipo", "inicioAsalto");
                        data.put("mensaje", mensaje.getCuerpo());
                        data.put("titulo", mensaje.getTitulo());
                        // Datos extra Asalto
                        data.put("idCamp", mensaje.getIdCamp());
                        data.put("idCat", mensaje.getIdCat());
                        data.put("idZona", mensaje.getIdZona());
                        data.put("idCombateActual", mensaje.getIdCombateActual());
                        data.put("idAsaltoActual", mensaje.getIdAsaltoActual());
                        data.put("idRojo", mensaje.getIdRojo());
                        data.put("idAzul", mensaje.getIdAzul());
                    }

                    // Comprobación de la estructura del mensaje
                    Toast.makeText(LobbyArbitraje.this, "(LobbyArbitraje) Mensaje a enviar --> " + data, Toast.LENGTH_SHORT).show();
                    Toast.makeText(LobbyArbitraje.this, "Contenido del Mensaje -->" + data.get("mensaje").toString(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        // Se devuelve el resultado de la petición HTTPS para invocar a la Cloud Function llamada solicitarEnvio con los datos especificados en data.
        return mFunctions
                .getHttpsCallable("solicitarEnvio")
                .call(data)
                .continueWith(new Continuation<HttpsCallableResult, String>() {
                    @Override
                    public String then(@NonNull Task<HttpsCallableResult> task) throws NullPointerException {
                        try {
                            Map<String, Object> result = (Map<String, Object>) task.getResult().getData();
                            return (String) result.get("mensaje");
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                            return null;
                        }
                    }
                });
    }
}
