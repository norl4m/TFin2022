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
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
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
import com.marlon.apolo.tfinal2022.communicationAgora.video.AcceptVideoCallBroadcastReceiver;
import com.marlon.apolo.tfinal2022.communicationAgora.voice.AcceptVoiceCallBroadcastReceiver;
import com.marlon.apolo.tfinal2022.communicationAgora.voice.AgoraOnlyVoiceCallActivity;
import com.marlon.apolo.tfinal2022.communicationAgora.video.AgoraVideoCallActivity;
import com.marlon.apolo.tfinal2022.communicationAgora.video.RejectVideoCallBroadcastReceiver;
import com.marlon.apolo.tfinal2022.communicationAgora.voice.RejectVoiceCallBroadcastReceiver;

import com.marlon.apolo.tfinal2022.individualChat.model.MessageCloudPoc;
import com.marlon.apolo.tfinal2022.individualChat.view.CrazyDeleteBroadcastReceiver;
import com.marlon.apolo.tfinal2022.individualChat.view.CrazyIndividualChatActivity;
import com.marlon.apolo.tfinal2022.individualChat.view.CrazyReplyBroadcastReceiver;
import com.marlon.apolo.tfinal2022.model.Administrador;
import com.marlon.apolo.tfinal2022.model.Cita;
import com.marlon.apolo.tfinal2022.model.Empleador;
import com.marlon.apolo.tfinal2022.model.LlamadaVideo;
import com.marlon.apolo.tfinal2022.model.LlamadaVoz;
import com.marlon.apolo.tfinal2022.model.Participante;
import com.marlon.apolo.tfinal2022.model.Trabajador;
import com.marlon.apolo.tfinal2022.model.Usuario;
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
    private static final String ACTION_ACCEPT_CALL_VOICE = "ACTION_ACCEPT_CALL_VOICE";
    private static final String ACTION_DECLINE_CALL_VOICE = "ACTION_DECLINE_CALL_VOICE";
    private static final String ACTION_ACEPTAR_VIDEO_LLAMADA = "ACTION_ACEPTAR_VIDEO_LLAMADA";
    private static final String ACTION_RECHAZAR_VIDEO_LLAMADA = "ACTION_RECHAZAR_VIDEO_LLAMADA";


    private static final String VIDEO_CALLS_CHANNEL_ID = "VIDEO_CALLS_CHANNEL_ID";
    private static final String NOTIF_TEMPORALES_CHANNEL_ID = "NOTIFICACIONES_TEMPORALES";


    private DatabaseReference databaseReference;

    private DatabaseReference mNotificacionesRef;
    private ValueEventListener mNotificacionesListener;

    private DatabaseReference mTrabajadoresRef;
    private ValueEventListener mTrabajadoresListener;

    private Context contextInstance;
    private ArrayList<NotificacionStack> notificacionStackArrayList;
    private DatabaseReference mEmpleadoresRef;
    private ValueEventListener mEmpleadoresListener;

    private ArrayList<Trabajador> trabajadors;
    private ArrayList<Empleador> empleadors;

    private CrazyDeleteBroadcastReceiver crazyDeleteBroadcastReceiver;
    private CrazyReplyBroadcastReceiver crazyReplyBroadcastReceiver;
    private SharedPreferences myPreferences;
    private Usuario usuarioLocal;


    private SharedPreferences defaultSharedPreferences;
    private MediaPlayer mediaPlayerCallTone;
    private int startIdLocal;


    private ChildEventListener childEventListenerNotificacionesConMensajesLocas;
    private ChildEventListener childEventListenerCitasTrabajo;
    private ChildEventListener childEventListenerVideoCalls;
    //    private ArrayList<Integer> arrayListNotificationIds;
    private BroadcastReceiver acceptVideoCallBroadcastReceiver;
    private BroadcastReceiver rejectVideoCallBroadcastReceiver;
    private HashMap<String, Integer> meMap;
    private HashMap<String, Integer> meMapVoice;
    private ChildEventListener childEventListenerVoiceCalls;
    private BroadcastReceiver rejectVoiceCallBroadcastReceiver;
    private BroadcastReceiver acceptVoiceCallBroadcastReceiver;

    public class NotificacionStack {
        ArrayList<MessageCloudPoc> mensajeNubes;
        private int idNotification;
        private long numberMessages;

        public NotificacionStack() {
        }

        public ArrayList<MessageCloudPoc> getMensajeNubes() {
            return mensajeNubes;
        }

        public void setMensajeNubes(ArrayList<MessageCloudPoc> mensajeNubes) {
            this.mensajeNubes = mensajeNubes;
        }

        public int getIdNotification() {
            return idNotification;
        }

        public void setIdNotification(int idNotification) {
            this.idNotification = idNotification;
        }

        public long getNumberMessages() {
            return numberMessages;
        }

        public void setNumberMesssages(long numberMessages) {
            this.numberMessages = numberMessages;
        }

        @Override
        public String toString() {
            return "NotificacionCustom{" +
                    "mensajeNubes=" + mensajeNubes +
                    ", idNotification=" + idNotification +
                    ", numberMessages=" + numberMessages +
                    '}';
        }
    }


    private void createForegroundNotificationChannel() {

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

    private void createNotificationChannel() {
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

    private void createLlamadasVozNotificationChannel() {
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

    private void sendForegroundNotification() {
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

    private void createVideoCallChannel() {
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

            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void createNotificationsTempChannel() {
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

    private void listenerForegroundAdminFunctions() {
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

//
//        FirebaseDatabase.getInstance().getReference()
//                .child("trabajadores")
//                .addChildEventListener(new ChildEventListener() {
//                    @Override
//                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//
//                    }
//
//                    @Override
//                    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//
//                    }
//
//                    @Override
//                    public void onChildRemoved(@NonNull DataSnapshot snapshot) {
//                        String key = snapshot.getKey();
//                        try {
//                            if (key.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
//                                stopService(contextInstance);
//                                FirebaseAuth.getInstance().signOut();
//
//                            }
//                        } catch (Exception e) {
//                            Log.d(TAG, e.toString());
//                        }
//                    }
//
//                    @Override
//                    public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//
//                    }
//                });
//        FirebaseDatabase.getInstance().getReference()
//                .child("empleadores")
//                .addChildEventListener(new ChildEventListener() {
//                    @Override
//                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//
//                    }
//
//                    @Override
//                    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//
//                    }
//
//                    @Override
//                    public void onChildRemoved(@NonNull DataSnapshot snapshot) {
//                        String key = snapshot.getKey();
//                        try {
//                            if (key.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
//                                stopService(contextInstance);
//                                FirebaseAuth.getInstance().signOut();
//
//                            }
//                        } catch (Exception e) {
//                            Log.d(TAG, e.toString());
//                        }
//                    }
//
//                    @Override
//                    public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//
//                    }
//                });


    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        startIdLocal = startId;
        contextInstance = this;
//        Toast.makeText(this, "CRAZY service starting", Toast.LENGTH_SHORT).show();
        sendForegroundNotification();
        crazyDeleteBroadcastReceiver = new CrazyDeleteBroadcastReceiver();
        crazyReplyBroadcastReceiver = new CrazyReplyBroadcastReceiver();


        loadUsuarioLocal();

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

    private void listenerVoiceCalls() {
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

    private void listenerVideoCalls() {

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

    private NotificationCompat.Builder createVideoCallNotification(LlamadaVideo llamadaVideo, int notificationId) {

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, VIDEO_CALLS_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_oficios)
                .setContentTitle("Videollamada entrante...")
                .setContentText(llamadaVideo.getParticipanteCaller().getNombreParticipante())
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        // Build a PendingIntent for the reply action to trigger.

        Intent answerIntent = new Intent(contextInstance.getString(R.string.filter_incoing_call));
        answerIntent.putExtra("callStatus", "llamadaEntrante");
        answerIntent.putExtra("llamadaVideo", llamadaVideo);
        answerIntent.putExtra("notificationId", notificationId);

        Intent rejectIntent = new Intent(contextInstance.getString(R.string.filter_reject_call));
        rejectIntent.putExtra("callStatus", "llamadaEntrante");
        rejectIntent.putExtra("llamadaVideo", llamadaVideo);
        rejectIntent.putExtra("notificationId", notificationId);

        PendingIntent rejectPendingIntent = null;
        PendingIntent answerPendingIntent = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            rejectPendingIntent = PendingIntent.getBroadcast(contextInstance, notificationId, rejectIntent, FLAG_MUTABLE);

            answerPendingIntent =
                    PendingIntent.getBroadcast(contextInstance, notificationId, answerIntent, FLAG_MUTABLE);
        } else {
            rejectPendingIntent = PendingIntent.getBroadcast(contextInstance, notificationId, rejectIntent, FLAG_UPDATE_CURRENT);

            answerPendingIntent =
                    PendingIntent.getBroadcast(contextInstance, notificationId, answerIntent, FLAG_UPDATE_CURRENT);
            /*Bandera mala: PendingIntent.FLAG_IMMUTABLE*/
//
        }


        Intent fullScreenIntent = new Intent(this, AgoraVideoCallActivity.class);
        fullScreenIntent.putExtra("callStatus", "llamadaEntrante");
        fullScreenIntent.putExtra("llamadaVideo", llamadaVideo);
//        fullScreenIntent.putExtra("joinValue", "false");
        //fullScreenIntent.putExtra("extraJoin", "noconectar");


        PendingIntent fullScreenPendingIntent = null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            fullScreenPendingIntent = PendingIntent.getActivity(this, notificationId,
                    fullScreenIntent, PendingIntent.FLAG_MUTABLE);
        } else {
            fullScreenPendingIntent = PendingIntent.getActivity(this, notificationId,
                    fullScreenIntent, FLAG_UPDATE_CURRENT);
            /*Bandera mala: PendingIntent.FLAG_IMMUTABLE*/
//
        }


//        Intent aIntent = new Intent(this, AgoraVideoCallActivity.class);
//        aIntent.putExtra("callStatus", "llamadaEntrante");
//        aIntent.putExtra("llamadaVideo", llamadaVideo);
////        fullScreenIntent.putExtra("joinValue", "false");
//        aIntent.putExtra("extraJoin", "conectar");
//
//
//        PendingIntent aPendingIntent = null;
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//            aPendingIntent = PendingIntent.getActivity(this, notificationId,
//                    aIntent, PendingIntent.FLAG_MUTABLE);
//        } else {
//            aPendingIntent = PendingIntent.getActivity(this, notificationId,
//                    aIntent, FLAG_UPDATE_CURRENT);
//            /*Bandera mala: PendingIntent.FLAG_IMMUTABLE*/
////
//        }


//        Intent fullScreenIntentAccept = new Intent(this, AgoraVideoCallActivity.class);
//        fullScreenIntentAccept.putExtra("callStatus", "llamadaEntrante");
//        fullScreenIntentAccept.putExtra("llamadaVideo", llamadaVideo);
//        //fullScreenIntentAccept.putExtra("join", "true");
//
//
//        PendingIntent fullScreenPendingIntentAccept = null;
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//            fullScreenPendingIntentAccept = PendingIntent.getActivity(this, notificationId,
//                    fullScreenIntentAccept, PendingIntent.FLAG_MUTABLE);
//        } else {
//            fullScreenPendingIntentAccept = PendingIntent.getActivity(this, notificationId,
//                    fullScreenIntentAccept, FLAG_UPDATE_CURRENT);
//            /*Bandera mala: PendingIntent.FLAG_IMMUTABLE*/
////
//        }
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


    private NotificationCompat.Builder createVoiceCallNotification(LlamadaVoz llamadaVideo, int notificationId) {

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, VIDEO_CALLS_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_oficios)
                .setContentTitle("Llamada entrante...")
                .setContentText(llamadaVideo.getParticipanteCaller().getNombreParticipante())
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        // Build a PendingIntent for the reply action to trigger.

        Intent answerIntent = new Intent(contextInstance.getString(R.string.filter_incoing_voice_call));
        answerIntent.putExtra("callStatus", "llamadaEntrante");
        answerIntent.putExtra("llamadaVoz", llamadaVideo);
        answerIntent.putExtra("notificationId", notificationId);

        Intent rejectIntent = new Intent(contextInstance.getString(R.string.filter_reject_voice_call));
        rejectIntent.putExtra("callStatus", "llamadaEntrante");
        rejectIntent.putExtra("llamadaVoz", llamadaVideo);
        rejectIntent.putExtra("notificationId", notificationId);

        PendingIntent rejectPendingIntent = null;
        PendingIntent answerPendingIntent = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            rejectPendingIntent = PendingIntent.getBroadcast(contextInstance, notificationId, rejectIntent, FLAG_MUTABLE);

            answerPendingIntent =
                    PendingIntent.getBroadcast(contextInstance, notificationId, answerIntent, FLAG_MUTABLE);
        } else {
            rejectPendingIntent = PendingIntent.getBroadcast(contextInstance, notificationId, rejectIntent, FLAG_UPDATE_CURRENT);

            answerPendingIntent =
                    PendingIntent.getBroadcast(contextInstance, notificationId, answerIntent, FLAG_UPDATE_CURRENT);
            /*Bandera mala: PendingIntent.FLAG_IMMUTABLE*/
//
        }


        Intent fullScreenIntent = new Intent(this, AgoraOnlyVoiceCallActivity.class);
        fullScreenIntent.putExtra("callStatus", "llamadaEntrante");
        fullScreenIntent.putExtra("llamadaVoz", llamadaVideo);
//        fullScreenIntent.putExtra("joinValue", "false");
        //fullScreenIntent.putExtra("extraJoin", "noconectar");


        PendingIntent fullScreenPendingIntent = null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            fullScreenPendingIntent = PendingIntent.getActivity(this, notificationId,
                    fullScreenIntent, PendingIntent.FLAG_MUTABLE);
        } else {
            fullScreenPendingIntent = PendingIntent.getActivity(this, notificationId,
                    fullScreenIntent, FLAG_UPDATE_CURRENT);
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


        playingInconmingVideoCallAudio();
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


        playingInconmingCallAudio();
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

    private NotificationCompat.Builder createTempNotification(Cita cita, int notificationId) {

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


    public void cancelVideoCallNotification(int notificationId) {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        // notificationId is a unique int for each notification that you must define
        notificationManager.cancel(notificationId);
    }

    private void loadUsuarioLocal() {
        FirebaseDatabase.getInstance().getReference()
                .child("administrador")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Administrador administrador = snapshot.getValue(Administrador.class);
                        if (administrador != null) {
                            usuarioLocal = administrador;
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        FirebaseDatabase.getInstance().getReference()
                .child("trabajadores")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Trabajador trabajador = snapshot.getValue(Trabajador.class);
                        if (trabajador != null) {
                            usuarioLocal = trabajador;
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        FirebaseDatabase.getInstance().getReference()
                .child("empleadores")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Empleador empleador = snapshot.getValue(Empleador.class);
                        if (empleador != null) {
                            usuarioLocal = empleador;
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }


    private void listenerMessagePocNotificacionesFirebase() {
//        basicListen();
        registerReceiver(crazyDeleteBroadcastReceiver, new IntentFilter(ACTION_DELETE_NOTIFICATION));
        registerReceiver(crazyReplyBroadcastReceiver, new IntentFilter(ACTION_REPLY_NOTIFICATION));

        childEventListenerRecycler();
    }

    private void listenerLlamadasDeVozFirebase() {
//        basicListen();
        //registerReceiver(crazyDeleteBroadcastReceiver, new IntentFilter(ACTION_DELETE_NOTIFICATION));
        //registerReceiver(crazyReplyBroadcastReceiver, new IntentFilter(ACTION_REPLY_NOTIFICATION));


//        childEventListenerLallamadasVoz();

    }

//    private void childEventListenerLallamadasVoz() {
//
//        notificacionCustomLlamadas = new ArrayList<>();
//
//        LlamadaDeVozListener = new ChildEventListener() {
//            @Override
//            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//                Log.d(TAG, "onChildAdded -- llamadas de voz");
//                try {
//                    LlamadaVoz llamadaVoz = new LlamadaVoz();
//                    llamadaVoz = snapshot.getValue(LlamadaVoz.class);
//                    final int min = 5000;
//                    final int max = 5999;
//                    int idNotification = new Random().nextInt((max - min) + 1) + min;
//
//
//                    if (llamadaVoz.getParticipanteDestiny().getIdParticipante().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
//
//                        NotificacionCustomLlamada notificacionCustomLlamada = new NotificacionCustomLlamada();
//                        notificacionCustomLlamada.setIdNotification(idNotification);
//                        notificacionCustomLlamada.setLlamadaVoz(llamadaVoz);
//                        notificacionCustomLlamada.setIdCall(llamadaVoz.getId());
//
//                        notificacionCustomLlamadas.add(notificacionCustomLlamada);
//
////                        seleccionarUsuarioParaLlamada(llamadaVoz);
//                        seleccionarUsuarioParaLlamada(notificacionCustomLlamada);
//                    }
//                } catch (Exception e) {
//                    Log.d(TAG, e.toString());
//                }
//            }
//
//            @Override
//            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//                Log.d(TAG, "onChildChanged -- llamadas de voz");
//                LlamadaVoz llamadaVoz = snapshot.getValue(LlamadaVoz.class);
//
//                try {
//                    if (llamadaVoz.getParticipanteDestiny().getIdParticipante().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
//
//                        if (llamadaVoz.isRejectCallStatus()) {
//                            stopPlaying();
//                        }
//
//                        if (llamadaVoz.isDestinyStatus()) {
//                            stopPlaying();
//                        }
//
//                    }
//                } catch (Exception e) {
//
//                }
//            }
//
//            @Override
//            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
//
////                String callDelete = snapshot.getKey();
////                try {
////                    for (NotificacionCustomLlamada nl : notificacionCustomLlamadas) {
////                        if (nl.getIdCall().equals(callDelete)) {
////                            cancelNotification(nl.getIdNotification());
////                            notificacionCustomLlamadas.remove(nl);
////                            break;
////                        }
////                    }
////                } catch (Exception e) {
////                    Log.d(TAG, e.toString());
////                }
//
//
//                String callDelete = snapshot.getKey();
//                try {
//                    for (NotificacionCustomLlamada nl : notificacionCustomLlamadas) {
//                        if (nl.getIdCall().equals(callDelete)) {
//                            try {
//                                cancelNotification(nl.getIdNotification());
//                            } catch (Exception e) {
//
//                            }
//                            notificacionCustomLlamadas.remove(nl);
//                            break;
//                        }
//                    }
//                } catch (Exception e) {
//                    Log.d(TAG, e.toString());
//                }
//
//            }
//
//            @Override
//            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        };
//        FirebaseDatabase.getInstance().getReference()
//                .child("llamadasDeVoz")
//                .addChildEventListener(LlamadaDeVozListener);
//    }

//    private void seleccionarUsuarioParaLlamada(NotificacionCustomLlamada notificacionCustomLlamada) {
//
//        FirebaseDatabase.getInstance().getReference()
//                .child("administrador")
//                .child(notificacionCustomLlamada.getLlamadaVoz().getParticipanteCaller().getIdParticipante())
//                .addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        try {
//                            Administrador administrador = snapshot.getValue(Administrador.class);
//                            if (administrador != null) {
//                                showCallNotification(notificacionCustomLlamada, administrador);
//                            }
//                        } catch (Exception e) {
//
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
//                .child(notificacionCustomLlamada.getLlamadaVoz().getParticipanteCaller().getIdParticipante())
//                .addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        try {
//                            Empleador empleador = snapshot.getValue(Empleador.class);
//                            if (empleador != null) {
//                                showCallNotification(notificacionCustomLlamada, empleador);
//                            }
//                        } catch (Exception e) {
//
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
//                .child(notificacionCustomLlamada.getLlamadaVoz().getParticipanteCaller().getIdParticipante())
//                .addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        try {
//                            Trabajador trabajador = snapshot.getValue(Trabajador.class);
//                            if (trabajador != null) {
//                                showCallNotification(notificacionCustomLlamada, trabajador);
//                            }
//                        } catch (Exception e) {
//
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//
//                    }
//                });
//    }

//    private void listenerNotificacionesDeVideoLlamadas() {
//
//        notificacionCustomVideoLlamadaArrayList = new ArrayList<>();
//
////        registerReceiver(foregroundRejectVideoCallReceiver, new IntentFilter(ACTION_RECHAZAR_VIDEO_LLAMADA);
//
//        LlamadaDeVideoListener = new ChildEventListener() {
//            @Override
//            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//                Log.d(TAG, "");
//                try {
//                    LlamadaVideo videoLlamada = new LlamadaVideo();
//                    videoLlamada = snapshot.getValue(LlamadaVideo.class);
//                    if (videoLlamada.getParticipanteDestiny().getIdParticipante().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
//                        seleccionarUsuarioParaVideoLlamada(videoLlamada);
//                    }
//                } catch (Exception e) {
//                    Log.d(TAG, e.toString());
//                }
//            }
//
//            @Override
//            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//                LlamadaVideo llamadaVideoDB = snapshot.getValue(LlamadaVideo.class);
//
//                try {
//                    if (llamadaVideoDB.getParticipanteDestiny().getIdParticipante().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
//
//                        if (llamadaVideoDB.isRejectCallStatus()) {
//                            stopPlaying();
//                        }
//
//                        if (llamadaVideoDB.isDestinyStatus()) {
//                            stopPlaying();
//                        }
//
//                    }
//                } catch (Exception e) {
//
//                }
//
//
//            }
//
//            @Override
//            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
//
//                String callDelete = snapshot.getKey();
//                try {
//                    for (NotificacionCustomVideoLlamada nl : notificacionCustomVideoLlamadaArrayList) {
//                        if (nl.getIdCall().equals(callDelete)) {
//                            try {
//                                cancelNotification(nl.getIdNotification());
//                            } catch (Exception e) {
//                                Log.d(TAG, e.toString());
//                            }
//                            notificacionCustomLlamadas.remove(nl);
//                            break;
//                        }
//                    }
//                } catch (Exception e) {
//                    Log.d(TAG, e.toString());
//                }
//            }
//
//            @Override
//            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        };
//        FirebaseDatabase.getInstance().getReference()
//                .child("llamadasDeVideo")
//                .addChildEventListener(LlamadaDeVideoListener);
//    }

//    private void seleccionarUsuarioParaVideoLlamada(LlamadaVideo llamadaVideo) {
//
//        FirebaseDatabase.getInstance().getReference()
//                .child("administrador")
//                .child(llamadaVideo.getParticipanteCaller().getIdParticipante())
//                .addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        try {
//                            Administrador administrador = snapshot.getValue(Administrador.class);
//                            if (administrador != null) {
//                                showVideoCallNotification(llamadaVideo, administrador);
//                            }
//                        } catch (Exception e) {
//
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
//                .child(llamadaVideo.getParticipanteCaller().getIdParticipante())
//                .addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        try {
//                            Empleador empleador = snapshot.getValue(Empleador.class);
//                            if (empleador != null) {
//                                showVideoCallNotification(llamadaVideo, empleador);
//                            }
//                        } catch (Exception e) {
//
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
//                .child(llamadaVideo.getParticipanteCaller().getIdParticipante())
//                .addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        try {
//                            Trabajador trabajador = snapshot.getValue(Trabajador.class);
//                            if (trabajador != null) {
//                                showVideoCallNotification(llamadaVideo, trabajador);
//                            }
//                        } catch (Exception e) {
//
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//
//                    }
//                });
//    }

    public void playingInconmingCallAudio() {
//        mediaPlayerCallTone = MediaPlayer.create(contextInstance, R.raw.beat_it_gameboy);
        mediaPlayerCallTone = MediaPlayer.create(contextInstance, R.raw.katyusha_8_bit);
        mediaPlayerCallTone.setLooping(true);
        mediaPlayerCallTone.start(); // no need to call prepare(); create() does that for you
    }

    public void playingInconmingVideoCallAudio() {
//        mediaPlayerCallTone = MediaPlayer.create(contextInstance, R.raw.beat_it_gameboy);
        mediaPlayerCallTone = MediaPlayer.create(contextInstance, R.raw.skype_caller_tone);
        mediaPlayerCallTone.setLooping(true);
        mediaPlayerCallTone.start(); // no need to call prepare(); create() does that for you
    }

    public void stopPlaying() {
        try {
            mediaPlayerCallTone.stop();
            if (mediaPlayerCallTone != null) mediaPlayerCallTone.release();
        } catch (Exception e) {
            Log.d(TAG, e.toString());
        }

    }

//    private void showVideoCallNotification(LlamadaVideo llamadaVideo, Usuario usuarioTo) {
//        playingInconmingCallAudio();
//
//        final int min = 6000;
//        final int max = 6999;
//        int idNotification = new Random().nextInt((max - min) + 1) + min;
//
//
//        Intent notifyIntent = new Intent(this, VideoLlamadaActivity.class);
//        // Set the Activity to start in a new, empty task
//        notifyIntent.putExtra("callStatus", 1);
//        notifyIntent.putExtra("usuarioFrom", usuarioLocal);
//        notifyIntent.putExtra("usuarioTo", usuarioTo);
//        notifyIntent.putExtra("llamadaVideo", llamadaVideo);
//        notifyIntent.putExtra("channelNameShare", llamadaVideo.getId());
//        notifyIntent.putExtra("contest", 1);
//
//        PendingIntent notifyPendingIntent = null;
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//            notifyPendingIntent = PendingIntent.getActivity(this, idNotification, notifyIntent, FLAG_MUTABLE);
//        } else {
//            notifyPendingIntent = PendingIntent.getActivity(this, idNotification, notifyIntent, FLAG_UPDATE_CURRENT);
//        }
//
//        Intent replyIntent = new Intent(ACTION_ACEPTAR_VIDEO_LLAMADA);
//        replyIntent.putExtra("idNotification", idNotification);
//        replyIntent.putExtra("usuarioFrom", usuarioLocal);
//        replyIntent.putExtra("usuarioTo", usuarioTo);
//        replyIntent.putExtra("llamadaVideo", llamadaVideo);
//        replyIntent.putExtra("contestar", true);
//
//        PendingIntent replyPendingIntent = null;
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//            replyPendingIntent =
//                    PendingIntent.getBroadcast(getApplicationContext(),
//                            idNotification,
//                            replyIntent,
//                            FLAG_MUTABLE);
//        } else {
//            replyPendingIntent =
//                    PendingIntent.getBroadcast(getApplicationContext(),
//                            idNotification,
//                            replyIntent,
//                            FLAG_UPDATE_CURRENT);
//        }
//
//
//        NotificationCompat.Action actionResponder =
//                new NotificationCompat.Action.Builder(R.drawable.ic_baseline_send_24,
//                        "Aceptar", replyPendingIntent)
//                        .build();
//
//
////        Intent ggIntent = new Intent(this, VideoLlamadaActivity.class);
////        // Set the Activity to start in a new, empty task
////        ggIntent.putExtra("callStatus", 1);
////        ggIntent.putExtra("usuarioFrom", usuarioLocal);
////        ggIntent.putExtra("usuarioTo", usuarioTo);
////        ggIntent.putExtra("llamadaVideo", llamadaVideo);
////        ggIntent.putExtra("channelNameShare", llamadaVideo.getId());
////        ggIntent.putExtra("contest", 0);
////
////        PendingIntent ggPendingIntent = PendingIntent.getActivity(this, idNotification, ggIntent, PendingIntent.FLAG_UPDATE_CURRENT);
////
//
////        Intent replyIntent = new Intent(ACTION_ACEPTAR_VIDEO_LLAMADA);
////        replyIntent.putExtra("idNotification", idNotification);
////        replyIntent.putExtra("usuarioFrom", usuarioLocal);
////        replyIntent.putExtra("usuarioTo", usuarioTo);
////        replyIntent.putExtra("llamadaVideo", llamadaVideo);
//////        replyIntent.putExtra("contestar", true);
////        replyIntent.putExtra("channelNameShare", llamadaVideo.getId());
//////        replyIntent.putExtra("callStatus", 1);
//
//
////        Intent replyIntent = new Intent(ACTION_ACEPTAR_VIDEO_LLAMADA);
////        // Set the Activity to start in a new, empty task
////        replyIntent.putExtra("idNotification", idNotification);
////        replyIntent.putExtra("usuarioFrom", usuarioLocal);
////        replyIntent.putExtra("usuarioTo", usuarioTo);
////        replyIntent.putExtra("llamadaVideo", llamadaVideo);
////
////
////        PendingIntent replyPendingIntent = PendingIntent
////                .getBroadcast(getApplicationContext(), idNotification, replyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//
//
////
////        PendingIntent replyPendingIntent =
////                PendingIntent.getBroadcast(getApplicationContext(),
////                        idNotification,
////                        replyIntent,
////                        PendingIntent.FLAG_UPDATE_CURRENT);
//
//
////        NotificationCompat.Action actionResponder =
////                new NotificationCompat.Action.Builder(R.drawable.ic_baseline_send_24,
////                        "Aceptar", ggPendingIntent)
////                        .build();
//
//
//        Intent rejectIntent = new Intent(ACTION_RECHAZAR_VIDEO_LLAMADA);
//        rejectIntent.putExtra("idNotification", idNotification);
//        rejectIntent.putExtra("usuarioFrom", usuarioLocal);
//        rejectIntent.putExtra("usuarioTo", usuarioTo);
//        rejectIntent.putExtra("llamadaVideo", llamadaVideo);
//        rejectIntent.putExtra("contestar", false);
//        PendingIntent rejectPendingIntent = null;
//
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//            rejectPendingIntent =
//                    PendingIntent.getBroadcast(getApplicationContext(),
//                            idNotification,
//                            rejectIntent,
//                            FLAG_MUTABLE);
//        } else {
//            rejectPendingIntent =
//                    PendingIntent.getBroadcast(getApplicationContext(),
//                            idNotification,
//                            rejectIntent,
//                            FLAG_UPDATE_CURRENT);
//        }
//
//        NotificationCompat.Action actionRechazar =
//                new NotificationCompat.Action.Builder(R.drawable.ic_baseline_send_24,
//                        "Rechazar", rejectPendingIntent)
//                        .build();
//
//
//        // Create a call style notification for an incoming call.
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
//
//            final Icon icon =
//                    Icon.createWithResource(contextInstance,
//                            android.R.drawable.ic_dialog_info);
//
//            Notification.Action acceptAction =
//                    new Notification.Action.Builder(
//                            icon, "Contestar", replyPendingIntent)
//                            .build();
//
//
//            Notification.Action declineAction =
//                    new Notification.Action.Builder(
//                            icon, "Rechazar", rejectPendingIntent)
//                            .build();
//
//            Notification.Builder callNotification = new Notification.Builder(contextInstance, VOICE_CALL_NOTIFICATIONS_CHANNEL_ID)
//                    //                .setContentIntent(contentIntent)
//                    .setSmallIcon(R.drawable.ic_oficios)
//                    .setContentTitle(getString(R.string.incoming_video_call_text))
//                    .setContentText(llamadaVideo.getParticipanteCaller().getNombreParticipante())
//
//                    .setFullScreenIntent(notifyPendingIntent, true)
//                    .setDeleteIntent(rejectPendingIntent)
//                    .addAction(acceptAction)
//                    .addAction(declineAction)
//                    .setAutoCancel(true);
//
//
//            NotificationManager notificationManagerX = getSystemService(NotificationManager.class);
//            notificationManagerX.notify(idNotification, callNotification.build());
//
//        } else {
//
//            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
////                Notification.Builder callNotification = new Notification.Builder(contextInstance, VOICE_CALL_NOTIFICATIONS_CHANNEL_ID)
////                        //                .setContentIntent(contentIntent)
////                        .setSmallIcon(R.drawable.ic_oficios)
////                        .setContentTitle(getString(R.string.incoming_call_text) + "-" + String.valueOf(Build.VERSION.SDK_INT))
////                        .setContentText(usuarioRemoto.getNombre() + " " + usuarioRemoto.getApellido())
////                        .setFullScreenIntent(fullScreenPendingIntent, true)
////                        .setDeleteIntent(declinePendingIntent)
//////                        .addAction(actionAccept)
//////                        .addAction(actionDecline)
////                        .setAutoCancel(true);
//////                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(contextInstance);
//////                notificationManager.notify(notificacionCustomLlamada.getIdNotification(), callNotification.build());
//
//                final Icon icon =
//                        Icon.createWithResource(contextInstance,
//                                android.R.drawable.ic_dialog_info);
//
//                Notification.Action acceptAction =
//                        new Notification.Action.Builder(
//                                icon, "Contestar", replyPendingIntent)
//                                .build();
//
//
//                Notification.Action declineAction =
//                        new Notification.Action.Builder(
//                                icon, "Rechazar", rejectPendingIntent)
//                                .build();
//
//                Notification callNotification = new Notification.Builder(this, VOICE_CALL_NOTIFICATIONS_CHANNEL_ID)
//                        .setContentTitle(getString(R.string.incoming_video_call_text))
//                        .setContentText(llamadaVideo.getParticipanteCaller().getNombreParticipante())
//                        .setSmallIcon(R.drawable.ic_oficios)
//                        .setFullScreenIntent(notifyPendingIntent, true)
//                        .setDeleteIntent(rejectPendingIntent).setTicker(getText(R.string.ticker_text))
//                        .setPriority(Notification.PRIORITY_HIGH)
//                        .setDefaults(Notification.DEFAULT_ALL)
//                        .addAction(acceptAction)
//                        .addAction(declineAction)
//                        .build();
//                NotificationManager notificationManagerX = getSystemService(NotificationManager.class);
//                notificationManagerX.notify(idNotification, callNotification);
//
//
//            } else {
//                // Create the reply action and add the remote input.
//                NotificationCompat.Action actionAccept =
//                        new NotificationCompat.Action.Builder(R.drawable.ic_oficios,
//                                "Contestar", replyPendingIntent)
//                                .build();
//
//                NotificationCompat.Action actionDecline =
//                        new NotificationCompat.Action.Builder(R.drawable.ic_oficios,
//                                "Rechazar", rejectPendingIntent)
//                                .build();
//                NotificationCompat.Builder builder = new NotificationCompat.Builder(contextInstance, VOICE_CALL_NOTIFICATIONS_CHANNEL_ID)
//                        .setSmallIcon(R.drawable.ic_oficios)
//                        .setContentTitle(getString(R.string.incoming_video_call_text))
//                        .setContentText(llamadaVideo.getParticipanteCaller().getNombreParticipante())
//                        .setPriority(NotificationCompat.PRIORITY_HIGH)
//                        .setDefaults(NotificationCompat.DEFAULT_ALL)
//                        .setFullScreenIntent(notifyPendingIntent, true)
//                        .setDeleteIntent(rejectPendingIntent)
//                        .addAction(actionAccept)
//                        .addAction(actionDecline)
//                        // Set the intent that will fire when the user taps the notification
////                    .setContentIntent(pendingIntent)
////                    .setContentIntent(pendingIntent)
//                        .setAutoCancel(true);
//                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(contextInstance);
//                notificationManager.notify(idNotification, builder.build());
//
//            }
//        }
//
//
//        NotificationCompat.Builder notification = null;
//        NotificationCompat.MessagingStyle messagingStyle = new NotificationCompat.MessagingStyle("Responder");
//        notification = new NotificationCompat.Builder(this, VOICE_CALL_NOTIFICATIONS_CHANNEL_ID)
//                .setSmallIcon(R.drawable.ic_oficios)
//                .setAutoCancel(true)// Quita la notification cunado el usuario la presiona
//                //.addAction(action)
//                .setContentIntent(notifyPendingIntent)
//                .setDeleteIntent(rejectPendingIntent)
//                .addAction(actionResponder)
//                .addAction(actionRechazar)
//                .setPriority(NotificationCompat.PRIORITY_HIGH)
//                .setDefaults(NotificationCompat.DEFAULT_ALL);
//
//
//        long timeStamp = System.currentTimeMillis();
//        NotificationCompat.MessagingStyle.Message message =
//                new NotificationCompat.MessagingStyle.Message(
//                        "Llamada de video entrante...",
//                        timeStamp,
//                        llamadaVideo.getParticipanteCaller().getNombreParticipante());
////                            not.getFrom() + " " + not.getFrom());
//        messagingStyle.addMessage(message);
//
//
//        notification.setStyle(messagingStyle);
//
//        //000000000000000000000000000000000000000000NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
//
//        // notificationId is a unique int for each notification that you must define
////        notificationManager.notify(idNotification, notification.build());
//
//
//        NotificacionCustomVideoLlamada notificacionCustomVideoLlamada = new NotificacionCustomVideoLlamada();
//        notificacionCustomVideoLlamada.setIdNotification(idNotification);
//        notificacionCustomVideoLlamada.setVideoLlamada(llamadaVideo);
//        notificacionCustomVideoLlamada.setIdCall(llamadaVideo.getId());
//        notificacionCustomVideoLlamadaArrayList.add(notificacionCustomVideoLlamada);
//    }

    public void programarAlarmaLocalCustomLoco(Cita cita) {
        Log.d(TAG, cita.toString());

        final int min = 7000;
        final int max = 7999;
        int random = new Random().nextInt((max - min) + 1) + min;
//        int random = 7000;


//        DateFormat formatFecPoc = new SimpleDateFormat("dd MMMM yyyy HH:mm a", new Locale("es", "ES"));

//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy HH:mm", Locale.getDefault());


        Date d = null;
        try {
            d = sdf.parse(cita.getFechaCita());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar calz = Calendar.getInstance();
        calz.setTime(d);
        Calendar objCalendarPoCLoco = calz;


        final AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent alarmIntent = new Intent(contextInstance, AlarmReceiver.class);
        Log.d(TAG, "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
        Log.d(TAG, "CONFIGURANDO ALARMA REMOTA LOCAL LOCO");
        Log.d(TAG, "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
        alarmIntent.putExtra("idCita", cita.getIdCita());
        alarmIntent.putExtra("nT", cita.getNombreTrabajador());
        alarmIntent.putExtra("nE", cita.getNombreEmpleador());
        alarmIntent.putExtra("idFrom", cita.getFrom());
        alarmIntent.putExtra("idTo", cita.getTo());
        alarmIntent.putExtra("fec", cita.getFechaCita());
        //cita.setItems(null);
//        alarmIntent.putExtra("cita", cita);
        alarmIntent.putExtra("random", random);

//        Cita cita1 = new Cita();
//        cita1.setIdCita(cita.getIdCita());
//        alarmIntent.putExtra("cita",cita1);

//        boolean alarmUp = (PendingIntent.getBroadcast(getApplicationContext(), random, alarmIntent, PendingIntent.FLAG_NO_CREATE) != null);

//        Log.d(TAG, String.format("Estado de alarma: %s", alarmUp));
        PendingIntent notifyPendingIntent = null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            notifyPendingIntent = PendingIntent.getBroadcast
                    (getApplicationContext(), random, alarmIntent,
                            FLAG_MUTABLE);
        } else {
            notifyPendingIntent = PendingIntent.getBroadcast
                    (getApplicationContext(), random, alarmIntent,
                            FLAG_UPDATE_CURRENT);
            /*Bandera de mrda: PendingIntent.FLAG_IMMUTABLE*/
//
        }


        /*
         *  La fecha y hora que he mencionado aquí es: 2012- 28 de junio, 11:20:00 AM. Y lo más importante es que el mes se especifica de 0 a 11 solamente. Medios de junio debe ser especificado por 5.
         * */

        AlarmManager objAlarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Calendar objCalendarPoC = Calendar.getInstance();
        objCalendarPoC.set(Calendar.YEAR, 2022);
        //objCalendarPoC.set(Calendar.YEAR, objCalendarPoC.get(Calendar.YEAR));
        objCalendarPoC.set(Calendar.MONTH, 7);/*0 - 11*/
        objCalendarPoC.set(Calendar.DAY_OF_MONTH, 15);
        objCalendarPoC.set(Calendar.HOUR_OF_DAY, 13);
        objCalendarPoC.set(Calendar.MINUTE, 20);
        objCalendarPoC.set(Calendar.SECOND, 0);
        objCalendarPoC.set(Calendar.MILLISECOND, 0);
        objCalendarPoC.set(Calendar.AM_PM, Calendar.PM);
        //Intent alamShowIntent = new Intent(this, AlarmReceiver.class);
        //PendingIntent alarmPendingIntent = PendingIntent.getActivity(this, 0, alamShowIntent, 0);
        //objAlarmManager.set(AlarmManager.RTC_WAKEUP, objCalendarPoC.getTimeInMillis(), alarmPendingIntent);
        DateFormat formatFecPoc = new SimpleDateFormat("dd MMMM yyyy HH:mm a", new Locale("es", "ES"));
//        try {
////            objCalendarPoC.setTime(Objects.requireNonNull(formatFecPoc.parse(cita.getFechaCita())));
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
        Log.d(TAG, "FECHA NUEVA SUPER EXACTA: " + formatFecPoc.format(objCalendarPoCLoco.getTime()));


        Calendar objCalendar = Calendar.getInstance();

        DateFormat formatFec = new SimpleDateFormat("dd MMMM yyyy HH:mm a", new Locale("es", "ES"));
        Calendar calendar = Calendar.getInstance();

        try {
            objCalendar.setTime(Objects.requireNonNull(formatFec.parse(cita.getFechaCita())));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (alarmManager != null) {
//            Log.d(TAG, "Configurando alarma RTC");
            Log.d(TAG, "Configurando alarma RTC-WAKE-UP  objCalendarPoCLoco "); /* es el mas exacto*/
            /*Las alarmas y notificaciones son un infierno en android*/
//            Log.d(TAG, "Configurando alarma ELAPSED_REALTIME_WAKsEUP  objCalendarPoCLoco ");
//            alarmManager.set(AlarmManager.RTC,
            alarmManager.set(AlarmManager.RTC_WAKEUP,
//                    objCalendar.getTimeInMillis(),
//                    objCalendarPoC.getTimeInMillis(),
                    objCalendarPoCLoco.getTimeInMillis(),
                    notifyPendingIntent);
//            boolean alarmUp = (PendingIntent.getBroadcast(getApplicationContext(), random, alarmIntent, PendingIntent.FLAG_NO_CREATE) != null);

            boolean alarmUp = (PendingIntent.getBroadcast(getApplicationContext(), random, alarmIntent, PendingIntent.FLAG_NO_CREATE) != null);
            if (alarmUp) {
                Log.d("myTag", "Alarm is already active");
            }
//
//
//            Log.d(TAG, String.format("EstadO ALARMA LOCO: %s", alarmUp));
//            Log.d(TAG, String.format("EstadO ALARMA LOCO: %s", cita.toString()));
//            Log.d(TAG, String.format("EstadO ALARMA LOCO: %s", String.valueOf(random)));


            String patronFechaYHora = "dd MMMM yyyy HH:mm aa";
            Locale locale = new Locale("es", "ES");
            DateFormat format = new SimpleDateFormat("dd MMMM yyyy HH:mm aa", new Locale("es", "ES"));


            try {
                SimpleDateFormat formatFecha = new SimpleDateFormat(patronFechaYHora, locale);
                Date date = formatFecha.parse(cita.getFechaCita());
                //Log.d(TAG, "INPUT: " + horaYFecha);

                Calendar cal = Calendar.getInstance();
                if (date != null) {
                    cal.setTime(date);
                }

//            Date date1 = format.parse(format.format(calendar.getTime()));
//                Date date1 = format.parse(format.format(cal.getTime()));
                Date date1 = format.parse(format.format(objCalendarPoCLoco.getTime()));
                Date date2 = new Date();
                Log.d(TAG, "Date 1 selected(alarm date) objCalendarPoCLoco: " + format.format(date1));
                Log.d(TAG, "Date 2 compare(now): " + format.format(date2));
                if (date1.compareTo(date2) > 0) {
                    Log.d(TAG, "alarma configurada");

                } else if (date1.compareTo(date2) < 0) {
                    Log.d(TAG, "La alarma ha expirado!");
                    alarmManager.cancel(notifyPendingIntent);
                    Log.d(TAG, "La alarma ha sido descativada!");

//                                            arrayListErrores.add("La fecha seleccionada es incorrecta");
//
//                                            validacion = false;
                } else if (date1.compareTo(date2) == 0) {

                }
//                if (alarmUp) {
//                    Log.d(TAG, "Alarm is active");
//                    Log.d(TAG, cita.toString());
//                    Log.d(TAG, String.valueOf(random));
//                } else {
//                    Log.d(TAG, "Alarm is no active");
//                }
            } catch (ParseException e) {
                e.printStackTrace();
            }


//            objAlarmManager.set(AlarmManager.RTC_WAKEUP,objCalendar.getTimeInMillis(), notifyPendingIntent);

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


//    private void seleccionarUsuarioParaLlamada(LlamadaVoz llamadaVoz) {
//
//        FirebaseDatabase.getInstance().getReference()
//                .child("administrador")
//                .child(llamadaVoz.getParticipanteCaller().getIdParticipante())
//                .addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        try {
//                            Administrador administrador = snapshot.getValue(Administrador.class);
//                            if (administrador != null) {
//                                showCallNotification(llamadaVoz, administrador);
//                            }
//                        } catch (Exception e) {
//
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
//                .child(llamadaVoz.getParticipanteCaller().getIdParticipante())
//                .addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        try {
//                            Empleador empleador = snapshot.getValue(Empleador.class);
//                            if (empleador != null) {
//                                showCallNotification(llamadaVoz, empleador);
//                            }
//                        } catch (Exception e) {
//
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
//                .child(llamadaVoz.getParticipanteCaller().getIdParticipante())
//                .addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        try {
//                            Trabajador trabajador = snapshot.getValue(Trabajador.class);
//                            if (trabajador != null) {
//                                showCallNotification(llamadaVoz, trabajador);
//                            }
//                        } catch (Exception e) {
//
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//
//                    }
//                });
//    }


    //    private void showCallNotification(LlamadaVoz llamadaVoz, Usuario usuarioRemoto) {
////        final int min = 5000;
////        final int max = 5999;
////        int idNotification = new Random().nextInt((max - min) + 1) + min;
//
//
//
//
//        Log.d(TAG, "Llamada entrante:");
//        Log.d(TAG, usuarioRemoto.getNombre() + " " + usuarioRemoto.getApellido());
//
//
//        Intent declineIntent = new Intent(ACTION_DECLINE_CALL_VOICE);
//        declineIntent.putExtra("idNotification", idNotification);
//        declineIntent.putExtra("usuarioFrom", usuarioLocal);
//        declineIntent.putExtra("usuarioTo", usuarioRemoto);
//        declineIntent.putExtra("llamadaVoz", llamadaVoz);
//        declineIntent.putExtra("contestar", false);
//
//        Intent answerIntent = new Intent(ACTION_ACCEPT_CALL_VOICE);
//        answerIntent.putExtra("idNotification", idNotification);
//        answerIntent.putExtra("usuarioFrom", usuarioLocal);
//        answerIntent.putExtra("usuarioTo", usuarioRemoto);
//        answerIntent.putExtra("llamadaVoz", llamadaVoz);
//        answerIntent.putExtra("contestar", true);
//
//        PendingIntent declinePendingIntent = null;
//        PendingIntent answerPendingIntent = null;
//
//
//        // Create a call style notification for an incoming call.
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
//
//            declinePendingIntent = PendingIntent.getBroadcast(contextInstance, idNotification, declineIntent, FLAG_MUTABLE);
//            answerPendingIntent = PendingIntent.getBroadcast(contextInstance, idNotification, answerIntent, FLAG_MUTABLE);
//
//
//            Intent fullScreenIntent = new Intent(this, LlamadaVozActivity.class);
//            fullScreenIntent.putExtra("idNotification", idNotification);
//            fullScreenIntent.putExtra("callStatus", 1);
//            fullScreenIntent.putExtra("usuarioFrom", usuarioLocal);
//            fullScreenIntent.putExtra("usuarioTo", usuarioRemoto);
//            fullScreenIntent.putExtra("llamadaVoz", llamadaVoz);
//            fullScreenIntent.putExtra("channelNameShare", llamadaVoz.getId());
//            PendingIntent fullScreenPendingIntent = PendingIntent.getActivity(this, idNotification,
//                    fullScreenIntent, FLAG_MUTABLE);
//
//
//            final Icon icon =
//                    Icon.createWithResource(contextInstance,
//                            android.R.drawable.ic_dialog_info);
//
//            Notification.Action acceptAction =
//                    new Notification.Action.Builder(
//                            icon, "Aceptar", answerPendingIntent)
//                            .build();
//
//
//            Notification.Action declineAction =
//                    new Notification.Action.Builder(
//                            icon, "Rechazar", declinePendingIntent)
//                            .build();
//
//            Notification.Builder callNotification = new Notification.Builder(contextInstance, VOICE_CALL_NOTIFICATIONS_CHANNEL_ID)
//                    //                .setContentIntent(contentIntent)
//                    .setSmallIcon(R.drawable.ic_oficios)
//                    .setContentTitle(getString(R.string.incoming_call_text))
//                    .setContentText(usuarioRemoto.getNombre() + " " + usuarioRemoto.getApellido())
//                    .setFullScreenIntent(fullScreenPendingIntent, true)
//                    .setDeleteIntent(declinePendingIntent)
//                    .addAction(acceptAction)
//                    .addAction(declineAction)
//                    .setAutoCancel(true);
//            ;
////                    .setStyle(
////                            Notification.CallStyle.forIncomingCall(incoming_caller, declinePendingIntent, answerPendingIntent))
////                    .addPerson(incoming_caller)
////            ;
//
//            // Issue the notification.
////            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(contextInstance);
////            notificationManager.notify(idNotification, callNotification.build());
//
//            NotificationManager notificationManagerX = getSystemService(NotificationManager.class);
//            notificationManagerX.notify(idNotification, callNotification.build());
//
//        } else {
//            // Create an explicit intent for an Activity in your app
//            declinePendingIntent = PendingIntent.getBroadcast(contextInstance, idNotification, declineIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//            answerPendingIntent = PendingIntent.getBroadcast(contextInstance, idNotification, answerIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//
//
//            NotificationCompat.Builder builder = new NotificationCompat.Builder(contextInstance, VOICE_CALL_NOTIFICATIONS_CHANNEL_ID)
//                    .setSmallIcon(R.drawable.ic_oficios)
//                    .setContentTitle("Llamada entrante")
//                    .setContentText(usuarioRemoto.getNombre() + " " + usuarioRemoto.getApellido())
//                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
//                    // Set the intent that will fire when the user taps the notification
////                    .setContentIntent(pendingIntent)
////                    .setContentIntent(pendingIntent)
//                    .setAutoCancel(true);
//            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(contextInstance);
//            notificationManager.notify(idNotification, builder.build());
//
//        }
//    }
//    private void showCallNotification(NotificacionCustomLlamada notificacionCustomLlamada, Usuario usuarioRemoto) {
////        final int min = 5000;
////        final int max = 5999;
////        int idNotification = new Random().nextInt((max - min) + 1) + min;
//
//
//        playingInconmingCallAudio();
//
//        Log.d(TAG, "Llamada entrante:");
//        Log.d(TAG, usuarioRemoto.getNombre() + " " + usuarioRemoto.getApellido());
//
//        Intent fullScreenIntent = new Intent(this, LlamadaVozActivity.class);
//        fullScreenIntent.putExtra("idNotification", notificacionCustomLlamada.getIdNotification());
//        fullScreenIntent.putExtra("callStatus", 1);
//        fullScreenIntent.putExtra("usuarioFrom", usuarioLocal);
//        fullScreenIntent.putExtra("usuarioTo", usuarioRemoto);
//        fullScreenIntent.putExtra("llamadaVoz", notificacionCustomLlamada.getLlamadaVoz());
//        fullScreenIntent.putExtra("channelNameShare", notificacionCustomLlamada.getLlamadaVoz().getId());
//
//
//        Intent declineIntent = new Intent(ACTION_DECLINE_CALL_VOICE);
//        declineIntent.putExtra("idNotification", notificacionCustomLlamada.getIdNotification());
//        declineIntent.putExtra("usuarioFrom", usuarioLocal);
//        declineIntent.putExtra("usuarioTo", usuarioRemoto);
//        declineIntent.putExtra("llamadaVoz", notificacionCustomLlamada.getLlamadaVoz());
//        declineIntent.putExtra("contestar", false);
//
//        Intent answerIntent = new Intent(ACTION_ACCEPT_CALL_VOICE);
//        answerIntent.putExtra("idNotification", notificacionCustomLlamada.getIdNotification());
//        answerIntent.putExtra("usuarioFrom", usuarioLocal);
//        answerIntent.putExtra("usuarioTo", usuarioRemoto);
//        answerIntent.putExtra("llamadaVoz", notificacionCustomLlamada.getLlamadaVoz());
//        answerIntent.putExtra("contestar", true);
//
//        PendingIntent fullScreenPendingIntent = null;
//        PendingIntent declinePendingIntent = null;
//        PendingIntent answerPendingIntent = null;
//
//
//        // Create a call style notification for an incoming call.
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
//
//            declinePendingIntent = PendingIntent.getBroadcast(contextInstance, notificacionCustomLlamada.getIdNotification(), declineIntent, FLAG_MUTABLE);
//            answerPendingIntent = PendingIntent.getBroadcast(contextInstance, notificacionCustomLlamada.getIdNotification(), answerIntent, FLAG_MUTABLE);
//            fullScreenPendingIntent = PendingIntent.getActivity(this, notificacionCustomLlamada.getIdNotification(),
//                    fullScreenIntent, FLAG_MUTABLE);
//
//            final Icon icon =
//                    Icon.createWithResource(contextInstance,
//                            android.R.drawable.ic_dialog_info);
//
//            Notification.Action acceptAction =
//                    new Notification.Action.Builder(
//                            icon, "Contestar", answerPendingIntent)
//                            .build();
//
//
//            Notification.Action declineAction =
//                    new Notification.Action.Builder(
//                            icon, "Rechazar", declinePendingIntent)
//                            .build();
//
//            Notification.Builder callNotification = new Notification.Builder(contextInstance, VOICE_CALL_NOTIFICATIONS_CHANNEL_ID)
//                    //                .setContentIntent(contentIntent)
//                    .setSmallIcon(R.drawable.ic_oficios)
//                    .setContentTitle(getString(R.string.incoming_call_text))
//                    .setContentText(usuarioRemoto.getNombre() + " " + usuarioRemoto.getApellido())
//                    .setFullScreenIntent(fullScreenPendingIntent, true)
//                    .setDeleteIntent(declinePendingIntent)
//                    .addAction(acceptAction)
//                    .addAction(declineAction)
//                    .setAutoCancel(true);
//            ;
////                    .setStyle(
////                            Notification.CallStyle.forIncomingCall(incoming_caller, declinePendingIntent, answerPendingIntent))
////                    .addPerson(incoming_caller)
////            ;
//
//            // Issue the notification.
////            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(contextInstance);
////            notificationManager.notify(idNotification, callNotification.build());
//
//            NotificationManager notificationManagerX = getSystemService(NotificationManager.class);
//            notificationManagerX.notify(notificacionCustomLlamada.getIdNotification(), callNotification.build());
//
//        } else {
//            // Create an explicit intent for an Activity in your app
//            declinePendingIntent = PendingIntent.getBroadcast(contextInstance, notificacionCustomLlamada.getIdNotification(), declineIntent, FLAG_UPDATE_CURRENT);
//            answerPendingIntent = PendingIntent.getBroadcast(contextInstance, notificacionCustomLlamada.getIdNotification(), answerIntent, FLAG_UPDATE_CURRENT);
//            fullScreenPendingIntent = PendingIntent.getActivity(contextInstance, notificacionCustomLlamada.getIdNotification(),
//                    fullScreenIntent, FLAG_UPDATE_CURRENT);
//
//
//            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
////                Notification.Builder callNotification = new Notification.Builder(contextInstance, VOICE_CALL_NOTIFICATIONS_CHANNEL_ID)
////                        //                .setContentIntent(contentIntent)
////                        .setSmallIcon(R.drawable.ic_oficios)
////                        .setContentTitle(getString(R.string.incoming_call_text) + "-" + String.valueOf(Build.VERSION.SDK_INT))
////                        .setContentText(usuarioRemoto.getNombre() + " " + usuarioRemoto.getApellido())
////                        .setFullScreenIntent(fullScreenPendingIntent, true)
////                        .setDeleteIntent(declinePendingIntent)
//////                        .addAction(actionAccept)
//////                        .addAction(actionDecline)
////                        .setAutoCancel(true);
//////                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(contextInstance);
//////                notificationManager.notify(notificacionCustomLlamada.getIdNotification(), callNotification.build());
//
//                final Icon icon =
//                        Icon.createWithResource(contextInstance,
//                                android.R.drawable.ic_dialog_info);
//
//                Notification.Action acceptAction =
//                        new Notification.Action.Builder(
//                                icon, "Contestar", answerPendingIntent)
//                                .build();
//
//
//                Notification.Action declineAction =
//                        new Notification.Action.Builder(
//                                icon, "Rechazar", declinePendingIntent)
//                                .build();
//
//                Notification callNotification = new Notification.Builder(this, VOICE_CALL_NOTIFICATIONS_CHANNEL_ID)
//                        .setContentTitle(getString(R.string.incoming_call_text))
//                        .setContentText(usuarioRemoto.getNombre() + " " + usuarioRemoto.getApellido())
//                        .setSmallIcon(R.drawable.ic_oficios)
//                        .setFullScreenIntent(fullScreenPendingIntent, true)
//                        .setDeleteIntent(declinePendingIntent).setTicker(getText(R.string.ticker_text))
//                        .setPriority(Notification.PRIORITY_HIGH)
//                        .setDefaults(Notification.DEFAULT_ALL)
//                        .addAction(acceptAction)
//                        .addAction(declineAction)
//                        .build();
//                NotificationManager notificationManagerX = getSystemService(NotificationManager.class);
//                notificationManagerX.notify(notificacionCustomLlamada.getIdNotification(), callNotification);
//
//
//            } else {
//                // Create the reply action and add the remote input.
//                NotificationCompat.Action actionAccept =
//                        new NotificationCompat.Action.Builder(R.drawable.ic_oficios,
//                                "Contestar", answerPendingIntent)
//                                .build();
//
//                NotificationCompat.Action actionDecline =
//                        new NotificationCompat.Action.Builder(R.drawable.ic_oficios,
//                                "Rechazar", declinePendingIntent)
//                                .build();
//                NotificationCompat.Builder builder = new NotificationCompat.Builder(contextInstance, VOICE_CALL_NOTIFICATIONS_CHANNEL_ID)
//                        .setSmallIcon(R.drawable.ic_oficios)
//                        .setContentTitle(getString(R.string.incoming_call_text))
//                        .setContentText(usuarioRemoto.getNombre() + " " + usuarioRemoto.getApellido())
//                        .setPriority(NotificationCompat.PRIORITY_HIGH)
//                        .setDefaults(NotificationCompat.DEFAULT_ALL)
//                        .setFullScreenIntent(fullScreenPendingIntent, true)
//                        .setDeleteIntent(declinePendingIntent)
//                        .addAction(actionAccept)
//                        .addAction(actionDecline)
//                        // Set the intent that will fire when the user taps the notification
////                    .setContentIntent(pendingIntent)
////                    .setContentIntent(pendingIntent)
//                        .setAutoCancel(true);
//                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(contextInstance);
//                notificationManager.notify(notificacionCustomLlamada.getIdNotification(), builder.build());
//
//            }
//        }
//    }


    public void basicListen() {

        // [START basic_listen]
        // Get a reference to Messages and attach a listener
        ArrayList<NotificacionStack> notificacionStackArrayList = new ArrayList<>();
        mNotificacionesRef = databaseReference.child("notificaciones");
        mNotificacionesListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // New data at this path. This method will be called after every change in the
                // data at this path or a subpath.


                // Get the data as Message objects
                for (DataSnapshot dataUser : dataSnapshot.getChildren()) {
                    NotificacionStack notificacionStack = new NotificacionStack();

                    final int min = 3000;
                    final int max = 3999;
                    int idNotification = new Random().nextInt((max - min) + 1) + min;
                    notificacionStack.setIdNotification(idNotification);

                    Log.d(TAG, "####################################################");
                    Log.d(TAG, "Number of messages: " + dataUser.getChildrenCount());
                    Log.d(TAG, "idNotification: " + String.valueOf(idNotification));
                    notificacionStack.setNumberMesssages(dataUser.getChildrenCount());
                    ArrayList<MessageCloudPoc> mensajeNubes = new ArrayList<>();
                    for (DataSnapshot data : dataUser.getChildren()) {
                        MessageCloudPoc mensajeNube = data.getValue(MessageCloudPoc.class);
                        mensajeNubes.add(mensajeNube);
                        Log.d(TAG, mensajeNube.toString());
                    }
                    notificacionStack.setMensajeNubes(mensajeNubes);
                    if (notificacionStackArrayList.size() > 0) {
                        int index = 0;
                        boolean flagFound = false;
                        for (NotificacionStack nt : notificacionStackArrayList) {
                            if (nt.getMensajeNubes().get(0).getFrom().equals(mensajeNubes.get(0).getFrom())) {
                                nt.setMensajeNubes(mensajeNubes);
                                notificacionStackArrayList.set(index, nt);
                                flagFound = true;
                                break;
                            }
                            index++;
                        }
                        if (!flagFound) {
                            notificacionStackArrayList.add(notificacionStack);

                        }
                    } else {
                        notificacionStackArrayList.add(notificacionStack);
                    }

                    Log.d(TAG, "####################################################");
                }

                showAllNotifications(notificacionStackArrayList);

//                for (DataSnapshot child : dataSnapshot.getChildren()) {
//                    // Extract a Message object from the DataSnapshot
//                    Message message = child.getValue(Message.class);
//
//                    // Use the Message
//                    // [START_EXCLUDE]
//                    Log.d(TAG, "message text:" + message.getText());
//                    Log.d(TAG, "message sender name:" + message.getName());
//                    // [END_EXCLUDE]
//                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Could not successfully listen for data, log the error
                Log.e(TAG, "messages:onCancelled:" + error.getMessage());
            }
        };
        mNotificacionesRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(mNotificacionesListener);
        // [END basic_listen]
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


    private void showAllNotifications(ArrayList<NotificacionStack> notificacionStackArrayList) {
        Log.d(TAG, "###################################");
        Log.d(TAG, "showAllNotifications");
        Log.d(TAG, "###################################");
        for (NotificacionStack nt : notificacionStackArrayList) {

            FirebaseDatabase.getInstance().getReference().child("empleadores")
                    .child(nt.getMensajeNubes().get(0).getFrom())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Empleador empleador = snapshot.getValue(Empleador.class);
                            if (empleador != null) {
                                Log.d(TAG, "###################################");
                                Log.d(TAG, "show" + String.valueOf(nt.getIdNotification()));
                                Log.d(TAG, "###################################");
                                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                                    Person user = new Person.Builder().setName(empleador.getNombre() + " " + empleador.getApellido()).build();
                                    Notification.MessagingStyle style = new Notification.MessagingStyle(user.getName());

                                    for (MessageCloudPoc me : nt.getMensajeNubes()) {
                                        long timeStamp = System.currentTimeMillis();
//                    Person user = new Person.Builder().setIcon(userIcon).setName("userName").build();
                                        Notification.MessagingStyle.Message message = new Notification.MessagingStyle.Message(me.getContenido(), timeStamp, user.getName());
//                            not.getFrom() + " " + not.getFrom());
                                        style.addMessage(message);
                                    }

                                    Notification.Builder notification = new Notification.Builder(contextInstance, NOTIFICATIONS_CHANNEL_ID)
                                            .setContentTitle(getText(R.string.notification_title) + "-" + String.valueOf(Build.VERSION.SDK_INT))
                                            .setContentText(getText(R.string.notification_message))
                                            .setSmallIcon(R.drawable.ic_oficios)
                                            //.setContentIntent(pendingIntent)
                                            .setStyle(style)
                                            .setTicker(getText(R.string.ticker_text));
                                    // Notification ID cannot be 0.

                                    NotificationManagerCompat notificationManager = NotificationManagerCompat.from(contextInstance);
                                    notificationManager.notify(nt.getIdNotification(), notification.build());
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

                                    for (MessageCloudPoc me : nt.getMensajeNubes()) {
                                        long timeStamp = System.currentTimeMillis();
//                    Person user = new Person.Builder().setIcon(userIcon).setName("userName").build();
                                        NotificationCompat.MessagingStyle.Message message = new NotificationCompat.MessagingStyle.Message(me.getContenido(), timeStamp, user);
//                            not.getFrom() + " " + not.getFrom());
                                        style.addMessage(message);
                                    }
                                    NotificationCompat.Builder notification = new NotificationCompat.Builder(contextInstance, NOTIFICATIONS_CHANNEL_ID)
                                            .setSmallIcon(R.drawable.ic_oficios)
                                            .setContentTitle(getText(R.string.notification_title) + "-" + String.valueOf(Build.VERSION.SDK_INT))
                                            .setContentText(getText(R.string.notification_message))
                                            // Set the intent that will fire when the user taps the notification
                                            .setStyle(style)
                                            //.setContentIntent(pendingIntent)
                                            .setPriority(NotificationCompat.PRIORITY_HIGH)
                                            .setDefaults(NotificationCompat.DEFAULT_ALL)

                                            .setSound(alarmSound)
                                            .setAutoCancel(true);
                                    // Notification ID cannot be 0.
                                    // Issue the new notification.
                                    NotificationManagerCompat notificationManager = NotificationManagerCompat.from(contextInstance);
                                    notificationManager.notify(nt.getIdNotification(), notification.build());
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });


        }


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


//        unregisterReceiver(crazyDeleteBroadcastReceiver);
//        unregisterReceiver(crazyReplyBroadcastReceiver);
//        unregisterReceiver(foregroundAcceptCallReceiver);
//        unregisterReceiver(foregroundRejectCallReceiver);
//        unregisterReceiver(foregrounAcceptVideoCallReceiver);
//        unregisterReceiver(foregroundRejectVideoCallReceiver);


    }


}