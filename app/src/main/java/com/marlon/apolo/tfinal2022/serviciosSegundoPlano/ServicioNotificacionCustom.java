package com.marlon.apolo.tfinal2022.serviciosSegundoPlano;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.RemoteInput;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.marlon.apolo.tfinal2022.R;
import com.marlon.apolo.tfinal2022.individualChat.receiver.EliminarNotificationReceiver;
import com.marlon.apolo.tfinal2022.individualChat.receiver.RespuestaDirectaReceiver;
import com.marlon.apolo.tfinal2022.individualChat.repository.MensajitoRepository;
import com.marlon.apolo.tfinal2022.individualChat.view.IndividualChatActivity;
import com.marlon.apolo.tfinal2022.individualChat.view.MensajeNube;
import com.marlon.apolo.tfinal2022.model.Administrador;
import com.marlon.apolo.tfinal2022.model.Chat;
import com.marlon.apolo.tfinal2022.model.Empleador;
import com.marlon.apolo.tfinal2022.model.NotificacionCustom;
import com.marlon.apolo.tfinal2022.model.Participante;
import com.marlon.apolo.tfinal2022.model.Trabajador;
import com.marlon.apolo.tfinal2022.model.Usuario;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

public class ServicioNotificacionCustom extends Service {
    private static final String TAG = ServicioNotificacionCustom.class.getSimpleName();

    private static final String CHANNEL_ID = "CANAL_DE_MENSAJES";
    private static final int ONGOING_NOTIFICATION_ID = 150;
    private static final String ACTION_DELETE_NOTIFICATION = "DELETE_NOTIFICATION";
    private static final String KEY_TEXT_REPLY = "KEY_TEXT_REPLY";
    private static final String ACTION_REPLY_NOTIFICATION = "REPLY_NOTIFICATION";
    private FirebaseUser firebaseUserLocal;
    private ArrayList<NotificacionCustom> notificacionCustomArrayList;
    private ArrayList<Chat> chats;
    private NotificationManager notificationManager;
    private EliminarNotificationReceiver eliminarNotificationReceiver;
    private RespuestaDirectaReceiver respuestaDirectaReceiver;
    private MensajitoRepository mensajitoRepository;
    private Usuario usuarioLocal;
    private SharedPreferences myPreferences;
    private String usurioBloquedo;

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                notificationManager = getSystemService(NotificationManager.class);
            }
        }
    }

    private void listenerNotificaciones() {
        registerReceiver(eliminarNotificationReceiver, new IntentFilter(ACTION_DELETE_NOTIFICATION));
        registerReceiver(respuestaDirectaReceiver, new IntentFilter(ACTION_REPLY_NOTIFICATION));


        firebaseUserLocal = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseDatabase.getInstance().getReference()
                .child("notificaciones")
                .child(firebaseUserLocal.getUid())
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        ArrayList<MensajeNube> mensajeNubeArrayList = new ArrayList<>();
                        usurioBloquedo = myPreferences.getString("usurioBloqueado", "");
                        Log.d(TAG, String.format("Usuario bloqueado: %s", usurioBloquedo));
                        for (DataSnapshot data : snapshot.getChildren()) {
                            try {
                                MensajeNube mensajeNube = data.getValue(MensajeNube.class);
                                mensajeNubeArrayList.add(mensajeNube);
                                Log.d(TAG, String.format("Notificacion: %s", mensajeNube));
                            } catch (Exception e) {
                                Log.e(TAG, e.toString());
                            }
                        }
                        if (!usurioBloquedo.equals(mensajeNubeArrayList.get(0).getFrom())) {
                            seleccionarUsuarioFrom(mensajeNubeArrayList);
                        }

                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        ArrayList<MensajeNube> mensajeNubeArrayList = new ArrayList<>();
                        usurioBloquedo = myPreferences.getString("usurioBloqueado", "");
                        Log.d(TAG, String.format("Usuario bloqueado: %s", usurioBloquedo));
                        for (DataSnapshot data : snapshot.getChildren()) {
                            try {
                                MensajeNube mensajeNube = data.getValue(MensajeNube.class);
                                mensajeNubeArrayList.add(mensajeNube);
                                //Log.d(TAG, String.format("Notificacion: %s", mensajeNube));
                            } catch (Exception e) {
                                Log.e(TAG, e.toString());
                            }
                        }
                        if (!usurioBloquedo.equals(mensajeNubeArrayList.get(0).getFrom())) {
                            seleccionarUsuarioFromUpdate(mensajeNubeArrayList);
                        }
                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                        ArrayList<MensajeNube> mensajeNubeArrayList = new ArrayList<>();
                        for (DataSnapshot data : snapshot.getChildren()) {
                            Log.d(TAG, "###################################");

                            MensajeNube mensajeNube = data.getValue(MensajeNube.class);
                            Log.d(TAG, mensajeNube.toString());
                            mensajeNubeArrayList.add(mensajeNube);
                        }

                        HashSet<NotificacionCustom> hs = new HashSet<NotificacionCustom>(notificacionCustomArrayList);
//                        hs.addAll(al);
                        notificacionCustomArrayList.clear();
                        notificacionCustomArrayList.addAll(hs);


                        if (notificacionCustomArrayList != null) {
                            if (notificacionCustomArrayList.size() > 0) {
                                int index = 0;
                                for (NotificacionCustom nt : notificacionCustomArrayList) {
                                    Log.d(TAG, nt.toString());
                                    Log.d(TAG, String.valueOf(index));
                                    if (nt.getIdFrom().equals(mensajeNubeArrayList.get(0).getFrom())) {
                                        try {
                                            notificationManager.cancel(nt.getIdNotification());
                                        } catch (Exception e) {

                                        }
                                        notificacionCustomArrayList.remove(nt);
                                        index++;

                                        break;
                                    }
                                }
                            }
                        }

//                        ArrayList<MensajeNube> mensajeNubeArrayList = new ArrayList<>();
//                        Log.d(TAG, "Eliminando mensajes");
//
//                        try {
//                            for (DataSnapshot data : snapshot.getChildren()) {
//                                MensajeNube mensajeNube = data.getValue(MensajeNube.class);
//                                mensajeNubeArrayList.add(mensajeNube);
//                            }
//
//                            if (notificacionCustomArrayList != null) {
//                                if (notificacionCustomArrayList.size() > 0) {
//                                    for (NotificacionCustom nt : notificacionCustomArrayList) {
//                                        if (nt.getIdFrom().equals(mensajeNubeArrayList.get(0).getFrom())) {
//                                            notificacionCustomArrayList.remove(nt);
//                                            try {
//                                                notificationManager.cancel(nt.getIdNotification());
//                                            } catch (Exception e) {
//
//                                            }
//                                            break;
//                                        }
//                                    }
//                                }
//                            }
//                        } catch (Exception e) {
//
//                        }
//

                        Log.d(TAG, "###################################");
                        Log.d(TAG, "NOTIFICACIONES");

                        try {
                            for (NotificacionCustom nt : notificacionCustomArrayList) {
                                Log.d(TAG, nt.toString());
                            }
                        } catch (Exception e) {
                            Log.d(TAG, "###################################");
                            Log.d(TAG, e.toString());
                            Log.d(TAG, "###################################");
                        }
                        Log.d(TAG, "###################################");


                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

//        usurioBloquedo = myPreferences.getString("usurioBloqueado", "");

//        if (firebaseUserLocal != null) {
//            FirebaseDatabase.getInstance().getReference()
//                    .child("notificaciones")
//                    .child(firebaseUserLocal.getUid())
//                    .addChildEventListener(new ChildEventListener() {
//                        @Override
//                        public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//                            ArrayList<MensajeNube> mensajeNubeArrayList = new ArrayList<>();
//                            try {
//                                for (DataSnapshot data : snapshot.getChildren()) {
//                                    MensajeNube mensajeNube = data.getValue(MensajeNube.class);
//                                    mensajeNubeArrayList.add(mensajeNube);
//                                }
//
//
//                                FirebaseDatabase.getInstance().getReference()
//                                        .child("administrador")
//                                        .child(mensajeNubeArrayList.get(0).getFrom())
//                                        .addListenerForSingleValueEvent(new ValueEventListener() {
//                                            @Override
//                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
//
//                                                try {
//                                                    Administrador administrador = snapshot.getValue(Administrador.class);
//                                                    if (administrador != null) {
//                                                        mostrarNotificacion(mensajeNubeArrayList, administrador);
//                                                    }
////                                                    if (usurioBloquedo.equals(mensajeNubeArrayList.get(0).getFrom())) {
////
////                                                    } else {
////                                                        if (administrador!=null){
////                                                            mostrarNotificacion(mensajeNubeArrayList, administrador);
////                                                        }
////                                                    }
//                                                } catch (Exception e) {
//
//                                                }
//                                            }
//
//                                            @Override
//                                            public void onCancelled(@NonNull DatabaseError error) {
//
//                                            }
//                                        });
//                                FirebaseDatabase.getInstance().getReference()
//                                        .child("empleadores")
//                                        .child(mensajeNubeArrayList.get(0).getFrom())
//                                        .addListenerForSingleValueEvent(new ValueEventListener() {
//                                            @Override
//                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
//
//                                                try {
//                                                    Empleador empleador = snapshot.getValue(Empleador.class);
//                                                    Log.d(TAG, String.format("Usuario bloqueado: %s", usurioBloquedo));
//
//                                                    if (empleador != null) {
//                                                        if (usurioBloquedo.equals(empleador.getIdUsuario())) {
//
//                                                        } else {
//                                                            mostrarNotificacion(mensajeNubeArrayList, empleador);
//
//                                                        }
//                                                    }
////                                                    usurioBloquedo = myPreferences.getString("usurioBloqueado", "");
////
////                                                    if (usurioBloquedo.equals(mensajeNubeArrayList.get(0).getFrom())) {
////
////                                                    } else {
////                                                        if (empleador != null) {
////                                                            mostrarNotificacion(mensajeNubeArrayList, empleador);
////                                                        }
////                                                    }
//                                                } catch (Exception e) {
//
//                                                }
//                                            }
//
//                                            @Override
//                                            public void onCancelled(@NonNull DatabaseError error) {
//
//                                            }
//                                        });
//                                FirebaseDatabase.getInstance().getReference()
//                                        .child("trabajadores")
//                                        .child(mensajeNubeArrayList.get(0).getFrom())
//                                        .addListenerForSingleValueEvent(new ValueEventListener() {
//                                            @Override
//                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
//
//                                                try {
//                                                    Trabajador trabajador = snapshot.getValue(Trabajador.class);
//                                                    usurioBloquedo = myPreferences.getString("usurioBloqueado", "");
//                                                    Log.d(TAG, String.format("Usuario bloqueado: %s", usurioBloquedo));
//
//                                                    if (trabajador != null) {
//                                                        if (usurioBloquedo.equals(trabajador.getIdUsuario())) {
//
//                                                        } else {
//                                                            mostrarNotificacion(mensajeNubeArrayList, trabajador);
//
//                                                        }
//                                                    }
////                                                    if (usurioBloquedo.equals(mensajeNubeArrayList.get(0).getFrom())) {
////
////                                                    } else {
////
////                                                        if (trabajador != null) {
////                                                            mostrarNotificacion(mensajeNubeArrayList, trabajador);
////                                                        }
////                                                    }
//                                                } catch (Exception e) {
//
//                                                }
//                                            }
//
//                                            @Override
//                                            public void onCancelled(@NonNull DatabaseError error) {
//
//                                            }
//                                        });
//                            } catch (Exception e) {
//
//                            }
//
//                        }
//
//                        @Override
//                        public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//                            ArrayList<MensajeNube> mensajeNubeArrayList = new ArrayList<>();
//                            try {
//                                for (DataSnapshot data : snapshot.getChildren()) {
//                                    MensajeNube mensajeNube = data.getValue(MensajeNube.class);
//                                    mensajeNubeArrayList.add(mensajeNube);
//                                }
//                                if (notificacionCustomArrayList != null) {
//                                    if (notificacionCustomArrayList.size() > 0) {
//
//                                        FirebaseDatabase.getInstance().getReference()
//                                                .child("administrador")
//                                                .child(mensajeNubeArrayList.get(0).getFrom())
//                                                .addListenerForSingleValueEvent(new ValueEventListener() {
//                                                    @Override
//                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//
//                                                        try {
//                                                            Administrador administrador = snapshot.getValue(Administrador.class);
//                                                            if (administrador != null) {
//                                                                actualizarNotificaction(mensajeNubeArrayList, administrador);
//                                                            }
//
//                                                            //                                                            usurioBloquedo = myPreferences.getString("usurioBloqueado", "");
////
////                                                            if (usurioBloquedo.equals(mensajeNubeArrayList.get(0).getFrom())) {
////
////                                                            } else {
////                                                                if (administrador != null) {
////                                                                    actualizarNotificaction(mensajeNubeArrayList, administrador);
////                                                                }
////                                                            }
//                                                        } catch (Exception e) {
//
//                                                        }
//                                                    }
//
//                                                    @Override
//                                                    public void onCancelled(@NonNull DatabaseError error) {
//
//                                                    }
//                                                });
//                                        FirebaseDatabase.getInstance().getReference()
//                                                .child("empleadores")
//                                                .child(mensajeNubeArrayList.get(0).getFrom())
//                                                .addListenerForSingleValueEvent(new ValueEventListener() {
//                                                    @Override
//                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//
//                                                        try {
//                                                            Empleador empleador = snapshot.getValue(Empleador.class);
//                                                            if (empleador != null) {
//                                                                Log.d(TAG, String.format("Usuario bloqueado: %s", usurioBloquedo));
//
//                                                                if (usurioBloquedo.equals(empleador.getIdUsuario())) {
//
//                                                                } else {
//                                                                    actualizarNotificaction(mensajeNubeArrayList, empleador);
//
//                                                                }
//                                                            }
////
////                                                            usurioBloquedo = myPreferences.getString("usurioBloqueado", "");
////
////                                                            if (usurioBloquedo.equals(mensajeNubeArrayList.get(0).getFrom())) {
////
////                                                            } else {
////                                                                if (empleador != null) {
////                                                                    actualizarNotificaction(mensajeNubeArrayList, empleador);
////                                                                }
////                                                            }
//
//                                                        } catch (Exception e) {
//
//                                                        }
//                                                    }
//
//                                                    @Override
//                                                    public void onCancelled(@NonNull DatabaseError error) {
//
//                                                    }
//                                                });
//                                        FirebaseDatabase.getInstance().getReference()
//                                                .child("trabajadores")
//                                                .child(mensajeNubeArrayList.get(0).getFrom())
//                                                .addListenerForSingleValueEvent(new ValueEventListener() {
//                                                    @Override
//                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//
//                                                        try {
//                                                            Trabajador trabajador = snapshot.getValue(Trabajador.class);
//                                                            usurioBloquedo = myPreferences.getString("usurioBloqueado", "");
//                                                            Log.d(TAG, String.format("Usuario bloqueado: %s", usurioBloquedo));
//
//                                                            if (trabajador != null) {
//                                                                if (usurioBloquedo.equals(trabajador.getIdUsuario())) {
//
//                                                                } else {
//                                                                    actualizarNotificaction(mensajeNubeArrayList, trabajador);
//                                                                }
//                                                            }
////                                                            usurioBloquedo = myPreferences.getString("usurioBloqueado", "");
////
////                                                            if (usurioBloquedo.equals(mensajeNubeArrayList.get(0).getFrom())) {
////
////                                                            } else {
////
////                                                                if (trabajador != null) {
////                                                                    actualizarNotificaction(mensajeNubeArrayList, trabajador);
////                                                                }
////                                                            }
//                                                        } catch (Exception e) {
//
//                                                        }
//                                                    }
//
//                                                    @Override
//                                                    public void onCancelled(@NonNull DatabaseError error) {
//
//                                                    }
//                                                });
//
//                                    }
//                                }
//                            } catch (Exception e) {
//
//                            }
//                        }
//
//                        @Override
//                        public void onChildRemoved(@NonNull DataSnapshot snapshot) {
//                            ArrayList<MensajeNube> mensajeNubeArrayList = new ArrayList<>();
//                            Log.d(TAG, "Eliminando mensajes");
//
//                            try {
//                                for (DataSnapshot data : snapshot.getChildren()) {
//                                    MensajeNube mensajeNube = data.getValue(MensajeNube.class);
//                                    mensajeNubeArrayList.add(mensajeNube);
//                                }
//
//                                if (notificacionCustomArrayList != null) {
//                                    if (notificacionCustomArrayList.size() > 0) {
//                                        for (NotificacionCustom nt : notificacionCustomArrayList) {
//                                            if (nt.getIdFrom().equals(mensajeNubeArrayList.get(0).getFrom())) {
//                                                notificacionCustomArrayList.remove(nt);
//                                                try {
//                                                    notificationManager.cancel(nt.getIdNotification());
//                                                } catch (Exception e) {
//
//                                                }
//                                                break;
//                                            }
//                                        }
//                                    }
//                                }
//                            } catch (Exception e) {
//
//                            }
//
//
//                            try {
//                                for (NotificacionCustom nt : notificacionCustomArrayList) {
//                                    Log.d(TAG, nt.toString());
//                                }
//                            } catch (Exception e) {
//                                Log.d(TAG, "###################################");
//                                Log.d(TAG, e.toString());
//                                Log.d(TAG, "###################################");
//                            }
//
//                        }
//
//                        @Override
//                        public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//
//                        }
//
//                        @Override
//                        public void onCancelled(@NonNull DatabaseError error) {
//
//                        }
//                    });
//        }

    }

    private void seleccionarUsuarioFrom(ArrayList<MensajeNube> mensajeNubeArrayList) {

        FirebaseDatabase.getInstance().getReference()
                .child("administrador")
                .child(mensajeNubeArrayList.get(0).getFrom())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        try {
                            Administrador administrador = snapshot.getValue(Administrador.class);
                            if (administrador != null) {
                                mostrarNotificacion(mensajeNubeArrayList, administrador);
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
                .child(mensajeNubeArrayList.get(0).getFrom())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        try {
                            Empleador empleador = snapshot.getValue(Empleador.class);
                            if (empleador != null) {
                                mostrarNotificacion(mensajeNubeArrayList, empleador);
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
                                mostrarNotificacion(mensajeNubeArrayList, trabajador);
                            }
                        } catch (Exception e) {

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void seleccionarUsuarioFromUpdate(ArrayList<MensajeNube> mensajeNubeArrayList) {

        FirebaseDatabase.getInstance().getReference()
                .child("administrador")
                .child(mensajeNubeArrayList.get(0).getFrom())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        try {
                            Administrador administrador = snapshot.getValue(Administrador.class);
                            if (administrador != null) {
                                actualizarNotificaction(mensajeNubeArrayList, administrador);
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
                .child(mensajeNubeArrayList.get(0).getFrom())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        try {
                            Empleador empleador = snapshot.getValue(Empleador.class);
                            if (empleador != null) {
                                actualizarNotificaction(mensajeNubeArrayList, empleador);
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
                                actualizarNotificaction(mensajeNubeArrayList, trabajador);
                            }
                        } catch (Exception e) {

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void mostrarNotificacion(ArrayList<MensajeNube> mensajeNubeArrayList, Usuario usuarioTo) {
        final int min = 3000;
        final int max = 3999;
        int idNotification = new Random().nextInt((max - min) + 1) + min;

        Chat chatFound = null;
        for (Chat chatAux : chats) {
            for (Participante p : chatAux.getParticipantes()) {
                if (p.getIdParticipante().equals(mensajeNubeArrayList.get(0).getFrom())) {
                    //Toast.makeText(getApplicationContext(),chatAux.toString(),Toast.LENGTH_SHORT).show();
                    chatFound = chatAux;
                    break;
                }
            }
        }


        Intent notifyIntent = new Intent(this, IndividualChatActivity.class);
        // Set the Activity to start in a new, empty task

        notifyIntent.putExtra("chat", chatFound);
        notifyIntent.putExtra("usuarioFrom", usuarioLocal);
        notifyIntent.putExtra("notificationIdFrom", mensajeNubeArrayList.get(0).getFrom());

        //notifyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        // Create the PendingIntent
        PendingIntent notifyPendingIntent = PendingIntent.getActivity(this, idNotification, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);


        Intent deleteIntent = new Intent(ACTION_DELETE_NOTIFICATION);
        deleteIntent.putExtra("notificationIdFrom", mensajeNubeArrayList.get(0).getFrom());
        //deleteIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent deletePendingIntent =
                PendingIntent.getBroadcast(getApplicationContext(),
                        idNotification,
                        deleteIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);

        String replyLabel = getResources().getString(R.string.reply_label);
        RemoteInput remoteInput = new RemoteInput.Builder(KEY_TEXT_REPLY)
                .setLabel(replyLabel)
                .build();


        /*
         * Si reutilizas un PendingIntent, el usuario podría responder a una conversación diferente de la que intenta responder.
         * Debes proporcionar un código de solicitud diferente para cada conversación o un intent que no muestre true cuando
         * llames a equals() en el intent de respuesta de cualquier otra conversación. Con frecuencia, el ID de la conversación
         * se transfiere como parte del paquete de servicios adicionales del intent, pero se ignora cuando llamas a equals().
         * */
        Intent updateIntent = new Intent(ACTION_REPLY_NOTIFICATION);
        updateIntent.putExtra("idNotification", idNotification);
        updateIntent.putExtra("notificationIdFrom", mensajeNubeArrayList.get(0).getFrom());
        updateIntent.putExtra("usuarioFrom", usuarioLocal);
        //updateIntent.putExtra("usuarioFromId", usuarioLocal.getIdUsuario());
        updateIntent.putExtra("chat", chatFound);

        // Build a PendingIntent for the reply action to trigger.
        PendingIntent replyPendingIntent =
                PendingIntent.getBroadcast(getApplicationContext(),
                        idNotification,
                        updateIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Action action =
                new NotificationCompat.Action.Builder(R.drawable.ic_baseline_send_24,
                        "Responder", replyPendingIntent)
                        .addRemoteInput(remoteInput)
//                                        .addExtras(bundle)
                        .build();


//        NotificationCompat.MessagingStyle.Message message2 =
//                new NotificationCompat.MessagingStyle.Message(text,
//                        tm,
//                        "Paul");


        NotificationCompat.Builder notification = null;
        NotificationCompat.MessagingStyle messagingStyle = new NotificationCompat.MessagingStyle("Responder");
        notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_oficios)
                .setAutoCancel(true)// Quita la notification cunado el usuario la presiona
                //.addAction(action)
                .setContentIntent(notifyPendingIntent)
                .setDeleteIntent(deletePendingIntent)
                .addAction(action)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_ALL);
//
//        NotificationCompat.Action action =
//                new NotificationCompat.Action.Builder(R.drawable.ic_baseline_message_24,
//                        "Responder", replyPendingIntent)
//                        .addRemoteInput(remoteInput)
//                        .build();

        NotificacionCustom notificacionCustom = new NotificacionCustom();
        notificacionCustom.setMensajeNubes(mensajeNubeArrayList);
        notificacionCustom.setIdNotification(idNotification);
        notificacionCustom.setIdFrom(mensajeNubeArrayList.get(0).getFrom());

        for (MensajeNube not : mensajeNubeArrayList) {
            Log.d(TAG, not.toString());
//            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
//                    .setSmallIcon(R.drawable.ic_oficios)
//                    .setContentTitle(not.getFrom())
//                    .setContentText(not.getContenido())
//                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
//                    // Set the intent that will fire when the user taps the notification
//                    .setContentIntent(pendingIntent)
//                    .setAutoCancel(true);

            // notificationId is a unique int for each notification that you must define
//            notificationManager.notify(notificationId, builder.build());

//            long tm = 120245648;
            long timeStamp = System.currentTimeMillis();
            NotificationCompat.MessagingStyle.Message message =
                    new NotificationCompat.MessagingStyle.Message(
                            not.getContenido(),
                            timeStamp,
                            usuarioTo.getNombre() + " " + usuarioTo.getApellido());
            messagingStyle.addMessage(message);


        }
        notification.setStyle(messagingStyle);
        //notificationArrayList.add(notification);
        //notificationsIds.add(idNotification);
        notificacionCustomArrayList.add(notificacionCustom);
        //mensajeNubes.clear();
        notificationManager.notify(idNotification, notification.build());


    }

    private void actualizarNotificaction(ArrayList<MensajeNube> mensajeNubes, Usuario usuarioFrom) {
        Log.d(TAG, "#####################################");
        Log.d(TAG, "ACTUALIZANDO NOTIFICACION");
        Log.d(TAG, "#####################################");

        Chat chatFound = null;
        for (Chat chatAux : chats) {
            for (Participante p : chatAux.getParticipantes()) {
                if (p.getIdParticipante().equals(mensajeNubes.get(0).getFrom())) {
                    //Toast.makeText(getApplicationContext(),chatAux.toString(),Toast.LENGTH_SHORT).show();
                    chatFound = chatAux;
                    break;
                }
            }
        }

        int indexUpdateNot = 0;
        int indexUpdate = 0;
        for (NotificacionCustom nt : notificacionCustomArrayList) {
            if (nt.getIdFrom().equals(mensajeNubes.get(0).getFrom())) {
//                indexUpdate = notificacionCustomArrayList.indexOf(nt);
                indexUpdateNot = nt.getIdNotification();
                indexUpdate = notificacionCustomArrayList.indexOf(nt);
                nt.setMensajeNubes(mensajeNubes);
                Log.d(TAG, nt.toString());
                Log.d(TAG, String.valueOf(indexUpdate));
                Log.d(TAG, String.valueOf(indexUpdateNot));
                break;
            }
        }

//
//        if (indexUpdateNot == 0) {
//            final int min = 3000;
//            final int max = 3999;
//            int idNotification = new Random().nextInt((max - min) + 1) + min;
//            indexUpdateNot = idNotification;
//        }


        Intent notifyIntent = new Intent(this, IndividualChatActivity.class);
        // Set the Activity to start in a new, empty task

        notifyIntent.putExtra("chat", chatFound);
        notifyIntent.putExtra("usuarioFrom", usuarioLocal);
        notifyIntent.putExtra("notificationIdFrom", mensajeNubes.get(0).getFrom());

        //notifyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        // Create the PendingIntent
        PendingIntent notifyPendingIntent = PendingIntent.getActivity(this, indexUpdateNot, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);


        Intent deleteIntent = new Intent(ACTION_DELETE_NOTIFICATION);
        deleteIntent.putExtra("notificationIdFrom", mensajeNubes.get(0).getFrom());
        //deleteIntent.putExtra("chatID", chatID);
        //deleteIntent.putExtra("from", from);
        //  deleteIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                        PendingIntent deletePendingIntent = PendingIntent.getActivity(getApplicationContext(), idLocalNotification, deleteIntent, 0);

        PendingIntent deletePendingIntent =
                PendingIntent.getBroadcast(getApplicationContext(),
                        indexUpdateNot,
                        deleteIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);


        String replyLabel = getResources().getString(R.string.reply_label);
        RemoteInput remoteInput = new RemoteInput.Builder(KEY_TEXT_REPLY)
                .setLabel(replyLabel)
                .build();
//
//        Intent updateIntent = new Intent(ACTION_REPLY_NOTIFICATION);
//        updateIntent.putExtra("idNotification", indexUpdateNot);
//        updateIntent.putExtra("notificationIdFrom", mensajeNubes.get(0).getFrom());

        Intent updateIntent = new Intent(ACTION_REPLY_NOTIFICATION);
//        updateIntent.putExtra("idNotification", indexUpdateNot);
//        updateIntent.putExtra("notificationIdFrom", mensajeNubes.get(0).getFrom());
//        updateIntent.putExtra("usuarioLocal", usuarioLocal);

        updateIntent.putExtra("idNotification", indexUpdateNot);
        updateIntent.putExtra("notificationIdFrom", mensajeNubes.get(0).getFrom());
        updateIntent.putExtra("usuarioFrom", usuarioLocal);
//        updateIntent.putExtra("usuarioLocal", usuarioLocal);
        //updateIntent.putExtra("usuarioFromId", usuarioLocal.getIdUsuario());
        updateIntent.putExtra("chat", chatFound);
        //updateIntent.putExtra("chat", chatFound);


        //updateIntent.putExtra("chatID", chatID);
        //updateIntent.putExtra("from", from);

        // Build a PendingIntent for the reply action to trigger.
        PendingIntent replyPendingIntent =
                PendingIntent.getBroadcast(getApplicationContext(),
                        indexUpdateNot,
                        updateIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Action action =
                new NotificationCompat.Action.Builder(R.drawable.ic_baseline_send_24,
                        "Responder", replyPendingIntent)
                        .addRemoteInput(remoteInput)
                        .build();


        NotificationCompat.Builder notification = null;
        NotificationCompat.MessagingStyle messagingStyle = new NotificationCompat.MessagingStyle("Responder");
        notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_oficios)
                .setAutoCancel(true)// Quita la notification cunado el usuario la presiona
                .addAction(action)
                .setContentIntent(notifyPendingIntent)
                .setDeleteIntent(deletePendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_ALL);


        NotificacionCustom notificacionCustom = new NotificacionCustom();
        notificacionCustom.setMensajeNubes(mensajeNubes);
        notificacionCustom.setIdNotification(indexUpdateNot);
        notificacionCustom.setIdFrom(mensajeNubes.get(0).getFrom());

        for (MensajeNube not : mensajeNubes) {

            long timeStamp = System.currentTimeMillis();
            NotificationCompat.MessagingStyle.Message message =
                    new NotificationCompat.MessagingStyle.Message(
                            not.getContenido(),
                            timeStamp,
                            usuarioFrom.getNombre() + " " + usuarioFrom.getApellido());
            messagingStyle.addMessage(message);


        }
        notification.setStyle(messagingStyle);

//        if (indexUpdateNot == 0) {
//            notificacionCustomArrayList.add(notificacionCustom);
//        } else {
            notificacionCustomArrayList.set(indexUpdate, notificacionCustom);
        //}
        for (NotificacionCustom nt : notificacionCustomArrayList) {
                Log.d(TAG, nt.toString());
        }

        notificationManager.notify(indexUpdateNot, notification.build());
    }

    private void listenerChats() {
        Log.d(TAG, "LISTENER CHATS");
        FirebaseDatabase.getInstance().getReference()
                .child("chats")
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        Chat chat = snapshot.getValue(Chat.class);
                        for (Participante participante : chat.getParticipantes()) {
                            if (participante.getIdParticipante().equals(firebaseUserLocal.getUid())) {
                                Log.d(TAG, chat.toString());
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

    private void notificacionEnSegundoPlano() {
//        Intent notificationIntent = new Intent(this, ExampleActivity.class);
//        PendingIntent pendingIntent =
//                PendingIntent.getActivity(this, 0, notificationIntent, 0);

        NotificationCompat.Builder notification =
                new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setContentTitle(getText(R.string.notification_title))
                        .setContentText(getText(R.string.notification_message))
                        .setSmallIcon(R.drawable.ic_oficios)
                //.setContentIntent(pendingIntent)
//                        .setTicker(getText(R.string.ticker_text))
                ;

        startForeground(ONGOING_NOTIFICATION_ID, notification.build());

    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        myPreferences = this.getSharedPreferences("MyPreferences", MODE_PRIVATE);

        notificacionCustomArrayList = new ArrayList<>();
        chats = new ArrayList<>();
        mensajitoRepository = new MensajitoRepository(getApplication());
        /* Notificaciones */
        eliminarNotificationReceiver = new EliminarNotificationReceiver();
        respuestaDirectaReceiver = new RespuestaDirectaReceiver();
        respuestaDirectaReceiver.setMensajitoRepository(mensajitoRepository);
        /* Notificaciones */
        listenerUsuarioLocal();
        createNotificationChannel();/*API > 28*/
        notificacionEnSegundoPlano();
        listenerChats();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //listenerUsuarioLocal(this);
//        Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();
//        Toast.makeText(this, usuarioLocal.toString(), Toast.LENGTH_SHORT).show();

        listenerNotificaciones();
        return super.onStartCommand(intent, flags, startId);
    }

    private void listenerUsuarioLocal() {
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
                            }

                        } catch (Exception e) {

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    public MensajitoRepository getMensajitoRepository() {
        return mensajitoRepository;
    }

    public void setMensajitoRepository(MensajitoRepository mensajitoRepository) {
        this.mensajitoRepository = mensajitoRepository;
    }
}
