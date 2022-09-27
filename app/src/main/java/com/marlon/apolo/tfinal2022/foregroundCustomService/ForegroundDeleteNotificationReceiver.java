package com.marlon.apolo.tfinal2022.foregroundCustomService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class ForegroundDeleteNotificationReceiver extends BroadcastReceiver {

    private String notificationIdFrom;

    public ForegroundDeleteNotificationReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
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
