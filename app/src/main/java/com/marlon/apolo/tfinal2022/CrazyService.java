package com.marlon.apolo.tfinal2022;

import static android.app.PendingIntent.FLAG_MUTABLE;
import static android.app.PendingIntent.FLAG_UPDATE_CURRENT;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Icon;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.Person;
import androidx.core.app.RemoteInput;
import androidx.preference.PreferenceManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.marlon.apolo.tfinal2022.citasTrabajo.receivers.AlarmReceiver;
import com.marlon.apolo.tfinal2022.citasTrabajo.view.DetalleServicioActivity;
import com.marlon.apolo.tfinal2022.communicationAgora.video.receivers.AcceptVideoCallBroadcastReceiver;
import com.marlon.apolo.tfinal2022.communicationAgora.video.view.VideoCallMainActivity;
import com.marlon.apolo.tfinal2022.communicationAgora.voice.receivers.AcceptVoiceCallBroadcastReceiver;
import com.marlon.apolo.tfinal2022.communicationAgora.video.view.AgoraVideoCallActivity;
import com.marlon.apolo.tfinal2022.communicationAgora.video.receivers.RejectVideoCallBroadcastReceiver;
import com.marlon.apolo.tfinal2022.communicationAgora.voice.receivers.RejectVoiceCallBroadcastReceiver;

import com.marlon.apolo.tfinal2022.communicationAgora.voice.view.AgoraVoiceCallActivityPoc;
import com.marlon.apolo.tfinal2022.communicationAgora.voice.view.VoiceCallMainActivity;
import com.marlon.apolo.tfinal2022.individualChat.model.MessageCloudPoc;
import com.marlon.apolo.tfinal2022.individualChat.receivers.CrazyDeleteBroadcastReceiver;
import com.marlon.apolo.tfinal2022.individualChat.view.CrazyIndividualChatActivity;
import com.marlon.apolo.tfinal2022.individualChat.receivers.CrazyReplyBroadcastReceiver;
import com.marlon.apolo.tfinal2022.model.Cita;
import com.marlon.apolo.tfinal2022.model.Empleador;
import com.marlon.apolo.tfinal2022.model.LlamadaVideo;
import com.marlon.apolo.tfinal2022.model.LlamadaVoz;
import com.marlon.apolo.tfinal2022.model.NotificacionStack;
import com.marlon.apolo.tfinal2022.model.Participante;
import com.marlon.apolo.tfinal2022.model.Trabajador;
import com.marlon.apolo.tfinal2022.puntoEntrada.view.MainActivity;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;

public class CrazyService extends Service {


    private static final String FOREGROUND_CHANNEL_ID = "FOREGROUND_CHANNEL_ID";
    private static final int ONGOING_NOTIFICATION_ID = 8000;
    private static final String NOTIFICATIONS_CHANNEL_ID = "NOTIFICATIONS_CHANNEL_ID";
    private static final String VOICE_CALL_NOTIFICATIONS_CHANNEL_ID = "VOICE_CALL_NOTIFICATIONS_CHANNEL_ID";
    private static final String TAG = CrazyService.class.getSimpleName();
    private static final String ACTION_DELETE_NOTIFICATION = "ACTION_DELETE_NOTIFICATION";
    private static final String KEY_TEXT_REPLY = "KEY_TEXT_REPLY";
    private static final String ACTION_REPLY_NOTIFICATION = "ACTION_REPLY_NOTIFICATION";


    private static final String VIDEO_CALLS_CHANNEL_ID = "VIDEO_CALLS_CHANNEL_ID";
    private static final String NOTIF_TEMPORALES_CHANNEL_ID = "NOTIFICACIONES_TEMPORALES";


    private DatabaseReference databaseReference;

    private DatabaseReference mNotificacionesRef;


    private Context contextInstance;
    private ArrayList<NotificacionStack> notificacionStackArrayList;


    private CrazyDeleteBroadcastReceiver crazyDeleteBroadcastReceiver;
    private CrazyReplyBroadcastReceiver crazyReplyBroadcastReceiver;
    private SharedPreferences myPreferences;


    private SharedPreferences defaultSharedPreferences;
    private MediaPlayer mediaPlayerCallTone;
    private int startIdLocal;


    private ChildEventListener childEventListenerNotificacionesConMensajesLocas;
    private ChildEventListener childEventListenerCitasTrabajo;
    private ChildEventListener childEventListenerVideoCalls;
    private BroadcastReceiver acceptVideoCallBroadcastReceiver;
    private BroadcastReceiver rejectVideoCallBroadcastReceiver;
    private HashMap<String, Integer> meMap;
    private HashMap<String, Integer> meMapVoice;
    private ChildEventListener childEventListenerVoiceCalls;
    private BroadcastReceiver rejectVoiceCallBroadcastReceiver;
    private BroadcastReceiver acceptVoiceCallBroadcastReceiver;

//    public class NotificacionStack {
//        ArrayList<MessageCloudPoc> mensajeNubes;
//        private int idNotification;
//        private long numberMessages;
//
//        public NotificacionStack() {
//        }
//
//        public ArrayList<MessageCloudPoc> getMensajeNubes() {
//            return mensajeNubes;
//        }
//
//        public void setMensajeNubes(ArrayList<MessageCloudPoc> mensajeNubes) {
//            this.mensajeNubes = mensajeNubes;
//        }
//
//        public int getIdNotification() {
//            return idNotification;
//        }
//
//        public void setIdNotification(int idNotification) {
//            this.idNotification = idNotification;
//        }
//
//        public long getNumberMessages() {
//            return numberMessages;
//        }
//
//        public void setNumberMesssages(long numberMessages) {
//            this.numberMessages = numberMessages;
//        }
//
//        @Override
//        public String toString() {
//            return "NotificacionCustom{" +
//                    "mensajeNubes=" + mensajeNubes +
//                    ", idNotification=" + idNotification +
//                    ", numberMessages=" + numberMessages +
//                    '}';
//        }
//    }


    public void createForegroundNotificationChannel() {

        Log.d(TAG, "########################################");
        Log.d(TAG, "createForegroundNotificationChannel");
        Log.d(TAG, "########################################");
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            CharSequence name = "Notificación en primer plano";
            String description = "Servicio de notificaciones entrantes y salientes";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(FOREGROUND_CHANNEL_ID, name, importance);
            channel.setDescription(description);
            channel.setSound(defaultSoundUri, null);
            channel.enableVibration(true);
            channel.enableLights(true);
            channel.setLightColor(Color.RED);

            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public void createNotificationChannel() {
        Log.d(TAG, "########################################");
        Log.d(TAG, "createNotificationChannel");
        Log.d(TAG, "########################################");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {


            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            CharSequence name = "Notificaciones de mensajes";
            String description = "Notificaciones  de mensajes entrantes y salientes";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(NOTIFICATIONS_CHANNEL_ID, name, importance);
            channel.setDescription(description);
            channel.setSound(defaultSoundUri, null);
            channel.enableVibration(true);
            channel.enableLights(true);
            channel.setLightColor(Color.RED);

            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public void createLlamadasVozNotificationChannel() {
        Log.d(TAG, "########################################");
        Log.d(TAG, "createNotificationChannel");
        Log.d(TAG, "########################################");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            CharSequence name = "Notificaciones de llamadas de voz";
            String description = "Notificaciones  de llamadas de voz entrantes y salientes";

            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(VOICE_CALL_NOTIFICATIONS_CHANNEL_ID, name, importance);
            channel.setDescription(description);
            channel.setSound(defaultSoundUri, null);
            channel.enableVibration(true);
            channel.enableLights(true);
            channel.setLightColor(Color.RED);

            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public void sendForegroundNotification() {
        // If the notification supports a direct reply action, use
// PendingIntent.FLAG_MUTABLE instead.
        Log.d(TAG, "########################################");
        Log.d(TAG, "sendForegroundNotification");
        Log.d(TAG, "########################################");
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent =
                PendingIntent.getActivity(this, ONGOING_NOTIFICATION_ID, notificationIntent,
                        PendingIntent.FLAG_IMMUTABLE);
        /*Si no se llama a la notificacion en los proximos 5 segundos, la aplicación crashea*/

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            Notification notification = new Notification.Builder(this, FOREGROUND_CHANNEL_ID)
//                    .setContentTitle(getText(R.string.notification_title) + "-" + String.valueOf(Build.VERSION.SDK_INT))
                    .setContentTitle(getText(R.string.notification_title))
                    .setContentText(getText(R.string.notification_message))
                    .setSmallIcon(R.drawable.ic_oficios)
                    .setContentIntent(pendingIntent)
                    .setStyle(new Notification.BigTextStyle()
                            .bigText(getText(R.string.notification_message)))
                    .setTicker(getText(R.string.ticker_text))
                    .setPriority(Notification.PRIORITY_HIGH)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .build();
            // Notification ID cannot be 0.
            startForeground(ONGOING_NOTIFICATION_ID, notification);

        } else {
            Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
            if (alarmSound == null) {
                alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
                if (alarmSound == null) {
                    alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                }
            }
            NotificationCompat.Builder notification = new NotificationCompat.Builder(this, FOREGROUND_CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_oficios)
                    .setContentTitle(getText(R.string.notification_title) + "-" + String.valueOf(Build.VERSION.SDK_INT))
                    .setContentText(getText(R.string.notification_message))
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    // Set the intent that will fire when the user taps the notification
                    .setStyle(new NotificationCompat.BigTextStyle()
                            .bigText(getText(R.string.notification_message)))
                    .setContentIntent(pendingIntent)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setDefaults(NotificationCompat.DEFAULT_ALL)
                    .setSound(alarmSound)
                    .setAutoCancel(true);
            // Notification ID cannot be 0.
            startForeground(ONGOING_NOTIFICATION_ID, notification.build());

        }
    }

    public void createVideoCallChannel() {
        Log.d(TAG, "########################################");
        Log.d(TAG, "createVideoCallChannel");
        Log.d(TAG, "########################################");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {


            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            CharSequence name = "Notificaciones de videollamadas";
            String description = "Notificaciones  de videollamadas entrantes y salientes";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(VIDEO_CALLS_CHANNEL_ID, name, importance);
            channel.setDescription(description);
            channel.setSound(defaultSoundUri, null);
            channel.enableVibration(true);
            channel.enableLights(true);
            channel.setLightColor(Color.RED);
//            AudioAttributes audioAttributes = new AudioAttributes.Builder()
//                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
//                    .build();
//            channel.setSound(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.skype_caller_tone), audioAttributes);


            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public void createNotificationsTempChannel() {
        Log.d(TAG, "########################################");
        Log.d(TAG, "createVideoCallChannel");
        Log.d(TAG, "########################################");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {


            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            CharSequence name = "Notificaciones temporales";
            String description = "Notificaciones temporales de recordatorios";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(NOTIF_TEMPORALES_CHANNEL_ID, name, importance);
            channel.setDescription(description);
            channel.setSound(defaultSoundUri, null);
            channel.enableVibration(true);
            channel.enableLights(true);
            channel.setLightColor(Color.RED);

            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public void stopService(Context context) {
        Log.d(TAG, "Stoping service");
        try {
            Intent stopIntent = new Intent(context, CrazyService.class);
            context.stopService(stopIntent);
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }

    }

    public void listenerForegroundAdminFunctions() {
        FirebaseDatabase.getInstance().getReference()
                .child("usuariosEliminados")
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        String key = snapshot.getKey();
                        try {
                            if (key.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                                stopService(contextInstance);
                                FirebaseAuth.getInstance().signOut();

                            }
                        } catch (Exception e) {
                            Log.d(TAG, e.toString());
                        }
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }

    public void listenerVoiceCalls() {
        meMapVoice = new HashMap<String, Integer>();
//        meMap.put("Color2","Blue");
//        meMap.put("Color3","Green");
//        meMap.put("Color4","White");


        /*1. Crear el receiver*/
        acceptVoiceCallBroadcastReceiver = new AcceptVoiceCallBroadcastReceiver();
        /*2. Registrar el receiver y la acción*/
//        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        IntentFilter filterAnswer = new IntentFilter();
        filterAnswer.addAction(contextInstance.getString(R.string.filter_incoing_voice_call));
        this.registerReceiver(acceptVoiceCallBroadcastReceiver, filterAnswer);

        /*1. Crear el receiver*/
        rejectVoiceCallBroadcastReceiver = new RejectVoiceCallBroadcastReceiver();
        /*2. Registrar el receiver y la acción*/
//        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        IntentFilter filterReject = new IntentFilter();
        filterReject.addAction(contextInstance.getString(R.string.filter_reject_voice_call));
        this.registerReceiver(rejectVoiceCallBroadcastReceiver, filterReject);


        childEventListenerVoiceCalls = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                LlamadaVoz llamadaVoz = snapshot.getValue(LlamadaVoz.class);
                Participante participanteDestiny = llamadaVoz.getParticipanteDestiny();
                if (participanteDestiny.getIdParticipante().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                    final int min = 10000;
                    final int max = 10999;
                    int notificationId = new Random().nextInt((max - min) + 1) + min;
                    //arrayListNotificationIds.add(notificationId);
                    meMapVoice.put(llamadaVoz.getId(), notificationId);
                    NotificationCompat.Builder videoCallNotification = createVoiceCallNotification(llamadaVoz, notificationId);
                    showVoiceCallNotification(videoCallNotification, notificationId);
                }

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                LlamadaVoz llamadaVozChanged = snapshot.getValue(LlamadaVoz.class);
                Participante participanteDestiny = llamadaVozChanged.getParticipanteDestiny();
                if (participanteDestiny.getIdParticipante().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                    //*contestando llamada*/
                    if (llamadaVozChanged.isDestinyStatus() && llamadaVozChanged.isChannelConnectedStatus()) {
                        stopPlaying();
                    }
                    //*rechanzando llamada*/
                    if (llamadaVozChanged.isRejectCallStatus()) {
                        stopPlaying();
                    }
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                LlamadaVoz llamadaVozRemoved = snapshot.getValue(LlamadaVoz.class);
                Participante participanteDestiny = llamadaVozRemoved.getParticipanteDestiny();
                if (participanteDestiny.getIdParticipante().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                    cancelNotification(meMapVoice.get(llamadaVozRemoved.getId()));
//                    arrayListNotificationIds.remove(0);
                    stopPlaying();
                    meMapVoice.remove(llamadaVozRemoved.getId());
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        FirebaseDatabase.getInstance().getReference().child("voiceCalls")
                .addChildEventListener(childEventListenerVoiceCalls);
    }

    public void listenerVideoCalls() {

        Log.d(TAG, "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
        Log.d(TAG, "listenerVideoCalls");
        Log.d(TAG, "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
//        arrayListNotificationIds = new ArrayList<>();


        /*1. Crear el receiver*/
        acceptVideoCallBroadcastReceiver = new AcceptVideoCallBroadcastReceiver();
        /*2. Registrar el receiver y la acción*/
//        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        IntentFilter filterAnswer = new IntentFilter();
        filterAnswer.addAction(contextInstance.getString(R.string.filter_incoing_call));
        this.registerReceiver(acceptVideoCallBroadcastReceiver, filterAnswer);

        /*1. Crear el receiver*/
        rejectVideoCallBroadcastReceiver = new RejectVideoCallBroadcastReceiver();
        /*2. Registrar el receiver y la acción*/
//        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        IntentFilter filterReject = new IntentFilter();
        filterReject.addAction(contextInstance.getString(R.string.filter_reject_call));
        this.registerReceiver(rejectVideoCallBroadcastReceiver, filterReject);


        meMap = new HashMap<String, Integer>();
//        meMap.put("Color2","Blue");
//        meMap.put("Color3","Green");
//        meMap.put("Color4","White");


        childEventListenerVideoCalls = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                LlamadaVideo llamadaVideo = snapshot.getValue(LlamadaVideo.class);
                Participante participanteDestiny = llamadaVideo.getParticipanteDestiny();
                if (participanteDestiny.getIdParticipante().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                    final int min = 8000;
                    final int max = 9999;
                    int notificationId = new Random().nextInt((max - min) + 1) + min;
                    //arrayListNotificationIds.add(notificationId);
                    meMap.put(llamadaVideo.getId(), notificationId);
                    NotificationCompat.Builder videoCallNotification = createVideoCallNotification(llamadaVideo, notificationId);
                    showVideoCallNotification(videoCallNotification, notificationId);
                }

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                LlamadaVideo llamadaVideoChanged = snapshot.getValue(LlamadaVideo.class);
                Participante participanteDestiny = llamadaVideoChanged.getParticipanteDestiny();
                if (participanteDestiny.getIdParticipante().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                    //*contestando llamada*/
                    if (llamadaVideoChanged.isDestinyStatus() && llamadaVideoChanged.isChannelConnectedStatus()) {
                        stopPlaying();
                    }
                    //*rechanzando llamada*/
                    if (llamadaVideoChanged.isRejectCallStatus()) {
                        stopPlaying();
                    }
                }
                Log.d(TAG, "onChildChanged");
                Log.d(TAG, String.valueOf(llamadaVideoChanged.isRejectCallStatus()));
                Log.d(TAG, String.valueOf(llamadaVideoChanged));
                Log.d(TAG, String.valueOf(meMap.get(llamadaVideoChanged.getId())));
                stopPlaying();

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                LlamadaVideo llamadaVideo = snapshot.getValue(LlamadaVideo.class);
                Participante participanteDestiny = llamadaVideo.getParticipanteDestiny();
                if (participanteDestiny.getIdParticipante().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                    cancelNotification(meMap.get(llamadaVideo.getId()));
//                    arrayListNotificationIds.remove(0);
                    stopPlaying();
                    meMap.remove(llamadaVideo.getId());
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
                .addChildEventListener(childEventListenerVideoCalls);
    }

    public NotificationCompat.Builder createVideoCallNotification(LlamadaVideo llamadaVideo, int notificationId) {

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, VIDEO_CALLS_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_oficios)
                .setContentTitle(new String(Character.toChars(0x1F4F9)) + "Videollamada entrante...")
                .setContentText(llamadaVideo.getParticipanteCaller().getNombreParticipante())
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        Intent rejectIntent = new Intent();
        rejectIntent.setAction(contextInstance.getString(R.string.filter_reject_call));
        rejectIntent.putExtra("callStatus", "llamadaEntrante");
        rejectIntent.putExtra("llamadaVideo", llamadaVideo);
        rejectIntent.putExtra("idNotification", notificationId);

//        Intent fullScreenIntent = new Intent(this, AgoraVideoCallActivity.class);
        Intent answerIntent = new Intent(this, VideoCallMainActivity.class);
        answerIntent.setAction("CALL_ANSWER");
        answerIntent.putExtra("callStatus", "llamadaEntrante");
        answerIntent.putExtra("llamadaVideo", llamadaVideo);
        answerIntent.putExtra("idNotification", notificationId);


        Intent contentIntent = new Intent(this, VideoCallMainActivity.class);
        contentIntent.setAction("CALL_SCREEN");
        contentIntent.putExtra("callStatus", "llamadaEntrante");
        contentIntent.putExtra("llamadaVideo", llamadaVideo);
        contentIntent.putExtra("idNotification", notificationId);

        PendingIntent contentPendingIntent = null;

        PendingIntent answerPendingIntent = null;

        PendingIntent rejectPendingIntent = null;


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            contentPendingIntent = PendingIntent.getActivity(this, notificationId,
                    contentIntent, PendingIntent.FLAG_MUTABLE);

            answerPendingIntent = PendingIntent.getActivity(this, notificationId,
                    answerIntent, PendingIntent.FLAG_MUTABLE);

            rejectPendingIntent = PendingIntent.getBroadcast(contextInstance, notificationId,
                    rejectIntent, FLAG_MUTABLE);

        } else {
            contentPendingIntent = PendingIntent.getActivity(this, notificationId,
                    contentIntent, FLAG_UPDATE_CURRENT);

            answerPendingIntent = PendingIntent.getActivity(this, notificationId,
                    answerIntent, FLAG_UPDATE_CURRENT);

            rejectPendingIntent = PendingIntent.getBroadcast(contextInstance, notificationId,
                    rejectIntent, FLAG_UPDATE_CURRENT);
        }

        NotificationCompat.Action actionAccept =
                new NotificationCompat.Action.Builder(R.drawable.ic_baseline_navigate_next_24,
                        "Contestar",
                        answerPendingIntent)
                        .build();

        NotificationCompat.Action actionReject =
                new NotificationCompat.Action.Builder(R.drawable.ic_baseline_navigate_next_24,
                        "Rechazar",
                        rejectPendingIntent)
                        .build();

        builder.addAction(actionAccept);
        builder.addAction(actionReject);
//        builder.setSound(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.bomberman));

//        builder.setSound(Uri.parse("android.resource://" + getPackageName() + "/raw/" + R.raw.bomberman));

        builder.setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_CALL)
                .setDeleteIntent(rejectPendingIntent)
                .setFullScreenIntent(contentPendingIntent, true);

        return builder;
    }


    public NotificationCompat.Builder createVoiceCallNotification(LlamadaVoz llamadaVideo, int notificationId) {

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, VIDEO_CALLS_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_oficios)
                .setContentTitle(new String(Character.toChars(0x260E)) + "Llamada entrante...")
                .setContentText(llamadaVideo.getParticipanteCaller().getNombreParticipante())
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        // Build a PendingIntent for the reply action to trigger.

        Intent answerIntent = new Intent(this, VoiceCallMainActivity.class);
        answerIntent.setAction("CALL_ANSWER");
        answerIntent.putExtra("callStatus", "llamadaEntrante");
        answerIntent.putExtra("llamadaVoz", llamadaVideo);
        answerIntent.putExtra("idNotification", notificationId);

        Intent rejectIntent = new Intent();
        rejectIntent.setAction(contextInstance.getString(R.string.filter_reject_voice_call));
        rejectIntent.putExtra("callStatus", "llamadaEntrante");
        rejectIntent.putExtra("llamadaVoz", llamadaVideo);
        rejectIntent.putExtra("notificationId", notificationId);


        Intent fullScreenIntent = new Intent(this, VoiceCallMainActivity.class);
        fullScreenIntent.setAction("CALL_SCREEN");
        fullScreenIntent.putExtra("callStatus", "llamadaEntrante");
        fullScreenIntent.putExtra("llamadaVoz", llamadaVideo);
        fullScreenIntent.putExtra("idNotification", notificationId);

//        fullScreenIntent.putExtra("joinValue", "false");
        //fullScreenIntent.putExtra("extraJoin", "noconectar");

        PendingIntent rejectPendingIntent = null;
        PendingIntent answerPendingIntent = null;
        PendingIntent fullScreenPendingIntent = null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            rejectPendingIntent = PendingIntent.getBroadcast(contextInstance, notificationId, rejectIntent, FLAG_MUTABLE);

            answerPendingIntent =
                    PendingIntent.getActivity(contextInstance, notificationId, answerIntent, FLAG_MUTABLE);

            fullScreenPendingIntent = PendingIntent.getActivity(this, notificationId,
                    fullScreenIntent, PendingIntent.FLAG_MUTABLE);
        } else {
            fullScreenPendingIntent = PendingIntent.getActivity(this, notificationId,
                    fullScreenIntent, FLAG_UPDATE_CURRENT);
            rejectPendingIntent = PendingIntent.getBroadcast(contextInstance, notificationId, rejectIntent, FLAG_UPDATE_CURRENT);

            answerPendingIntent =
                    PendingIntent.getActivity(contextInstance, notificationId, answerIntent, FLAG_UPDATE_CURRENT);
            /*Bandera mala: PendingIntent.FLAG_IMMUTABLE*/
//
        }
//
//        // Create the reply action and add the remote input.
        NotificationCompat.Action actionAccept =
                new NotificationCompat.Action.Builder(R.drawable.ic_baseline_navigate_next_24,
                        "Contestar",
                        answerPendingIntent)
                        .build();
        builder.addAction(actionAccept);

        NotificationCompat.Action actionReject =
                new NotificationCompat.Action.Builder(R.drawable.ic_baseline_navigate_next_24,
                        "Rechazar",
                        rejectPendingIntent)
                        .build();

        builder.addAction(actionReject);


        builder.setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_CALL)
                .setDeleteIntent(rejectPendingIntent)
                .setFullScreenIntent(fullScreenPendingIntent, true);

        return builder;
    }

    public void showTempNotification(NotificationCompat.Builder builder, int notificationId) {

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(contextInstance);

        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(notificationId, builder.build());


    }


    public void showVideoCallNotification(NotificationCompat.Builder builder, int notificationId) {

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(contextInstance);

        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(notificationId, builder.build());
//        Toast.makeText(getApplicationContext(), "Notificando a lo loco", Toast.LENGTH_LONG).show();

        try {
            if (meMap.size() > 1) {

            } else {
                playingInconmingVideoCallAudio();
            }
        } catch (Exception e) {

        }

//        playingInconmingVideoCallAudio();
//        mediaPlayerCallTone = MediaPlayer.create(contextInstance, R.raw.skype_caller_tone);
//        mediaPlayerCallTone.setAudioStreamType(AudioManager.STREAM_MUSIC);
//        try {
//            mediaPlayerCallTone.prepare();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        mediaPlayerCallTone.setLooping(true);
//        mediaPlayerCallTone.start(); // no need to call prepare(); create() does that for you


    }

    public void showVoiceCallNotification(NotificationCompat.Builder builder, int notificationId) {

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(contextInstance);

        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(notificationId, builder.build());

        try {
            if (meMapVoice.size() > 1) {

            } else {
                playingInconmingCallAudio();
            }
        } catch (Exception e) {

        }
//        mediaPlayerCallTone = MediaPlayer.create(contextInstance, R.raw.skype_caller_tone);
//        mediaPlayerCallTone.setAudioStreamType(AudioManager.STREAM_MUSIC);
//        try {
//            mediaPlayerCallTone.prepare();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        mediaPlayerCallTone.setLooping(true);
//        mediaPlayerCallTone.start(); // no need to call prepare(); create() does that for you


    }

    public NotificationCompat.Builder createTempNotification(Cita cita, int notificationId) {

        Intent contentIntent = new Intent(this, DetalleServicioActivity.class);
        contentIntent.putExtra("cita", cita);
        contentIntent.putExtra("notificationId", notificationId);
        PendingIntent contentPendingIntent = null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            contentPendingIntent = PendingIntent.getActivity
                    (contextInstance, notificationId, contentIntent, FLAG_MUTABLE);
        } else {
            contentPendingIntent = PendingIntent.getActivity
                    (contextInstance, notificationId, contentIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        }
        String text = "";

        SharedPreferences mPreferences = contextInstance.getSharedPreferences("MyPreferences", MODE_PRIVATE);
        int usuario = mPreferences.getInt("usuario", -1);

        switch (usuario) {
            case 0:
                text = "Usted tiene una cita de trabajo con: " + cita.getNombreTrabajador() + " el " + cita.getFechaCita();
            case 1:
                text = "Usted tiene una cita de trabajo con: " + cita.getNombreTrabajador() + " el " + cita.getFechaCita();
                break;
            case 2:
                text = "Usted tiene una cita de trabajo con: " + cita.getNombreEmpleador() + " el " + cita.getFechaCita();
                break;
        }


        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, NOTIF_TEMPORALES_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_oficios)
                .setPriority(NotificationCompat.PRIORITY_HIGH);


        builder.setContentTitle("Recordatorio!")
                .setContentText(text)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(text))
                .setContentIntent(contentPendingIntent);


        builder.setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_CALL);

        return builder;
    }


//    private void loadUsuarioLocal() {
//        FirebaseDatabase.getInstance().getReference()
//                .child("administrador")
//                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
//                .addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        Administrador administrador = snapshot.getValue(Administrador.class);
//                        if (administrador != null) {
//                            usuarioLocal = administrador;
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//
//                    }
//                });
//        FirebaseDatabase.getInstance().getReference()
//                .child("trabajadores")
//                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
//                .addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        Trabajador trabajador = snapshot.getValue(Trabajador.class);
//                        if (trabajador != null) {
//                            usuarioLocal = trabajador;
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//
//                    }
//                });
//        FirebaseDatabase.getInstance().getReference()
//                .child("empleadores")
//                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
//                .addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        Empleador empleador = snapshot.getValue(Empleador.class);
//                        if (empleador != null) {
//                            usuarioLocal = empleador;
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//
//                    }
//                });
//    }


    public void listenerMessagePocNotificacionesFirebase() {
//        basicListen();
        registerReceiver(crazyDeleteBroadcastReceiver, new IntentFilter(ACTION_DELETE_NOTIFICATION));
        registerReceiver(crazyReplyBroadcastReceiver, new IntentFilter(ACTION_REPLY_NOTIFICATION));

        childEventListenerRecycler();
    }

    public void playingInconmingCallAudio() {
//        mediaPlayerCallTone = MediaPlayer.create(contextInstance, R.raw.beat_it_gameboy);
//        mediaPlayerCallTone = MediaPlayer.create(contextInstance, R.raw.katyusha_8_bit);
        mediaPlayerCallTone = MediaPlayer.create(contextInstance, R.raw.bomberman);
        mediaPlayerCallTone.setLooping(true);
        mediaPlayerCallTone.start(); // no need to call prepare(); create() does that for you
    }

    public void playingInconmingVideoCallAudio() {
//        mediaPlayerCallTone = MediaPlayer.create(contextInstance, R.raw.beat_it_gameboy);
//        mediaPlayerCallTone = MediaPlayer.create(contextInstance, R.raw.skype_caller_tone);
        mediaPlayerCallTone = MediaPlayer.create(contextInstance, R.raw.bomberman);
        mediaPlayerCallTone.setLooping(true);
        mediaPlayerCallTone.start(); // no need to call prepare(); create() does that for you

        // Start the MediaPlayerService to play the sound


    }

    public void stopPlaying() {
        try {
            mediaPlayerCallTone.stop();
            if (mediaPlayerCallTone != null) mediaPlayerCallTone.release();
        } catch (Exception e) {
            Log.d(TAG, e.toString());
        }

    }


    public void programarAlarmaLocal(Cita cita) {

        final int min = 7000;
        final int max = 7999;
        int random = new Random().nextInt((max - min) + 1) + min;


        AlarmManager alarmMgr;
        PendingIntent alarmIntent;
        alarmMgr = (AlarmManager) contextInstance.getSystemService(Context.ALARM_SERVICE);


        Log.d(TAG, "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
        Log.d(TAG, "CONFIGURANDO ALARMA REMOTA loco");
        Log.d(TAG, "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");

        Intent intent = new Intent(contextInstance, AlarmReceiver.class);
        intent.putExtra("idCita", cita.getIdCita());
        //alarmIntent1.putExtra("cita", cita);
        intent.putExtra("nT", cita.getNombreTrabajador());
        intent.putExtra("nE", cita.getNombreEmpleador());
        intent.putExtra("idFrom", cita.getFrom());
        intent.putExtra("idTo", cita.getTo());
        intent.putExtra("fec", cita.getFechaCita());
        intent.putExtra("random", random);

        DateFormat formatFecD = new SimpleDateFormat("dd MMMM yyyy HH:mm a", new Locale("es", "ES"));
        Calendar calendarD = Calendar.getInstance();
        try {
            calendarD.setTime(Objects.requireNonNull(formatFecD.parse(cita.getFechaCita())));
        } catch (Exception e) {
            Log.d(TAG, e.toString());

        }
        Log.d(TAG, "calendarX:" + String.valueOf(formatFecD.format(calendarD.getTime())) + ":");
        Log.d(TAG, "FecÑ:" + cita.getFechaCita() + ":");


        Log.d(TAG, "SDK VERSION: " + String.valueOf(Build.VERSION.SDK_INT));
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//            alarmIntent = PendingIntent.getBroadcast(this,
//                    1700, intent, FLAG_MUTABLE);
//        } else {
//            alarmIntent = PendingIntent.getBroadcast(this,
//                    1700, intent, 0);
//        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            alarmIntent = PendingIntent.getBroadcast
                    (getApplicationContext(), random, intent,
                            FLAG_MUTABLE);
        } else {
            alarmIntent = PendingIntent.getBroadcast
                    (getApplicationContext(), random, intent,
                            FLAG_UPDATE_CURRENT);
//
        }


//        alarmMgr.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,
//                SystemClock.elapsedRealtime() +
//                        60 * 1000, alarmIntent);


        Date date = null;


        try {
//            date = new SimpleDateFormat("dd MMMM yyyy HH:mm", new Locale("es", "ES")).parse("13 agosto 2022 22:50");
            date = new SimpleDateFormat("dd MMMM yyyy HH:mm", new Locale("es", "ES")).parse(cita.getFechaCita());
            String fecha = new SimpleDateFormat("dd MMMM yyyy HH:mm", new Locale("es", "ES")).format(date);
            Log.d(TAG, "fecha de mrda: " + fecha);
            Calendar calendarz = Calendar.getInstance();
            calendarz.setTime(date);
            alarmMgr.set(AlarmManager.RTC_WAKEUP, calendarz.getTimeInMillis(), alarmIntent);
            Log.e(TAG, "ALARMA CONFIGURADA");


            final int minx = 10000;
            final int maxx = 10999;
            int notificationIdx = new Random().nextInt((maxx - minx) + 1) + minx;
            NotificationCompat.Builder tempNotification = createTempNotification(cita, notificationIdx);
            showTempNotification(tempNotification, notificationIdx);


        } catch (ParseException e) {
            Log.e(TAG, "fecha de mrda: " + e);
            e.printStackTrace();
        }

        /*try {
//            date = new SimpleDateFormat("dd MMMM yyyy HH:mm", new Locale("es", "ES")).parse("13 agosto 2022 22:50");
            date = new SimpleDateFormat("dd MMMM yyyy HH:mm", new Locale("es", "ES")).parse(cita.getFechaCita());
            String fecha = new SimpleDateFormat("dd MMMM yyyy HH:mm", new Locale("es", "ES")).format(date);
            Log.d(TAG, "fecha de mrda: " + fecha);
            Calendar calendarz = Calendar.getInstance();
            calendarz.setTime(date);

        } catch (ParseException e) {
            Log.e(TAG, "fecha de mrda: " + e);
            e.printStackTrace();
        }*/


//        String dateInString = cita.getFechaCita();
//
//        DateFormat formatFecx = new SimpleDateFormat("dd MMMM yyyy HH:mm a", Locale.getDefault());
//        Calendar calendarXx = Calendar.getInstance();
//        try {
//            calendarXx.setTime(Objects.requireNonNull(formatFecx.parse(cita.getFechaCita())));
//        } catch (Exception e) {
//            Log.d(TAG, e.toString());
//        }
//
//
//        SimpleDateFormat sdfx = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
//        Date datex = null;
//        try {
//            datex = sdfx.parse(cita.getFechaCita());
//        } catch (ParseException e) {
//            Log.d(TAG, e.toString());
//        }
//        Calendar calx = Calendar.getInstance();
//        calx.setTime(datex);
//
////        Log.d(TAG, "Fecha cita" + cita.getFechaCita());
////        Log.d(TAG, "calendarX" + String.valueOf(formatFecx.format(calendarXx.getTime())));
////        Log.d(TAG, "calendarX" + String.valueOf(formatFecx.format(calx.getTime())));
////        Log.d(TAG, "Hours: " + String.valueOf(calendarXx.getTime().getHours()));
////        Log.d(TAG, "Minutes: " + String.valueOf(calendarXx.getTime().getMinutes()));
////        Log.d(TAG, "Date: " + String.valueOf(calendarXx.getTime().getDate()
////        ));
//
//
//        // Set the alarm to start at 8:30 a.m.
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTimeInMillis(System.currentTimeMillis());
//        calendar.set(Calendar.MILLISECOND, 0);
//        calendar.set(Calendar.SECOND, 0);
//
//        calendar.set(Calendar.HOUR_OF_DAY, calendarXx.getTime().getHours());
////        calendar.set(Calendar.HOUR_OF_DAY, calendarXx.getTime().getHours());
////        calendar.set(Calendar.MINUTE, 4);
//        calendar.set(Calendar.MINUTE, 33);
//
//
//        calendarXx.set(Calendar.MILLISECOND, 0);
//        calendarXx.set(Calendar.SECOND, 0);

        // setRepeating() lets you specify a precise custom interval--in this case,
        // 20 minutes.

//        try {
////            date = new SimpleDateFormat("dd MMMM yyyy HH:mm", new Locale("es", "ES")).parse("13 agosto 2022 22:50");
//            date = new SimpleDateFormat("dd MMMM yyyy HH:mm", new Locale("es", "ES")).parse(cita.getFechaCita());
//            String fecha = new SimpleDateFormat("dd MMMM yyyy HH:mm", new Locale("es", "ES")).format(date);
//            Log.d(TAG, "fecha de mrda: " + fecha);
//            Calendar calendarz = Calendar.getInstance();
//            calendarz.setTime(date);
//            alarmMgr.set(AlarmManager.RTC_WAKEUP, calendarz.getTimeInMillis(), alarmIntent);
//            Log.e(TAG, "ALARMA CONFIGURADA");
//
//        } catch (ParseException e) {
//            Log.e(TAG, "fecha de mrda: " + e);
//            e.printStackTrace();
//        }

//        alarmMgr.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), alarmIntent);
//        alarmMgr.set(AlarmManager.RTC_WAKEUP, calendarXx.getTimeInMillis(), alarmIntent);
//        alarmMgr.set(AlarmManager.ELAPSED_REALTIME, calendarXx.getTimeInMillis(), alarmIntent); no sirve
//        alarmMgr.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, calendarXx.getTimeInMillis(), alarmIntent); no sirve


        //        Toast.makeText(getApplicationContext(), "Programando alarma local", Toast.LENGTH_LONG).show();

//        final int min = 7000;
//        final int max = 7999;
//        int random = new Random().nextInt((max - min) + 1) + min;


//        final AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
//        Intent alarmIntent1 = new Intent(this, AlarmReceiver.class);
//
//        Log.d(TAG, cita.toString());
//        Log.d(TAG, "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
//        alarmIntent1.putExtra("idCita", cita.getIdCita());
//        //alarmIntent1.putExtra("cita", cita);
//        alarmIntent1.putExtra("nT", cita.getNombreTrabajador());
//        alarmIntent1.putExtra("nE", cita.getNombreEmpleador());
//        alarmIntent1.putExtra("idFrom", cita.getFrom());
//        alarmIntent1.putExtra("idTo", cita.getTo());
//        alarmIntent1.putExtra("fec", cita.getFechaCita());

//        Cita cita1 = new Cita();
//        cita1.setIdCita(cita.getIdCita());
//        alarmIntent.putExtra("cita",cita1);

//        boolean alarmUp = (PendingIntent.getBroadcast(getApplicationContext(), random, alarmIntent,
//                PendingIntent.FLAG_NO_CREATE) != null);
//        Log.d(TAG, String.format("Estado de alarma: %s", alarmUp));
//        final PendingIntent notifyPendingIntent = PendingIntent.getBroadcast
//                (getApplicationContext(), NOTIFICATION_ID, alarmIntent1,
//                        PendingIntent.FLAG_UPDATE_CURRENT);
//        final PendingIntent notifyPendingIntent = PendingIntent.getBroadcast
//                (getApplicationContext(), 1700, alarmIntent1,
//                        PendingIntent.FLAG_MUTABLE);
//
//        Calendar objCalendar = Calendar.getInstance();
//
//        DateFormat formatFec = new SimpleDateFormat("dd MMMM yyyy HH:mm a", new Locale("es", "ES"));
//        Calendar calendar = Calendar.getInstance();
//
//        try {
//            objCalendar.setTime(Objects.requireNonNull(formatFec.parse(cita.getFechaCita())));
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//
//        if (alarmManager != null) {
//            Log.d(TAG, "Configurando alarma");
//            alarmManager.set(AlarmManager.RTC,
//                    objCalendar.getTimeInMillis(),
//                    notifyPendingIntent);
//
//            String patronFechaYHora = "dd MMMM yyyy HH:mm aa";
//            Locale locale = new Locale("es", "ES");
//            DateFormat format = new SimpleDateFormat("dd MMMM yyyy HH:mm aa", new Locale("es", "ES"));
//
//
//            try {
//                SimpleDateFormat formatFecha = new SimpleDateFormat(patronFechaYHora, locale);
//                Date date = formatFecha.parse(cita.getFechaCita());
//                //Log.d(TAG, "INPUT: " + horaYFecha);
//
//                Calendar cal = Calendar.getInstance();
//                cal.setTime(date);
//
////            Date date1 = format.parse(format.format(calendar.getTime()));
//                Date date1 = format.parse(format.format(cal.getTime()));
//                Date date2 = new Date();
//                Log.d(TAG, "Date 1 selected(alarm date): " + format.format(date1));
//                Log.d(TAG, "Date 2 compare(now): " + format.format(date2));
//                if (date1.compareTo(date2) > 0) {
//                    //Log.d(TAG, "La fecha seleccionada es correcta");
//
//                } else if (date1.compareTo(date2) < 0) {
//                    Log.d(TAG, "La alarma ha expirado!");
//                    alarmManager.cancel(notifyPendingIntent);
//                    Log.d(TAG, "La alarma ha sido descativada!");
//
////                                            arrayListErrores.add("La fecha seleccionada es incorrecta");
////
////                                            validacion = false;
//                } else if (date1.compareTo(date2) == 0) {
//
//                }
//            } catch (ParseException e) {
//                e.printStackTrace();
//            }
//
//
////            objAlarmManager.set(AlarmManager.RTC_WAKEUP,objCalendar.getTimeInMillis(), notifyPendingIntent);
//
//        }

        // Set up the Notification Broadcast Intent.
        Intent notifyIntent = new Intent(this, AlarmReceiver.class);

        boolean alarmUp = (PendingIntent.getBroadcast(this, 1700, notifyIntent, PendingIntent.FLAG_NO_CREATE) != null);

//        final PendingIntent notifyPendingIntent = PendingIntent.getBroadcast
//                (this, NOTIFICATION_ID, notifyIntent,
//                        PendingIntent.FLAG_UPDATE_CURRENT);

        final AlarmManager alarmManager = (AlarmManager) getSystemService
                (ALARM_SERVICE);


//        long repeatInterval = 800000L;
//                            long repeatInterval = 0;

//        long triggerTime = SystemClock.elapsedRealtime() + repeatInterval;

        // If the Toggle is turned on, set the repeating alarm with
        // a 15 minute interval.
//                            Calendar objCalendar = Calendar.getInstance();
        // Set the toast message for the "on" case.
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);


        Date d = null;
        try {
            d = sdf.parse("2022-08-06 13:45:00");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar cal = Calendar.getInstance();
//        cal.setTime(d);
        cal.setTime(new Date());
        if (alarmManager != null) {
//                                objCalendar = Calendar.getInstance();
//                                alarmManager.setInexactRepeating
//                                        (AlarmManager.ELAPSED_REALTIME_WAKEUP,
//                                                triggerTime, repeatInterval,
//                                                notifyPendingIntent);
//            alarmManager.set(AlarmManager.RTC,
//                    cal.getTimeInMillis(),
//                    notifyPendingIntent);
        }

        String dateStr1 = sdf.format(cal.getTime());

//                            toastMessage = getString(R.string.alarm_on_toast) + objCalendar.getTime().toLocaleString();
        String toastMessage = getString(R.string.alarm_on_toast) + dateStr1;
        Log.d(TAG, toastMessage);

//        AlarmManager alarmMgr;
//        PendingIntent alarmIntent;
        alarmMgr = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
//        Intent intent = new Intent(this, AlarmReceiver.class);
//        alarmIntent = PendingIntent.getBroadcast(this, NOTIFICATION_ID, intent, 0);


//        Calendar calendar = Calendar.getInstance();
//        calendar.setTimeInMillis(System.currentTimeMillis());
//        calendar.set(Calendar.HOUR_OF_DAY, 14);
        // Set the alarm to start at 8:30 a.m.

        DateFormat formatFec = new SimpleDateFormat("dd MMMM yyyy HH:mm a", new Locale("es", "ES"));
        Calendar calendarX = Calendar.getInstance();
        try {
            calendarX.setTime(Objects.requireNonNull(formatFec.parse(cita.getFechaCita())));
        } catch (Exception e) {

        }
        Log.d(TAG, "calendarX" + String.valueOf(formatFec.format(calendarX.getTime())));

//        Calendar calendarm = Calendar.getInstance();
//        calendarm.setTimeInMillis(SystemClock.elapsedRealtime());
//        calendarm.set(Calendar.HOUR_OF_DAY, 2);
//        calendarm.set(Calendar.HOUR_OF_DAY, calendarX.getTime().getHours());
//        calendarm.set(Calendar.MINUTE, 39);
//        calendarm.setTimeInMillis(System.currentTimeMillis());
//        calendarm.set(Calendar.HOUR_OF_DAY, 2);
        calendarX.set(Calendar.SECOND, 0);
        calendarX.set(Calendar.MILLISECOND, 0);
//        calendarm.set(Calendar.MONTH, calendarX.getTime().getMonth() + 1);
//        calendarm.set(Calendar.DAY_OF_MONTH, calendarX.getTime().getDay());
//        calendarm.set(Calendar.HOUR_OF_DAY, calendarX.getTime().getHours());
//        calendarm.set(Calendar.MINUTE, 51);
//        calendarm.set(Calendar.MINUTE, calendarX.getTime().getMinutes());
//        calendarm.set(Calendar.MINUTE, calendarX.getTime().getMinutes());

//        alarmMgr.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,
//                SystemClock.elapsedRealtime() + 60 * 1000,
//                alarmIntent);

//        alarmMgr.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + 60 * 1000, notifyPendingIntent);
//        alarmMgr.set(AlarmManager.RTC_WAKEUP, SystemClock.elapsedRealtime(), notifyPendingIntent);
//        alarmMgr.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, calendarm.getTimeInMillis(), notifyPendingIntent);
//        alarmMgr.set(AlarmManager.RTC_WAKEUP, calendarm.getTimeInMillis(), notifyPendingIntent);
        //alarmMgr.set(AlarmManager.RTC_WAKEUP, calendarX.getTimeInMillis(), notifyPendingIntent);

//        alarmMgr.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,
//                calendarm.getTimeInMillis(),
//                alarmIntent);


//        Log.d(TAG, String.valueOf(SystemClock.elapsedRealtime() + 60 * 1000));
//
//        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss.SSS", Locale.US);
//
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTimeInMillis(SystemClock.elapsedRealtime() + (60 * 1000));
//
//        Log.d(TAG, String.valueOf(calendar.getTime()));
//        Log.d(TAG, String.valueOf(formatter.format(calendar.getTime())));


//        alarmMgr.set(AlarmManager.RTC, calendarX.getTimeInMillis(), notifyPendingIntent);
//        alarmMgr.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, calendarX.getTimeInMillis(), alarmIntent);

        Log.d(TAG, "###################################");
//        Log.d(TAG, String.valueOf(calendarX.getTime()));
//        Log.d(TAG, String.valueOf(formatFec.format(calendar.getTime())));
        Log.d(TAG, String.valueOf(formatFec.format(calendarX.getTime())));
//        Log.d(TAG, String.valueOf(formatFec.format(calendarX.getTime())));
//        Log.d(TAG, String.valueOf(formatFec.format(cal.getTime())));
    }

    private void listenerNotificacionesDeCitasTrabajo() {
        Log.d(TAG, "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
        Log.d(TAG, "LISTENER ALARMAS REMOTAS");
        Log.d(TAG, "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");


        childEventListenerCitasTrabajo = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Log.d(TAG, "onChildAdded");
                try {
                    Cita cita = snapshot.getValue(Cita.class);
                    if (cita != null) {
                        if (cita.getTo().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
//                            citaArrayList.add(cita);
                            if (cita.isStateReceive()) {

                            } else {
//                                boolean notificacionesCitas = defaultSharedPreferences.getBoolean("sync_notificaciones_citas", true);

                                Log.d(TAG, cita.toString());


//                                if (notificacionesCitas) {
//                                Toast.makeText(contextInstance, cita.toString(), Toast.LENGTH_LONG).show();


                                cita.setStateReceive(true);
                                cita.actualizarCitaEstado();
//                                Log.d(TAG, "ACTUALIZANDO CITA");
//                                FirebaseDatabase.getInstance()
//                                        .getReference()
//                                        .child("citas")
//                                        .child(cita.getIdCita())
//                                        .child("stateReceive")
//                                        .setValue(true)
//                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
//                                            @Override
//                                            public void onComplete(@NonNull Task<Void> task) {
//                                                if (task.isSuccessful()) {
//                                                    Log.d(TAG, "CITA ACTUALIZADA");
//
//                                                } else {
//                                                    Log.d(TAG, "ERROR ACTUALIZANDO CITA");
//
//                                                }
//                                            }
//                                        });


                                programarAlarmaLocal(cita);

//                                programarAlarmaLocalCustomLoco(cita);
//                                showNotificationCita(cita);


//                                cita.actualizarCita();
//                                }

//                                programarAlarmaLocal(cita);
////                                showNotificationCita(cita);
//                                cita.setStateReceive(true);
//                                cita.actualizarCita();
                            }
                        }

                    }

                } catch (Exception e) {

                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        FirebaseDatabase.getInstance().getReference()
                .child("citas")
                .addChildEventListener(childEventListenerCitasTrabajo);

    }

    private void childEventListenerRecycler() {
        final Context mContext = this;
        notificacionStackArrayList = new ArrayList<>();

        mNotificacionesRef = databaseReference
                .child("notificaciones")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        // [START child_event_listener_recycler]
        childEventListenerNotificacionesConMensajesLocas = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildAdded:" + dataSnapshot.getKey());

                // A new comment has been added, add it to the displayed list
                NotificacionStack notificacionStack = new NotificacionStack();
                ArrayList<MessageCloudPoc> mensajeNubes = new ArrayList<>();

                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    MessageCloudPoc mensajeNube = data.getValue(MessageCloudPoc.class);
                    mensajeNubes.add(mensajeNube);
                    Log.d(TAG, mensajeNube.toString());
                }
                notificacionStack.setMensajeNubes(mensajeNubes);
                final int min = 3000;
                final int max = 4999;
                int idNotification = new Random().nextInt((max - min) + 1) + min;
                notificacionStack.setIdNotification(idNotification);

                notificacionStackArrayList.add(notificacionStack);

                String usuarioBloqueado = myPreferences.getString("idUserBlocking", "");
                Log.d(TAG, "·###################################");
                Log.d(TAG, "usuarioBloqueado: " + usuarioBloqueado);
                Log.d(TAG, "·###################################");


                try {

                    if (!usuarioBloqueado.equals(notificacionStack.getMensajeNubes().get(0).getFrom())) {
                        showNotification(notificacionStack);
                    }
                } catch (Exception e) {
                    Log.d(TAG, e.toString());
                }


            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildChanged:" + dataSnapshot.getKey());

                // A comment has changed, use the key to determine if we are displaying this
                // comment and if so displayed the changed comment.
                String commentKey = dataSnapshot.getKey();
                int index = 0;
                NotificacionStack notificacionStackFound = new NotificacionStack();
                for (NotificacionStack nt : notificacionStackArrayList) {

                    if (nt.getMensajeNubes().get(0).getFrom().equals(dataSnapshot.getKey())) {
                        ArrayList<MessageCloudPoc> mensajeNubes = new ArrayList<>();
                        for (DataSnapshot data : dataSnapshot.getChildren()) {
                            MessageCloudPoc mensajeNube = data.getValue(MessageCloudPoc.class);
                            mensajeNubes.add(mensajeNube);
                            Log.d(TAG, mensajeNube.toString());
                        }
                        nt.setMensajeNubes(mensajeNubes);
                        notificacionStackFound = nt;
                        notificacionStackArrayList.set(index, nt);


                        String usuarioBloqueado = myPreferences.getString("idUserBlocking", "");
                        Log.d(TAG, "·###################################");
                        Log.d(TAG, "usuarioBloqueado: " + usuarioBloqueado);
                        Log.d(TAG, "·###################################");

                        try {

                            if (!usuarioBloqueado.equals(notificacionStackFound.getMensajeNubes().get(0).getFrom())) {
                                updateNotification(notificacionStackFound);
                            }
                        } catch (Exception e) {
                            Log.d(TAG, e.toString());
                        }

//                        updateNotification(notificacionStackFound);
                        break;
                    }
                    index++;
                }

                // ...
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onChildRemoved:" + dataSnapshot.getKey());

                // A comment has changed, use the key to determine if we are displaying this
                // comment and if so remove it.
                String commentKey = dataSnapshot.getKey();
                int index = 0;
                NotificacionStack notificacionStackFound = new NotificacionStack();
                for (NotificacionStack nt : notificacionStackArrayList) {

                    if (nt.getMensajeNubes().get(0).getFrom().equals(dataSnapshot.getKey())) {
                        try {
                            cancelNotification(nt.getIdNotification());
                        } catch (Exception e) {
                            Log.d(TAG, e.toString());
                        }
                        notificacionStackArrayList.remove(index);
                        break;
                    }
                    index++;
                }
                // ...
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildMoved:" + dataSnapshot.getKey());

                // A comment has changed position, use the key to determine if we are
                // displaying this comment and if so move it.
                String commentKey = dataSnapshot.getKey();

                // ...
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "postComments:onCancelled", databaseError.toException());
                Toast.makeText(mContext, "Failed to load notifications.",
                        Toast.LENGTH_SHORT).show();
            }
        };
        mNotificacionesRef.addChildEventListener(childEventListenerNotificacionesConMensajesLocas);
        // [END child_event_listener_recycler]
    }

    private void cancelNotification(int idNotification) {

//        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(contextInstance);
        // notificationId is a unique int for each notification that you must define
//        notificationManager.cancel(idNotification);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

            NotificationManager notificationManagerX = getSystemService(NotificationManager.class);
            notificationManagerX.cancel(idNotification);


            //notificacionStackArrayList.add(notificacionStack);

        } else {

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(contextInstance);
            notificationManager.cancel(idNotification);
            //notificacionStackArrayList.add(notificacionStack);
        }


    }

    private void showNotification(NotificacionStack notificacionStack) {

        FirebaseDatabase.getInstance().getReference().child("empleadores")
                .child(notificacionStack.getMensajeNubes().get(0).getFrom())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Empleador empleador = snapshot.getValue(Empleador.class);
                        if (empleador != null) {
                            Log.d(TAG, "###################################");
                            Log.d(TAG, "show" + String.valueOf(notificacionStack.getIdNotification()));
                            Log.d(TAG, "###################################");

                            final int min = 3000;
                            final int max = 3999;
//                            int idNotification = new Random().nextInt((max - min) + 1) + min;
                            //notificacionStack.setIdNotification(idNotification);

                            // Create an explicit intent for an Activity in your app
                            Intent intent = new Intent(contextInstance, CrazyIndividualChatActivity.class);
                            intent.putExtra("idRemoteUser", empleador.getIdUsuario());
//                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            PendingIntent pendingIntent = null;

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                                pendingIntent = PendingIntent.getActivity(contextInstance, notificacionStack.getIdNotification(), intent,
                                        PendingIntent.FLAG_MUTABLE);
                            } else {
                                pendingIntent = PendingIntent.getActivity(contextInstance, notificacionStack.getIdNotification(), intent,
                                        FLAG_UPDATE_CURRENT);
                                /*Bandera mala: PendingIntent.FLAG_IMMUTABLE*/
//
                            }


                            Intent deleteIntent = new Intent(ACTION_DELETE_NOTIFICATION);
                            deleteIntent.putExtra("idNotification", notificacionStack.getIdNotification());
                            deleteIntent.putExtra("idRemoteUser", empleador.getIdUsuario());

                            PendingIntent deletePendingIntent = null;

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                                deletePendingIntent =
                                        PendingIntent.getBroadcast(contextInstance,
                                                notificacionStack.getIdNotification(),
                                                deleteIntent,
                                                FLAG_MUTABLE);
                            } else {
                                deletePendingIntent =
                                        PendingIntent.getBroadcast(contextInstance,
                                                notificacionStack.getIdNotification(),
                                                deleteIntent,
                                                FLAG_UPDATE_CURRENT);
                            }

                            // Key for the string that's delivered in the action's intent.
//                            String KEY_TEXT_REPLY = "key_text_reply";


                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                                Person user = new Person.Builder().setName(empleador.getNombre() + " " + empleador.getApellido()).build();
                                Notification.MessagingStyle style = new Notification.MessagingStyle(user.getName());

                                for (MessageCloudPoc me : notificacionStack.getMensajeNubes()) {
                                    long timeStamp = System.currentTimeMillis();
//                    Person user = new Person.Builder().setIcon(userIcon).setName("userName").build();
                                    Notification.MessagingStyle.Message message = new Notification.MessagingStyle.Message(me.getContenido(), timeStamp, user.getName());
//                            not.getFrom() + " " + not.getFrom());
                                    if (me.getType() == 1) {
                                        message = new Notification.MessagingStyle.Message(new String(Character.toChars(0x1F4F7)) + " Foto", timeStamp, user.getName());
                                    }
                                    if (me.getType() == 2) {
                                        message = new Notification.MessagingStyle.Message(new String(Character.toChars(0x1F3A7)) + " Audio", timeStamp, user.getName());

                                    }
                                    style.addMessage(message);
                                }


                                // Create the reply action and add the remote input.

                                Intent updateIntent = new Intent(ACTION_REPLY_NOTIFICATION);
                                updateIntent.putExtra("idNotification", notificacionStack.getIdNotification());
                                updateIntent.putExtra("idRemoteUser", empleador.getIdUsuario());


                                PendingIntent replyPendingIntent =
                                        PendingIntent.getBroadcast(getApplicationContext(),
                                                notificacionStack.getIdNotification(),
                                                updateIntent,
                                                FLAG_UPDATE_CURRENT | FLAG_MUTABLE);


                                final Icon icon =
                                        Icon.createWithResource(contextInstance,
                                                android.R.drawable.ic_dialog_info);

                                Notification.Action action =
                                        new Notification.Action.Builder(
                                                icon, "Responder", replyPendingIntent)
                                                .addRemoteInput(new android.app.RemoteInput.Builder(KEY_TEXT_REPLY)
                                                        .setLabel("Escribir...").build())
                                                .build();


                                Notification.Builder notification = new Notification.Builder(contextInstance, NOTIFICATIONS_CHANNEL_ID)
                                        //.setContentTitle(getText(R.string.notification_title) + "-" + String.valueOf(Build.VERSION.SDK_INT))
//                                        .setContentText(getText(R.string.notification_message))
                                        .setSmallIcon(R.drawable.ic_oficios)
                                        //.setContentIntent(pendingIntent)
                                        .setDeleteIntent(deletePendingIntent)
                                        .setContentIntent(pendingIntent)
                                        .setStyle(style)
                                        .addAction(action)
//                                        .addAction(replyAction)
                                        .setAutoCancel(true)
                                        .setTicker(getText(R.string.ticker_text));
                                // Notification ID cannot be 0.


                                //NotificationManagerCompat notificationManager = NotificationManagerCompat.from(contextInstance);
                                //notificationManager.notify(notificacionStack.getIdNotification(), notification.build());
                                NotificationManager notificationManagerX = getSystemService(NotificationManager.class);
                                notificationManagerX.notify(notificacionStack.getIdNotification(), notification.build());


                                //notificacionStackArrayList.add(notificacionStack);

                            } else {
                                Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
                                if (alarmSound == null) {
                                    alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
                                    if (alarmSound == null) {
                                        alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                                    }
                                }
                                Person user = new Person.Builder().setName(empleador.getNombre() + " " + empleador.getApellido()).build();
                                NotificationCompat.MessagingStyle style = new NotificationCompat.MessagingStyle(user);

                                for (MessageCloudPoc me : notificacionStack.getMensajeNubes()) {
                                    long timeStamp = System.currentTimeMillis();
//                    Person user = new Person.Builder().setIcon(userIcon).setName("userName").build();
                                    NotificationCompat.MessagingStyle.Message message = new NotificationCompat.MessagingStyle.Message(me.getContenido(), timeStamp, user);
//                            not.getFrom() + " " + not.getFrom());

                                    if (me.getType() == 1) {
                                        message = new NotificationCompat.MessagingStyle.Message(new String(Character.toChars(0x1F4F7)) + " Foto", timeStamp, user);
                                    }
                                    if (me.getType() == 2) {
                                        message = new NotificationCompat.MessagingStyle.Message(new String(Character.toChars(0x1F3A7)) + " Audio", timeStamp, user);

                                    }
                                    style.addMessage(message);
                                }


                                RemoteInput remoteInput = new RemoteInput.Builder(KEY_TEXT_REPLY)
                                        .setLabel("Escribir...")
                                        .build();

                                Intent updateIntent = new Intent(ACTION_REPLY_NOTIFICATION);
                                updateIntent.putExtra("idNotification", notificacionStack.getIdNotification());
                                updateIntent.putExtra("idRemoteUser", empleador.getIdUsuario());

                                // Build a PendingIntent for the reply action to trigger.
                                PendingIntent replyPendingIntent =
                                        PendingIntent.getBroadcast(getApplicationContext(),
                                                notificacionStack.getIdNotification(),
                                                updateIntent,
                                                FLAG_UPDATE_CURRENT);


                                // Create the reply action and add the remote input.
                                NotificationCompat.Action action =
                                        new NotificationCompat.Action.Builder(R.drawable.ic_oficios,
                                                "Responder", replyPendingIntent)
                                                .addRemoteInput(remoteInput)
                                                .build();


                                NotificationCompat.Builder notification = new NotificationCompat.Builder(contextInstance, NOTIFICATIONS_CHANNEL_ID)
                                        .setSmallIcon(R.drawable.ic_oficios)
//                                        .setContentTitle(getText(R.string.notification_title) + "-" + String.valueOf(Build.VERSION.SDK_INT))
//                                        .setContentText(getText(R.string.notification_message))
                                        // Set the intent that will fire when the user taps the notification
                                        .setStyle(style)
                                        .setAutoCancel(true)
                                        .setContentIntent(pendingIntent)
                                        .setDeleteIntent(deletePendingIntent)
                                        .addAction(action)
                                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                                        .setDefaults(NotificationCompat.DEFAULT_ALL)

                                        .setSound(alarmSound)
                                        .setAutoCancel(true);
                                // Notification ID cannot be 0.
                                // Issue the new notification.
                                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(contextInstance);
                                notificationManager.notify(notificacionStack.getIdNotification(), notification.build());
                                //notificacionStackArrayList.add(notificacionStack);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


        FirebaseDatabase.getInstance().getReference().child("trabajadores")
                .child(notificacionStack.getMensajeNubes().get(0).getFrom())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Trabajador trabajador = snapshot.getValue(Trabajador.class);
                        if (trabajador != null) {
                            Log.d(TAG, "###################################");
                            Log.d(TAG, "show" + String.valueOf(notificacionStack.getIdNotification()));
                            Log.d(TAG, "###################################");

                            final int min = 3000;
                            final int max = 3999;
//                            int idNotification = new Random().nextInt((max - min) + 1) + min;
                            //notificacionStack.setIdNotification(idNotification);

                            Intent intent = new Intent(contextInstance, CrazyIndividualChatActivity.class);
                            intent.putExtra("idRemoteUser", trabajador.getIdUsuario());
//                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);


                            PendingIntent pendingIntent = null;

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                                pendingIntent = PendingIntent.getActivity(contextInstance, notificacionStack.getIdNotification(), intent,
                                        PendingIntent.FLAG_MUTABLE);
                            } else {
                                pendingIntent = PendingIntent.getActivity(contextInstance, notificacionStack.getIdNotification(), intent,
                                        FLAG_UPDATE_CURRENT);
                                /*Bandera mala: PendingIntent.FLAG_IMMUTABLE*/
//
                            }


                            Intent deleteIntent = new Intent(ACTION_DELETE_NOTIFICATION);
                            deleteIntent.putExtra("idNotification", notificacionStack.getIdNotification());
                            deleteIntent.putExtra("idRemoteUser", trabajador.getIdUsuario());
//                            deleteIntent.putExtra("notificationIdFrom", notificacionCustom.getMensajeNubes().get(0).getFrom());
//                            deleteIntent.putExtra("chat", chatFound);

                            //deleteIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                            PendingIntent deletePendingIntent = null;

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                                deletePendingIntent =
                                        PendingIntent.getBroadcast(contextInstance,
                                                notificacionStack.getIdNotification(),
                                                deleteIntent,
                                                FLAG_MUTABLE);
                            } else {
                                deletePendingIntent =
                                        PendingIntent.getBroadcast(contextInstance,
                                                notificacionStack.getIdNotification(),
                                                deleteIntent,
                                                FLAG_UPDATE_CURRENT);
                            }


                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                                Person user = new Person.Builder().setName(trabajador.getNombre() + " " + trabajador.getApellido()).build();
                                Notification.MessagingStyle style = new Notification.MessagingStyle(user.getName());

                                for (MessageCloudPoc me : notificacionStack.getMensajeNubes()) {
                                    long timeStamp = System.currentTimeMillis();
//                    Person user = new Person.Builder().setIcon(userIcon).setName("userName").build();
                                    Notification.MessagingStyle.Message message = new Notification.MessagingStyle.Message(me.getContenido(), timeStamp, user.getName());
//                            not.getFrom() + " " + not.getFrom());
                                    if (me.getType() == 1) {
                                        message = new Notification.MessagingStyle.Message(new String(Character.toChars(0x1F4F7)) + " Foto", timeStamp, user.getName());
                                    }
                                    if (me.getType() == 2) {
                                        message = new Notification.MessagingStyle.Message(new String(Character.toChars(0x1F3A7)) + " Audio", timeStamp, user.getName());

                                    }
                                    style.addMessage(message);
                                }

                                Intent updateIntent = new Intent(ACTION_REPLY_NOTIFICATION);
                                updateIntent.putExtra("idNotification", notificacionStack.getIdNotification());
                                updateIntent.putExtra("idRemoteUser", trabajador.getIdUsuario());

                                PendingIntent replyPendingIntent =
                                        PendingIntent.getBroadcast(getApplicationContext(),
                                                notificacionStack.getIdNotification(),
                                                updateIntent,
                                                FLAG_UPDATE_CURRENT | FLAG_MUTABLE);

                                final Icon icon =
                                        Icon.createWithResource(contextInstance,
                                                android.R.drawable.ic_dialog_info);

                                Notification.Action action =
                                        new Notification.Action.Builder(
                                                icon, "Responder", replyPendingIntent)
                                                .addRemoteInput(new android.app.RemoteInput.Builder(KEY_TEXT_REPLY)
                                                        .setLabel("Escribir...").build())
                                                .build();


                                Notification.Builder notification = new Notification.Builder(contextInstance, NOTIFICATIONS_CHANNEL_ID)
                                        .setContentTitle(getText(R.string.notification_title) + "-" + String.valueOf(Build.VERSION.SDK_INT))
                                        .setContentText(getText(R.string.notification_message))
                                        .setSmallIcon(R.drawable.ic_oficios)
                                        .setContentIntent(pendingIntent)
                                        .setDeleteIntent(deletePendingIntent)
                                        .setStyle(style)
                                        .addAction(action)
                                        .setAutoCancel(true)
                                        .setTicker(getText(R.string.ticker_text));


                                // Notification ID cannot be 0.

//                                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(contextInstance);
//                                notificationManager.notify(notificacionStack.getIdNotification(), notification.build());
//
                                NotificationManager notificationManagerX = getSystemService(NotificationManager.class);
                                notificationManagerX.notify(notificacionStack.getIdNotification(), notification.build());

                                //notificacionStackArrayList.add(notificacionStack);

                            } else {
                                Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
                                if (alarmSound == null) {
                                    alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
                                    if (alarmSound == null) {
                                        alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                                    }
                                }
                                Person user = new Person.Builder().setName(trabajador.getNombre() + " " + trabajador.getApellido()).build();
                                NotificationCompat.MessagingStyle style = new NotificationCompat.MessagingStyle(user);

                                for (MessageCloudPoc me : notificacionStack.getMensajeNubes()) {
                                    long timeStamp = System.currentTimeMillis();
//                    Person user = new Person.Builder().setIcon(userIcon).setName("userName").build();
                                    NotificationCompat.MessagingStyle.Message message = new NotificationCompat.MessagingStyle.Message(me.getContenido(), timeStamp, user);
//                            not.getFrom() + " " + not.getFrom());


                                    if (me.getType() == 1) {
                                        message = new NotificationCompat.MessagingStyle.Message(new String(Character.toChars(0x1F4F7)) + " Foto", timeStamp, user);
                                    }
                                    if (me.getType() == 2) {
                                        message = new NotificationCompat.MessagingStyle.Message(new String(Character.toChars(0x1F3A7)) + " Audio", timeStamp, user);

                                    }

                                    style.addMessage(message);
                                }


                                RemoteInput remoteInput = new RemoteInput.Builder(KEY_TEXT_REPLY)
                                        .setLabel("Escribir...")
                                        .build();
                                // Build a PendingIntent for the reply action to trigger.
                                Intent updateIntent = new Intent(ACTION_REPLY_NOTIFICATION);
                                updateIntent.putExtra("idNotification", notificacionStack.getIdNotification());
                                updateIntent.putExtra("idRemoteUser", trabajador.getIdUsuario());

                                PendingIntent replyPendingIntent =
                                        PendingIntent.getBroadcast(getApplicationContext(),
                                                notificacionStack.getIdNotification(),
                                                updateIntent,
                                                FLAG_UPDATE_CURRENT | FLAG_MUTABLE);

                                // Create the reply action and add the remote input.
                                NotificationCompat.Action action =
                                        new NotificationCompat.Action.Builder(R.drawable.ic_oficios,
                                                "Responder", replyPendingIntent)
                                                .addRemoteInput(remoteInput)
                                                .build();


                                NotificationCompat.Builder notification = new NotificationCompat.Builder(contextInstance, NOTIFICATIONS_CHANNEL_ID)
                                        .setSmallIcon(R.drawable.ic_oficios)
                                        //.setContentTitle(getText(R.string.notification_title) + "-" + String.valueOf(Build.VERSION.SDK_INT))
                                        //.setContentText(getText(R.string.notification_message))
                                        // Set the intent that will fire when the user taps the notification
                                        .setStyle(style)
                                        .setContentIntent(pendingIntent)
                                        .setDeleteIntent(deletePendingIntent)
                                        .setAutoCancel(true)
                                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                                        .setDefaults(NotificationCompat.DEFAULT_ALL)
                                        .addAction(action)

                                        .setSound(alarmSound)
                                        .setAutoCancel(true);
                                // Notification ID cannot be 0.
                                // Issue the new notification.
                                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(contextInstance);
                                notificationManager.notify(notificacionStack.getIdNotification(), notification.build());
                                //notificacionStackArrayList.add(notificacionStack);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


    }

    private void updateNotification(NotificacionStack notificacionStackFound) {
        FirebaseDatabase.getInstance().getReference().child("empleadores")
                .child(notificacionStackFound.getMensajeNubes().get(0).getFrom())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Empleador empleador = snapshot.getValue(Empleador.class);
                        if (empleador != null) {
                            Log.d(TAG, "###################################");
                            Log.d(TAG, "show" + String.valueOf(notificacionStackFound.getIdNotification()));
                            Log.d(TAG, "###################################");

                            Intent intent = new Intent(contextInstance, CrazyIndividualChatActivity.class);
                            intent.putExtra("idRemoteUser", empleador.getIdUsuario());
//                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);


                            PendingIntent pendingIntent = null;

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                                pendingIntent = PendingIntent.getActivity(contextInstance, notificacionStackFound.getIdNotification(), intent,
                                        PendingIntent.FLAG_MUTABLE);
                            } else {
                                pendingIntent = PendingIntent.getActivity(contextInstance, notificacionStackFound.getIdNotification(), intent,
                                        FLAG_UPDATE_CURRENT);
                                /*Bandera mala: PendingIntent.FLAG_IMMUTABLE*/
//
                            }


                            Intent deleteIntent = new Intent(ACTION_DELETE_NOTIFICATION);
                            deleteIntent.putExtra("idNotification", notificacionStackFound.getIdNotification());
                            deleteIntent.putExtra("idRemoteUser", empleador.getIdUsuario());
//                            deleteIntent.putExtra("notificationIdFrom", notificacionCustom.getMensajeNubes().get(0).getFrom());
//                            deleteIntent.putExtra("chat", chatFound);

                            //deleteIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                            PendingIntent deletePendingIntent = null;

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                                deletePendingIntent =
                                        PendingIntent.getBroadcast(contextInstance,
                                                notificacionStackFound.getIdNotification(),
                                                deleteIntent,
                                                FLAG_MUTABLE);
                            } else {
                                deletePendingIntent =
                                        PendingIntent.getBroadcast(contextInstance,
                                                notificacionStackFound.getIdNotification(),
                                                deleteIntent,
                                                FLAG_UPDATE_CURRENT);
                            }

                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                                Person user = new Person.Builder().setName(empleador.getNombre() + " " + empleador.getApellido()).build();
                                Notification.MessagingStyle style = new Notification.MessagingStyle(user.getName());

                                for (MessageCloudPoc me : notificacionStackFound.getMensajeNubes()) {
                                    long timeStamp = System.currentTimeMillis();
//                    Person user = new Person.Builder().setIcon(userIcon).setName("userName").build();
                                    Notification.MessagingStyle.Message message = new Notification.MessagingStyle.Message(me.getContenido(), timeStamp, user.getName());
//                            not.getFrom() + " " + not.getFrom());

                                    if (me.getType() == 1) {
                                        message = new Notification.MessagingStyle.Message(new String(Character.toChars(0x1F4F7)) + " Foto", timeStamp, user.getName());
                                    }
                                    if (me.getType() == 2) {
                                        message = new Notification.MessagingStyle.Message(new String(Character.toChars(0x1F3A7)) + " Audio", timeStamp, user.getName());

                                    }
                                    style.addMessage(message);
                                }


                                Intent updateIntent = new Intent(ACTION_REPLY_NOTIFICATION);
                                updateIntent.putExtra("idNotification", notificacionStackFound.getIdNotification());
                                updateIntent.putExtra("idRemoteUser", empleador.getIdUsuario());

                                PendingIntent replyPendingIntent =
                                        PendingIntent.getBroadcast(getApplicationContext(),
                                                notificacionStackFound.getIdNotification(),
                                                updateIntent,
                                                FLAG_UPDATE_CURRENT | FLAG_MUTABLE);


                                final Icon icon =
                                        Icon.createWithResource(contextInstance,
                                                android.R.drawable.ic_dialog_info);

                                Notification.Action action =
                                        new Notification.Action.Builder(
                                                icon, "Responder", replyPendingIntent)
                                                .addRemoteInput(new android.app.RemoteInput.Builder(KEY_TEXT_REPLY)
                                                        .setLabel("Escribir...").build())
                                                .build();

                                Notification.Builder notification = new Notification.Builder(contextInstance, NOTIFICATIONS_CHANNEL_ID)
                                        .setContentTitle(getText(R.string.notification_title) + "-" + String.valueOf(Build.VERSION.SDK_INT))
                                        .setContentText(getText(R.string.notification_message))
                                        .setSmallIcon(R.drawable.ic_oficios)
                                        .setContentIntent(pendingIntent)
                                        .setDeleteIntent(deletePendingIntent)
                                        .setAutoCancel(true)
                                        .setStyle(style)
                                        .addAction(action)
                                        .setTicker(getText(R.string.ticker_text));
                                // Notification ID cannot be 0.

//                                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(contextInstance);
//                                notificationManager.notify(notificacionStackFound.getIdNotification(), notification.build());

                                NotificationManager notificationManagerX = getSystemService(NotificationManager.class);
                                notificationManagerX.notify(notificacionStackFound.getIdNotification(), notification.build());


                            } else {
                                Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
                                if (alarmSound == null) {
                                    alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
                                    if (alarmSound == null) {
                                        alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                                    }
                                }
                                Person user = new Person.Builder().setName(empleador.getNombre() + " " + empleador.getApellido()).build();
                                NotificationCompat.MessagingStyle style = new NotificationCompat.MessagingStyle(user);

                                for (MessageCloudPoc me : notificacionStackFound.getMensajeNubes()) {
                                    long timeStamp = System.currentTimeMillis();
//                    Person user = new Person.Builder().setIcon(userIcon).setName("userName").build();
                                    NotificationCompat.MessagingStyle.Message message = new NotificationCompat.MessagingStyle.Message(me.getContenido(), timeStamp, user);
//                            not.getFrom() + " " + not.getFrom());


                                    if (me.getType() == 1) {
                                        message = new NotificationCompat.MessagingStyle.Message(new String(Character.toChars(0x1F4F7)) + " Foto", timeStamp, user);
                                    }
                                    if (me.getType() == 2) {
                                        message = new NotificationCompat.MessagingStyle.Message(new String(Character.toChars(0x1F3A7)) + " Audio", timeStamp, user);

                                    }
                                    style.addMessage(message);
                                }


                                RemoteInput remoteInput = new RemoteInput.Builder(KEY_TEXT_REPLY)
                                        .setLabel("Escribir...")
                                        .build();
                                // Build a PendingIntent for the reply action to trigger.
                                Intent updateIntent = new Intent(ACTION_REPLY_NOTIFICATION);
                                updateIntent.putExtra("idNotification", notificacionStackFound.getIdNotification());
                                updateIntent.putExtra("idRemoteUser", empleador.getIdUsuario());

                                PendingIntent replyPendingIntent =
                                        PendingIntent.getBroadcast(getApplicationContext(),
                                                notificacionStackFound.getIdNotification(),
                                                updateIntent,
                                                FLAG_UPDATE_CURRENT | FLAG_MUTABLE);

                                // Create the reply action and add the remote input.
                                NotificationCompat.Action action =
                                        new NotificationCompat.Action.Builder(R.drawable.ic_oficios,
                                                "Responder", replyPendingIntent)
                                                .addRemoteInput(remoteInput)
                                                .build();

                                NotificationCompat.Builder notification = new NotificationCompat.Builder(contextInstance, NOTIFICATIONS_CHANNEL_ID)
                                        .setSmallIcon(R.drawable.ic_oficios)
//                                        .setContentTitle(getText(R.string.notification_title) + "-" + String.valueOf(Build.VERSION.SDK_INT))
//                                        .setContentText(getText(R.string.notification_message))
                                        // Set the intent that will fire when the user taps the notification
                                        .setStyle(style)
                                        .setContentIntent(pendingIntent)
                                        .setDeleteIntent(deletePendingIntent)
                                        .addAction(action)

                                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                                        .setDefaults(NotificationCompat.DEFAULT_ALL)

                                        .setSound(alarmSound)
                                        .setAutoCancel(true);
                                // Notification ID cannot be 0.
                                // Issue the new notification.
                                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(contextInstance);
                                notificationManager.notify(notificacionStackFound.getIdNotification(), notification.build());
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        FirebaseDatabase.getInstance().getReference().child("trabajadores")
                .child(notificacionStackFound.getMensajeNubes().get(0).getFrom())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Trabajador trabajador = snapshot.getValue(Trabajador.class);
                        if (trabajador != null) {
                            Log.d(TAG, "###################################");
                            Log.d(TAG, "show" + String.valueOf(notificacionStackFound.getIdNotification()));
                            Log.d(TAG, "###################################");

                            Intent intent = new Intent(contextInstance, CrazyIndividualChatActivity.class);
                            intent.putExtra("idRemoteUser", trabajador.getIdUsuario());
//                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);


                            PendingIntent pendingIntent = null;

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                                pendingIntent = PendingIntent.getActivity(contextInstance, notificacionStackFound.getIdNotification(), intent,
                                        PendingIntent.FLAG_MUTABLE);
                            } else {
                                pendingIntent = PendingIntent.getActivity(contextInstance, notificacionStackFound.getIdNotification(), intent,
                                        FLAG_UPDATE_CURRENT);
                                /*Bandera mala: PendingIntent.FLAG_IMMUTABLE*/
//
                            }


                            Intent deleteIntent = new Intent(ACTION_DELETE_NOTIFICATION);
                            deleteIntent.putExtra("idNotification", notificacionStackFound.getIdNotification());
                            deleteIntent.putExtra("idRemoteUser", trabajador.getIdUsuario());
//                            deleteIntent.putExtra("notificationIdFrom", notificacionCustom.getMensajeNubes().get(0).getFrom());
//                            deleteIntent.putExtra("chat", chatFound);

                            //deleteIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                            PendingIntent deletePendingIntent = null;

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                                deletePendingIntent =
                                        PendingIntent.getBroadcast(contextInstance,
                                                notificacionStackFound.getIdNotification(),
                                                deleteIntent,
                                                FLAG_MUTABLE);
                            } else {
                                deletePendingIntent =
                                        PendingIntent.getBroadcast(contextInstance,
                                                notificacionStackFound.getIdNotification(),
                                                deleteIntent,
                                                FLAG_UPDATE_CURRENT);
                            }

                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                                Person user = new Person.Builder().setName(trabajador.getNombre() + " " + trabajador.getApellido()).build();
                                Notification.MessagingStyle style = new Notification.MessagingStyle(user.getName());

                                for (MessageCloudPoc me : notificacionStackFound.getMensajeNubes()) {
                                    long timeStamp = System.currentTimeMillis();
//                    Person user = new Person.Builder().setIcon(userIcon).setName("userName").build();
                                    Notification.MessagingStyle.Message message = new Notification.MessagingStyle.Message(me.getContenido(), timeStamp, user.getName());
//                            not.getFrom() + " " + not.getFrom());

                                    if (me.getType() == 1) {
                                        message = new Notification.MessagingStyle.Message(new String(Character.toChars(0x1F4F7)) + " Foto", timeStamp, user.getName());
                                    }
                                    if (me.getType() == 2) {
                                        message = new Notification.MessagingStyle.Message(new String(Character.toChars(0x1F3A7)) + " Audio", timeStamp, user.getName());

                                    }
                                    style.addMessage(message);
                                }


                                Intent updateIntent = new Intent(ACTION_REPLY_NOTIFICATION);
                                updateIntent.putExtra("idNotification", notificacionStackFound.getIdNotification());
                                updateIntent.putExtra("idRemoteUser", trabajador.getIdUsuario());

                                PendingIntent replyPendingIntent =
                                        PendingIntent.getBroadcast(getApplicationContext(),
                                                notificacionStackFound.getIdNotification(),
                                                updateIntent,
                                                FLAG_UPDATE_CURRENT | FLAG_MUTABLE);


                                final Icon icon =
                                        Icon.createWithResource(contextInstance,
                                                android.R.drawable.ic_dialog_info);

                                Notification.Action action =
                                        new Notification.Action.Builder(
                                                icon, "Responder", replyPendingIntent)
                                                .addRemoteInput(new android.app.RemoteInput.Builder(KEY_TEXT_REPLY)
                                                        .setLabel("Escribir...").build())
                                                .build();


                                Notification.Builder notification = new Notification.Builder(contextInstance, NOTIFICATIONS_CHANNEL_ID)
//                                        .setContentTitle(getText(R.string.notification_title) + "-" + String.valueOf(Build.VERSION.SDK_INT))
//                                        .setContentText(getText(R.string.notification_message))
                                        .setSmallIcon(R.drawable.ic_oficios)
                                        .setContentIntent(pendingIntent)
                                        .setDeleteIntent(deletePendingIntent)
                                        .setAutoCancel(true)
                                        .addAction(action)
                                        .setStyle(style)
                                        .setTicker(getText(R.string.ticker_text));
                                // Notification ID cannot be 0.

//                                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(contextInstance);
//                                notificationManager.notify(notificacionStackFound.getIdNotification(), notification.build());


                                NotificationManager notificationManagerX = getSystemService(NotificationManager.class);
                                notificationManagerX.notify(notificacionStackFound.getIdNotification(), notification.build());


                            } else {
                                Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
                                if (alarmSound == null) {
                                    alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
                                    if (alarmSound == null) {
                                        alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                                    }
                                }
                                Person user = new Person.Builder().setName(trabajador.getNombre() + " " + trabajador.getApellido()).build();
                                NotificationCompat.MessagingStyle style = new NotificationCompat.MessagingStyle(user);

                                for (MessageCloudPoc me : notificacionStackFound.getMensajeNubes()) {
                                    long timeStamp = System.currentTimeMillis();
//                    Person user = new Person.Builder().setIcon(userIcon).setName("userName").build();
                                    NotificationCompat.MessagingStyle.Message message = new NotificationCompat.MessagingStyle.Message(me.getContenido(), timeStamp, user);
//                            not.getFrom() + " " + not.getFrom());
                                    if (me.getType() == 1) {
                                        message = new NotificationCompat.MessagingStyle.Message(new String(Character.toChars(0x1F4F7)) + " Foto", timeStamp, user);
                                    }
                                    if (me.getType() == 2) {
                                        message = new NotificationCompat.MessagingStyle.Message(new String(Character.toChars(0x1F3A7)) + " Audio", timeStamp, user);

                                    }
                                    style.addMessage(message);
                                }


                                RemoteInput remoteInput = new RemoteInput.Builder(KEY_TEXT_REPLY)
                                        .setLabel("Escribir...")
                                        .build();
                                // Build a PendingIntent for the reply action to trigger.
                                Intent updateIntent = new Intent(ACTION_REPLY_NOTIFICATION);
                                updateIntent.putExtra("idNotification", notificacionStackFound.getIdNotification());
                                updateIntent.putExtra("idRemoteUser", trabajador.getIdUsuario());

                                PendingIntent replyPendingIntent =
                                        PendingIntent.getBroadcast(getApplicationContext(),
                                                notificacionStackFound.getIdNotification(),
                                                updateIntent,
                                                FLAG_UPDATE_CURRENT | FLAG_MUTABLE);

                                // Create the reply action and add the remote input.
                                NotificationCompat.Action action =
                                        new NotificationCompat.Action.Builder(R.drawable.ic_oficios,
                                                "Responder", replyPendingIntent)
                                                .addRemoteInput(remoteInput)
                                                .build();

                                NotificationCompat.Builder notification = new NotificationCompat.Builder(contextInstance, NOTIFICATIONS_CHANNEL_ID)
                                        .setSmallIcon(R.drawable.ic_oficios)
                                        .setContentTitle(getText(R.string.notification_title) + "-" + String.valueOf(Build.VERSION.SDK_INT))
                                        .setContentText(getText(R.string.notification_message))
                                        // Set the intent that will fire when the user taps the notification
                                        .setStyle(style)
                                        .setContentIntent(pendingIntent)
                                        .setDeleteIntent(deletePendingIntent)
                                        .addAction(action)
                                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                                        .setDefaults(NotificationCompat.DEFAULT_ALL)

                                        .setSound(alarmSound)
                                        .setAutoCancel(true);
                                // Notification ID cannot be 0.
                                // Issue the new notification.
                                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(contextInstance);
                                notificationManager.notify(notificacionStackFound.getIdNotification(), notification.build());
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
//        Toast.makeText(this, "CRAZY service creating...", Toast.LENGTH_SHORT).show();

        // Get a reference to the Firebase Database
        databaseReference = FirebaseDatabase.getInstance().getReference();
        createForegroundNotificationChannel();

        listenerForegroundAdminFunctions();

        createNotificationChannel();
        createLlamadasVozNotificationChannel();
        createVideoCallChannel();
        createNotificationsTempChannel();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        startIdLocal = startId;
        contextInstance = this;
//        Toast.makeText(this, "CRAZY service starting", Toast.LENGTH_SHORT).show();
        sendForegroundNotification();
        crazyDeleteBroadcastReceiver = new CrazyDeleteBroadcastReceiver();
        crazyReplyBroadcastReceiver = new CrazyReplyBroadcastReceiver();


//        loadUsuarioLocal();

        myPreferences = this.getSharedPreferences("MyPreferences", MODE_PRIVATE);
        defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);


        listenerMessagePocNotificacionesFirebase();
//        listenerLlamadasDeVozFirebase();
//        listenerNotificacionesDeVideoLlamadas();
        listenerNotificacionesDeCitasTrabajo();

        listenerVideoCalls();
        listenerVoiceCalls();


        // If we get killed, after returning from here, restart
        return START_STICKY;
    }

    @Override
    public void onDestroy() {

//        Toast.makeText(this, "CRAZY service done", Toast.LENGTH_SHORT).show();

        databaseReference
                .child("notificaciones")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .removeEventListener(childEventListenerNotificacionesConMensajesLocas);


        FirebaseDatabase.getInstance().getReference()
                .child("citas")
                .removeEventListener(childEventListenerCitasTrabajo);


        contextInstance.unregisterReceiver(crazyDeleteBroadcastReceiver);
        contextInstance.unregisterReceiver(crazyReplyBroadcastReceiver);


        contextInstance.unregisterReceiver(acceptVideoCallBroadcastReceiver);
        contextInstance.unregisterReceiver(rejectVideoCallBroadcastReceiver);

        contextInstance.unregisterReceiver(acceptVoiceCallBroadcastReceiver);
        contextInstance.unregisterReceiver(rejectVoiceCallBroadcastReceiver);
        try {
            stopForeground(true);
            stopSelfResult(startIdLocal);
        } catch (Exception e) {
            Log.d(TAG, e.toString());
        }


        FirebaseDatabase.getInstance().getReference().child("videoCalls")
                .removeEventListener(childEventListenerVideoCalls);

        FirebaseDatabase.getInstance().getReference().child("voiceCalls")
                .removeEventListener(childEventListenerVoiceCalls);

//        unregisterReceiver(crazyDeleteBroadcastReceiver);
//        unregisterReceiver(crazyReplyBroadcastReceiver);
//        unregisterReceiver(foregroundAcceptCallReceiver);
//        unregisterReceiver(foregroundRejectCallReceiver);
//        unregisterReceiver(foregrounAcceptVideoCallReceiver);
//        unregisterReceiver(foregroundRejectVideoCallReceiver);


    }


    public class MusicPlaybackService extends Service implements MediaPlayer.OnCompletionListener {
        private MediaPlayer mediaPlayer;

        @Override
        public void onCreate() {
            super.onCreate();
            mediaPlayer = MediaPlayer.create(this, R.raw.skype_caller_tone); // Cambia por tu canción
            mediaPlayer.setOnCompletionListener(this);
        }

        @Override
        public int onStartCommand(Intent intent, int flags, int startId) {
            if (!mediaPlayer.isPlaying()) {
                mediaPlayer.start();
            }
            return START_STICKY;
        }

        @Override
        public void onDestroy() {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.release();
            super.onDestroy();
        }

        @Override
        public void onCompletion(MediaPlayer mediaPlayer) {
            stopSelf(); // Detener el servicio cuando la canción termine
        }

        @Nullable
        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }
    }


}