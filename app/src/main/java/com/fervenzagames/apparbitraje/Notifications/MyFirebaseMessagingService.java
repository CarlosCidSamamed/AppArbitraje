package com.fervenzagames.apparbitraje.Notifications;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.fervenzagames.apparbitraje.Arbitraje_Activities.LobbyArbitraje;
import com.fervenzagames.apparbitraje.Arbitraje_Activities.SillaArbitrajeActivity;
import com.fervenzagames.apparbitraje.MainActivity;
import com.fervenzagames.apparbitraje.Models.Arbitros;
import com.fervenzagames.apparbitraje.R;
import com.fervenzagames.apparbitraje.StartActivity;
import com.fervenzagames.apparbitraje.User_Activities.LoginActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static android.support.constraint.Constraints.TAG;
import static com.google.firebase.messaging.RemoteMessage.PRIORITY_HIGH;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    String NOTIFICATION_CHANNEL_ID = "com.fervenzagames.apparbitraje.Notifications";
    String mTipo = "";
    PendingIntent mPendingIntent;
    Bundle mBundle;

    DatabaseReference mRootDB;
    DatabaseReference mArbiDB;
    FirebaseAuth mAuth;
    String mUid;

    public MyFirebaseMessagingService() {
        //myGetToken(this);
        mAuth = FirebaseAuth.getInstance();
        try{
            mUid = mAuth.getCurrentUser().getUid();
        } catch (NullPointerException e){
            e.printStackTrace();
        }
    }

    String token;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // ...

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());

            /*if (*//* Check if data needs to be processed by long running job *//* true) {
                // For long-running tasks (10 seconds or more) use Firebase Job Dispatcher.
                //scheduleJob();
            } else {*/
            // Handle message within 10 seconds
            //handleNow();
            super.onMessageReceived(remoteMessage);
            // Gestionar los mensajes de tipo DATA
            showNotificationData(remoteMessage);

            //}

        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
            super.onMessageReceived(remoteMessage);
            showNotification(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody());
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
        Log.d(TAG, "(Firebase) Refreshed token: " + token);
        // Save Token in SharedPreferences
        getSharedPreferences("_", MODE_PRIVATE).edit().putString("fbToken", token).apply();

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        //sendRegistrationToServer(token);
    }

    private String myGetToken(final Context context){
        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
            @Override
            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                if(!task.isSuccessful()){
                    Log.w(TAG, "getInstanceId failed", task.getException());
                    return;
                }
                // Get the new Instance ID token
                token = task.getResult().getToken();

                // Log and Toast
                String msg = token;
                Log.d(TAG, msg);
                Toast.makeText(MyFirebaseMessagingService.this, "TOKEN " + token, Toast.LENGTH_SHORT).show();

            }
        });
        return context.getSharedPreferences("_", MODE_PRIVATE).getString("fbToken", "empty");
    }

    private void showNotification(String title, String body){
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){ // Versión Oreo (26 y 27) (Android 8.0 y 8.1) o superior
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "Notificaciones", NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.setDescription("Notificaciones de AppArbitraje");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.BLUE);
            notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            notificationManager.createNotificationChannel(notificationChannel);

        }

        // Notification sin modificar
        /*//Para versiones anteriores a Oreo
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        notificationBuilder.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.luchar)
                .setContentTitle(title)
                .setContentText(body)
                .setContentInfo("Info");
        notificationManager.notify(new Random().nextInt(), notificationBuilder.build());*/

        // Custom Notification
        RemoteViews collapsedView = new RemoteViews(getPackageName(), R.layout.notification_collapsed);
        RemoteViews expandedView = new RemoteViews(getPackageName(), R.layout.notification_expanded);

        /* Los extras que debemos mandar para poder abrir la actividad de SillaArbitraje:
            idCategoría
            idCombate
            idAsalto
           Dichos extras deberán enviarse desde la App de la Mesa.
        */

        Intent clickIntent = new Intent(this, NotificationReceiver.class);
        PendingIntent clickPendingIntent = PendingIntent.getBroadcast(this, 0, clickIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        // https://developer.android.com/reference/android/app/PendingIntent
        // FLAG_UPDATE_CURRENT
        // Flag indicating that if the described PendingIntent already exists, then keep it but replace its extra data with what is in this new Intent.
        // Para poder enviar extras a través del PendingIntent

        expandedView.setOnClickPendingIntent(R.id.myNotification_expanded_info, clickPendingIntent);
        String info = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, " +
                "sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. " +
                "Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut " +
                "aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit " +
                "in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint " +
                "occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.";
        expandedView.setTextViewText(R.id.myNotification_expanded_info, info);
        expandedView.setTextViewText(R.id.myNotification_expanded_title, "Aviso de AppArbitraje");

        collapsedView.setTextViewText(R.id.myNotification_collapsed_title, "Aviso de AppArbitraje");
        collapsedView.setTextViewText(R.id.myNotification_collapsed_info, "Pulse para expandir");


        Notification notification = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setCustomContentView(collapsedView)
                .setCustomBigContentView(expandedView)
                .setSmallIcon(R.drawable.boxeo)
                .setPriority(PRIORITY_HIGH)
                .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                .build();

        notificationManager.notify(1, notification);
    }

    private void showNotificationData(RemoteMessage remoteMessage){

        // Custom Notification
        RemoteViews collapsedView = new RemoteViews(getPackageName(), R.layout.notification_collapsed);
        RemoteViews expandedView = new RemoteViews(getPackageName(), R.layout.notification_expanded);

        collapsedView.setTextViewText(R.id.myNotification_collapsed_title, remoteMessage.getData().get("title"));
        collapsedView.setTextViewText(R.id.myNotification_collapsed_info, "Pulse para expandir");

        expandedView.setTextViewText(R.id.myNotification_collapsed_title, remoteMessage.getData().get("title"));
        expandedView.setTextViewText(R.id.myNotification_expanded_info, remoteMessage.getData().get("body"));

        mTipo = remoteMessage.getData().get("type");

        // Evaluar mTipo y lanzar la Activity correspondiente
        switch(mTipo){
            case "inicioAsalto":{
                /*Info Necesaria
                    idCamp
                    idCat
                    idComb
                    idZona
                    idAsalto
                */
                obtenerDatosCloudFunction();
                Intent arbitrarIntent = new Intent(this, SillaArbitrajeActivity.class);
                mPendingIntent = PendingIntent.getActivity(this, 0, arbitrarIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                break;
            }
            case "confirmacion":{
                Intent inicioIntent = new Intent(this , MainActivity.class);
                mPendingIntent = PendingIntent.getActivity(this, 0, inicioIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                if(mUid != null) {
                    modificarListo(mUid); // Cambiar el valor de Arbitro.Listo a TRUE.
                }
                break;
            }
            case "login":{
                Intent loginIntent = new Intent(this, LoginActivity.class);
                mPendingIntent = PendingIntent.getActivity(this, 0, loginIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                break;
            }
            default:{
                Intent inicioIntent = new Intent(this, StartActivity.class);
                mPendingIntent = PendingIntent.getActivity(this, 0, inicioIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                break;
            }
        }

        collapsedView.setOnClickPendingIntent(R.id.myNotification_collapsed_info, mPendingIntent);
        expandedView.setOnClickPendingIntent(R.id.myNotification_expanded_info, mPendingIntent);

        Notification notification = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setCustomContentView(collapsedView)
                .setCustomBigContentView(expandedView)
                .setContentIntent(mPendingIntent) // Se indica la Activity que se debe abrir al pulsar sobre la notificación.
                .setSmallIcon(R.drawable.logo_app1)
                .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                .setAutoCancel(true)
                .build();

        NotificationManagerCompat manager = NotificationManagerCompat.from(getApplicationContext());
        manager.notify(2, notification);
    }

    // En este método voy a invocar una función de las Cloud Functions que he definido en TypeScript para este proyecto de Firebase
    // Dicha invocación me devolverá los datos que necesito para poder llamar a la activity SillaArbitraje de manera correcta.
    // La funcion que voy a invocar se llama recuperarDatos y tiene esta cabecera
    //          export function recuperarDatos(idZona: string, idCamp: string, idArbitro: string)
    // Es decir, que necesita tres parámetros idZona, idCamp e idArbitro.

    // Documentación a seguir para esta llamada directa a la Cloud Function --> https://firebase.google.com/docs/functions/callable?hl=es-419 (Llama a funciones desde tu app)

    // La Cloud Function devolverá los datos en formato JSON.
    private void obtenerDatosCloudFunction() {

    }


    private void modificarListo(String idArbi){
        mRootDB = FirebaseDatabase.getInstance().getReference("Arbitraje");
        mArbiDB = FirebaseDatabase.getInstance().getReference("Arbitraje/Arbitros").child(idArbi);
        Query consulta = mArbiDB;
        consulta.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()){
                    // No se encuentra el Arbitro con ese ID
                } else {
                    Arbitros arbi = dataSnapshot.getValue(Arbitros.class);
                    arbi.setListo(true);
                    Map<String, Object> arbiMap = arbi.toMap();
                    HashMap<String, Object> updates = new HashMap<>();
                    updates.put("Arbitros/" + arbi.getIdArbitro(), arbiMap);
                    mRootDB.updateChildren(updates);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}

