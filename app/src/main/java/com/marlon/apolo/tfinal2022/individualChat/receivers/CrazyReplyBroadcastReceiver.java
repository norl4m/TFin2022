package com.marlon.apolo.tfinal2022.individualChat.receivers;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.RemoteInput;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.marlon.apolo.tfinal2022.R;
import com.marlon.apolo.tfinal2022.individualChat.model.ChatPoc;
import com.marlon.apolo.tfinal2022.individualChat.model.MessageCloudPoc;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CrazyReplyBroadcastReceiver extends BroadcastReceiver {
    private static final String KEY_TEXT_REPLY = "KEY_TEXT_REPLY";
    private static final String TAG = CrazyReplyBroadcastReceiver.class.getSimpleName();
    private NotificationManagerCompat notificationManager;


    public CrazyReplyBroadcastReceiver() {
    }

    public void responderNotificacion(String idRemoteUser, String respuesta) {
        MessageCloudPoc messageCloudPoc = new MessageCloudPoc();
        //mensajeNube.setIdMensaje();
        //mensajeNube.setIdChat("-N5Jb_EbmyyX7RXVyhs");
        messageCloudPoc.setContenido(respuesta);
        messageCloudPoc.setFrom(FirebaseAuth.getInstance().getCurrentUser().getUid());
        messageCloudPoc.setTo(idRemoteUser);
        messageCloudPoc.setEstadoLectura(false);
        messageCloudPoc.setType(0);/*0 texto */
        Log.d(TAG, messageCloudPoc.toString());
        sendMessage(messageCloudPoc);
    }

    // [START write_fan_out]
    public void sendMessage(MessageCloudPoc messageCloudPoc) {
        Log.d(TAG, "###########################");
        Log.d(TAG, "sendMessage");
        Log.d(TAG, messageCloudPoc.toString());
        Log.d(TAG, "###########################");
        Timestamp timestamp = new Timestamp(new Date());
        messageCloudPoc.setTimeStamp(timestamp.toString());

        // Create new post at /user-posts/$userid/$postid and at
        // /posts/$postid simultaneously
        String key = FirebaseDatabase.getInstance().getReference().child("crazyMessages").push().getKey();
        messageCloudPoc.setIdMensaje(key);
        //MessageCloudPoc post = new MessageCloudPoc();
        Map<String, Object> postValues = messageCloudPoc.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/crazyMessages/" + messageCloudPoc.getFrom() + "/" + messageCloudPoc.getTo() + "/" + key, postValues);
        childUpdates.put("/crazyMessages/" + messageCloudPoc.getTo() + "/" + messageCloudPoc.getFrom() + "/" + key, postValues);
        childUpdates.put("/notificaciones/" + messageCloudPoc.getTo() + "/" + messageCloudPoc.getFrom() + "/" + key, postValues);

        FirebaseDatabase.getInstance().getReference().updateChildren(childUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "Mensaje enviado");
                } else {
                    Log.d(TAG, "Error al enviar mensaje");
                }
            }
        });


        ChatPoc chatPoc = new ChatPoc();
        chatPoc.setIdRemoteUser(messageCloudPoc.getTo());
        chatPoc.setLastMessageCloudPoc(messageCloudPoc);
        FirebaseDatabase.getInstance().getReference().child("crazyChats")
                .child(messageCloudPoc.getFrom())
                .child(chatPoc.getIdRemoteUser())
                .setValue(chatPoc);

        ChatPoc chatPocRemoto = new ChatPoc();
        chatPocRemoto.setIdRemoteUser(messageCloudPoc.getFrom());
        chatPocRemoto.setLastMessageCloudPoc(messageCloudPoc);
        FirebaseDatabase.getInstance().getReference().child("crazyChats")
                .child(messageCloudPoc.getTo())
                .child(chatPocRemoto.getIdRemoteUser())
                .setValue(chatPocRemoto);

        /*Guardando localmente en la nube*/
        //FirebaseDatabase.getInstance().getReference().child("crazyMessages").child(messageCloudPoc.getFrom()).updateChildren(childUpdates);
        /*Guardando localmente en la nube*/
        //FirebaseDatabase.getInstance().getReference().child("crazyMessages").child(messageCloudPoc.getTo()).updateChildren(childUpdates);

    }
    // [END write_fan_out]

    public void deleteNotifications(String idRemoteUser) {
        FirebaseDatabase.getInstance().getReference()
                .child("notificaciones")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(idRemoteUser)
                .setValue(null).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull com.google.android.gms.tasks.Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Notificaciones eliminadas");

                        } else {
                            Log.d(TAG, "Error al eliminar notificaciones");

                        }
                    }
                });
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        //Toast.makeText(context, "contenido", Toast.LENGTH_LONG).show();
        String contenido = "";
        String respuesta = "";
        int idNotification = intent.getIntExtra("idNotification", -1);
        String idRemoteUser = intent.getStringExtra("idRemoteUser");
        notificationManager = NotificationManagerCompat.from(context);

        //Toast.makeText(context, contenido, Toast.LENGTH_LONG).show();
        Bundle remoteInput = RemoteInput.getResultsFromIntent(intent);
        if (remoteInput != null) {
            contenido = "Tù: " + contenido;
            contenido += remoteInput.getCharSequence(KEY_TEXT_REPLY).toString();
            respuesta = remoteInput.getCharSequence(KEY_TEXT_REPLY).toString();
        }
        responderNotificacion(idRemoteUser, respuesta);

        Log.d(TAG, contenido.toString());
        Log.d(TAG, String.valueOf(idNotification));


//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
//            StatusBarNotification[] activeNotifications = NotificationManager.getActiveNotifications();
//        }


        // Issue the new notification.
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.cancel(idNotification);

        NotificationManager notificationManager1 = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager1.cancel(idNotification);
        Log.d(TAG, "CANCEL NOTIFICATION AND UPDATE WITH ID: " + String.valueOf(idNotification));
//        notificationManager.cancelAll();
//        notificationManager1.cancelAll();

        // handled their interaction with the previous notification.
        Notification repliedNotification = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            repliedNotification = new Notification.Builder(context, "NOTIFICATIONS_CHANNEL_ID")
                    .setSmallIcon(R.drawable.ic_oficios)
                    .setContentText(contenido)
                    .build();

            notificationManager.notify(idNotification, repliedNotification);
//            notificationManager.cancel("TAG", idNotification);
//            notificationManager1.cancel(idNotification);/*COMPORTAMIENTO EXTRAÑO*/


        } else {
            NotificationCompat.Builder notification = new NotificationCompat.Builder(context, "NOTIFICATIONS_CHANNEL_ID")
                    .setSmallIcon(R.drawable.ic_oficios)
                    .setContentText(contenido)

//                                        .setContentTitle(getText(R.string.notification_title) + "-" + String.valueOf(Build.VERSION.SDK_INT))
//                                        .setContentText(getText(R.string.notification_message))
                    // Set the intent that will fire when the user taps the notification
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setDefaults(NotificationCompat.DEFAULT_ALL)

                    .setAutoCancel(true);
            notificationManager.notify(idNotification, notification.build());

        }

        deleteNotifications(idRemoteUser);


        final PendingResult pendingResult = goAsync();
        AsyncTaskDeleteReplyNotification asyncAsyncTaskDeleteReplyNotification = new AsyncTaskDeleteReplyNotification(pendingResult, intent);
        asyncAsyncTaskDeleteReplyNotification.setIdNotification(idNotification);
        asyncAsyncTaskDeleteReplyNotification.setNotificationManagerCompat(notificationManager);
        asyncAsyncTaskDeleteReplyNotification.execute();

    }

    public static class AsyncTaskDeleteReplyNotification extends AsyncTask<String, Integer, String> {

        private final PendingResult pendingResult;
        private final Intent intent;
        private int idNotification;
        private NotificationManagerCompat notificationManagerCompat;

        public AsyncTaskDeleteReplyNotification(PendingResult pendingResult, Intent intent) {
            this.pendingResult = pendingResult;
            this.intent = intent;
        }

        public void setIdNotification(int idNotification) {
            this.idNotification = idNotification;
        }

        public void cancelNotification(int idNotification) {

            Log.d(TAG, "cancelNotification: :)" + idNotification);
            // notificationId is a unique int for each notification that you must define
            notificationManagerCompat.cancel(idNotification);


        }

        public void setNotificationManagerCompat(NotificationManagerCompat notificationManagerCompat) {
            this.notificationManagerCompat = notificationManagerCompat;
        }

        @Override
        protected String doInBackground(String... strings) {
            StringBuilder sb = new StringBuilder();
            sb.append("Action: " + intent.getAction() + "\n");
            sb.append("URI: " + intent.toUri(Intent.URI_INTENT_SCHEME).toString() + "\n");
            String log = sb.toString();
            Log.d(TAG, log);
            try {
                Thread.sleep(2000);
                cancelNotification(idNotification);

            } catch (InterruptedException e) {
                // We were cancelled; stop sleeping!
            }
//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    cancelNotification(idNotification);
//                }
//            }, 1500);
            return log;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.d(TAG, "onPostExecute");
            // Must call finish() so the BroadcastReceiver can be recycled.
            pendingResult.finish();
        }


    }


}
