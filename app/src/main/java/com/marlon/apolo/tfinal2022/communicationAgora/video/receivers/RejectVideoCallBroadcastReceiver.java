package com.marlon.apolo.tfinal2022.communicationAgora.video.receivers;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.marlon.apolo.tfinal2022.CrazyService;
import com.marlon.apolo.tfinal2022.model.LlamadaVideo;

public class RejectVideoCallBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = RejectVideoCallBroadcastReceiver.class.getSimpleName();

    public RejectVideoCallBroadcastReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
//        StringBuilder sb = new StringBuilder();
//        sb.append("Action: " + intent.getAction() + "\n");
//        sb.append("URI: " + intent.toUri(Intent.URI_INTENT_SCHEME).toString() + "\n");
//        String log = sb.toString();
//        Log.d(TAG, log);

        LlamadaVideo llamadaVideo = (LlamadaVideo) intent.getSerializableExtra("llamadaVideo");
        int notificationId = intent.getIntExtra("notificationId", -1);
        //Toast.makeText(context, context.getString(R.string.filter_reject_call), Toast.LENGTH_LONG).show();
        cancelNotification(context, notificationId);
        rejectVideoCall(context, llamadaVideo);



    }

    private void rejectVideoCall(Context context, LlamadaVideo llamadaVideo) {
        llamadaVideo.setRejectCallStatus(true);
        FirebaseDatabase.getInstance().getReference().child("videoCalls")
                .child(llamadaVideo.getId())
                .setValue(llamadaVideo).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(context, "Llamada rechazada", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void cancelNotification(Context context, int idNotification) {

//        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(contextInstance);
        // notificationId is a unique int for each notification that you must define
//        notificationManager.cancel(idNotification);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

            NotificationManager notificationManagerX = context.getSystemService(NotificationManager.class);
            notificationManagerX.cancel(idNotification);


            //notificacionStackArrayList.add(notificacionStack);

        } else {

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
            notificationManager.cancel(idNotification);
            //notificacionStackArrayList.add(notificacionStack);
        }


    }

}
