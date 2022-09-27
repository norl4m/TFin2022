package com.marlon.apolo.tfinal2022.serviciosSegundoPlano.offlaneService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class EliminarNotificacionReceiver extends BroadcastReceiver {

    private String idFrom;

    public EliminarNotificacionReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            idFrom = intent.getStringExtra("idFrom");
            FirebaseDatabase.getInstance().getReference()
                    .child("notificaciones")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .child(idFrom)
                    .removeValue()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(context, "Notificaciones eliminadas", Toast.LENGTH_LONG).show();
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
