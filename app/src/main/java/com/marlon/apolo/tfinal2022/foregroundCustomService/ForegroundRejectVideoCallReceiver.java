package com.marlon.apolo.tfinal2022.foregroundCustomService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import androidx.core.app.NotificationManagerCompat;

import com.marlon.apolo.tfinal2022.model.LlamadaVideo;
import com.marlon.apolo.tfinal2022.model.Usuario;
import com.marlon.apolo.tfinal2022.model.VideoLlamada;
import com.marlon.apolo.tfinal2022.videoLlamada.VideoLlamadaActivity;

public class ForegroundRejectVideoCallReceiver extends BroadcastReceiver {

    private NotificationManagerCompat notificationManager;
    private int idNotification;

    public ForegroundRejectVideoCallReceiver() {

    }

    @Override
    public void onReceive(Context context, Intent intent) {

        try {
            notificationManager = NotificationManagerCompat.from(context);

            Usuario usuarioFrom = (Usuario) intent.getSerializableExtra("usuarioFrom");
            LlamadaVideo llamadaVideo = (LlamadaVideo) intent.getSerializableExtra("llamadaVideo");
            //usuarioFrom.rechazarLlamadaDeVoz(llamadaVoz.getId());
            idNotification = intent.getIntExtra("idNotification", -1);
            cancelNotification(idNotification);
//            Toast.makeText(context, "Rechazando llamada" + llamadaVideo.toString(), Toast.LENGTH_LONG).show();

            usuarioFrom.rechazarLlamadaDeVideo(llamadaVideo.getId());


//            Intent fullScreenIntent = new Intent(context, VideoLlamadaActivity.class);
//            fullScreenIntent.putExtra("channelNameShare", llamadaVoz.getId());
//            fullScreenIntent.putExtra("callStatus", 1);/*llamada entrante*/
//            fullScreenIntent.putExtra("llamadaVideo", llamadaVoz);/*llamada entrante*/
//            fullScreenIntent.putExtra("usuarioFrom", usuarioFrom);/*llamada entrante*/
//            fullScreenIntent.putExtra("contestar", true);/*llamada entrante*/
//            fullScreenIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            context.startActivity(fullScreenIntent);
        } catch (Exception e) {

        }
    }

    public void cancelNotification(int idNotification) {
        // notificationId is a unique int for each notification that you must define
        notificationManager.cancel(idNotification);

    }
}
