package com.marlon.apolo.tfinal2022.serviciosSegundoPlano.offlaneService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.marlon.apolo.tfinal2022.llamadaVoz.LlamadaVozActivity;
import com.marlon.apolo.tfinal2022.model.LlamadaVoz;
import com.marlon.apolo.tfinal2022.model.Usuario;

public class AcceptCallReceiver extends BroadcastReceiver {

    private int idNotification;
    private NotificationManagerCompat mNotificationManager;

    public AcceptCallReceiver() {

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        mNotificationManager = NotificationManagerCompat.from(context);

        try {

            Usuario usuarioFrom = (Usuario) intent.getSerializableExtra("usuarioFrom");
            LlamadaVoz llamadaVoz = (LlamadaVoz) intent.getSerializableExtra("llamadaVoz");
            //usuarioFrom.rechazarLlamadaDeVoz(llamadaVoz.getId());
            idNotification = intent.getIntExtra("idNotification", -1);
            cancelNotification(idNotification);
            //Toast.makeText(context,"Aceptando llamada"+llamadaVoz.toString(),Toast.LENGTH_LONG).show();

            Intent fullScreenIntent = new Intent(context, LlamadaVozActivity.class);
            fullScreenIntent.putExtra("channelNameShare", llamadaVoz.getId());
            fullScreenIntent.putExtra("callStatus", 1);/*llamada entrante*/
            fullScreenIntent.putExtra("llamadaVoz", llamadaVoz);/*llamada entrante*/
            fullScreenIntent.putExtra("usuarioFrom", usuarioFrom);/*llamada entrante*/
            fullScreenIntent.putExtra("contestar", true);/*llamada entrante*/
            fullScreenIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(fullScreenIntent);
        } catch (Exception e) {

        }

    }

    public void cancelNotification(int idNotification) {
        mNotificationManager.cancel(idNotification);
    }
}
