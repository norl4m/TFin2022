package com.marlon.apolo.tfinal2022.serviciosSegundoPlano.offlaneService;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.MediaPlayer;
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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.marlon.apolo.tfinal2022.MainNavigationActivity;
import com.marlon.apolo.tfinal2022.R;
import com.marlon.apolo.tfinal2022.individualChat.repository.MensajitoRepository;
import com.marlon.apolo.tfinal2022.individualChat.view.IndividualChatActivity;
import com.marlon.apolo.tfinal2022.individualChat.view.MensajeNube;
import com.marlon.apolo.tfinal2022.llamadaVoz.LlamadaVozActivity;
import com.marlon.apolo.tfinal2022.model.Administrador;
import com.marlon.apolo.tfinal2022.model.Chat;
import com.marlon.apolo.tfinal2022.model.Empleador;
import com.marlon.apolo.tfinal2022.model.LlamadaVoz;
import com.marlon.apolo.tfinal2022.model.NotificacionCustom;
import com.marlon.apolo.tfinal2022.model.Participante;
import com.marlon.apolo.tfinal2022.model.Trabajador;
import com.marlon.apolo.tfinal2022.model.Usuario;

import java.util.ArrayList;
import java.util.Random;

public class OffLaneService extends Service {
    private static final String TAG = OffLaneService.class.getSimpleName();
    private static final int ONGOING_NOTIFICATION_ID = 150;
    private static final String CHANNEL_ID = "CANAL_DE_MENSAJES";
    private static final String ACTION_DELETE_NOTIFICATION = "DELETE_NOTIFICATION";
    private static final String ACTION_REPLY_NOTIFICATION = "REPLY_NOTIFICATION";
    // Key for the string that's delivered in the action's intent.
    private static final String KEY_TEXT_REPLY = "KEY_TEXT_REPLY";
    private static final String ACTION_ACCEPT_CALL = "ACCEPT_CALL";
    private static final String ACTION_REJECT_CALL = "REJECT_CALL";
    private NotificationManagerCompat mNotificationManager;

    int mStartMode = START_STICKY;       // indicates how to behave if the service is killed
    // If we get killed, after returning from here, restart

    IBinder mBinder;      // interface for clients that bind
    boolean mAllowRebind; // indicates whether onRebind should be used
    private Usuario usuarioLocal;
    private EliminarNotificacionReceiver eliminarNotificacionReceiver;
    private ResponderNotificacionReceiver responderNotificacionReceiver;
    private ArrayList<Chat> chats;
    private ArrayList<LlamadaVoz> llamadaVozArrayList;
    private MensajitoRepository mensajitoRepository;
    private ArrayList<NotificacionCustom> notificacionCustomArrayList;
    private ArrayList<NotificacionCustom> notificacionesLlamadasDeVoz;
    private ArrayList<NotificacionCustom> notificacionesLlamadasDeVideo;
    private SharedPreferences myPreferences;
    private SharedPreferences.Editor editorPref;
    private String idUserBlocking;
    MediaPlayer mediaPlayerTonoLlamadaIn;

    private RejectCallReceiver rejectCallReceiver;
    private AcceptCallReceiver acceptCallReceiver;

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            notificationChannel.setDescription(description);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            mNotificationManager.createNotificationChannel(notificationChannel);
        }
    }

    private void notificationInBackground() {
        Intent notificationIntent = new Intent(this, MainNavigationActivity.class);
        PendingIntent pendingIntent =
                PendingIntent.getActivity(this, ONGOING_NOTIFICATION_ID, notificationIntent, 0);

        NotificationCompat.Builder notification =
                new NotificationCompat.Builder(this, CHANNEL_ID)
//                        .setContentTitle(getText(R.string.notification_title))
                        .setContentText(getText(R.string.notification_message))
                        .setSmallIcon(R.drawable.ic_oficios)
                        .setContentIntent(pendingIntent)
//                        .setTicker(getText(R.string.ticker_text))
                ;

        startForeground(ONGOING_NOTIFICATION_ID, notification.build());

    }

    private void listenerUsuarioLocal() {
        Log.d(TAG, "listenerUsuarioLocal");

        FirebaseDatabase.getInstance().getReference()
                .child("administrador")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        try {
                            Administrador administrador = snapshot.getValue(Administrador.class);
                            if (administrador != null) {
                                usuarioLocal = administrador;
                            }
                        } catch (Exception e) {

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

                        try {
                            Empleador empleador = snapshot.getValue(Empleador.class);
                            if (empleador != null) {
                                usuarioLocal = empleador;
                                Log.d(TAG, usuarioLocal.toString());
                            }
                        } catch (Exception e) {

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

                        try {
                            Trabajador trabajador = snapshot.getValue(Trabajador.class);
                            if (trabajador != null) {
                                usuarioLocal = trabajador;
                                Log.d(TAG, usuarioLocal.toString());

                            }

                        } catch (Exception e) {

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }


    @Override
    public void onCreate() {
        // The service is being created
        super.onCreate();
        Log.d(TAG, "onCreate");
        myPreferences = this.getSharedPreferences("MyPreferences", MODE_PRIVATE);

        mensajitoRepository = new MensajitoRepository(getApplication());

        mNotificationManager = NotificationManagerCompat.from(getApplicationContext());
        eliminarNotificacionReceiver = new EliminarNotificacionReceiver();
        responderNotificacionReceiver = new ResponderNotificacionReceiver();
        rejectCallReceiver = new RejectCallReceiver();
        acceptCallReceiver = new AcceptCallReceiver();
        responderNotificacionReceiver.setMensajitoRepository(mensajitoRepository);
        listenerUsuarioLocal();
        createNotificationChannel();
        notificationInBackground();
        listenerChats();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // The service is starting, due to a call to startService()
        Toast.makeText(this, "OffLane service starting", Toast.LENGTH_SHORT).show();
        registerReceiver(eliminarNotificacionReceiver, new IntentFilter(ACTION_DELETE_NOTIFICATION));
        registerReceiver(responderNotificacionReceiver, new IntentFilter(ACTION_REPLY_NOTIFICATION));
        registerReceiver(acceptCallReceiver, new IntentFilter(ACTION_ACCEPT_CALL));
        registerReceiver(rejectCallReceiver, new IntentFilter(ACTION_REJECT_CALL));
        notificacionCustomArrayList = new ArrayList<>();

        listenerLlamadasDeVoz();
        listenerNotificaciones();
        return mStartMode;
    }

    private void listenerNotificaciones() {
        FirebaseDatabase.getInstance().getReference()
                .child("notificaciones")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        ArrayList<MensajeNube> mensajeNubeArrayList = new ArrayList<>();
                        for (DataSnapshot data : snapshot.getChildren()) {
                            MensajeNube mensajeNube = data.getValue(MensajeNube.class);
                            mensajeNubeArrayList.add(mensajeNube);
                        }
//                        mostrarNotificacion(mensajeNubeArrayList);
                        idUserBlocking = myPreferences.getString("idUserBlocking", "");
                        if (!idUserBlocking.equals(mensajeNubeArrayList.get(0).getFrom())) {
                            filtrarUsuario(mensajeNubeArrayList, 0);
                        }
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        ArrayList<MensajeNube> mensajeNubeArrayList = new ArrayList<>();
                        for (DataSnapshot data : snapshot.getChildren()) {
                            MensajeNube mensajeNube = data.getValue(MensajeNube.class);
                            mensajeNubeArrayList.add(mensajeNube);
                        }
                        idUserBlocking = myPreferences.getString("idUserBlocking", "");
                        if (!idUserBlocking.equals(mensajeNubeArrayList.get(0).getFrom())) {
                            filtrarUsuario(mensajeNubeArrayList, 1);
                        }
//                        updateNotificacion(mensajeNubeArrayList);

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                        ArrayList<MensajeNube> mensajeNubeArrayList = new ArrayList<>();
                        for (DataSnapshot data : snapshot.getChildren()) {
                            MensajeNube mensajeNube = data.getValue(MensajeNube.class);
                            mensajeNubeArrayList.add(mensajeNube);
                        }


                        try {
                            int index = 0;
                            for (NotificacionCustom nt : notificacionCustomArrayList) {
                                if (nt.getIdFrom().equals(mensajeNubeArrayList.get(0).getFrom())) {
                                    cancelNotification(nt.getIdNotification());
                                    notificacionCustomArrayList.remove(index);
                                    break;
                                }
                                index++;
                            }
                        } catch (Exception e) {
                            Log.e(TAG, e.toString());
                        }
                        Log.d(TAG, "..................................");
                        Log.d(TAG, "..................................");
                        Log.d(TAG, "NOTIFICACIONES LOCALES");
                        try {
                            for (NotificacionCustom nt : notificacionCustomArrayList) {
                                Log.d(TAG, nt.toString());
                            }
                        } catch (Exception e) {
                            Log.e(TAG, e.toString());
                        }
                        Log.d(TAG, "..................................");
                        Log.d(TAG, "..................................");

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void filtrarUsuario(ArrayList<MensajeNube> mensajeNubeArrayList, int createUdate) {
        FirebaseDatabase.getInstance().getReference()
                .child("empleadores")
                .child(mensajeNubeArrayList.get(0).getFrom())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        try {
                            Empleador empleador = snapshot.getValue(Empleador.class);
                            if (empleador != null) {
                                switch (createUdate) {
                                    case 0:
                                        mostrarNotificacion(mensajeNubeArrayList, empleador);
                                        break;
                                    case 1:
                                        updateNotificacion(mensajeNubeArrayList, empleador);
                                        break;
                                }
                            }
                        } catch (Exception e) {

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        FirebaseDatabase.getInstance().getReference()
                .child("trabajadores")
                .child(mensajeNubeArrayList.get(0).getFrom())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        try {
                            Trabajador trabajador = snapshot.getValue(Trabajador.class);
                            if (trabajador != null) {
                                switch (createUdate) {
                                    case 0:
                                        mostrarNotificacion(mensajeNubeArrayList, trabajador);
                                        break;
                                    case 1:
                                        updateNotificacion(mensajeNubeArrayList, trabajador);
                                        break;
                                }
                            }
                        } catch (Exception e) {

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void updateNotificacion(ArrayList<MensajeNube> mensajeNubeArrayList, Usuario usuarioFrom) {
        NotificationCompat.Builder notification = null;


        Intent intentChat = new Intent(this, IndividualChatActivity.class);
        // Set the Activity to start in a new, empty task

        Chat chatFound = new Chat();
        boolean exitFlag = false;
        for (Chat chat : chats) {
            for (Participante p : chat.getParticipantes()) {
                if (p.getIdParticipante().equals(mensajeNubeArrayList.get(0).getFrom())) {
                    chatFound = chat;
                    exitFlag = true;
                    break;
                }
            }
            if (exitFlag) {
                break;
            }
        }

        int index = 0;
        int idNotificationUpdate = 0;
        for (NotificacionCustom nt : notificacionCustomArrayList) {
            if (nt.getIdFrom().equals(usuarioFrom.getIdUsuario())) {
                idNotificationUpdate = nt.getIdNotification();
                nt.setMensajeNubes(mensajeNubeArrayList);
                break;
            }
            index++;
        }

        intentChat.putExtra("chat", chatFound);
        //intentChat.putExtra("usuarioFrom", usuarioLocal);
        intentChat.putExtra("notificationIdFrom", mensajeNubeArrayList.get(0).getFrom());
        //intentChat.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intentChat.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

        //intentChat.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        // Create the PendingIntent
        PendingIntent pendingIntentChat = PendingIntent.getActivity(this, idNotificationUpdate, intentChat, PendingIntent.FLAG_UPDATE_CURRENT);


        Intent deleteIntent = new Intent(ACTION_DELETE_NOTIFICATION);
        deleteIntent.putExtra("idFrom", mensajeNubeArrayList.get(0).getFrom());
        PendingIntent deletePendingIntent = PendingIntent.getBroadcast(getApplicationContext(), idNotificationUpdate, deleteIntent, 0);


        String replyLabel = getResources().getString(R.string.reply_label);
        RemoteInput remoteInput = new RemoteInput.Builder(KEY_TEXT_REPLY)
                .setLabel(replyLabel)
                .build();

        Intent updateIntent = new Intent(ACTION_REPLY_NOTIFICATION);

        updateIntent.putExtra("idNotification", idNotificationUpdate);
        updateIntent.putExtra("idTo", mensajeNubeArrayList.get(0).getFrom());
        updateIntent.putExtra("chat", chatFound);
//        updateIntent.putExtra("idNotification", idNotification);
//        updateIntent.putExtra("notificationIdFrom", mensajeNubeArrayList.get(0).getFrom());
//        updateIntent.putExtra("usuarioFrom", usuarioLocal);
//        //updateIntent.putExtra("usuarioFromId", usuarioLocal.getIdUsuario());
//        updateIntent.putExtra("chat", chatFound);

        // Build a PendingIntent for the reply action to trigger.
        PendingIntent replyPendingIntent =
                PendingIntent.getBroadcast(getApplicationContext(),
                        idNotificationUpdate,
                        updateIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);


        // Create the reply action and add the remote input.
        NotificationCompat.Action action =
                new NotificationCompat.Action.Builder(R.drawable.ic_baseline_send_24,
                        getString(R.string.reply_label), replyPendingIntent)
                        .addRemoteInput(remoteInput)
                        .build();


        notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_oficios)
                .setContentTitle(String.format("%s %s", usuarioFrom.getNombre(), usuarioFrom.getApellido()))
//                .setContentText(mensajeNubeArrayList.toString())
                .setAutoCancel(true)// Quita la notification cunado el usuario la presiona
                .setContentIntent(pendingIntentChat)
                .setDeleteIntent(deletePendingIntent)
                .addAction(action)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_ALL);

        Person user = new Person.Builder().setName(String.format("%s %s", usuarioFrom.getNombre(), usuarioFrom.getApellido())).build();
        NotificationCompat.MessagingStyle style = new NotificationCompat.MessagingStyle(user).setGroupConversation(false);

        for (MensajeNube not : mensajeNubeArrayList) {
            long timeStamp = System.currentTimeMillis();
            NotificationCompat.MessagingStyle.Message message = new NotificationCompat.MessagingStyle.Message(not.getContenido(), timeStamp, user);
            style.addMessage(message);
        }

        notification.setStyle(style);


        //Delivers the notification
        mNotificationManager.notify(idNotificationUpdate, notification.build());
    }

    private void mostrarNotificacion(ArrayList<MensajeNube> mensajeNubeArrayList, Usuario usuarioFrom) {
        final int min = 3000;
        final int max = 3999;
        int idNotification = new Random().nextInt((max - min) + 1) + min;

        //idNotificationGlobalAux = idNotification;

        Intent intentChat = new Intent(this, IndividualChatActivity.class);
        // Set the Activity to start in a new, empty task

        Chat chatFound = new Chat();
        boolean exitFlag = false;
        for (Chat chat : chats) {
            for (Participante p : chat.getParticipantes()) {
                if (p.getIdParticipante().equals(mensajeNubeArrayList.get(0).getFrom())) {
                    chatFound = chat;
                    exitFlag = true;
                    break;
                }
            }
            if (exitFlag) {
                break;
            }
        }

        intentChat.putExtra("chat", chatFound);
        //intentChat.putExtra("usuarioFrom", usuarioLocal);
        intentChat.putExtra("notificationIdFrom", mensajeNubeArrayList.get(0).getFrom());

//        intentChat.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        // intentChat.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intentChat.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

        //intentChat.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        // Create the PendingIntent
        PendingIntent pendingIntentChat = PendingIntent.getActivity(this, idNotification, intentChat, PendingIntent.FLAG_UPDATE_CURRENT);


        Intent deleteIntent = new Intent(ACTION_DELETE_NOTIFICATION);
        deleteIntent.putExtra("idFrom", mensajeNubeArrayList.get(0).getFrom());
        PendingIntent deletePendingIntent = PendingIntent.getBroadcast(getApplicationContext(), idNotification, deleteIntent, 0);


        String replyLabel = getResources().getString(R.string.reply_label);
        RemoteInput remoteInput = new RemoteInput.Builder(KEY_TEXT_REPLY)
                .setLabel(replyLabel)
                .build();

        Intent updateIntent = new Intent(ACTION_REPLY_NOTIFICATION);
        updateIntent.putExtra("idNotification", idNotification);
        updateIntent.putExtra("idTo", mensajeNubeArrayList.get(0).getFrom());
        updateIntent.putExtra("chat", chatFound);

        // Build a PendingIntent for the reply action to trigger.
        PendingIntent replyPendingIntent =
                PendingIntent.getBroadcast(getApplicationContext(),
                        idNotification,
                        updateIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);


        // Create the reply action and add the remote input.
        NotificationCompat.Action action =
                new NotificationCompat.Action.Builder(R.drawable.ic_baseline_send_24,
                        getString(R.string.reply_label), replyPendingIntent)
                        .addRemoteInput(remoteInput)
                        .build();

        NotificationCompat.Builder notification = null;
        notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_oficios)
                .setContentTitle(String.format("%s %s", usuarioFrom.getNombre(), usuarioFrom.getApellido()))
//                .setContentText(mensajeNubeArrayList.toString())
                .setAutoCancel(true)// Quita la notification cunado el usuario la presiona
                .addAction(action)
                .setContentIntent(pendingIntentChat)
                .setDeleteIntent(deletePendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_ALL);

        Person user = new Person.Builder().setName(String.format("%s %s", usuarioFrom.getNombre(), usuarioFrom.getApellido())).build();
        NotificationCompat.MessagingStyle style = new NotificationCompat.MessagingStyle(user).setGroupConversation(false);

        for (MensajeNube not : mensajeNubeArrayList) {
            long timeStamp = System.currentTimeMillis();
            NotificationCompat.MessagingStyle.Message message = new NotificationCompat.MessagingStyle.Message(not.getContenido(), timeStamp, user);
            style.addMessage(message);
        }

        notification.setStyle(style);

        NotificacionCustom notificacionCustom = new NotificacionCustom();
        notificacionCustom.setIdNotification(idNotification);
        notificacionCustom.setMensajeNubes(mensajeNubeArrayList);
        notificacionCustom.setIdFrom(mensajeNubeArrayList.get(0).getFrom());

        notificacionCustomArrayList.add(notificacionCustom);

        //Delivers the notification
        mNotificationManager.notify(idNotification, notification.build());
    }

    public void cancelNotification(int idNotification) {
        mNotificationManager.cancel(idNotification);
    }

    private void listenerChats() {
        Log.d(TAG, "..................................");
        Log.d(TAG, "LISTENER CHATS");
        Log.d(TAG, "..................................");
        chats = new ArrayList<>();
        FirebaseDatabase.getInstance().getReference()
                .child("chats")
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        Chat chat = snapshot.getValue(Chat.class);
                        for (Participante participante : chat.getParticipantes()) {
                            if (participante.getIdParticipante().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                                Log.d(TAG, "..................................");
                                Log.d(TAG, chat.toString());
                                Log.d(TAG, "..................................");
                                chats.add(chat);
                                break;
                            }
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

    private void listenerLlamadasDeVoz() {
        notificacionesLlamadasDeVoz = new ArrayList<>();
        mediaPlayerTonoLlamadaIn = new MediaPlayer();

        Log.d(TAG, "..................................");
        Log.d(TAG, "LISTENER LLAMADAS DE VOZ");
        Log.d(TAG, "..................................");
        FirebaseDatabase.getInstance().getReference()
                .child("llamadasDeVoz")
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        try {
                            LlamadaVoz llamadaVoz = snapshot.getValue(LlamadaVoz.class);
                            if (llamadaVoz.getParticipanteDestiny().getIdParticipante()
                                    .equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                                Log.d(TAG, llamadaVoz.toString());
                                //llamadaVozArrayList.add(llamadaVoz);
                                mostrarNotificacionDeLlamaDeVoz(llamadaVoz);
                            }
                        } catch (Exception e) {
                            Log.d(TAG, e.toString());
                        }
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        LlamadaVoz llamadaVoz = snapshot.getValue(LlamadaVoz.class);

                        if (notificacionesLlamadasDeVoz != null) {
                            if (notificacionesLlamadasDeVoz.size() > 0) {
                                for (NotificacionCustom nt : notificacionesLlamadasDeVoz) {
                                    if (nt.equals(llamadaVoz.getId())) {
//                                        nt.
                                    }
                                }
                            }
                        }
                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                        LlamadaVoz llamadaVoz = snapshot.getValue(LlamadaVoz.class);

                        if (notificacionesLlamadasDeVoz != null) {
                            if (notificacionesLlamadasDeVoz.size() > 0) {
                                int index = 0;
                                try {
                                    for (NotificacionCustom nt : notificacionesLlamadasDeVoz) {
                                        if (nt.getIdFrom().equals(llamadaVoz.getId())) {
                                            cancelNotification(nt.getIdNotification());
                                            stopMediaPlayer();
                                            notificacionesLlamadasDeVoz.remove(index);
                                            break;
                                        }
                                        index++;
                                    }
                                } catch (Exception e) {

                                }

                            }
                        }
                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    public void stopMediaPlayer() {
        try {
            if (mediaPlayerTonoLlamadaIn != null) {
                mediaPlayerTonoLlamadaIn.stop();
                mediaPlayerTonoLlamadaIn.release();
            }
        } catch (Exception e) {

        }

    }

    private void mostrarNotificacionDeLlamaDeVoz(LlamadaVoz llamadaVoz) {
        mediaPlayerTonoLlamadaIn = MediaPlayer.create(this, R.raw.beat_it_gameboy);
        mediaPlayerTonoLlamadaIn.setLooping(true);
        //mediaPlayerTonoLlamadaIn.start();

        final int min = 4000;
        final int max = 4999;
        int idNotification = new Random().nextInt((max - min) + 1) + min;

        //idNotificationGlobalAux = idNotification;

        //Intent intentLlamadaDeVoz = new Intent(this, LlamadaVozActivity.class);
        // Set the Activity to start in a new, empty task

//        Chat chatFound = new Chat();
//        boolean exitFlag = false;
//        for (Chat chat : chats) {
//            for (Participante p : chat.getParticipantes()) {
//                if (p.getIdParticipante().equals(mensajeNubeArrayList.get(0).getFrom())) {
//                    chatFound = chat;
//                    exitFlag = true;
//                    break;
//                }
//            }
//            if (exitFlag) {
//                break;
//            }
//        }

        //intentLlamadaDeVoz.putExtra("channelNameShare", llamadaVoz.getId());
        //intentLlamadaDeVoz.putExtra("callStatus", 1);/*llamada entrante*/
        //intentLlamadaDeVoz.putExtra("llamadaVoz", llamadaVoz);/*llamada entrante*/

//        intentChat.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        // intentChat.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        //intentLlamadaDeVoz.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

        //intentChat.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        // Create the PendingIntent
        //PendingIntent pendingIntentChat = PendingIntent.getActivity(this, idNotification, intentLlamadaDeVoz, PendingIntent.FLAG_UPDATE_CURRENT);


        Intent fullScreenIntent = new Intent(this, LlamadaVozActivity.class);
        fullScreenIntent.putExtra("channelNameShare", llamadaVoz.getId());
        fullScreenIntent.putExtra("callStatus", 1);/*llamada entrante*/
        fullScreenIntent.putExtra("llamadaVoz", llamadaVoz);/*llamada entrante*/
        fullScreenIntent.putExtra("usuarioFrom", usuarioLocal);/*llamada entrante*/

        PendingIntent fullScreenPendingIntent = PendingIntent.getActivity(this, idNotification, fullScreenIntent, PendingIntent.FLAG_UPDATE_CURRENT);


        Intent acceptCallIntent = new Intent(ACTION_ACCEPT_CALL);
        acceptCallIntent.putExtra("channelNameShare", llamadaVoz.getId());
        //fullScreenIntent.putExtra("callStatus", 1);/*llamada entrante*/
        acceptCallIntent.putExtra("llamadaVoz", llamadaVoz);/*llamada entrante*/
        acceptCallIntent.putExtra("usuarioFrom", usuarioLocal);/*llamada entrante*/
        //acceptCallIntent.putExtra("contestar", true);/*llamada entrante*/
        acceptCallIntent.putExtra("idNotification", idNotification);
//        updateIntent.putExtra("idTo", mensajeNubeArrayList.get(0).getFrom());
//        updateIntent.putExtra("chat", chatFound);

        // Build a PendingIntent for the reply action to trigger.
        PendingIntent acceptCallPendingIntent = PendingIntent.getBroadcast(getApplicationContext(),
                idNotification, acceptCallIntent, PendingIntent.FLAG_UPDATE_CURRENT);


        // Create the reply action and add the remote input.
        NotificationCompat.Action actionAccept =
                new NotificationCompat.Action.Builder(R.drawable.ic_baseline_send_24,
                        "Aceptar", acceptCallPendingIntent)
                        .build();


        Intent rejectCallIntent = new Intent(ACTION_REJECT_CALL);
        rejectCallIntent.putExtra("llamadaVoz", llamadaVoz);/*llamada entrante*/
        rejectCallIntent.putExtra("usuarioFrom", usuarioLocal);/*llamada entrante*/
        rejectCallIntent.putExtra("idNotification", idNotification);/*llamada entrante*/
//        updateIntent.putExtra("idNotification", idNotification);
//        updateIntent.putExtra("idTo", mensajeNubeArrayList.get(0).getFrom());
//        updateIntent.putExtra("chat", chatFound);

        // Build a PendingIntent for the reply action to trigger.
        PendingIntent rejectCallPendingIntent = PendingIntent.getBroadcast(getApplicationContext(),
                idNotification, rejectCallIntent, PendingIntent.FLAG_UPDATE_CURRENT);


        NotificationCompat.Action actionReject =
                new NotificationCompat.Action.Builder(R.drawable.ic_baseline_send_24,
                        "Rechazar", rejectCallPendingIntent)
                        .build();

        NotificationCompat.Builder notification = null;
        notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_oficios)
                .setContentTitle(String.format("%s", llamadaVoz.getParticipanteDestiny().getNombreParticipante()))
                .setContentText("Llamada entrante...")
                .setAutoCancel(true)// Quita la notification cunado el usuario la presiona
                .addAction(actionAccept)
                .addAction(actionReject)
                //.setContentIntent(pendingIntentChat)
                .setDeleteIntent(rejectCallPendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setColor(Color.RED)
                .setLights(Color.MAGENTA, 1000, 1000)
                .setCategory(Notification.CATEGORY_SERVICE)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
//                .setDefaults(NotificationCompat.DEFAULT_SOUND)
                .setFullScreenIntent(fullScreenPendingIntent, true)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
//
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
//                .setSmallIcon(R.drawable.ic_oficios)
//                .setContentText("Llamada entrante...")
//                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
//                .setFullScreenIntent(fullScreenPendingIntent, true);


//        Person user = new Person.Builder().setName(String.format("%s %s", usuarioFrom.getNombre(), usuarioFrom.getApellido())).build();
//        NotificationCompat.MessagingStyle style = new NotificationCompat.MessagingStyle(user).setGroupConversation(false);
//
//        for (MensajeNube not : mensajeNubeArrayList) {
//            long timeStamp = System.currentTimeMillis();
//            NotificationCompat.MessagingStyle.Message message = new NotificationCompat.MessagingStyle.Message(not.getContenido(), timeStamp, user);
//            style.addMessage(message);
//        }
//
//        notification.setStyle(style);

//        NotificacionCustom notificacionCustom = new NotificacionCustom();
//        notificacionCustom.setIdNotification(idNotification);
//        notificacionCustom.setMensajeNubes(mensajeNubeArrayList);
//        notificacionCustom.setIdFrom(mensajeNubeArrayList.get(0).getFrom());
//
//        notificacionCustomArrayList.add(notificacionCustom);

        //Delivers the notification
        NotificacionCustom notificacionCustom = new NotificacionCustom();
        notificacionCustom.setIdNotification(idNotification);
        notificacionCustom.setIdFrom(llamadaVoz.getId());
        notificacionesLlamadasDeVoz.add(notificacionCustom);
        mNotificationManager.notify(idNotification, notification.build());
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        // All clients have unbound with unbindService()
        return mAllowRebind;
    }

    @Override
    public void onRebind(Intent intent) {
        // A client is binding to the service with bindService(),
        // after onUnbind() has already been called
    }

    @Override
    public void onDestroy() {
        // The service is no longer used and is being destroyed
        //Toast.makeText(this, "OffLane service stopped", Toast.LENGTH_SHORT).show();

    }
}