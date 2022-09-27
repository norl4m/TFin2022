package com.marlon.apolo.tfinal2022.foregroundCustomService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import androidx.core.app.NotificationManagerCompat;

import com.marlon.apolo.tfinal2022.llamadaVoz.LlamadaVozActivity;
import com.marlon.apolo.tfinal2022.model.LlamadaVideo;
import com.marlon.apolo.tfinal2022.model.LlamadaVoz;
import com.marlon.apolo.tfinal2022.model.Usuario;
import com.marlon.apolo.tfinal2022.videoLlamada.VideoLlamadaActivity;

public class ForegrounAcceptVideoCallReceiver extends BroadcastReceiver {

    private NotificationManagerCompat notificationManager;
    private int idNotification;

    public ForegrounAcceptVideoCallReceiver() {
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
//            Toast.makeText(context, "Aceptando llamada" + llamadaVideo.toString(), Toast.LENGTH_LONG).show();
            // Toast.makeText(context, "Aceptando llamada" + usuarioFrom.toString(), Toast.LENGTH_LONG).show();

            Intent fullScreenIntent = new Intent(context, VideoLlamadaActivity.class);
            fullScreenIntent.putExtra("channelNameShare", llamadaVideo.getId());
            fullScreenIntent.putExtra("callStatus", 1);/*llamada entrante*/
            fullScreenIntent.putExtra("llamadaVideo", llamadaVideo);/*llamada entrante*/
            fullScreenIntent.putExtra("usuarioFrom", usuarioFrom);/*llamada entrante*/
//            fullScreenIntent.putExtra("contest", 1);/*llamada entrante*/
            fullScreenIntent.putExtra("contest", 0);/*llamada entrante*/
            fullScreenIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //Toast.makeText(context, "Aceptando llamada" + usuarioFrom.toString(), Toast.LENGTH_LONG).show();

            context.startActivity(fullScreenIntent);
        } catch (Exception e) {

        }
    }

    public void cancelNotification(int idNotification) {

        // notificationId is a unique int for each notification that you must define
        notificationManager.cancel(idNotification);

    }
}
