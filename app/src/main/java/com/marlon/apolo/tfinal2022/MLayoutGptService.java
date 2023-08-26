package com.marlon.apolo.tfinal2022;


import static android.app.PendingIntent.FLAG_MUTABLE;
import static android.app.PendingIntent.FLAG_UPDATE_CURRENT;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.marlon.apolo.tfinal2022.communicationAgora.video.receivers.RejectVideoCallBroadcastReceiver;
import com.marlon.apolo.tfinal2022.communicationAgora.video.view.VideoCallMainActivity;
import com.marlon.apolo.tfinal2022.model.LlamadaVideo;
import com.marlon.apolo.tfinal2022.model.Participante;

import java.util.ArrayList;
import java.util.Random;

public class MLayoutGptService extends Service {

    private NotificationManager notificationManager;
    private RejectVideoCallBroadcastReceiver rejectVideoCallBroadcastReceiver;


    public class NotifiLoca {
        String idDbFirebase;
        int idLocal;

        public NotifiLoca(String idDbFirebase, int idLocal) {
            this.idDbFirebase = idDbFirebase;
            this.idLocal = idLocal;
        }

        public String getIdDbFirebase() {
            return idDbFirebase;
        }

        public void setIdDbFirebase(String idDbFirebase) {
            this.idDbFirebase = idDbFirebase;
        }

        public int getIdLocal() {
            return idLocal;
        }

        public void setIdLocal(int idLocal) {
            this.idLocal = idLocal;
        }
    }


    private static final String CHANNEL_ID = "MyForegroundServiceChannel";
    private static final String VOICE_CALL_NOTIFICATIONS_CHANNEL_ID = "VOICE_CALL_NOTIFICATIONS_CHANNEL_ID";

    private ArrayList<NotifiLoca> notifiLocaArrayList;

    private void  showVideoCallNotification(int idNotification, LlamadaVideo llamadaVideo) {


        //CONFIGURANDO ACCIÓN DE RESPONDER
        Intent answerIntent = new Intent(this, VideoCallMainActivity.class);
        answerIntent.setAction("CALL_ANSWER");
        answerIntent.putExtra("llamadaVideo", llamadaVideo);
        answerIntent.putExtra("callStatus", "llamadaEntrante");
        answerIntent.putExtra("idNotification", idNotification);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            answerIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }

        PendingIntent answerPendingIntent = null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            answerPendingIntent = PendingIntent.getActivity(this, idNotification, answerIntent, FLAG_MUTABLE);


        } else {
            answerPendingIntent = PendingIntent.getActivity(this, idNotification, answerIntent, FLAG_UPDATE_CURRENT);
        }

        //CONFIGURANDO ACCIÓN DE CLICK SOBRE LA NOTIFICACIÓN
        Intent contentIntent = new Intent(this, VideoCallMainActivity.class);
        contentIntent.setAction("CALL_SCREEN");
        contentIntent.putExtra("llamadaVideo", llamadaVideo);
        contentIntent.putExtra("callStatus", "llamadaEntrante");
        contentIntent.putExtra("idNotification", idNotification);

        PendingIntent contentPendingIntent = null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            contentPendingIntent = PendingIntent.getActivity(this, idNotification, contentIntent, FLAG_MUTABLE);
        } else {
            contentPendingIntent = PendingIntent.getActivity(this, idNotification, contentIntent, FLAG_UPDATE_CURRENT);
        }

        //CONFIGURANDO ACCIÓN DE ELIMINACIÓN SOBRE LA NOTIFICACIÓN
//        Intent deleteIntent = new Intent(this.getString(R.string.filter_reject_call));
        Intent deleteIntent = new Intent();
        deleteIntent.setAction(this.getString(R.string.filter_reject_call));
        deleteIntent.putExtra("callStatus", "llamadaEntrante");
        deleteIntent.putExtra("llamadaVideo", llamadaVideo);
        deleteIntent.putExtra("idNotification", idNotification);

        PendingIntent deletePendingIntent = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            deletePendingIntent = PendingIntent.getBroadcast(this, idNotification, deleteIntent, FLAG_MUTABLE);

        } else {
            deletePendingIntent = PendingIntent.getBroadcast(this, idNotification, deleteIntent, FLAG_UPDATE_CURRENT);
        }

        //CONFIGURANDO ACCIÓN DE FULL SCREEN SOBRE LA NOTIFICACIÓN
        Intent fullScreenIntent = new Intent(this, VideoCallMainActivity.class);
        contentIntent.setAction("CALL_SCREEN");
        fullScreenIntent.putExtra("callStatus", "llamadaEntrante");
        fullScreenIntent.putExtra("llamadaVideo", llamadaVideo);
        fullScreenIntent.putExtra("idNotification", idNotification);


        PendingIntent fullScreenPendingIntent = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            fullScreenPendingIntent = PendingIntent.getActivity(this, idNotification, fullScreenIntent, FLAG_MUTABLE);
        } else {
            fullScreenPendingIntent = PendingIntent.getActivity(this, idNotification, fullScreenIntent, FLAG_UPDATE_CURRENT);
        }

        /*OJITO*/
        /*                .addAction(R.drawable.ic_oficios, "Contestar", answerPendingIntent)
         */
        // Construir la notificación personalizada
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, VOICE_CALL_NOTIFICATIONS_CHANNEL_ID)
                .setContentTitle("Videollamada entrante...")
                .setContentText(llamadaVideo.getParticipanteCaller().getNombreParticipante())
                .setSmallIcon(R.drawable.ic_oficios)
                .setContentIntent(contentPendingIntent)
                .setDeleteIntent(deletePendingIntent)
                .addAction(R.drawable.ic_oficios, "Contestar", answerPendingIntent)
                .addAction(R.drawable.ic_oficios, "Rechazar", deletePendingIntent)
                .setColor(Color.BLUE); // Color de fondo de la notificación (opcional)
        /*OJITO*/

        builder.setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_CALL)
                .setDeleteIntent(deletePendingIntent)
                .setFullScreenIntent(fullScreenPendingIntent, true);


        //MOSTRANDO NOTIFICACIÓN EN LA BANDEJA DE ENTRADA DE ANDROID
        notificationManager.notify(idNotification, builder.build());
    }

    public void createLlamadasVideoNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            CharSequence name = "Notificaciones de llamadas de video";
            String description = "Notificaciones  de llamadas de video entrantes y salientes";

            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(VOICE_CALL_NOTIFICATIONS_CHANNEL_ID, name, importance);
            channel.setDescription(description);
            channel.setSound(null, null);
            channel.enableVibration(true);
            channel.enableLights(true);
            channel.setLightColor(Color.RED);

            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
//            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public void listenerVideoLlamadas() {
        notifiLocaArrayList = new ArrayList<>();
        /*1. Crear el receiver*/
        rejectVideoCallBroadcastReceiver = new RejectVideoCallBroadcastReceiver();
        /*2. Registrar el receiver y la acción*/
//        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        IntentFilter filterReject = new IntentFilter();
        filterReject.addAction(this.getString(R.string.filter_reject_call));
        this.registerReceiver(rejectVideoCallBroadcastReceiver, filterReject);


        ChildEventListener childEventListenerVideoLlamadas = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                LlamadaVideo llamadaVideo = snapshot.getValue(LlamadaVideo.class);
                Participante participanteDestiny = llamadaVideo.getParticipanteDestiny();
                if (participanteDestiny.getIdParticipante().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                    final int min = 8000;
                    final int max = 9999;
                    int notificationId = new Random().nextInt((max - min) + 1) + min;
                    notifiLocaArrayList.add(new NotifiLoca(llamadaVideo.getId(), notificationId));
                    //arrayListNotificationIds.add(notificationId);
                    showVideoCallNotification(notificationId, llamadaVideo);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                LlamadaVideo llamadaVideo = snapshot.getValue(LlamadaVideo.class);
                Participante participanteDestiny = llamadaVideo.getParticipanteDestiny();
                if (participanteDestiny.getIdParticipante().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                    for (NotifiLoca not : notifiLocaArrayList) {
                        if (not.getIdDbFirebase().equals(llamadaVideo.getId())) {
                            notificationManager.cancel(not.getIdLocal());
                            notifiLocaArrayList.remove(not);
                            break;
                        }
                    }
// ...

// Luego, cuando desees cancelar la notificación, puedes hacerlo llamando a:
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        FirebaseDatabase.getInstance().getReference().child("videoCalls")
                .addChildEventListener(childEventListenerVideoLlamadas);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // Realiza cualquier inicialización aquí.
        //Toast.makeText(getApplicationContext(), "onCreate", Toast.LENGTH_SHORT).show();

        listenerVideoLlamadas();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        Toast.makeText(getApplicationContext(), "onStartCommand", Toast.LENGTH_LONG).show();
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        createNotificationChannel();
        createLlamadasVideoNotificationChannel();


        // Construye la notificación para el servicio en primer plano.
        Notification notification = buildNotification();

        // Inicia el servicio en primer plano.
        startForeground(1, notification);


        // Verificar si la acción personalizada se recibió (botón "Cerrar Servicio" presionado).
        if (intent != null && "STOP_SERVICE_ACTION".equals(intent.getAction())) {
            // Realizar cualquier limpieza o liberación de recursos necesarios aquí.

            // Detener el servicio en primer plano y el propio servicio.
            stopForegroundService();
        }

        // Realiza el trabajo en segundo plano aquí.

        // Si el sistema mata el servicio, intentará reiniciarlo automáticamente.
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Realiza cualquier limpieza o liberación de recursos aquí.
    }

    @Override
    public IBinder onBind(Intent intent) {
        // Si el servicio no es enlazado, devuelve null.
        return null;
    }

    // Método para detener el servicio
    private void stopForegroundService() {
        // Detener el estado de primer plano del servicio, manteniendo la notificación mostrada.
        stopForeground(true);

        // Detener el servicio.
        stopSelf();
        // O bien, si el servicio fue iniciado mediante startService desde otra componente, puedes detenerlo con:
        // stopService(new Intent(this, MyForegroundService.class));
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "MLayoutGptService Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );

            // Configurar el canal para que no tenga sonido predeterminado
            channel.setSound(null, null);

//            NotificationManager manager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private Notification buildNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentText(getText(R.string.notification_message))
                .setSmallIcon(R.drawable.ic_oficios);

        Intent stopIntent = new Intent(this, MLayoutGptService.class);
        stopIntent.setAction("STOP_SERVICE_ACTION");
        PendingIntent stopPendingIntent = null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            stopPendingIntent = PendingIntent.getService(this, 0, stopIntent, FLAG_MUTABLE);

        } else {
            stopPendingIntent = PendingIntent.getService(this, 0, stopIntent, FLAG_UPDATE_CURRENT);

            /*Bandera mala: PendingIntent.FLAG_IMMUTABLE*/
//
        }


        // Agregar la acción personalizada al botón.
        NotificationCompat.Action stopAction = new NotificationCompat.Action.Builder(
                R.drawable.icon_encryption,
                "Cerrar",
                stopPendingIntent
        ).build();

        // Agregar la acción a la notificación.
        builder.addAction(stopAction);

        // Devolver la notificación construida.
        return builder.build();

    }
}
