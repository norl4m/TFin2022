package com.marlon.apolo.tfinal2022.foregroundCustomService;

import static android.app.PendingIntent.FLAG_MUTABLE;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.RemoteInput;
import androidx.preference.PreferenceManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.marlon.apolo.tfinal2022.MainNavigationActivity;
import com.marlon.apolo.tfinal2022.R;
import com.marlon.apolo.tfinal2022.citasTrabajo.AlarmReceiver;
import com.marlon.apolo.tfinal2022.individualChat.view.IndividualChatActivity;
import com.marlon.apolo.tfinal2022.individualChat.view.MensajeNube;
import com.marlon.apolo.tfinal2022.llamadaVoz.LlamadaVozActivity;
import com.marlon.apolo.tfinal2022.model.Administrador;
import com.marlon.apolo.tfinal2022.model.Chat;
import com.marlon.apolo.tfinal2022.model.Cita;
import com.marlon.apolo.tfinal2022.model.Empleador;
import com.marlon.apolo.tfinal2022.model.LlamadaVideo;
import com.marlon.apolo.tfinal2022.model.LlamadaVoz;
import com.marlon.apolo.tfinal2022.model.NotificacionCustom;
import com.marlon.apolo.tfinal2022.model.NotificacionCustomLlamada;
import com.marlon.apolo.tfinal2022.model.NotificacionCustomVideoLlamada;
import com.marlon.apolo.tfinal2022.model.Participante;
import com.marlon.apolo.tfinal2022.model.Trabajador;
import com.marlon.apolo.tfinal2022.model.Usuario;
import com.marlon.apolo.tfinal2022.videoLlamada.VideoLlamadaActivity;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;

public class ForegroundCustomService extends Service {

    private static final int ONGOING_NOTIFICATION_ID = 1000;
    private static final String FOREGROUND_CHANNEL_ID = "BACKGROUND_CHANNEL";
    private static final String TAG = ForegroundCustomService.class.getSimpleName();
    private static final String KEY_TEXT_REPLY = "KEY_TEXT_REPLY";
    private static final String NOTIFICATIONS_CHANNEL_ID = "CANAL_DE_NOTIFICACIONES";
    private static final String ACTION_DELETE_NOTIFICATION = "DELETE";
    private static final String ACTION_REPLY_NOTIFICATION = "REPLY";
    private static final String CHANNEL_LLAMADAS_VOZ_ID = "CHANNEL_LLAMADAS_VOZ_ID";
    private static final String CHANNEL_VIDELLAMADAS_ID = " CHANNEL_VIDELLAMADAS_ID";
    private static final String ACTION_RESPONDER_LLAMADA_VOZ = "ACTION_RESPONDER_LLAMADA_VOZ";
    private static final String ACTION_RECHAZAR_LLAMADA_VOZ = "ACTION_RECHAZAR_LLAMADA_VOZ";
    private static final String ACTION_ACEPTAR_VIDEO_LLAMADA = "ACTION_ACEPTAR_VIDEO_LLAMADA";
    private static final String ACTION_RECHAZAR_VIDEO_LLAMADA = "ACTION_RECHAZAR_VIDEO_LLAMADA";
    private static final String PRIMARY_CHANNEL_ID = "primary_notification_channelx";
    private DatabaseReference databaseReference;
    private ArrayList<NotificacionCustom> notificacionCustomArrayList;
    private ArrayList<Chat> chats;
    private ForegroundDeleteNotificationReceiver foregroundDeleteNotificationReceiver;
    private ForegroundReplyNotificationReceiver foregroundReplyNotificationReceiver;
    private ForegroundAcceptCallReceiver foregroundAcceptCallReceiver;
    private ForegroundRejectCallReceiver foregroundRejectCallReceiver;
    private ForegrounAcceptVideoCallReceiver foregrounAcceptVideoCallReceiver;
    private ForegroundRejectVideoCallReceiver foregroundRejectVideoCallReceiver;
    private Usuario usuarioLocal;
    private SharedPreferences myPreferences;
    private String usurioBloquedo;
    private ArrayList<NotificacionCustomLlamada> notificacionCustomLlamadas;
    private ArrayList<NotificacionCustomVideoLlamada> notificacionCustomVideoLlamadaArrayList;
    private static final String ALARM_CHANNEL_ID = "ALARM_NOTIFICATION_CHANNEL";

    private Context contextLocal;
    private Dialog alertD;
    private ChildEventListener childEventListenerNotificaciones;
    private ChildEventListener LlamadaDeVozListener;
    private ChildEventListener LlamadaDeVideoListener;
    private ChildEventListener childEventListenerCitasTrabajo;
    private SharedPreferences defaultSharedPreferences;


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void createNotificationChannelToForegroundServices() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel = new NotificationChannel(FOREGROUND_CHANNEL_ID, name, importance);
            notificationChannel.setDescription(description);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }

    private void createNotificationChannelToNotifications() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Notificaciones";
            String description = "Notificaciones desde Firebase";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannelMessages = new NotificationChannel(NOTIFICATIONS_CHANNEL_ID, name, importance);
            notificationChannelMessages.setDescription(description);
            notificationChannelMessages.enableLights(true);
            notificationChannelMessages.setLightColor(Color.RED);
            notificationChannelMessages.enableVibration(true);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(notificationChannelMessages);
        }
    }

    private void createNotificationChannelToLlamadasDeVoz() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Notificaciones de llamadas de voz";
            String description = "Notificaciones  de llamadas de voz desde Firebase";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannelMessages = new NotificationChannel(CHANNEL_LLAMADAS_VOZ_ID, name, importance);
            notificationChannelMessages.setDescription(description);
            notificationChannelMessages.enableLights(true);
            notificationChannelMessages.setLightColor(Color.RED);
            notificationChannelMessages.enableVibration(true);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(notificationChannelMessages);
        }
    }

    private void createNotificationChannelToAlarm() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library

        Log.i(TAG, "Creando canal de notificaciones locales");

        CharSequence name = "Notificaciones de alarmas";
        String description = "Notificaciones  de alarmas locales";

        if (android.os.Build.VERSION.SDK_INT >=
                android.os.Build.VERSION_CODES.O) {

//            // Create the NotificationChannel with all the parameters.
//            NotificationChannel notificationChannel = new NotificationChannel
//                    (ALARM_CHANNEL_ID, name, NotificationManager.IMPORTANCE_HIGH);
//
//            notificationChannel.enableLights(true);
//            notificationChannel.setLightColor(Color.RED);
//            notificationChannel.enableVibration(true);
//            notificationChannel.setDescription(description);
////            mNotificationManager.createNotificationChannel(notificationChannel);
//
//            NotificationManager notificationManager = getSystemService(NotificationManager.class);
//            notificationManager.createNotificationChannel(notificationChannel);


            // Create the NotificationChannel with all the parameters.
            NotificationChannel notificationChannel = new NotificationChannel
                    (PRIMARY_CHANNEL_ID,
                            "No se por que chucha no vale",
                            NotificationManager.IMPORTANCE_HIGH);

            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setDescription("Notifies every aaaa minutes to ");
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(notificationChannel);
//            mNotificationManager.createNotificationChannel(notificationChannel);


        }


    }

    private void notificationInBackground() {
        Intent notificationIntent = new Intent(this, MainNavigationActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, ONGOING_NOTIFICATION_ID, notificationIntent, FLAG_MUTABLE);

        NotificationCompat.Builder notification = new NotificationCompat.Builder(this, FOREGROUND_CHANNEL_ID)
                .setContentTitle(getText(R.string.app_name))
                .setContentText(getText(R.string.notification_message))
                .setSmallIcon(R.drawable.ic_oficios)
                .setContentIntent(pendingIntent);

        startForeground(ONGOING_NOTIFICATION_ID, notification.build());

    }

    @Override
    public void onCreate() {
        //Toast.makeText(this, "Service onCreate()...", Toast.LENGTH_SHORT).show();
        createNotificationChannelToForegroundServices();
        notificationInBackground();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //Toast.makeText(this, "Service starting...", Toast.LENGTH_SHORT).show();

        contextLocal = this;
        databaseReference = FirebaseDatabase.getInstance().getReference();
        createNotificationChannelToNotifications();
        createNotificationChannelToLlamadasDeVoz();
        createNotificationChannelToAlarm();
        foregroundDeleteNotificationReceiver = new ForegroundDeleteNotificationReceiver();
        foregroundReplyNotificationReceiver = new ForegroundReplyNotificationReceiver();
        foregroundAcceptCallReceiver = new ForegroundAcceptCallReceiver();
        foregroundRejectCallReceiver = new ForegroundRejectCallReceiver();
        foregrounAcceptVideoCallReceiver = new ForegrounAcceptVideoCallReceiver();
        foregroundRejectVideoCallReceiver = new ForegroundRejectVideoCallReceiver();
        myPreferences = this.getSharedPreferences("MyPreferences", MODE_PRIVATE);
        defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        loadLocalUser();
        listenerChats();
        listenerNotifications();
        listenerNotificacionesDeLlamadas();
        listenerNotificacionesDeVideoLlamadas();
        listenerNotificacionesDeCitasTrabajo();
        // If we get killed, after returning from here, restart
        return START_STICKY;
    }


    private void listenerNotificacionesDeCitasTrabajo() {
        ArrayList<Cita> citaArrayList = new ArrayList<>();
        childEventListenerCitasTrabajo = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                try {
                    Cita cita = snapshot.getValue(Cita.class);
                    if (cita != null) {
                        if (cita.getTo().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                            citaArrayList.add(cita);
                            if (cita.isStateReceive()) {

                            } else {
                                boolean notificacionesCitas = defaultSharedPreferences.getBoolean("sync_notificaciones_citas", true);

                                if (notificacionesCitas) {
                                    programarAlarmaLocal(cita);
//                                showNotificationCita(cita);
                                    cita.setStateReceive(true);
                                    cita.actualizarCita();
                                }

//                                programarAlarmaLocal(cita);
////                                showNotificationCita(cita);
//                                cita.setStateReceive(true);
//                                cita.actualizarCita();
                            }
                        }
                    }

                } catch (Exception e) {

                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        FirebaseDatabase.getInstance().getReference()
                .child("citas")
                .addChildEventListener(childEventListenerCitasTrabajo);

    }


    private void showNotificationCita(Cita citaDB) {


//        String text = "";
//        switch (usuario) {
//            case 0:
//                text = "Usted tiene una cita de trabajo con: " + citaDB.getNombreTrabajador() + " el: " + citaDB.getFechaCita();
//            case 1:
//                text = "Usted tiene una cita de trabajo con: " + citaDB.getNombreTrabajador() + " el: " + citaDB.getFechaCita();
//                break;
//            case 2:
//                text = "Usted tiene una cita de trabajo con: " + citaDB.getNombreEmpleador() + " el: " + citaDB.getFechaCita();
//                break;
//        }
//        Log.d(TAG, "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
//        Log.d(TAG, text);
////        Log.d(TAG, String.valueOf(usuario));
//        Log.d(TAG, "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");

//
//

        int random = 1700;


        ArrayList<Cita> citas = new ArrayList<>();
        Cita cita = citaDB;
        Log.d(TAG, cita.toString());
        Cita citaPass = new Cita();
        citaPass.setChatID(cita.getChatID());
        citaPass.setIdCita(cita.getIdCita());
        citaPass.setFechaCita(cita.getFechaCita());
        citaPass.setTotal(cita.getTotal());
        citaPass.setNombreEmpleador(cita.getNombreEmpleador());
        citaPass.setNombreTrabajador(cita.getNombreTrabajador());
//        citaPass.setParticipants(cita.getParticipants());
        citaPass.setFrom(cita.getFrom());
        citaPass.setTo(cita.getTo());
        citaPass.setState(cita.isState());
        citaPass.setStateReceive(citaDB.isStateReceive());


        /***************************************************/

        final AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent alarmIntent = new Intent(getApplicationContext(), AlarmReceiver.class);
        Log.d(TAG, "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
        Log.d(TAG, "CONFIGURANDO ALARMA");
        Log.d(TAG, "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
        alarmIntent.putExtra("idCita", cita.getIdCita());
//        alarmIntent.putExtra("nT", cita.getNombreTrabajador());
//        alarmIntent.putExtra("nE", citaDB.getNombreEmpleador());
//        alarmIntent.putExtra("idFrom", citaDB.getFrom());
//        alarmIntent.putExtra("idTo", citaDB.getTo());
//        alarmIntent.putExtra("fechaCita", citaDB.getFechaCita());

        alarmIntent.putExtra("nT", cita.getNombreTrabajador());
        alarmIntent.putExtra("nE", cita.getNombreEmpleador());
        alarmIntent.putExtra("idFrom", cita.getFrom());
        alarmIntent.putExtra("idTo", cita.getTo());
        alarmIntent.putExtra("fec", cita.getFechaCita());


        // alarmIntent.putExtra("cita",citaPass);


        boolean alarmUp = (PendingIntent.getBroadcast(getApplicationContext(), random, alarmIntent,
                PendingIntent.FLAG_NO_CREATE) != null);
        Log.d(TAG, String.format("Estado de alarma: %s", alarmUp));
        final PendingIntent notifyPendingIntent = PendingIntent.getBroadcast
                (getApplicationContext(), random, alarmIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);

        Calendar objCalendar = Calendar.getInstance();

        DateFormat formatFec = new SimpleDateFormat("dd MMMM yyyy HH:mm a", new Locale("es", "ES"));
        Calendar calendar = Calendar.getInstance();

        try {
            objCalendar.setTime(Objects.requireNonNull(formatFec.parse(cita.getFechaCita())));
        } catch (ParseException e) {
            e.printStackTrace();
        }

//                                objCalendar.set(Calendar.YEAR, 2022);
//                                objCalendar.set(Calendar.MONTH, 1);//meses de 0-11
//                                objCalendar.set(Calendar.DAY_OF_MONTH, 9);
//                                objCalendar.set(Calendar.HOUR_OF_DAY, 3);
//                                objCalendar.set(Calendar.MINUTE, 58);
//                                objCalendar.set(Calendar.SECOND, 0);
//                                objCalendar.set(Calendar.MILLISECOND, 0);
//                                objCalendar.set(Calendar.AM_PM, Calendar.AM);

        if (alarmManager != null) {
            Log.d(TAG, "Configurando alarma");
//            alarmManager.setInexactRepeating
//                    (AlarmManager.ELAPSED_REALTIME_WAKEUP,
//                            triggerTime, repeatInterval,
//                            notifyPendingIntent);
//            alarmManager.set(AlarmManager.ELAPSED_REALTIME,
//                    SystemClock.elapsedRealtime() + 1000*60*minutes,
//                    notifyPendingIntent);
            alarmManager.set(AlarmManager.RTC,
                    objCalendar.getTimeInMillis(),
                    notifyPendingIntent);

            String patronFechaYHora = "dd MMMM yyyy HH:mm aa";
            Locale locale = new Locale("es", "ES");
            DateFormat format = new SimpleDateFormat("dd MMMM yyyy HH:mm aa", new Locale("es", "ES"));


            try {
                SimpleDateFormat formatFecha = new SimpleDateFormat(patronFechaYHora, locale);
                Date date = formatFecha.parse(cita.getFechaCita());
                //Log.d(TAG, "INPUT: " + horaYFecha);

                Calendar cal = Calendar.getInstance();
                cal.setTime(date);

//            Date date1 = format.parse(format.format(calendar.getTime()));
                Date date1 = format.parse(format.format(cal.getTime()));
                Date date2 = new Date();
                Log.d(TAG, "Date 1 selected(alarm date): " + format.format(date1));
                Log.d(TAG, "Date 2 compare(now): " + format.format(date2));
                if (date1.compareTo(date2) > 0) {
                    //Log.d(TAG, "La fecha seleccionada es correcta");

                } else if (date1.compareTo(date2) < 0) {
                    Log.d(TAG, "La alarma ha expirado!");
                    alarmManager.cancel(notifyPendingIntent);
                    Log.d(TAG, "La alarma ha sido descativada!");

//                                            arrayListErrores.add("La fecha seleccionada es incorrecta");
//
//                                            validacion = false;
                } else if (date1.compareTo(date2) == 0) {

                }
            } catch (ParseException e) {
                e.printStackTrace();
            }


//            objAlarmManager.set(AlarmManager.RTC_WAKEUP,objCalendar.getTimeInMillis(), notifyPendingIntent);

        }
        /***************************************************/


//        mNotifyManager.notify(random, notification);
//        mNotifyManager.notify(random, builder.build());

    }


    public void programarAlarmaLocal(Cita cita) {
        AlarmManager alarmMgr;
        PendingIntent alarmIntent;
        alarmMgr = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReceiver.class);
        intent.putExtra("idCita", cita.getIdCita());
        //alarmIntent1.putExtra("cita", cita);
        intent.putExtra("nT", cita.getNombreTrabajador());
        intent.putExtra("nE", cita.getNombreEmpleador());
        intent.putExtra("idFrom", cita.getFrom());
        intent.putExtra("idTo", cita.getTo());
        intent.putExtra("fec", cita.getFechaCita());

        DateFormat formatFecD = new SimpleDateFormat("dd MMMM yyyy HH:mm a", new Locale("es", "ES"));
        Calendar calendarD = Calendar.getInstance();
        try {
            calendarD.setTime(Objects.requireNonNull(formatFecD.parse(cita.getFechaCita())));
        } catch (Exception e) {
            Log.d(TAG, e.toString());

        }
        Log.d(TAG, "calendarX:" + String.valueOf(formatFecD.format(calendarD.getTime())) + ":");
        Log.d(TAG, "FecÑ:" + cita.getFechaCita() + ":");


        Log.d(TAG, "SDK VERSION: " + String.valueOf(Build.VERSION.SDK_INT));
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//            alarmIntent = PendingIntent.getBroadcast(this,
//                    1700, intent, FLAG_MUTABLE);
//        } else {
//            alarmIntent = PendingIntent.getBroadcast(this,
//                    1700, intent, 0);
//        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            alarmIntent = PendingIntent.getBroadcast
                    (getApplicationContext(), 1700, intent,
                            FLAG_MUTABLE);
        } else {
            alarmIntent = PendingIntent.getBroadcast
                    (getApplicationContext(), 1700, intent,
                            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
//
        }


//        alarmMgr.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,
//                SystemClock.elapsedRealtime() +
//                        60 * 1000, alarmIntent);


        Date date = null;
        try {
//            date = new SimpleDateFormat("dd MMMM yyyy HH:mm", new Locale("es", "ES")).parse("13 agosto 2022 22:50");
            date = new SimpleDateFormat("dd MMMM yyyy HH:mm", new Locale("es", "ES")).parse(cita.getFechaCita());
            String fecha = new SimpleDateFormat("dd MMMM yyyy HH:mm", new Locale("es", "ES")).format(date);
            Log.d(TAG, "fecha de mrda: " + fecha);
            Calendar calendarz = Calendar.getInstance();
            calendarz.setTime(date);

        } catch (ParseException e) {
            Log.e(TAG, "fecha de mrda: " + e);
            e.printStackTrace();
        }


//        String dateInString = cita.getFechaCita();
//
//        DateFormat formatFecx = new SimpleDateFormat("dd MMMM yyyy HH:mm a", Locale.getDefault());
//        Calendar calendarXx = Calendar.getInstance();
//        try {
//            calendarXx.setTime(Objects.requireNonNull(formatFecx.parse(cita.getFechaCita())));
//        } catch (Exception e) {
//            Log.d(TAG, e.toString());
//        }
//
//
//        SimpleDateFormat sdfx = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
//        Date datex = null;
//        try {
//            datex = sdfx.parse(cita.getFechaCita());
//        } catch (ParseException e) {
//            Log.d(TAG, e.toString());
//        }
//        Calendar calx = Calendar.getInstance();
//        calx.setTime(datex);
//
////        Log.d(TAG, "Fecha cita" + cita.getFechaCita());
////        Log.d(TAG, "calendarX" + String.valueOf(formatFecx.format(calendarXx.getTime())));
////        Log.d(TAG, "calendarX" + String.valueOf(formatFecx.format(calx.getTime())));
////        Log.d(TAG, "Hours: " + String.valueOf(calendarXx.getTime().getHours()));
////        Log.d(TAG, "Minutes: " + String.valueOf(calendarXx.getTime().getMinutes()));
////        Log.d(TAG, "Date: " + String.valueOf(calendarXx.getTime().getDate()
////        ));
//
//
//        // Set the alarm to start at 8:30 a.m.
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTimeInMillis(System.currentTimeMillis());
//        calendar.set(Calendar.MILLISECOND, 0);
//        calendar.set(Calendar.SECOND, 0);
//
//        calendar.set(Calendar.HOUR_OF_DAY, calendarXx.getTime().getHours());
////        calendar.set(Calendar.HOUR_OF_DAY, calendarXx.getTime().getHours());
////        calendar.set(Calendar.MINUTE, 4);
//        calendar.set(Calendar.MINUTE, 33);
//
//
//        calendarXx.set(Calendar.MILLISECOND, 0);
//        calendarXx.set(Calendar.SECOND, 0);

        // setRepeating() lets you specify a precise custom interval--in this case,
        // 20 minutes.

        try {
//            date = new SimpleDateFormat("dd MMMM yyyy HH:mm", new Locale("es", "ES")).parse("13 agosto 2022 22:50");
            date = new SimpleDateFormat("dd MMMM yyyy HH:mm", new Locale("es", "ES")).parse(cita.getFechaCita());
            String fecha = new SimpleDateFormat("dd MMMM yyyy HH:mm", new Locale("es", "ES")).format(date);
            Log.d(TAG, "fecha de mrda: " + fecha);
            Calendar calendarz = Calendar.getInstance();
            calendarz.setTime(date);
            alarmMgr.set(AlarmManager.RTC_WAKEUP, calendarz.getTimeInMillis(), alarmIntent);

        } catch (ParseException e) {
            Log.e(TAG, "fecha de mrda: " + e);
            e.printStackTrace();
        }

//        alarmMgr.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), alarmIntent);
//        alarmMgr.set(AlarmManager.RTC_WAKEUP, calendarXx.getTimeInMillis(), alarmIntent);
//        alarmMgr.set(AlarmManager.ELAPSED_REALTIME, calendarXx.getTimeInMillis(), alarmIntent); no sirve
//        alarmMgr.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, calendarXx.getTimeInMillis(), alarmIntent); no sirve


        //        Toast.makeText(getApplicationContext(), "Programando alarma local", Toast.LENGTH_LONG).show();

//        final int min = 7000;
//        final int max = 7999;
//        int random = new Random().nextInt((max - min) + 1) + min;


//        final AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent alarmIntent1 = new Intent(this, AlarmReceiver.class);
        Log.d(TAG, "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
        Log.d(TAG, "CONFIGURANDO ALARMA REMOTA");
        Log.d(TAG, cita.toString());
        Log.d(TAG, "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
        alarmIntent1.putExtra("idCita", cita.getIdCita());
        //alarmIntent1.putExtra("cita", cita);
        alarmIntent1.putExtra("nT", cita.getNombreTrabajador());
        alarmIntent1.putExtra("nE", cita.getNombreEmpleador());
        alarmIntent1.putExtra("idFrom", cita.getFrom());
        alarmIntent1.putExtra("idTo", cita.getTo());
        alarmIntent1.putExtra("fec", cita.getFechaCita());

//        Cita cita1 = new Cita();
//        cita1.setIdCita(cita.getIdCita());
//        alarmIntent.putExtra("cita",cita1);

//        boolean alarmUp = (PendingIntent.getBroadcast(getApplicationContext(), random, alarmIntent,
//                PendingIntent.FLAG_NO_CREATE) != null);
//        Log.d(TAG, String.format("Estado de alarma: %s", alarmUp));
//        final PendingIntent notifyPendingIntent = PendingIntent.getBroadcast
//                (getApplicationContext(), NOTIFICATION_ID, alarmIntent1,
//                        PendingIntent.FLAG_UPDATE_CURRENT);
        final PendingIntent notifyPendingIntent = PendingIntent.getBroadcast
                (getApplicationContext(), 1700, alarmIntent1,
                        PendingIntent.FLAG_MUTABLE);
//
//        Calendar objCalendar = Calendar.getInstance();
//
//        DateFormat formatFec = new SimpleDateFormat("dd MMMM yyyy HH:mm a", new Locale("es", "ES"));
//        Calendar calendar = Calendar.getInstance();
//
//        try {
//            objCalendar.setTime(Objects.requireNonNull(formatFec.parse(cita.getFechaCita())));
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//
//        if (alarmManager != null) {
//            Log.d(TAG, "Configurando alarma");
//            alarmManager.set(AlarmManager.RTC,
//                    objCalendar.getTimeInMillis(),
//                    notifyPendingIntent);
//
//            String patronFechaYHora = "dd MMMM yyyy HH:mm aa";
//            Locale locale = new Locale("es", "ES");
//            DateFormat format = new SimpleDateFormat("dd MMMM yyyy HH:mm aa", new Locale("es", "ES"));
//
//
//            try {
//                SimpleDateFormat formatFecha = new SimpleDateFormat(patronFechaYHora, locale);
//                Date date = formatFecha.parse(cita.getFechaCita());
//                //Log.d(TAG, "INPUT: " + horaYFecha);
//
//                Calendar cal = Calendar.getInstance();
//                cal.setTime(date);
//
////            Date date1 = format.parse(format.format(calendar.getTime()));
//                Date date1 = format.parse(format.format(cal.getTime()));
//                Date date2 = new Date();
//                Log.d(TAG, "Date 1 selected(alarm date): " + format.format(date1));
//                Log.d(TAG, "Date 2 compare(now): " + format.format(date2));
//                if (date1.compareTo(date2) > 0) {
//                    //Log.d(TAG, "La fecha seleccionada es correcta");
//
//                } else if (date1.compareTo(date2) < 0) {
//                    Log.d(TAG, "La alarma ha expirado!");
//                    alarmManager.cancel(notifyPendingIntent);
//                    Log.d(TAG, "La alarma ha sido descativada!");
//
////                                            arrayListErrores.add("La fecha seleccionada es incorrecta");
////
////                                            validacion = false;
//                } else if (date1.compareTo(date2) == 0) {
//
//                }
//            } catch (ParseException e) {
//                e.printStackTrace();
//            }
//
//
////            objAlarmManager.set(AlarmManager.RTC_WAKEUP,objCalendar.getTimeInMillis(), notifyPendingIntent);
//
//        }

        // Set up the Notification Broadcast Intent.
        Intent notifyIntent = new Intent(this, AlarmReceiver.class);

        boolean alarmUp = (PendingIntent.getBroadcast(this, 1700, notifyIntent, PendingIntent.FLAG_NO_CREATE) != null);

//        final PendingIntent notifyPendingIntent = PendingIntent.getBroadcast
//                (this, NOTIFICATION_ID, notifyIntent,
//                        PendingIntent.FLAG_UPDATE_CURRENT);

        final AlarmManager alarmManager = (AlarmManager) getSystemService
                (ALARM_SERVICE);


//        long repeatInterval = 800000L;
//                            long repeatInterval = 0;

//        long triggerTime = SystemClock.elapsedRealtime() + repeatInterval;

        // If the Toggle is turned on, set the repeating alarm with
        // a 15 minute interval.
//                            Calendar objCalendar = Calendar.getInstance();
        // Set the toast message for the "on" case.
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);


        Date d = null;
        try {
            d = sdf.parse("2022-08-06 13:45:00");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar cal = Calendar.getInstance();
//        cal.setTime(d);
        cal.setTime(new Date());
        if (alarmManager != null) {
//                                objCalendar = Calendar.getInstance();
//                                alarmManager.setInexactRepeating
//                                        (AlarmManager.ELAPSED_REALTIME_WAKEUP,
//                                                triggerTime, repeatInterval,
//                                                notifyPendingIntent);
//            alarmManager.set(AlarmManager.RTC,
//                    cal.getTimeInMillis(),
//                    notifyPendingIntent);
        }

        String dateStr1 = sdf.format(cal.getTime());

//                            toastMessage = getString(R.string.alarm_on_toast) + objCalendar.getTime().toLocaleString();
        String toastMessage = getString(R.string.alarm_on_toast) + dateStr1;
        Log.d(TAG, toastMessage);

//        AlarmManager alarmMgr;
//        PendingIntent alarmIntent;
        alarmMgr = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
//        Intent intent = new Intent(this, AlarmReceiver.class);
//        alarmIntent = PendingIntent.getBroadcast(this, NOTIFICATION_ID, intent, 0);


//        Calendar calendar = Calendar.getInstance();
//        calendar.setTimeInMillis(System.currentTimeMillis());
//        calendar.set(Calendar.HOUR_OF_DAY, 14);
        // Set the alarm to start at 8:30 a.m.

        DateFormat formatFec = new SimpleDateFormat("dd MMMM yyyy HH:mm a", new Locale("es", "ES"));
        Calendar calendarX = Calendar.getInstance();
        try {
            calendarX.setTime(Objects.requireNonNull(formatFec.parse(cita.getFechaCita())));
        } catch (Exception e) {

        }
        Log.d(TAG, "calendarX" + String.valueOf(formatFec.format(calendarX.getTime())));

//        Calendar calendarm = Calendar.getInstance();
//        calendarm.setTimeInMillis(SystemClock.elapsedRealtime());
//        calendarm.set(Calendar.HOUR_OF_DAY, 2);
//        calendarm.set(Calendar.HOUR_OF_DAY, calendarX.getTime().getHours());
//        calendarm.set(Calendar.MINUTE, 39);
//        calendarm.setTimeInMillis(System.currentTimeMillis());
//        calendarm.set(Calendar.HOUR_OF_DAY, 2);
        calendarX.set(Calendar.SECOND, 0);
        calendarX.set(Calendar.MILLISECOND, 0);
//        calendarm.set(Calendar.MONTH, calendarX.getTime().getMonth() + 1);
//        calendarm.set(Calendar.DAY_OF_MONTH, calendarX.getTime().getDay());
//        calendarm.set(Calendar.HOUR_OF_DAY, calendarX.getTime().getHours());
//        calendarm.set(Calendar.MINUTE, 51);
//        calendarm.set(Calendar.MINUTE, calendarX.getTime().getMinutes());
//        calendarm.set(Calendar.MINUTE, calendarX.getTime().getMinutes());

//        alarmMgr.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,
//                SystemClock.elapsedRealtime() + 60 * 1000,
//                alarmIntent);

//        alarmMgr.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + 60 * 1000, notifyPendingIntent);
//        alarmMgr.set(AlarmManager.RTC_WAKEUP, SystemClock.elapsedRealtime(), notifyPendingIntent);
//        alarmMgr.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, calendarm.getTimeInMillis(), notifyPendingIntent);
//        alarmMgr.set(AlarmManager.RTC_WAKEUP, calendarm.getTimeInMillis(), notifyPendingIntent);
        //alarmMgr.set(AlarmManager.RTC_WAKEUP, calendarX.getTimeInMillis(), notifyPendingIntent);

//        alarmMgr.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,
//                calendarm.getTimeInMillis(),
//                alarmIntent);


//        Log.d(TAG, String.valueOf(SystemClock.elapsedRealtime() + 60 * 1000));
//
//        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss.SSS", Locale.US);
//
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTimeInMillis(SystemClock.elapsedRealtime() + (60 * 1000));
//
//        Log.d(TAG, String.valueOf(calendar.getTime()));
//        Log.d(TAG, String.valueOf(formatter.format(calendar.getTime())));


//        alarmMgr.set(AlarmManager.RTC, calendarX.getTimeInMillis(), notifyPendingIntent);
//        alarmMgr.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, calendarX.getTimeInMillis(), alarmIntent);

        Log.d(TAG, "###################################");
//        Log.d(TAG, String.valueOf(calendarX.getTime()));
//        Log.d(TAG, String.valueOf(formatFec.format(calendar.getTime())));
        Log.d(TAG, String.valueOf(formatFec.format(calendarX.getTime())));
//        Log.d(TAG, String.valueOf(formatFec.format(calendarX.getTime())));
//        Log.d(TAG, String.valueOf(formatFec.format(cal.getTime())));
    }


    private void listenerNotificacionesDeLlamadas() {
        registerReceiver(foregroundAcceptCallReceiver, new IntentFilter(ACTION_RESPONDER_LLAMADA_VOZ));
        registerReceiver(foregroundRejectCallReceiver, new IntentFilter(ACTION_RECHAZAR_LLAMADA_VOZ));
        notificacionCustomLlamadas = new ArrayList<>();

        LlamadaDeVozListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Log.d(TAG, "");
                try {
                    LlamadaVoz llamadaVoz = new LlamadaVoz();
                    llamadaVoz = snapshot.getValue(LlamadaVoz.class);
                    if (llamadaVoz.getParticipanteDestiny().getIdParticipante().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                        seleccionarUsuarioParaLlamada(llamadaVoz);
                    }
                } catch (Exception e) {
                    Log.d(TAG, e.toString());
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                String callDelete = snapshot.getKey();
                try {
                    for (NotificacionCustomLlamada nl : notificacionCustomLlamadas) {
                        if (nl.getIdCall().equals(callDelete)) {
                            cancelNotification(nl.getIdNotification());
                            notificacionCustomLlamadas.remove(nl);
                            break;
                        }
                    }
                } catch (Exception e) {
                    Log.d(TAG, e.toString());
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        FirebaseDatabase.getInstance().getReference()
                .child("llamadasDeVoz")
                .addChildEventListener(LlamadaDeVozListener);
    }

    private void listenerNotificacionesDeVideoLlamadas() {

        notificacionCustomVideoLlamadaArrayList = new ArrayList<>();
        registerReceiver(foregrounAcceptVideoCallReceiver, new IntentFilter(ACTION_ACEPTAR_VIDEO_LLAMADA));
        registerReceiver(foregroundRejectVideoCallReceiver, new IntentFilter(ACTION_RECHAZAR_VIDEO_LLAMADA));
//        registerReceiver(foregroundRejectVideoCallReceiver, new IntentFilter(ACTION_RECHAZAR_VIDEO_LLAMADA);

        LlamadaDeVideoListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Log.d(TAG, "");
                try {
                    LlamadaVideo videoLlamada = new LlamadaVideo();
                    videoLlamada = snapshot.getValue(LlamadaVideo.class);
                    if (videoLlamada.getParticipanteDestiny().getIdParticipante().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                        seleccionarUsuarioParaVideoLlamada(videoLlamada);
                    }
                } catch (Exception e) {
                    Log.d(TAG, e.toString());
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                String callDelete = snapshot.getKey();
                try {
                    for (NotificacionCustomVideoLlamada nl : notificacionCustomVideoLlamadaArrayList) {
                        if (nl.getIdCall().equals(callDelete)) {
                            cancelNotification(nl.getIdNotification());
                            notificacionCustomLlamadas.remove(nl);
                            break;
                        }
                    }
                } catch (Exception e) {
                    Log.d(TAG, e.toString());
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        FirebaseDatabase.getInstance().getReference()
                .child("llamadasDeVideo")
                .addChildEventListener(LlamadaDeVideoListener);
    }

    private void showCallNotification(LlamadaVoz llamadaVoz1, Usuario usuarioTo) {
        final int min = 4000;
        final int max = 4999;
        int idNotification = new Random().nextInt((max - min) + 1) + min;


        Intent notifyIntent = new Intent(this, LlamadaVozActivity.class);
        // Set the Activity to start in a new, empty task
        notifyIntent.putExtra("callStatus", 1);
        notifyIntent.putExtra("usuarioFrom", usuarioLocal);
        notifyIntent.putExtra("usuarioTo", usuarioTo);
        notifyIntent.putExtra("llamadaVoz", llamadaVoz1);
        notifyIntent.putExtra("channelNameShare", llamadaVoz1.getId());
        PendingIntent notifyPendingIntent = null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            notifyPendingIntent = PendingIntent.getActivity(this, idNotification, notifyIntent, FLAG_MUTABLE);
        } else {
            notifyPendingIntent = PendingIntent.getActivity(this, idNotification, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        }


        Intent replyIntent = new Intent(ACTION_RESPONDER_LLAMADA_VOZ);
        replyIntent.putExtra("idNotification", idNotification);
        replyIntent.putExtra("usuarioFrom", usuarioLocal);
        replyIntent.putExtra("usuarioTo", usuarioTo);
        replyIntent.putExtra("llamadaVoz", llamadaVoz1);
        replyIntent.putExtra("contestar", true);

        PendingIntent replyPendingIntent = null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            replyPendingIntent =
                    PendingIntent.getBroadcast(getApplicationContext(),
                            idNotification,
                            replyIntent,
                            FLAG_MUTABLE);
        } else {
            replyPendingIntent =
                    PendingIntent.getBroadcast(getApplicationContext(),
                            idNotification,
                            replyIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT);
        }


        NotificationCompat.Action actionResponder =
                new NotificationCompat.Action.Builder(R.drawable.ic_baseline_send_24,
                        "Aceptar", replyPendingIntent)
                        .build();


        Intent rejectIntent = new Intent(ACTION_RECHAZAR_LLAMADA_VOZ);
        rejectIntent.putExtra("idNotification", idNotification);
        rejectIntent.putExtra("usuarioFrom", usuarioLocal);
        rejectIntent.putExtra("usuarioTo", usuarioTo);
        rejectIntent.putExtra("llamadaVoz", llamadaVoz1);
        rejectIntent.putExtra("contestar", false);
        PendingIntent rejectPendingIntent = null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            rejectPendingIntent =
                    PendingIntent.getBroadcast(getApplicationContext(),
                            idNotification,
                            rejectIntent,
                            FLAG_MUTABLE);
        } else {
            rejectPendingIntent =
                    PendingIntent.getBroadcast(getApplicationContext(),
                            idNotification,
                            rejectIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT);
        }


        NotificationCompat.Action actionRechazar =
                new NotificationCompat.Action.Builder(R.drawable.ic_baseline_send_24,
                        "Rechazar", rejectPendingIntent)
                        .build();


        NotificationCompat.Builder notification = null;
        NotificationCompat.MessagingStyle messagingStyle = new NotificationCompat.MessagingStyle("Responder");
        notification = new NotificationCompat.Builder(this, NOTIFICATIONS_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_oficios)
                .setAutoCancel(true)// Quita la notification cunado el usuario la presiona
                //.addAction(action)
                .setContentIntent(notifyPendingIntent)
                .setDeleteIntent(rejectPendingIntent)
                .addAction(actionResponder)
                .addAction(actionRechazar)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_ALL);


        long timeStamp = System.currentTimeMillis();
        NotificationCompat.MessagingStyle.Message message =
                new NotificationCompat.MessagingStyle.Message(
                        "Llamada de voz entrante...",
                        timeStamp,
                        llamadaVoz1.getParticipanteCaller().getNombreParticipante());
//                            not.getFrom() + " " + not.getFrom());
        messagingStyle.addMessage(message);


        notification.setStyle(messagingStyle);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(idNotification, notification.build());

        NotificacionCustomLlamada notificacionCustomLlamada = new NotificacionCustomLlamada();
        notificacionCustomLlamada.setIdNotification(idNotification);
        notificacionCustomLlamada.setLlamadaVoz(llamadaVoz1);
        notificacionCustomLlamada.setIdCall(llamadaVoz1.getId());
        notificacionCustomLlamadas.add(notificacionCustomLlamada);
    }

    private void showVideoCallNotification(LlamadaVideo llamadaVideo, Usuario usuarioTo) {
        final int min = 5000;
        final int max = 5999;
        int idNotification = new Random().nextInt((max - min) + 1) + min;


        Intent notifyIntent = new Intent(this, VideoLlamadaActivity.class);
        // Set the Activity to start in a new, empty task
        notifyIntent.putExtra("callStatus", 1);
        notifyIntent.putExtra("usuarioFrom", usuarioLocal);
        notifyIntent.putExtra("usuarioTo", usuarioTo);
        notifyIntent.putExtra("llamadaVideo", llamadaVideo);
        notifyIntent.putExtra("channelNameShare", llamadaVideo.getId());
        notifyIntent.putExtra("contest", 1);

        PendingIntent notifyPendingIntent = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            notifyPendingIntent = PendingIntent.getActivity(this, idNotification, notifyIntent, FLAG_MUTABLE);
        } else {
            notifyPendingIntent = PendingIntent.getActivity(this, idNotification, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        }

        Intent replyIntent = new Intent(ACTION_ACEPTAR_VIDEO_LLAMADA);
        replyIntent.putExtra("idNotification", idNotification);
        replyIntent.putExtra("usuarioFrom", usuarioLocal);
        replyIntent.putExtra("usuarioTo", usuarioTo);
        replyIntent.putExtra("llamadaVideo", llamadaVideo);
        replyIntent.putExtra("contestar", true);

        PendingIntent replyPendingIntent = null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            replyPendingIntent =
                    PendingIntent.getBroadcast(getApplicationContext(),
                            idNotification,
                            replyIntent,
                            FLAG_MUTABLE);
        } else {
            replyPendingIntent =
                    PendingIntent.getBroadcast(getApplicationContext(),
                            idNotification,
                            replyIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT);
        }


        NotificationCompat.Action actionResponder =
                new NotificationCompat.Action.Builder(R.drawable.ic_baseline_send_24,
                        "Aceptar", replyPendingIntent)
                        .build();


//        Intent ggIntent = new Intent(this, VideoLlamadaActivity.class);
//        // Set the Activity to start in a new, empty task
//        ggIntent.putExtra("callStatus", 1);
//        ggIntent.putExtra("usuarioFrom", usuarioLocal);
//        ggIntent.putExtra("usuarioTo", usuarioTo);
//        ggIntent.putExtra("llamadaVideo", llamadaVideo);
//        ggIntent.putExtra("channelNameShare", llamadaVideo.getId());
//        ggIntent.putExtra("contest", 0);
//
//        PendingIntent ggPendingIntent = PendingIntent.getActivity(this, idNotification, ggIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//

//        Intent replyIntent = new Intent(ACTION_ACEPTAR_VIDEO_LLAMADA);
//        replyIntent.putExtra("idNotification", idNotification);
//        replyIntent.putExtra("usuarioFrom", usuarioLocal);
//        replyIntent.putExtra("usuarioTo", usuarioTo);
//        replyIntent.putExtra("llamadaVideo", llamadaVideo);
////        replyIntent.putExtra("contestar", true);
//        replyIntent.putExtra("channelNameShare", llamadaVideo.getId());
////        replyIntent.putExtra("callStatus", 1);


//        Intent replyIntent = new Intent(ACTION_ACEPTAR_VIDEO_LLAMADA);
//        // Set the Activity to start in a new, empty task
//        replyIntent.putExtra("idNotification", idNotification);
//        replyIntent.putExtra("usuarioFrom", usuarioLocal);
//        replyIntent.putExtra("usuarioTo", usuarioTo);
//        replyIntent.putExtra("llamadaVideo", llamadaVideo);
//
//
//        PendingIntent replyPendingIntent = PendingIntent
//                .getBroadcast(getApplicationContext(), idNotification, replyIntent, PendingIntent.FLAG_UPDATE_CURRENT);


//
//        PendingIntent replyPendingIntent =
//                PendingIntent.getBroadcast(getApplicationContext(),
//                        idNotification,
//                        replyIntent,
//                        PendingIntent.FLAG_UPDATE_CURRENT);


//        NotificationCompat.Action actionResponder =
//                new NotificationCompat.Action.Builder(R.drawable.ic_baseline_send_24,
//                        "Aceptar", ggPendingIntent)
//                        .build();


        Intent rejectIntent = new Intent(ACTION_RECHAZAR_VIDEO_LLAMADA);
        rejectIntent.putExtra("idNotification", idNotification);
        rejectIntent.putExtra("usuarioFrom", usuarioLocal);
        rejectIntent.putExtra("usuarioTo", usuarioTo);
        rejectIntent.putExtra("llamadaVideo", llamadaVideo);
        rejectIntent.putExtra("contestar", false);
        PendingIntent rejectPendingIntent = null;


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            rejectPendingIntent =
                    PendingIntent.getBroadcast(getApplicationContext(),
                            idNotification,
                            rejectIntent,
                            FLAG_MUTABLE);
        } else {
            rejectPendingIntent =
                    PendingIntent.getBroadcast(getApplicationContext(),
                            idNotification,
                            rejectIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT);
        }

        NotificationCompat.Action actionRechazar =
                new NotificationCompat.Action.Builder(R.drawable.ic_baseline_send_24,
                        "Rechazar", rejectPendingIntent)
                        .build();


        NotificationCompat.Builder notification = null;
        NotificationCompat.MessagingStyle messagingStyle = new NotificationCompat.MessagingStyle("Responder");
        notification = new NotificationCompat.Builder(this, NOTIFICATIONS_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_oficios)
                .setAutoCancel(true)// Quita la notification cunado el usuario la presiona
                //.addAction(action)
                .setContentIntent(notifyPendingIntent)
                .setDeleteIntent(rejectPendingIntent)
                .addAction(actionResponder)
                .addAction(actionRechazar)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_ALL);


        long timeStamp = System.currentTimeMillis();
        NotificationCompat.MessagingStyle.Message message =
                new NotificationCompat.MessagingStyle.Message(
                        "Llamada de video entrante...",
                        timeStamp,
                        llamadaVideo.getParticipanteCaller().getNombreParticipante());
//                            not.getFrom() + " " + not.getFrom());
        messagingStyle.addMessage(message);


        notification.setStyle(messagingStyle);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(idNotification, notification.build());

        NotificacionCustomVideoLlamada notificacionCustomVideoLlamada = new NotificacionCustomVideoLlamada();
        notificacionCustomVideoLlamada.setIdNotification(idNotification);
        notificacionCustomVideoLlamada.setVideoLlamada(llamadaVideo);
        notificacionCustomVideoLlamada.setIdCall(llamadaVideo.getId());
        notificacionCustomVideoLlamadaArrayList.add(notificacionCustomVideoLlamada);
    }

    private void loadLocalUser() {
        FirebaseDatabase.getInstance().getReference()
                .child("administrador")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        try {
                            Administrador administrador = snapshot.getValue(Administrador.class);
                            if (administrador != null) {
                                usuarioLocal = administrador;
                            }
                        } catch (Exception e) {

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        FirebaseDatabase.getInstance().getReference()
                .child("empleadores")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        try {
                            Empleador empleador = snapshot.getValue(Empleador.class);
                            if (empleador != null) {
                                usuarioLocal = empleador;
                            }
                        } catch (Exception e) {

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        FirebaseDatabase.getInstance().getReference()
                .child("trabajadores")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        try {
                            Trabajador trabajador = snapshot.getValue(Trabajador.class);
                            if (trabajador != null) {
                                usuarioLocal = trabajador;
                            }

                        } catch (Exception e) {

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void listenerChats() {
        chats = new ArrayList<>();
        Log.d(TAG, "############################################");
        Log.d(TAG, "listenerChats");
        Log.d(TAG, "############################################");
        final Context mContext = this;
        // [START child_event_listener_recycler]
        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildAdded:" + dataSnapshot.getKey());

                // A new comment has been added, add it to the displayed list
                //Comment comment = dataSnapshot.getValue(Comment.class);

                Chat chat = dataSnapshot.getValue(Chat.class);
                for (Participante p : chat.getParticipantes()) {
                    if (p.getIdParticipante().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                        Log.d(TAG, "onChildAdded:" + chat.toString());
                        chats.add(chat);
                        break;
                    }
                }
                // ...
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildChanged:" + dataSnapshot.getKey());

                // A comment has changed, use the key to determine if we are displaying this
                // comment and if so displayed the changed comment.
                //Comment newComment = dataSnapshot.getValue(Comment.class);
                String commentKey = dataSnapshot.getKey();


                // ...
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onChildRemoved:" + dataSnapshot.getKey());

                // A comment has changed, use the key to determine if we are displaying this
                // comment and if so remove it.
                String commentKey = dataSnapshot.getKey();

                // ...
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildMoved:" + dataSnapshot.getKey());

                // A comment has changed position, use the key to determine if we are
                // displaying this comment and if so move it.
                //Comment movedComment = dataSnapshot.getValue(Comment.class);
                String commentKey = dataSnapshot.getKey();

                // ...
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "postComments:onCancelled", databaseError.toException());
//                Toast.makeText(mContext, "Failed to load comments.",
//                        Toast.LENGTH_SHORT).show();
            }
        };
        databaseReference.child("chats").addChildEventListener(childEventListener);
        // [END child_event_listener_recycler]
    }


    private void seleccionarUsuarioParaLlamada(LlamadaVoz llamadaVoz) {

        FirebaseDatabase.getInstance().getReference()
                .child("administrador")
                .child(llamadaVoz.getParticipanteCaller().getIdParticipante())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        try {
                            Administrador administrador = snapshot.getValue(Administrador.class);
                            if (administrador != null) {
                                showCallNotification(llamadaVoz, administrador);
                            }
                        } catch (Exception e) {

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        FirebaseDatabase.getInstance().getReference()
                .child("empleadores")
                .child(llamadaVoz.getParticipanteCaller().getIdParticipante())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        try {
                            Empleador empleador = snapshot.getValue(Empleador.class);
                            if (empleador != null) {
                                showCallNotification(llamadaVoz, empleador);
                            }
                        } catch (Exception e) {

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        FirebaseDatabase.getInstance().getReference()
                .child("trabajadores")
                .child(llamadaVoz.getParticipanteCaller().getIdParticipante())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        try {
                            Trabajador trabajador = snapshot.getValue(Trabajador.class);
                            if (trabajador != null) {
                                showCallNotification(llamadaVoz, trabajador);
                            }
                        } catch (Exception e) {

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void seleccionarUsuarioParaVideoLlamada(LlamadaVideo llamadaVideo) {

        FirebaseDatabase.getInstance().getReference()
                .child("administrador")
                .child(llamadaVideo.getParticipanteCaller().getIdParticipante())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        try {
                            Administrador administrador = snapshot.getValue(Administrador.class);
                            if (administrador != null) {
                                showVideoCallNotification(llamadaVideo, administrador);
                            }
                        } catch (Exception e) {

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        FirebaseDatabase.getInstance().getReference()
                .child("empleadores")
                .child(llamadaVideo.getParticipanteCaller().getIdParticipante())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        try {
                            Empleador empleador = snapshot.getValue(Empleador.class);
                            if (empleador != null) {
                                showVideoCallNotification(llamadaVideo, empleador);
                            }
                        } catch (Exception e) {

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        FirebaseDatabase.getInstance().getReference()
                .child("trabajadores")
                .child(llamadaVideo.getParticipanteCaller().getIdParticipante())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        try {
                            Trabajador trabajador = snapshot.getValue(Trabajador.class);
                            if (trabajador != null) {
                                showVideoCallNotification(llamadaVideo, trabajador);
                            }
                        } catch (Exception e) {

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void seleccionarUsuarioFrom(NotificacionCustom notificacionCustom) {

        FirebaseDatabase.getInstance().getReference()
                .child("administrador")
                .child(notificacionCustom.getMensajeNubes().get(0).getFrom())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        try {
                            Administrador administrador = snapshot.getValue(Administrador.class);
                            if (administrador != null) {
                                showNotification(notificacionCustom, administrador);
                            }
                        } catch (Exception e) {

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        FirebaseDatabase.getInstance().getReference()
                .child("empleadores")
                .child(notificacionCustom.getMensajeNubes().get(0).getFrom())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        try {
                            Empleador empleador = snapshot.getValue(Empleador.class);
                            if (empleador != null) {
                                showNotification(notificacionCustom, empleador);
                            }
                        } catch (Exception e) {

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        FirebaseDatabase.getInstance().getReference()
                .child("trabajadores")
                .child(notificacionCustom.getMensajeNubes().get(0).getFrom())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        try {
                            Trabajador trabajador = snapshot.getValue(Trabajador.class);
                            if (trabajador != null) {
                                showNotification(notificacionCustom, trabajador);
                            }
                        } catch (Exception e) {

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void seleccionarUsuarioToUpdateFrom(NotificacionCustom notificacionCustom) {

        FirebaseDatabase.getInstance().getReference()
                .child("administrador")
                .child(notificacionCustom.getMensajeNubes().get(0).getFrom())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        try {
                            Administrador administrador = snapshot.getValue(Administrador.class);
                            if (administrador != null) {
                                updateNotification(notificacionCustom, administrador);
                            }
                        } catch (Exception e) {

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        FirebaseDatabase.getInstance().getReference()
                .child("empleadores")
                .child(notificacionCustom.getMensajeNubes().get(0).getFrom())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        try {
                            Empleador empleador = snapshot.getValue(Empleador.class);
                            if (empleador != null) {
                                updateNotification(notificacionCustom, empleador);
                            }
                        } catch (Exception e) {

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        FirebaseDatabase.getInstance().getReference()
                .child("trabajadores")
                .child(notificacionCustom.getMensajeNubes().get(0).getFrom())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        try {
                            Trabajador trabajador = snapshot.getValue(Trabajador.class);
                            if (trabajador != null) {
                                updateNotification(notificacionCustom, trabajador);
                            }
                        } catch (Exception e) {

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }


    private void listenerNotifications() {
        registerReceiver(foregroundDeleteNotificationReceiver, new IntentFilter(ACTION_DELETE_NOTIFICATION));
        registerReceiver(foregroundReplyNotificationReceiver, new IntentFilter(ACTION_REPLY_NOTIFICATION));
        Log.d(TAG, "############################################");
        Log.d(TAG, "listenerNotifications");
        Log.d(TAG, "############################################");
        notificacionCustomArrayList = new ArrayList<>();
        final Context mContext = this;
        // [START child_event_listener_recycler]
        childEventListenerNotificaciones = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildAdded NOTIFICACIONES:" + dataSnapshot.getKey());

                // A new comment has been added, add it to the displayed list
                //Comment comment = dataSnapshot.getValue(Comment.class);
                ArrayList<MensajeNube> mensajeNubeArrayList = new ArrayList<>();
                NotificacionCustom notificacionCustom = new NotificacionCustom();
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    MensajeNube mensajeNube = data.getValue(MensajeNube.class);
                    mensajeNubeArrayList.add(mensajeNube);
                    Log.d(TAG, mensajeNube.toString());
                }
                notificacionCustom.setMensajeNubes(mensajeNubeArrayList);
//                showNotification(notificacionCustom);
                usurioBloquedo = myPreferences.getString("idUserBlocking", "");
                Log.d(TAG, "##############################");
                Log.d(TAG, "Usuario bloqueado: " + usurioBloquedo);
                Log.d(TAG, "##############################");


                boolean notificacionesMensajes = defaultSharedPreferences.getBoolean("sync_notificaciones_mensajes", false);
//                boolean notificacionesCitas = defaultSharedPreferences.getBoolean("sync_notificaciones_citas", true);
                Log.d(TAG, "notificacionesMensajes: " + notificacionesMensajes);

//                if (notificacionesMensajes) {
                if (usurioBloquedo.equals(notificacionCustom.getMensajeNubes().get(0).getFrom())) {
                    try {
                        removeNotificationsIncoming(notificacionCustom);
                    } catch (Exception e) {
                        Log.d(TAG, e.toString());
                    }
                } else {
                    try {
                        seleccionarUsuarioFrom(notificacionCustom);

                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e(TAG, e.toString());
                    }
                }
//                }


                // ...
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildChanged NOTIFICACIONES: " + dataSnapshot.getKey());

                // A comment has changed, use the key to determine if we are displaying this
                // comment and if so displayed the changed comment.
                //Comment newComment = dataSnapshot.getValue(Comment.class);
                String commentKey = dataSnapshot.getKey();

                ArrayList<MensajeNube> mensajeNubeArrayList = new ArrayList<>();
                NotificacionCustom notificacionCustom = new NotificacionCustom();

                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    MensajeNube mensajeNube = data.getValue(MensajeNube.class);
                    mensajeNubeArrayList.add(mensajeNube);
                    Log.d(TAG, mensajeNube.toString());
                }
                notificacionCustom.setMensajeNubes(mensajeNubeArrayList);
//                showNotification(notificacionCustom);

                usurioBloquedo = myPreferences.getString("idUserBlocking", "");
                Log.d(TAG, commentKey);
                Log.d(TAG, "##############################");
                Log.d(TAG, "Usuario bloqueado: " + usurioBloquedo);
                Log.d(TAG, "##############################");

                boolean notificacionesMensajes = defaultSharedPreferences.getBoolean("sync_notificaciones_mensajes", false);
//                boolean notificacionesCitas = defaultSharedPreferences.getBoolean("sync_notificaciones_citas", true);
                Log.d(TAG, "notificacionesMensajes: " + notificacionesMensajes);

//                if (notificacionesMensajes) {
                if (usurioBloquedo.equals(notificacionCustom.getMensajeNubes().get(0).getFrom())) {
                    try {
                        removeNotificationsIncoming(notificacionCustom);
                    } catch (Exception e) {
                        Log.d(TAG, e.toString());
                    }
                } else {
                    try {
                        seleccionarUsuarioToUpdateFrom(notificacionCustom);

                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e(TAG, e.toString());
                    }
                }
//                }


                try {
                    for (NotificacionCustom nc : notificacionCustomArrayList) {
                        Log.d(TAG, nc.toString());
                    }
                } catch (Exception e) {
                    Log.e(TAG, e.toString());
                }
                // ...

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onChildRemoved NOTIFICACIONES:" + dataSnapshot.getKey());

                // A comment has changed, use the key to determine if we are displaying this
                // comment and if so remove it.
                String commentKey = dataSnapshot.getKey();
                try {
                    for (NotificacionCustom nc : notificacionCustomArrayList) {
                        if (nc.getIdFrom().equals(commentKey)) {
                            cancelNotification(nc.getIdNotification());
                            notificacionCustomArrayList.remove(nc);
                            break;
                        }
                    }
                } catch (Exception e) {
                    Log.e(TAG, e.toString());
                }
                try {
                    for (NotificacionCustom nc : notificacionCustomArrayList) {
                        Log.d(TAG, nc.toString());
                    }
                } catch (Exception e) {
                    Log.e(TAG, e.toString());
                }

                // ...
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildMoved:" + dataSnapshot.getKey());

                // A comment has changed position, use the key to determine if we are
                // displaying this comment and if so move it.
                //Comment movedComment = dataSnapshot.getValue(Comment.class);
                String commentKey = dataSnapshot.getKey();

                // ...
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "postComments:onCancelled", databaseError.toException());
//                Toast.makeText(mContext, "Failed to load comments.",
//                        Toast.LENGTH_SHORT).show();
            }
        };
        databaseReference.child("notificaciones").
                child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addChildEventListener(childEventListenerNotificaciones);
        // [END child_event_listener_recycler]
    }

    private void removeNotificationsIncoming(NotificacionCustom notificacionCustom) {
        FirebaseDatabase.getInstance().getReference()
                .child("notificaciones")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(notificacionCustom.getMensajeNubes().get(0).getFrom())
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Notificaciones eliminadas");
                        } else {

                        }
                    }
                });
    }

    private void cancelNotification(int idNotification) {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        // notificationId is a unique int for each notification that you must define
        notificationManager.cancel(idNotification);
    }

    private void showNotification(NotificacionCustom notificacionCustom, Usuario usuarioTo) {
        Log.d(TAG, "*********************************");
        Log.d(TAG, "SDK VERSION: " + String.valueOf(Build.VERSION.SDK_INT));
        Log.d(TAG, "SHOW NOTIFICATION");
        Log.d(TAG, "*********************************");
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

//        try {
//
//            // Create an explicit intent for an Activity in your app
//            Intent intent = new Intent(this, MainActivity.class);e
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//            PendingIntent pendingIntent = null;
//
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//                pendingIntent = PendingIntent.getActivity(this, 0, intent, FLAG_MUTABLE);
//            } else {
//                pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
//
//                /*Bandera mala: PendingIntent.FLAG_IMMUTABLE*/
//            }
//            NotificationCompat.Builder notificationx = new NotificationCompat.Builder(this, NOTIFICATIONS_CHANNEL_ID)
//                    .setSmallIcon(R.drawable.ic_oficios)
//                    .setAutoCancel(true)// Quita la notification cunado el usuario la presiona
//                    //.addAction(action)
//                    //.setContentIntent(notifyPendingIntent)
//                    .setContentTitle("PoC Notification")
//                    .setContentText("Proof and concept notification...")
//                    .setContentIntent(pendingIntent)
//                    .setPriority(NotificationCompat.PRIORITY_HIGH)
//                    .setDefaults(NotificationCompat.DEFAULT_ALL);
//            notificationManager.notify(7000, notificationx.build());
//        } catch (Exception e) {
//            e.printStackTrace();
//            Log.d(TAG, e.toString());
//        }

        final int min = 3000;
        final int max = 3999;
        int idNotification = new Random().nextInt((max - min) + 1) + min;

        Chat chatFound = null;
        for (Chat chatAux : chats) {
            for (Participante p : chatAux.getParticipantes()) {
                if (p.getIdParticipante().equals(notificacionCustom.getMensajeNubes().get(0).getFrom())) {
                    //Toast.makeText(getApplicationContext(),chatAux.toString(),Toast.LENGTH_SHORT).show();
                    chatFound = chatAux;
                    break;
                }
            }
        }


        Intent notifyIntent = new Intent(this, IndividualChatActivity.class);
        // Set the Activity to start in a new, empty task

        notifyIntent.putExtra("chat", chatFound);
        notifyIntent.putExtra("usuarioFrom", usuarioLocal);
        notifyIntent.putExtra("notificationIdFrom", notificacionCustom.getMensajeNubes().get(0).getFrom());

        //notifyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        // Create the PendingIntent
//        PendingIntent notifyPendingIntent = PendingIntent.getActivity(this, idNotification, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);/*update*/
        PendingIntent notifyPendingIntent = null;
        Log.d(TAG, "*********************************");
        Log.d(TAG, "SDK VERSION: " + String.valueOf(Build.VERSION.SDK_INT));
        Log.d(TAG, "*********************************");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            notifyPendingIntent = PendingIntent.getActivity(this, idNotification, notifyIntent, FLAG_MUTABLE);
        } else {
            notifyPendingIntent = PendingIntent.getActivity(this, idNotification, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        }


//        NotificationCompat.Builder notificationxu = new NotificationCompat.Builder(this, NOTIFICATIONS_CHANNEL_ID)
//                .setSmallIcon(R.drawable.ic_oficios)
//                .setAutoCancel(true)// Quita la notification cunado el usuario la presiona
//                //.addAction(action)
//                //.setContentIntent(notifyPendingIntent)
//                .setContentTitle("Mrda")
//                .setContentText("Mrda")
//                .setPriority(NotificationCompat.PRIORITY_HIGH)
//                .setDefaults(NotificationCompat.DEFAULT_ALL);
//        notificationManager.notify(7000, notificationxu.build());


        Intent deleteIntent = new Intent(ACTION_DELETE_NOTIFICATION);
        deleteIntent.putExtra("notificationIdFrom", notificacionCustom.getMensajeNubes().get(0).getFrom());
        deleteIntent.putExtra("chat", chatFound);

        //deleteIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent deletePendingIntent = null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            deletePendingIntent =
                    PendingIntent.getBroadcast(getApplicationContext(),
                            idNotification,
                            deleteIntent,
                            FLAG_MUTABLE);
        } else {
            deletePendingIntent =
                    PendingIntent.getBroadcast(getApplicationContext(),
                            idNotification,
                            deleteIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT);
        }

        String replyLabel = getResources().getString(R.string.reply_label);
        RemoteInput remoteInput = new RemoteInput.Builder(KEY_TEXT_REPLY)
                .setLabel(replyLabel)
                .build();


        /*
         * Si reutilizas un PendingIntent, el usuario podría responder a una conversación diferente de la que intenta responder.
         * Debes proporcionar un código de solicitud diferente para cada conversación o un intent que no muestre true cuando
         * llames a equals() en el intent de respuesta de cualquier otra conversación. Con frecuencia, el ID de la conversación
         * se transfiere como parte del paquete de servicios adicionales del intent, pero se ignora cuando llamas a equals().
         * */
        Intent updateIntent = new Intent(ACTION_REPLY_NOTIFICATION);
        updateIntent.putExtra("idNotification", idNotification);
        updateIntent.putExtra("notificationIdFrom", notificacionCustom.getMensajeNubes().get(0).getFrom());
        updateIntent.putExtra("usuarioFrom", usuarioLocal);
        updateIntent.putExtra("chat", chatFound);

        // Build a PendingIntent for the reply action to trigger.
        PendingIntent replyPendingIntent = null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {

            replyPendingIntent =
                    PendingIntent.getBroadcast(getApplicationContext(),
                            idNotification,
                            updateIntent,
                            FLAG_MUTABLE);
        } else {
            replyPendingIntent =
                    PendingIntent.getBroadcast(getApplicationContext(),
                            idNotification,
                            updateIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT);
        }

        NotificationCompat.Action action =
                new NotificationCompat.Action.Builder(R.drawable.ic_baseline_send_24,
                        "Responder", replyPendingIntent)
                        .addRemoteInput(remoteInput)
                        .build();


        NotificationCompat.Builder notification = null;
        NotificationCompat.MessagingStyle messagingStyle = new NotificationCompat.MessagingStyle("Responder");
        notification = new NotificationCompat.Builder(this, NOTIFICATIONS_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_oficios)
                .setAutoCancel(true)// Quita la notification cunado el usuario la presiona
                //.addAction(action)
                .setContentIntent(notifyPendingIntent)
                .setDeleteIntent(deletePendingIntent)
                .addAction(action)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_ALL);

        notificacionCustom.setIdNotification(idNotification);
        notificacionCustom.setIdFrom(usuarioTo.getIdUsuario());

        for (MensajeNube not : notificacionCustom.getMensajeNubes()) {
            Log.d(TAG, not.toString());

            long timeStamp = System.currentTimeMillis();
            NotificationCompat.MessagingStyle.Message message =
                    new NotificationCompat.MessagingStyle.Message(
                            not.getContenido(),
                            timeStamp,
                            usuarioTo.getNombre() + " " + usuarioTo.getApellido());
//                            not.getFrom() + " " + not.getFrom());
            messagingStyle.addMessage(message);


        }
        notification.setStyle(messagingStyle);
        //notificationArrayList.add(notification);
        //notificationsIds.add(idNotification);
        notificacionCustomArrayList.add(notificacionCustom);
        //mensajeNubes.clear();

        // notificationId is a unique int for each notification that you must define

        boolean notificacionesMensajes = defaultSharedPreferences.getBoolean("sync_notificaciones_mensajes", true);

//                boolean notificacionesCitas = defaultSharedPreferences.getBoolean("sync_notificaciones_citas", true);
        Log.d(TAG, "notificacionesMensajes: " + notificacionesMensajes);

        if (notificacionesMensajes) {
            Log.d(TAG, "#################################");
            Log.d(TAG, "SHOW NOTIFICATION");
            Log.d(TAG, "ID: " + String.valueOf(idNotification));
            Log.d(TAG, "#################################");
            notificationManager.notify(idNotification, notification.build());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//                NotificationCompat.Builder notificationx = new NotificationCompat.Builder(this, NOTIFICATIONS_CHANNEL_ID)
//                        .setSmallIcon(R.drawable.ic_oficios)
//                        .setAutoCancel(true)// Quita la notification cunado el usuario la presiona
//                        //.addAction(action)
//                        .setContentTitle("Mrda")
//                        .setContentText("Mrda")
//                        .setPriority(NotificationCompat.PRIORITY_HIGH)
//                        .setDefaults(NotificationCompat.DEFAULT_ALL);
//                notificationManager.notify(idNotification, notificationx.build());

            } else {
//                notificationManager.notify(idNotification, notification.build());

            }
        }

//        notificationManager.notify(idNotification, notification.build());


    }

    private void updateNotification(NotificacionCustom notificacionCustom, Usuario usuarioTo) {

        Log.d(TAG, "@@@@####################");
        Log.d(TAG, "updateNotification");
        Log.d(TAG, "@@@@####################");

//        final int min = 3000;
//        final int max = 3999;
//        int idNotification = new Random().nextInt((max - min) + 1) + min;
//        int idNotification = 1500;
        int indexUpdateArrayNot = 0;
        int idNotificationUpdate = 0;
        for (NotificacionCustom notificacionC : notificacionCustomArrayList) {
//            Log.d(TAG, notificacionC.toString());
            Log.d(TAG, notificacionC.getIdFrom());
            Log.d(TAG, usuarioTo.getIdUsuario());
            Log.d(TAG, String.valueOf(notificacionC.getIdNotification()));

            if (notificacionC.getIdFrom().equals(usuarioTo.getIdUsuario())) {
                idNotificationUpdate = notificacionC.getIdNotification();
                notificacionCustom.setIdFrom(notificacionC.getIdFrom());
                notificacionCustom.setIdNotification(idNotificationUpdate);
                break;
            }

            indexUpdateArrayNot++;
        }

        Log.d(TAG, String.valueOf(idNotificationUpdate));
        Log.d(TAG, String.valueOf(indexUpdateArrayNot));


        Chat chatFound = null;
        for (Chat chatAux : chats) {
            for (Participante p : chatAux.getParticipantes()) {
                if (p.getIdParticipante().equals(notificacionCustom.getMensajeNubes().get(0).getFrom())) {
                    //Toast.makeText(getApplicationContext(),chatAux.toString(),Toast.LENGTH_SHORT).show();
                    chatFound = chatAux;
                    Log.d(TAG, String.valueOf(chatFound));

                    break;
                }
            }
        }


        Intent notifyIntent = new Intent(this, IndividualChatActivity.class);
        // Set the Activity to start in a new, empty task

        notifyIntent.putExtra("chat", chatFound);
        notifyIntent.putExtra("usuarioFrom", usuarioLocal);
        notifyIntent.putExtra("notificationIdFrom", notificacionCustom.getMensajeNubes().get(0).getFrom());

        //notifyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        // Create the PendingIntent
        PendingIntent notifyPendingIntent = null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            notifyPendingIntent = PendingIntent.getActivity(this, idNotificationUpdate, notifyIntent, FLAG_MUTABLE);
        } else {
            notifyPendingIntent = PendingIntent.getActivity(this, idNotificationUpdate, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            /*Bandera mala: PendingIntent.FLAG_IMMUTABLE*/
//
        }

        Intent deleteIntent = new Intent(ACTION_DELETE_NOTIFICATION);
        deleteIntent.putExtra("notificationIdFrom", notificacionCustom.getMensajeNubes().get(0).getFrom());
        //deleteIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent deletePendingIntent = null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            deletePendingIntent =
                    PendingIntent.getBroadcast(getApplicationContext(),
                            idNotificationUpdate,
                            deleteIntent,
                            FLAG_MUTABLE);
        } else {
            deletePendingIntent =
                    PendingIntent.getBroadcast(getApplicationContext(),
                            idNotificationUpdate,
                            deleteIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT);
            /*Bandera mala: PendingIntent.FLAG_IMMUTABLE*/
//
        }


        String replyLabel = getResources().getString(R.string.reply_label);
        RemoteInput remoteInput = new RemoteInput.Builder(KEY_TEXT_REPLY)
                .setLabel(replyLabel)
                .build();


        /*
         * Si reutilizas un PendingIntent, el usuario podría responder a una conversación diferente de la que intenta responder.
         * Debes proporcionar un código de solicitud diferente para cada conversación o un intent que no muestre true cuando
         * llames a equals() en el intent de respuesta de cualquier otra conversación. Con frecuencia, el ID de la conversación
         * se transfiere como parte del paquete de servicios adicionales del intent, pero se ignora cuando llamas a equals().
         * */
        Intent updateIntent = new Intent(ACTION_REPLY_NOTIFICATION);
        updateIntent.putExtra("idNotification", idNotificationUpdate);
        updateIntent.putExtra("notificationIdFrom", notificacionCustom.getMensajeNubes().get(0).getFrom());
        updateIntent.putExtra("usuarioFrom", usuarioLocal);
        updateIntent.putExtra("chat", chatFound);

        // Build a PendingIntent for the reply action to trigger.
        PendingIntent replyPendingIntent = null;


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            replyPendingIntent =
                    PendingIntent.getBroadcast(getApplicationContext(),
                            idNotificationUpdate,
                            updateIntent,
                            FLAG_MUTABLE);
        } else {
            replyPendingIntent =
                    PendingIntent.getBroadcast(getApplicationContext(),
                            idNotificationUpdate,
                            updateIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT);
            /*Bandera mala: PendingIntent.FLAG_IMMUTABLE*/
//
        }

        NotificationCompat.Action action =
                new NotificationCompat.Action.Builder(R.drawable.ic_baseline_send_24,
                        "Responder", replyPendingIntent)
                        .addRemoteInput(remoteInput)
                        .build();


        NotificationCompat.Builder notification = null;
        NotificationCompat.MessagingStyle messagingStyle = new NotificationCompat.MessagingStyle("Responder");
        notification = new NotificationCompat.Builder(this, NOTIFICATIONS_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_oficios)
                .setAutoCancel(true)// Quita la notification cunado el usuario la presiona
                //.addAction(action)
                .setContentIntent(notifyPendingIntent)
                .setDeleteIntent(deletePendingIntent)
                .addAction(action)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_ALL);

        notificacionCustom.setIdNotification(idNotificationUpdate);

        for (MensajeNube not : notificacionCustom.getMensajeNubes()) {
            Log.d(TAG, not.toString());

            long timeStamp = System.currentTimeMillis();
            NotificationCompat.MessagingStyle.Message message =
                    new NotificationCompat.MessagingStyle.Message(
                            not.getContenido(),
                            timeStamp,
                            usuarioTo.getNombre() + " " + usuarioTo.getApellido());
//                            not.getFrom() + " " + not.getFrom());
            messagingStyle.addMessage(message);


        }
        notification.setStyle(messagingStyle);
        //notificationArrayList.add(notification);
        //notificationsIds.add(idNotification);
        notificacionCustomArrayList.set(indexUpdateArrayNot, notificacionCustom);
        //mensajeNubes.clear();
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        // notificationId is a unique int for each notification that you must define
//        notificationManager.notify(indexUpdateNot, notification.build());
        boolean notificacionesMensajes = defaultSharedPreferences.getBoolean("sync_notificaciones_mensajes", true);
//                boolean notificacionesCitas = defaultSharedPreferences.getBoolean("sync_notificaciones_citas", true);
        Log.d(TAG, "notificacionesMensajes: " + notificacionesMensajes);

        if (notificacionesMensajes) {
            Log.d(TAG, "#################################");
            Log.d(TAG, "UPDATE NOTIFICATION");
            Log.d(TAG, "ID: " + String.valueOf(idNotificationUpdate));
            Log.d(TAG, "#################################");
            notificationManager.notify(idNotificationUpdate, notification.build());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//                NotificationCompat.Builder notificationx = new NotificationCompat.Builder(this, NOTIFICATIONS_CHANNEL_ID)
//                        .setSmallIcon(R.drawable.ic_oficios)
//                        .setAutoCancel(true)// Quita la notification cunado el usuario la presiona
//                        //.addAction(action)
//                        .setContentTitle("Mrda")
//                        .setContentText("Mrda")
//                        .setPriority(NotificationCompat.PRIORITY_HIGH)
//                        .setDefaults(NotificationCompat.DEFAULT_ALL);
//                notificationManager.notify(indexUpdateNot, notificationx.build());

            } else {
//                notificationManager.notify(indexUpdateNot, notification.build());

            }
        }


    }

    @Override
    public void onDestroy() {

        FirebaseDatabase.getInstance().getReference()
                .child("notificaciones")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .removeEventListener(childEventListenerNotificaciones);

        FirebaseDatabase.getInstance().getReference()
                .child("llamadasDeVoz")
                .removeEventListener(LlamadaDeVozListener);

        FirebaseDatabase.getInstance().getReference()
                .child("llamadasDeVideo")
                .removeEventListener(LlamadaDeVideoListener);

        FirebaseDatabase.getInstance().getReference()
                .child("citas")
                .removeEventListener(childEventListenerCitasTrabajo);

        unregisterReceiver(foregroundDeleteNotificationReceiver);
        unregisterReceiver(foregroundReplyNotificationReceiver);
        unregisterReceiver(foregroundAcceptCallReceiver);
        unregisterReceiver(foregroundRejectCallReceiver);
        unregisterReceiver(foregrounAcceptVideoCallReceiver);
        unregisterReceiver(foregroundRejectVideoCallReceiver);
//        Toast.makeText(this, "Service destroy.", Toast.LENGTH_SHORT).show();
        // 1. Instantiate an <code><a href="/reference/android/app/AlertDialog.Builder.html">AlertDialog.Builder</a></code> with its constructor

    }


}