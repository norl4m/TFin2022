package com.marlon.apolo.tfinal2022.citasTrabajo.receivers;

import static android.app.PendingIntent.FLAG_MUTABLE;
import static android.content.Context.MODE_PRIVATE;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.marlon.apolo.tfinal2022.R;
import com.marlon.apolo.tfinal2022.citasTrabajo.view.DetalleServicioActivity;
import com.marlon.apolo.tfinal2022.model.Cita;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AlarmReceiver extends BroadcastReceiver {
    // private NotificationManagerCompat mNotificationManager;
    private static final int ALARM_NOTIFICATION_ID = 789;
    //private static final String ALARM_CHANNEL_ID = "ALARM_NOTIFICATION_CHANNEL";
    private String TAG = AlarmReceiver.class.getSimpleName();

    // Notification ID.
    //private static final int NOTIFICATION_ID = 186;
    // Notification channel ID.


    //private NotificationManagerCompat mNotificationManager;
    // Notification ID.
    //private static final int NOTIFICATION_ID = 1700;
    // Notification channel ID.
//    private static final String PRIMARY_CHANNEL_ID = "primary_notification_channelx";
    private int usuario;
    private DateFormat formatFec;
    private Calendar calendarNOw;

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
//        throw new UnsupportedOperationException("Not yet implemented");
        calendarNOw = Calendar.getInstance();
//        cal.setTime(d);
        calendarNOw.setTime(new Date());
//        DateFormat formatFec = new SimpleDateFormat("dd MMMM yyyy HH:mm a", new Locale("es", "ES"));
        formatFec = new SimpleDateFormat("dd MMMM yyyy HH:mm", new Locale("es", "ES"));
        Log.d(TAG, formatFec.format(calendarNOw.getTime()));

        Log.e(TAG, "alarm receiver");

        String idCita = intent.getStringExtra("idCita");
        String nombreTrabajador = intent.getStringExtra("nT");
        String nombreEmpleador = intent.getStringExtra("nE");
        String fechaCita = intent.getStringExtra("fec");
        String idFrom = intent.getStringExtra("idFrom");
        String idTo = intent.getStringExtra("idTo");
//        Cita citaLoca = (Cita) intent.getSerializableExtra("cita");
        int random = intent.getIntExtra("random", -1);

        Cita cita = new Cita();
        cita.setIdCita(idCita);
        cita.setNombreTrabajador(nombreTrabajador);
        cita.setNombreEmpleador(nombreEmpleador);
        cita.setFechaCita(fechaCita);
        cita.setFrom(idFrom);
        cita.setTo(idTo);
        try {

            Log.d(TAG, cita.toString());
            Log.d(TAG, String.valueOf(random));

        } catch (Exception e) {
            Log.d(TAG, e.toString());
        }

        SharedPreferences mPreferences = context.getSharedPreferences("MyPreferences", MODE_PRIVATE);
        usuario = mPreferences.getInt("usuario", -1);
//        mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        //mNotificationManager = NotificationManagerCompat.from(context);
//        deliverNotification(context);

        // Deliver the notification.
//        comprobarAlarmaLoco(context, random, citaLoca);
        deliverNotification(context, cita, random);
//        deliverNotification(context);
        try {
            comprobarAlarmaLoco(context, cita, random); /*eRROR api 31*/

        } catch (Exception e) {
            Log.d(TAG, e.toString());
        }

//        deliverNotification(context);
    }

    private void comprobarAlarmaLoco(Context context, Cita cita, int random) {
//        Log.d(TAG, "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
//        Log.d(TAG, "COMPROBANDO ALARMA LOCAL LOCO");
//        Log.d(TAG, "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
//        Log.d(TAG, cita.toString());
//        Log.d(TAG, String.valueOf(random));
        Intent alarmIntent = new Intent(context, AlarmReceiver.class);
//        cita.setItems(null);
//        alarmIntent.putExtra("cita", cita);
        alarmIntent.putExtra("idCita", cita.getIdCita());
        alarmIntent.putExtra("nT", cita.getNombreTrabajador());
        alarmIntent.putExtra("nE", cita.getNombreEmpleador());
        alarmIntent.putExtra("idFrom", cita.getFrom());
        alarmIntent.putExtra("idTo", cita.getTo());
        alarmIntent.putExtra("fec", cita.getFechaCita());
        alarmIntent.putExtra("random", random);


        boolean alarmUp = (PendingIntent.getBroadcast(context, random, alarmIntent, PendingIntent.FLAG_NO_CREATE | FLAG_MUTABLE) != null);
        if (alarmUp) {
            Log.d(TAG, "Alarm is already active");
            // If the alarm has been set, cancel it.
            AlarmManager alarmMgr;
            alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            if (alarmMgr != null) {
                PendingIntent notifyPendingIntent = null;

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    notifyPendingIntent = PendingIntent.getBroadcast
                            (context, random, alarmIntent,
                                    FLAG_MUTABLE);
                } else {
                    notifyPendingIntent = PendingIntent.getBroadcast
                            (context, random, alarmIntent,
                                    PendingIntent.FLAG_UPDATE_CURRENT);
                    /*Bandera de mrda: PendingIntent.FLAG_IMMUTABLE*/
//
                }
                alarmMgr.cancel(notifyPendingIntent);
                Log.d(TAG, "Alarm OFF");

            }

        } else {
            Log.d(TAG, "Alarm is no active");
        }


    }

    private void comprobarAlarmaLoco(Context context, int random, Cita cita) {
        Log.d(TAG, "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
        Log.d(TAG, "COMPROBANDO ALARMA LOCAL LOCO");
        Log.d(TAG, "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
        Log.d(TAG, String.valueOf(random));
        Log.d(TAG, String.valueOf(cita));
        Intent alarmIntent = new Intent(context, AlarmReceiver.class);


//        boolean alarmUp = (PendingIntent.getBroadcast(context, 0, alarmIntent, PendingIntent.FLAG_NO_CREATE) != null);
        boolean alarmUp = (PendingIntent.getBroadcast(context, random,
                alarmIntent, PendingIntent.FLAG_NO_CREATE) != null);
        if (alarmUp) {
            Log.d(TAG, "Alarm is already active");
            Log.d(TAG, String.valueOf(random));
            Log.d(TAG, String.valueOf(cita));
        } else {
            Log.d(TAG, "Alarm is no active");
        }


    }

    /**
     * Builds and delivers the notification.
     *
     * @param context, activity context.
     */
    private void deliverNotification(Context context, Cita cita, int random) {
        Log.d(TAG, "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
        Log.d(TAG, "LANZANDO ALARMA");
        Log.d(TAG, "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
        // Create the content intent for the notification, which launches
        // this activity
        Intent contentIntent = new Intent(context, DetalleServicioActivity.class);
        contentIntent.putExtra("cita", cita);
        PendingIntent contentPendingIntent = null;


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            contentPendingIntent = PendingIntent.getActivity
                    (context, random, contentIntent, FLAG_MUTABLE);
        } else {
            contentPendingIntent = PendingIntent.getActivity
                    (context, random, contentIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        }


        String text = "";


        switch (usuario) {
            case 0:
                text = "Usted tiene una cita de trabajo con: " + cita.getNombreTrabajador() + " el " + cita.getFechaCita();
            case 1:
                text = "Usted tiene una cita de trabajo con: " + cita.getNombreTrabajador() + " el " + cita.getFechaCita();
                break;
            case 2:
                text = "Usted tiene una cita de trabajo con: " + cita.getNombreEmpleador() + " el " + cita.getFechaCita();
                break;
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            Log.d(TAG, cita.toString());


            contentPendingIntent = PendingIntent.getActivity
                    (context, random, contentIntent, FLAG_MUTABLE);

            Notification.Builder callNotification = new Notification.Builder(context, "NOTIFICATIONS_CHANNEL_ID")
                    //                .setContentIntent(contentIntent)
                    .setSmallIcon(R.drawable.ic_oficios)
//                    .setContentTitle("Cita de trabajo-: " + String.valueOf(random) + " - " + String.valueOf(Build.VERSION.SDK_INT))
                    .setContentTitle("Cita de trabajo")
                    .setContentText(text)
                    .setStyle(new Notification.BigTextStyle()
                            .bigText(text))
                    .setContentIntent(contentPendingIntent)
                    .setAutoCancel(true);


            NotificationManager notificationManagerX = context.getSystemService(NotificationManager.class);
            notificationManagerX.notify(random, callNotification.build());


        } else {

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {


                Notification callNotification = new Notification.Builder(context, "NOTIFICATIONS_CHANNEL_ID")
                        .setContentTitle("Cita de trabajo")
                        .setContentText(text)
                        .setAutoCancel(true)
                        .setContentIntent(contentPendingIntent)
                        .setSmallIcon(R.drawable.ic_oficios)
                        .setStyle(new Notification.BigTextStyle()
                                .bigText(text))
                        .build();
                NotificationManager notificationManagerX = context.getSystemService(NotificationManager.class);
                notificationManagerX.notify(random, callNotification);


            } else {

                NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "NOTIFICATIONS_CHANNEL_ID")
                        .setSmallIcon(R.drawable.ic_oficios)
                        .setContentTitle("Cita de trabajo")
                        .setContentText(text)
                        .setAutoCancel(true)
                        .setContentIntent(contentPendingIntent)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setDefaults(NotificationCompat.DEFAULT_ALL)
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(text))

                        // Set the intent that will fire when the user taps the notification
//                    .setContentIntent(pendingIntent)
//                    .setContentIntent(pendingIntent)
                        .setAutoCancel(true);
                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
                notificationManager.notify(random, builder.build());

            }
        }


    }


}