package com.marlon.apolo.tfinal2022.individualChat.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class EliminarNotificationReceiver extends BroadcastReceiver {

    private String notificationIdFrom;

    public EliminarNotificationReceiver() {
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
//        int id = intent.getIntExtra("idNotification", -1);
//        String idChat = intent.getStringExtra("chatID");
//        String from = intent.getStringExtra("from");
//        Log.e(TAG, mensaje);
//        Log.e(TAG, "idNotification: " + String.valueOf(id));
//        Log.e(TAG, "idChat: " + String.valueOf(idChat));
//        Log.e(TAG, "from: " + String.valueOf(from));
//        responderNotification(mensaje, id, idChat, from);

        try {
            notificationIdFrom = intent.getStringExtra("notificationIdFrom");
            FirebaseDatabase.getInstance().getReference()
                    .child("notificaciones")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .child(notificationIdFrom)
                    .removeValue()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                //Toast.makeText(context, "Notificaciones eliminadas", Toast.LENGTH_LONG).show();
                            } else {

                            }
                        }
                    });            //Toast.makeText(getApplicationContext(), usuarioFrom.toString(), Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            //Log.d(TAG, "####################");
            //Log.d(TAG, e.toString());
           // Log.d(TAG, "####################");
        }
    }
}
