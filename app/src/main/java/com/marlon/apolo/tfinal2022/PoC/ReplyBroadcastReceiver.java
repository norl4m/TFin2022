package com.marlon.apolo.tfinal2022.PoC;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.RemoteInput;

import com.marlon.apolo.tfinal2022.R;

public class ReplyBroadcastReceiver extends BroadcastReceiver {
    private NotificationManagerCompat notificationManager;
    String KEY_TEXT_REPLY = "key_text_reply";

    @Override
    public void onReceive(Context context, Intent intent) {
        notificationManager = NotificationManagerCompat.from(context);
        int notificationId = intent.getIntExtra("EXTRA_NOTIFICATION_ID", 0);
//        Toast.makeText(context, "Eliminando notificación...", Toast.LENGTH_LONG).show();
//        Toast.makeText(context, "Actualizando notificación...", Toast.LENGTH_LONG).show();
//        updateNotification(context, intent, notificationId);
        cancelNotification(notificationId);

        /*Responder la notificacion*/
    }

    private void updateNotification(Context context, Intent intent, int notificationId) {
        // Build a new notification, which informs the user that the system
        // handled their interaction with the previous notification.
        Notification repliedNotification = new NotificationCompat.Builder(context, "MESSAGE_CHANNEL")
                .setSmallIcon(R.drawable.ic_oficios)
                .setContentTitle("Tú")
                .setContentText(getMessageText(intent))
                .build();

        // Issue the new notification.
        notificationManager.notify(notificationId, repliedNotification);
    }

    public void cancelNotification(int idNotification) {
        notificationManager.cancel(idNotification);
    }

    private CharSequence getMessageText(Intent intent) {
        Bundle remoteInput = RemoteInput.getResultsFromIntent(intent);
        if (remoteInput != null) {
            return remoteInput.getCharSequence(KEY_TEXT_REPLY);
        }
        return null;
    }

}
