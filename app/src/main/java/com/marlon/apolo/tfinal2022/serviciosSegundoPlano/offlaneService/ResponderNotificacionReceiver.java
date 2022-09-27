package com.marlon.apolo.tfinal2022.serviciosSegundoPlano.offlaneService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.RemoteInput;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.marlon.apolo.tfinal2022.individualChat.repository.MensajitoRepository;
import com.marlon.apolo.tfinal2022.individualChat.view.MensajeNube;
import com.marlon.apolo.tfinal2022.model.Administrador;
import com.marlon.apolo.tfinal2022.model.Chat;
import com.marlon.apolo.tfinal2022.model.Empleador;
import com.marlon.apolo.tfinal2022.model.Trabajador;
import com.marlon.apolo.tfinal2022.model.Usuario;

public class ResponderNotificacionReceiver extends BroadcastReceiver {

    private static final String TAG = ResponderNotificacionReceiver.class.getSimpleName();
    private String idTo;
    private Usuario usuarioLocal;
    private static final String KEY_TEXT_REPLY = "KEY_TEXT_REPLY";
    private int idNotification;
    private Chat chat;
    private NotificationManagerCompat mNotificationManager;

    private MensajitoRepository mensajitoRepository;

    public ResponderNotificacionReceiver() {
        try {
            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            filterByUser(firebaseUser);
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    public MensajitoRepository getMensajitoRepository() {
        return mensajitoRepository;
    }


    @Override
    public void onReceive(Context context, Intent intent) {
        mNotificationManager = NotificationManagerCompat.from(context);

        CharSequence contenido = getMessageText(intent);
//        Toast.makeText(context,contenido.toString(),Toast.LENGTH_LONG).show();
        try {
            idTo = intent.getStringExtra("idTo");
            idNotification = intent.getIntExtra("idNotification", -1);
            chat = (Chat) intent.getSerializableExtra("chat");

        } catch (Exception e) {

        }
        try {
            Log.d(TAG, contenido.toString());
            Log.d(TAG, idTo);
            Log.d(TAG, String.valueOf(idNotification));
            Log.d(TAG, chat.toString());
            Log.d(TAG, usuarioLocal.toString());

            cancelNotification(idNotification);

            MensajeNube mensajeNube = new MensajeNube();
            mensajeNube.setContenido(contenido.toString());
            mensajeNube.setFrom(usuarioLocal.getIdUsuario());
            mensajeNube.setTo(idTo);
            mensajeNube.setEstadoLectura(false);
            mensajeNube.setType(0);/*0 mensaje -1 imagen- 2 audio-3 video 5MB*/
            usuarioLocal.responderNotificacion(idTo, context, chat, mensajeNube,mensajitoRepository);
        } catch (Exception e) {
            Log.e(TAG, e.toString());

        }
    }

    public void cancelNotification(int idNotification) {
        mNotificationManager.cancel(idNotification);
    }

    public void setMensajitoRepository(MensajitoRepository mensajitoRepository) {
        this.mensajitoRepository = mensajitoRepository;
    }
}
