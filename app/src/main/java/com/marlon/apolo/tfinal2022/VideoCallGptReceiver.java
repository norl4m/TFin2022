package com.marlon.apolo.tfinal2022;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
        import android.content.Context;
        import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.marlon.apolo.tfinal2022.communicationAgora.video.view.VideoCallMainActivity;
import com.marlon.apolo.tfinal2022.model.LlamadaVideo;

public class VideoCallGptReceiver extends BroadcastReceiver {
    private NotificationManager notificationManager;

    @Override
    public void onReceive(Context context, Intent intent) {
        // Mostrar el Toast con el mensaje "Hola Receiver"
        Toast.makeText(context, "Hola Receiver", Toast.LENGTH_SHORT).show();
        int idNotification = intent.getIntExtra("idNotification",-1);
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if ("ACCEPT_INCOMING_VIDEO_CALL".equals(intent.getAction())) {
            // Obtener los datos de los extras del Intent (si es necesario)
            Bundle extras = intent.getExtras();

            // Abrir la Activity deseada
            Intent activityIntent = new Intent(context, VideoCallMainActivity.class);

            LlamadaVideo llamadaVideo = (LlamadaVideo) intent.getSerializableExtra("llamadaVideo");


            activityIntent.putExtra("llamadaVideo", llamadaVideo);
            activityIntent.putExtra("callStatus", "llamadaEntrante");

            activityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(activityIntent);
        }
        notificationManager.cancel(idNotification);

    }
}
