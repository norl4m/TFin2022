package com.marlon.apolo.tfinal2022.foregroundCustomService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationManagerCompat;

import com.marlon.apolo.tfinal2022.model.LlamadaVoz;
import com.marlon.apolo.tfinal2022.model.Usuario;

public class ForegroundRejectCallReceiver extends BroadcastReceiver {

    private NotificationManagerCompat mNotificationManager;
    private int idNotification;

    public ForegroundRejectCallReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        mNotificationManager = NotificationManagerCompat.from(context);

        try {

            Usuario usuarioFrom = (Usuario) intent.getSerializableExtra("usuarioFrom");
            LlamadaVoz llamadaVoz = (LlamadaVoz) intent.getSerializableExtra("llamadaVoz");
            usuarioFrom.rechazarLlamadaDeVoz(llamadaVoz.getId());
            idNotification = intent.getIntExtra("idNotification", -1);
            cancelNotification(idNotification);
        } catch (Exception e) {

        }

    }

    public void cancelNotification(int idNotification) {
        mNotificationManager.cancel(idNotification);
    }
}
