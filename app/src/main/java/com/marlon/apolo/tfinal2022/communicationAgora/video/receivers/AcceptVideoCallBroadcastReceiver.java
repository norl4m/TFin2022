package com.marlon.apolo.tfinal2022.communicationAgora.video.receivers;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import androidx.core.app.NotificationManagerCompat;

import com.marlon.apolo.tfinal2022.communicationAgora.video.view.AgoraVideoCallActivity;
import com.marlon.apolo.tfinal2022.communicationAgora.video.view.VideoCallMainActivity;
import com.marlon.apolo.tfinal2022.model.LlamadaVideo;

public class AcceptVideoCallBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = AcceptVideoCallBroadcastReceiver.class.getSimpleName();

    public AcceptVideoCallBroadcastReceiver() {
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
        //Toast.makeText(context, "Contestando videollamada", Toast.LENGTH_LONG).show();
        cancelNotification(context, notificationId);
        answerVideoCall(context, llamadaVideo);


    }

    private void answerVideoCall(Context context, LlamadaVideo llamadaVideo) {
//        Intent fullScreenIntent = new Intent(context, AgoraVideoCallActivity.class);
//        Intent fullScreenIntent = new Intent(context, AgoraVideoCallActivity.class);
        Intent fullScreenIntent = new Intent(context, VideoCallMainActivity.class);
        fullScreenIntent.putExtra("callStatus", "llamadaEntrante");
        llamadaVideo.setDestinyStatus(true);
        llamadaVideo.setChannelConnectedStatus(true);
        fullScreenIntent.putExtra("llamadaVideo", llamadaVideo);
//        fullScreenIntent.putExtra("joinValue", "false");
//        fullScreenIntent.putExtra("extraJoin", "conectar");

//        fullScreenIntent.putExtra("channelNameShare", llamadaVideo.getId());
//        fullScreenIntent.putExtra("callStatus", 1);/*llamada entrante*/
//        fullScreenIntent.putExtra("llamadaVideo", llamadaVideo);/*llamada entrante*/
//        fullScreenIntent.putExtra("usuarioFrom", usuarioFrom);/*llamada entrante*/
////            fullScreenIntent.putExtra("contest", 1);/*llamada entrante*/
//        fullScreenIntent.putExtra("contest", 0);/*llamada entrante*/
        fullScreenIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //Toast.makeText(context, "Aceptando llamada" + usuarioFrom.toString(), Toast.LENGTH_LONG).show();

        context.startActivity(fullScreenIntent);
    }

    private void cancelNotification(Context context, int idNotification) {

        Toast.makeText(context,"Eliminando notifiacion"+String.valueOf(idNotification),Toast.LENGTH_SHORT).show();
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
