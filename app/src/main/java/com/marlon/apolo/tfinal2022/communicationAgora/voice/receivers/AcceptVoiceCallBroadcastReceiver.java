package com.marlon.apolo.tfinal2022.communicationAgora.voice.receivers;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import androidx.core.app.NotificationManagerCompat;

import com.marlon.apolo.tfinal2022.communicationAgora.voice.view.AgoraOnlyVoiceCallActivity;
import com.marlon.apolo.tfinal2022.model.LlamadaVoz;

public class AcceptVoiceCallBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = AcceptVoiceCallBroadcastReceiver.class.getSimpleName();

    public AcceptVoiceCallBroadcastReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
//        StringBuilder sb = new StringBuilder();
//        sb.append("Action: " + intent.getAction() + "\n");
//        sb.append("URI: " + intent.toUri(Intent.URI_INTENT_SCHEME).toString() + "\n");
//        String log = sb.toString();
//        Log.d(TAG, log);
        LlamadaVoz llamadaVideo = (LlamadaVoz) intent.getSerializableExtra("llamadaVoz");
        int notificationId = intent.getIntExtra("notificationId", -1);
        Toast.makeText(context, "Contestando llamada", Toast.LENGTH_LONG).show();
        cancelNotification(context, notificationId);
        answerVideoCall(context, llamadaVideo);


    }

    private void answerVideoCall(Context context, LlamadaVoz llamadaVideo) {
//        Intent fullScreenIntent = new Intent(context, AgoraVideoCallActivity.class);
        Intent fullScreenIntent = new Intent(context, AgoraOnlyVoiceCallActivity.class);
        fullScreenIntent.putExtra("callStatus", "llamadaEntrante");
        fullScreenIntent.putExtra("llamadaVoz", llamadaVideo);
//        fullScreenIntent.putExtra("joinValue", "false");
        fullScreenIntent.putExtra("extraJoin", "conectar");

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
