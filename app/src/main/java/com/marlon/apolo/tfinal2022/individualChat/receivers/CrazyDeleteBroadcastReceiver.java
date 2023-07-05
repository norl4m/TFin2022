package com.marlon.apolo.tfinal2022.individualChat.receivers;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class CrazyDeleteBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = CrazyDeleteBroadcastReceiver.class.getSimpleName();

    //    public CrazyDeleteBroadcastReceiver() {
//    }
    private void deleteNotifications(String idRemoteUser) {
        FirebaseDatabase.getInstance().getReference()
                .child("notificaciones")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(idRemoteUser)
                .setValue(null).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
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
        //Toast.makeText(context, "Eliminando notificaciones", Toast.LENGTH_LONG).show();
        String idRemoteUser = intent.getStringExtra("idRemoteUser");
        int idNotification = intent.getIntExtra("idNotification", -1);
        deleteNotifications(idRemoteUser);
        Log.d(TAG, "Eliminando notificaciones");
        StringBuilder sb = new StringBuilder();
        sb.append("Action: " + intent.getAction() + "\n");
//        sb.append("URI: " + intent.toUri(Intent.URI_INTENT_SCHEME).toString() + "\n");
        String log = sb.toString();
        Log.d(TAG, log);
    }

}