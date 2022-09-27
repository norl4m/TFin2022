package com.marlon.apolo.tfinal2022.foregroundCustomService;

import static android.content.Context.NOTIFICATION_SERVICE;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.RemoteInput;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.marlon.apolo.tfinal2022.R;
import com.marlon.apolo.tfinal2022.individualChat.view.MensajeNube;
import com.marlon.apolo.tfinal2022.model.Administrador;
import com.marlon.apolo.tfinal2022.model.Chat;
import com.marlon.apolo.tfinal2022.model.Empleador;
import com.marlon.apolo.tfinal2022.model.Trabajador;
import com.marlon.apolo.tfinal2022.model.Usuario;

public class ForegroundReplyNotificationReceiver extends BroadcastReceiver {

    private static final String TAG = ForegroundReplyNotificationReceiver.class.getSimpleName();
    private static final String KEY_TEXT_REPLY = "KEY_TEXT_REPLY";
    private static final String NOTIFICATIONS_CHANNEL_ID = "CANAL_DE_NOTIFICACIONES";
    private Usuario usuarioLocal;
    private NotificationManagerCompat notificationManager;

    public ForegroundReplyNotificationReceiver() {
//        if (FirebaseAuth.getInstance().getCurrentUser() != null)
//            filterByUser(FirebaseAuth.getInstance().getCurrentUser());
    }

    private void filterByUser(FirebaseUser firebaseUser) {
        FirebaseDatabase.getInstance().getReference()
                .child("administrador")
                .child(firebaseUser.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        try {
                            Administrador administrador = snapshot.getValue(Administrador.class);
                            if (administrador != null) {
                                usuarioLocal = administrador;
                            }
                        } catch (Exception e) {
                            Log.e(TAG, e.toString());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        FirebaseDatabase.getInstance().getReference()
                .child("empleadores")
                .child(firebaseUser.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        try {
                            Empleador empleador = snapshot.getValue(Empleador.class);
                            if (empleador != null) {
                                usuarioLocal = empleador;
                            }
                        } catch (Exception e) {
                            Log.e(TAG, e.toString());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        FirebaseDatabase.getInstance().getReference()
                .child("trabajadores")
                .child(firebaseUser.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        try {
                            Trabajador trabajador = snapshot.getValue(Trabajador.class);
                            if (trabajador != null) {
                                usuarioLocal = trabajador;
                            }
                        } catch (Exception e) {
                            Log.e(TAG, e.toString());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private CharSequence getMessageText(Intent intent) {
        Bundle remoteInput = RemoteInput.getResultsFromIntent(intent);
        if (remoteInput != null) {
            return remoteInput.getCharSequence(KEY_TEXT_REPLY);
        }
        return null;
    }


    private void updateNotification(int idNotification, CharSequence respuesta, Context context) {
        // Build a new notification, which informs the user that the system
        // handled their interaction with the previous notification.
//        NotificationCompat.Builder repliedNotification = new NotificationCompat.Builder(context, CHANNEL_ID)
//                .setSmallIcon(R.drawable.ic_oficios)
//                .setContentText(respuesta)
//                .build();
//        Toast.makeText(context,)
        Log.d(TAG, "UPDATE NOTIFICATION");
        Log.d(TAG, "ID NOTIFICATION: " + String.valueOf(idNotification));

        NotificationCompat.Builder repliedNotification = new NotificationCompat
                .Builder(context, NOTIFICATIONS_CHANNEL_ID)
                .setContentTitle("Respuesta")
                .setContentText("Tú: " + respuesta)
                .setSmallIcon(R.drawable.ic_oficios)
                .setAutoCancel(true)// Quita la notification cunado el usuario la presiona
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_ALL);

//        // Issue the new notification.
//        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
////        notificationManager.notify(idNotification, repliedNotification.build());
//        //notificationManager.notify(idNotification, repliedNotification.build());
//
//        NotificationManager mNotifyManager =
//                (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
//        NotificationManager notificationManagerx = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        // Issue the new notification.
        //NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        //notificationManager.notify(idNotification, repliedNotification.build());

        // Build a new notification, which informs the user that the system
        // handled their interaction with the previous notification.
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationManager mNotifyManager = (NotificationManager)
                    context.getSystemService(NOTIFICATION_SERVICE);
            Notification repliedNotificationx = new Notification.Builder(context, NOTIFICATIONS_CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_oficios)
                    .setContentTitle("Respuesta: " + String.valueOf(Build.VERSION.SDK_INT))
                    .setContentText("Tú: " + respuesta)
                    .setAutoCancel(true)
                    .build();
//            notificationManager.notify(idNotification, repliedNotificationx);
            mNotifyManager.notify(idNotification, repliedNotificationx);

        } else {
            NotificationCompat.Builder repliedNotificationAPI = new NotificationCompat
                    .Builder(context, NOTIFICATIONS_CHANNEL_ID)
                    .setContentTitle("Respuesta: " + String.valueOf(Build.VERSION.SDK_INT))
                    .setContentText("Tú: " + respuesta)
                    .setSmallIcon(R.drawable.ic_oficios)
                    .setAutoCancel(true)// Quita la notification cunado el usuario la presiona
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setDefaults(NotificationCompat.DEFAULT_ALL);
            notificationManager.notify(idNotification, repliedNotificationAPI.build());

        }

        // Issue the new notification.
//        notificationManager.notify(idNotification, repliedNotificationx);

//        notificationManager.cancel(idNotification);

//        cancelNotification(idNotification);

    }


    @Override
    public void onReceive(Context context, Intent intent) {
        notificationManager = NotificationManagerCompat.from(context);


        CharSequence respuestaDirecta = getMessageText(intent);
        //Toast.makeText(context, respuestaDirecta.toString(), Toast.LENGTH_LONG).show();

        int idNotification = intent.getIntExtra("idNotification", -1);
        String idTo = intent.getStringExtra("notificationIdFrom");
        Usuario usuarioFrom = (Usuario) intent.getSerializableExtra("usuarioFrom");
//        Usuario usuarioFrom = usuarioLocal;
        Chat chat = (Chat) intent.getSerializableExtra("chat");
        updateNotification(idNotification, respuestaDirecta, context);

        Log.d(TAG, "@@@@@@@@@@@@@@@@@@@@@");
        Log.d(TAG, "RESPONDIENDO");
        Log.d(TAG, respuestaDirecta.toString());
        Log.d(TAG, String.valueOf(idNotification));
        Log.d(TAG, idTo);
        Log.d(TAG, chat.toString());
        Log.d(TAG, usuarioFrom.toString());
        //Log.d(TAG,usuarioFromId.toString());

        Log.d(TAG, "@@@@@@@@@@@@@@@@@@@@@");

        try {
            MensajeNube mensajeNube = new MensajeNube();
            mensajeNube.setContenido(respuestaDirecta.toString());
            mensajeNube.setFrom(FirebaseAuth.getInstance().getCurrentUser().getUid());
            mensajeNube.setTo(idTo);
            mensajeNube.setEstadoLectura(false);
            mensajeNube.setType(0);/*0 mensaje -1 imagen- 2 audio-3 video 5MB*/
            usuarioFrom.responderNotificacion(idTo, context, chat, mensajeNube);

            //Toast.makeText(context, "RESPONDIENDO", Toast.LENGTH_LONG).show();
//            usuarioFrom.responderNotificacion();
//            Toast.makeText(context, mensajeNube.toString(), Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            //Log.d(TAG, "####################");
            Log.d(TAG, e.toString());
            // Log.d(TAG, "####################");
        }

    }

    private void cancelNotification(int idNotification) {
        // notificationId is a unique int for each notification that you must define
        notificationManager.cancel(idNotification);
    }
}
