package com.marlon.apolo.tfinal2022.individualChat.receiver;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.RemoteInput;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.marlon.apolo.tfinal2022.R;
import com.marlon.apolo.tfinal2022.admin.AdminViewModel;
import com.marlon.apolo.tfinal2022.individualChat.repository.MensajitoRepository;
import com.marlon.apolo.tfinal2022.individualChat.view.MensajeNube;
import com.marlon.apolo.tfinal2022.model.Administrador;
import com.marlon.apolo.tfinal2022.model.Chat;
import com.marlon.apolo.tfinal2022.model.Empleador;
import com.marlon.apolo.tfinal2022.model.Participante;
import com.marlon.apolo.tfinal2022.model.Trabajador;
import com.marlon.apolo.tfinal2022.model.Usuario;
import com.marlon.apolo.tfinal2022.ui.empleadores.EmpleadorViewModel;
import com.marlon.apolo.tfinal2022.ui.trabajadores.TrabajadorViewModel;

public class RespuestaDirectaReceiver extends BroadcastReceiver {

    private static final String KEY_TEXT_REPLY = "KEY_TEXT_REPLY";
    private static final String TAG = RespuestaDirectaReceiver.class.getSimpleName();
    private static final String CHANNEL_ID = "CANAL_DE_MENSAJES";
    private MensajitoRepository mensajitoRepository;
    private Usuario usuarioLocal;

    public RespuestaDirectaReceiver() {
        try {
            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            filterByUser(firebaseUser);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    private CharSequence getMessageText(Intent intent) {
        Bundle remoteInput = RemoteInput.getResultsFromIntent(intent);
        if (remoteInput != null) {
            return remoteInput.getCharSequence(KEY_TEXT_REPLY);
        }
        return null;
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
                            if (administrador!=null){
                                usuarioLocal = administrador;
                            }
                        }catch (Exception e){
                            Log.e(TAG,e.toString());
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
                            if (empleador!=null){
                                usuarioLocal = empleador;
                            }
                        }catch (Exception e){
                            Log.e(TAG,e.toString());
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
                            if (trabajador!=null){
                                usuarioLocal = trabajador;
                            }
                        }catch (Exception e){
                            Log.e(TAG,e.toString());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    /**
     * Receives the incoming broadcasts and responds accordingly.
     *
     * @param context Context of the app when the broadcast is received.
     * @param intent  The broadcast intent containing the action.
     */
    @Override
    public void onReceive(Context context, Intent intent) {
//        // Update the notification.
//        String mensaje = getMessageText(intent).toString();
//        String idChat = intent.getStringExtra("chatID");
//        String from = intent.getStringExtra("from");
//        Log.e(TAG, mensaje);
//        Log.e(TAG, "idNotification: " + String.valueOf(id));
//        Log.e(TAG, "idChat: " + String.valueOf(idChat));
//        Log.e(TAG, "from: " + String.valueOf(from));
//        responderNotification(mensaje, id, idChat, from);



        CharSequence respuestaDirecta = getMessageText(intent);
        //Toast.makeText(context, respuestaDirecta.toString(), Toast.LENGTH_LONG).show();

        int idNotification = intent.getIntExtra("idNotification", -1);
        String idTo = intent.getStringExtra("notificationIdFrom");
//        Usuario usuarioFrom = (Usuario) intent.getSerializableExtra("usuarioFrom");
        Usuario usuarioFrom = usuarioLocal;
        Chat chat = (Chat) intent.getSerializableExtra("chat");
        updateNotification(idNotification, respuestaDirecta, context);

        Log.d(TAG,respuestaDirecta.toString());
        Log.d(TAG,String.valueOf(idNotification));
        Log.d(TAG,idTo);
        Log.d(TAG,chat.toString());
        Log.d(TAG,usuarioFrom.toString());
        //Log.d(TAG,usuarioFromId.toString());



        try {
            MensajeNube mensajeNube = new MensajeNube();
            mensajeNube.setContenido(respuestaDirecta.toString());
            mensajeNube.setFrom(FirebaseAuth.getInstance().getCurrentUser().getUid());
            mensajeNube.setTo(idTo);
            mensajeNube.setEstadoLectura(false);
            mensajeNube.setType(0);/*0 mensaje -1 imagen- 2 audio-3 video 5MB*/
            usuarioFrom.responderNotificacion(idTo,context,chat,mensajeNube,mensajitoRepository);

//            FirebaseDatabase.getInstance().getReference()
//                    .child("administrador")
//                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
//                    .addListenerForSingleValueEvent(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(@NonNull DataSnapshot snapshot) {
//                            try {
//                                Administrador administrador = snapshot.getValue(Administrador.class);
//                                administrador.responderNotificacion(idTo,context,chat,mensajeNube,mensajitoRepository);
//
//                            }catch (Exception e){
//
//                            }
//                        }
//
//                        @Override
//                        public void onCancelled(@NonNull DatabaseError error) {
//
//                        }
//                    });
//
//            FirebaseDatabase.getInstance().getReference()
//                    .child("empleadores")
//                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
//                    .addListenerForSingleValueEvent(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(@NonNull DataSnapshot snapshot) {
//                            try {
//                                Empleador empleador = snapshot.getValue(Empleador.class);
//                                empleador.responderNotificacion(idTo,context,chat,mensajeNube,mensajitoRepository);
//
//                            }catch (Exception e){
//
//                            }
//                        }
//
//                        @Override
//                        public void onCancelled(@NonNull DatabaseError error) {
//
//                        }
//                    });
//
//            FirebaseDatabase.getInstance().getReference()
//                    .child("trabajadores")
//                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
//                    .addListenerForSingleValueEvent(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(@NonNull DataSnapshot snapshot) {
//                            try {
//                                Trabajador trabajador = snapshot.getValue(Trabajador.class);
//                                trabajador.responderNotificacion(idTo,context,chat,mensajeNube,mensajitoRepository);
//
//                            }catch (Exception e){
//
//                            }
//                        }
//
//                        @Override
//                        public void onCancelled(@NonNull DatabaseError error) {
//
//                        }
//                    });

//            usuarioFrom.enviarMensaje(chat,);
            //usuarioLocal.responderNotificacion(chat,mensajeNube,context, mensajitoRepository, idTo);
//            FirebaseDatabase.getInstance().getReference()
//                    .child("notificaciones")
//                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
//                    .child(idTo)
//                    .removeValue()
//                    .addOnCompleteListener(new OnCompleteListener<Void>() {
//                        @Override
//                        public void onComplete(@NonNull Task<Void> task) {
//                            if (task.isSuccessful()) {
//                                Toast.makeText(context, "Notificaciones eliminadas", Toast.LENGTH_LONG).show();
//                            } else {
//
//                            }
//                        }
//                    });            //Toast.makeText(getApplicationContext(), usuarioFrom.toString(), Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            //Log.d(TAG, "####################");
            //Log.d(TAG, e.toString());
            // Log.d(TAG, "####################");
        }
    }

    private void updateNotification(int idNotification, CharSequence respuesta, Context context) {
        // Build a new notification, which informs the user that the system
        // handled their interaction with the previous notification.
//        NotificationCompat.Builder repliedNotification = new NotificationCompat.Builder(context, CHANNEL_ID)
//                .setSmallIcon(R.drawable.ic_oficios)
//                .setContentText(respuesta)
//                .build();



        NotificationCompat.Builder repliedNotification = new NotificationCompat
                .Builder(context, CHANNEL_ID)
                .setContentTitle("Respuesta")
                .setContentText("Tú: " + respuesta)
                .setSmallIcon(R.drawable.ic_oficios)
                .setAutoCancel(true)// Quita la notification cunado el usuario la presiona
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_ALL);

        // Issue the new notification.
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(idNotification, repliedNotification.build());

    }

    public void setMensajitoRepository(MensajitoRepository mensajitoRepository) {
        this.mensajitoRepository = mensajitoRepository;
    }

}