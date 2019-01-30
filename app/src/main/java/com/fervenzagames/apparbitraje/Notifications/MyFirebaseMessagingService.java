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
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.fervenzagames.apparbitraje.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Random;

import static android.support.constraint.Constraints.TAG;
import static com.google.firebase.messaging.RemoteMessage.PRIORITY_HIGH;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    public MyFirebaseMessagingService() {
        //myGetToken(this);
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

            if (/* Check if data needs to be processed by long running job */ true) {
                // For long-running tasks (10 seconds or more) use Firebase Job Dispatcher.
                //scheduleJob();
            } else {
                // Handle message within 10 seconds
                //handleNow();
                super.onMessageReceived(remoteMessage);
                showNotification(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody());
            }

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
        String NOTIFICATION_CHANNEL_ID = "com.fervenzagames.apparbitraje.Notifications";

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

        Intent clickIntent = new Intent(this, NotificationReceiver.class);
        PendingIntent clickPendingIntent = PendingIntent.getBroadcast(this, 0, clickIntent, 0);

        expandedView.setOnClickPendingIntent(R.id.myNotification_expanded_info, clickPendingIntent);
        String info = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, " +
                "sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. " +
                "Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut " +
                "aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit " +
                "in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint " +
                "occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.";
        expandedView.setTextViewText(R.id.myNotification_expanded_info, info);
        expandedView.setTextViewText(R.id.myNotification_expanded_title, "Aviso de AppArbitraje");


        Notification notification = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setCustomContentView(collapsedView)
                .setCustomContentView(expandedView)
                .setSmallIcon(R.drawable.boxeo)
                .setPriority(PRIORITY_HIGH)
                .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                .build();

        notificationManager.notify(1, notification);
    }
}

