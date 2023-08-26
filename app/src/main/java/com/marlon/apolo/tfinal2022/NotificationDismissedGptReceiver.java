package com.marlon.apolo.tfinal2022;


import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class NotificationDismissedGptReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
      NotificationManager  notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (intent.getAction() != null && intent.getAction().equals("ACTION_DISMISS_NOTIFICATION")) {
            int idNotification = intent.getIntExtra("idNotification",-1);

            // Aquí puedes realizar cualquier acción cuando el usuario elimina la notificación.
            // Por ejemplo, mostrar un mensaje o realizar una tarea específica.
            notificationManager.cancel(idNotification);

            Toast.makeText(context, "Notificación eliminada", Toast.LENGTH_SHORT).show();
        }
    }
}
