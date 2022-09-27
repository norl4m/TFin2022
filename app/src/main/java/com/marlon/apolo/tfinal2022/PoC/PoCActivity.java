package com.marlon.apolo.tfinal2022.PoC;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.Person;
import androidx.core.app.RemoteInput;
import androidx.core.content.LocusIdCompat;
import androidx.core.graphics.drawable.IconCompat;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.marlon.apolo.tfinal2022.R;
import com.marlon.apolo.tfinal2022.citasTrabajo.AlarmReceiver;
import com.marlon.apolo.tfinal2022.individualChat.view.IndividualChatActivity;
import com.marlon.apolo.tfinal2022.model.Cita;
import com.marlon.apolo.tfinal2022.serviciosSegundoPlano.offlaneService.AcceptCallReceiver;
import com.marlon.apolo.tfinal2022.videoLlamada.VideoLlamadaActivity;

import org.checkerframework.checker.units.qual.C;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

public class PoCActivity extends AppCompatActivity {

    private static final String CHANNEL_ID = "CHANNEL_POC";
    private static final String ACTION_ACCEPT_CALL = "ACCEPT_CALL";


    private static final String MESSAGE_CHANNEL_ID = "MESSAGE_CHANNEL";
    String GROUP_KEY_MESSAGES = "GROUP_KEY_MESSAGES";
    //use constant ID for notification used as group summary
    int SUMMARY_MESSAGES = 1000;

    private static final String CALL_CHANNEL_ID = "CALL_CHANNEL";
    private static final String APPOINTMENT_CHANNEL_ID = "APPOINTMENT_CHANNEL";
    private NotificationChannel notificationChannelHigh;
    private NotificationChannel notificationChannelMessages;
    private NotificationChannel notificationChannelCalls;
    private NotificationChannel notificationChannelAppointments;
    private IconCompat iconCompat;
    // Notification ID.
    private static final int NOTIFICATION_ID = 1700;
    // Notification channel ID.
    private static final String PRIMARY_CHANNEL_ID = "primary_notification_channelx";
    private NotificationManagerCompat mNotificationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_po_cactivity);


//        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mNotificationManager = NotificationManagerCompat.from(this);

        // Create the notification channel.
        createNotificationChannel();

        ToggleButton alarmToggle = findViewById(R.id.alarmToggle);

        // Set up the Notification Broadcast Intent.
        Intent notifyIntent = new Intent(this, AlarmReceiver.class);
        notifyIntent.putExtra("random", NOTIFICATION_ID);
        Cita cita = new Cita();
        cita.setIdCita("AAAAAAAAAAAAAAAAAAAAAAA");
        cita.setNombreEmpleador("UUUUUUUU");
        cita.setNombreTrabajador("UUUUUUUU");
        cita.setFrom("UUUUUUUU");
        cita.setTo("UUUUUUUU");
        cita.setFechaCita("16 agosto 2022 2:15 a. m.");
        cita.setItems(null);
//        notifyIntent.putExtra("cita", cita);
        notifyIntent.putExtra("idCita", cita.getIdCita());
        notifyIntent.putExtra("nT", cita.getNombreTrabajador());
        notifyIntent.putExtra("nE", cita.getNombreEmpleador());
        notifyIntent.putExtra("idFrom", cita.getFrom());
        notifyIntent.putExtra("idTo", cita.getTo());
        notifyIntent.putExtra("fec", cita.getFechaCita());
        notifyIntent.putExtra("random", NOTIFICATION_ID);
        boolean alarmUp = (PendingIntent.getBroadcast(this, NOTIFICATION_ID, notifyIntent, PendingIntent.FLAG_NO_CREATE) != null);
        alarmToggle.setChecked(alarmUp);

        final PendingIntent notifyPendingIntent = PendingIntent.getBroadcast
                (this, NOTIFICATION_ID, notifyIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);

        final AlarmManager alarmManager = (AlarmManager) getSystemService
                (ALARM_SERVICE);

        // Set the click listener for the toggle button.
        alarmToggle.setOnCheckedChangeListener
                (new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged
                            (CompoundButton buttonView, boolean isChecked) {
                        String toastMessage;
                        if (isChecked) {

//                            long repeatInterval = AlarmManager.INTERVAL_FIFTEEN_MINUTES;
                            long repeatInterval = 800000L;
//                            long repeatInterval = 0;

                            long triggerTime = SystemClock.elapsedRealtime()
                                    + repeatInterval;

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
                            cal.setTime(d);
                            cal.setTime(new Date());
                            if (alarmManager != null) {
//                                objCalendar = Calendar.getInstance();
//                                alarmManager.setInexactRepeating
//                                        (AlarmManager.ELAPSED_REALTIME_WAKEUP,
//                                                triggerTime, repeatInterval,
//                                                notifyPendingIntent);
                                alarmManager.set(AlarmManager.RTC,
                                        cal.getTimeInMillis(),
                                        notifyPendingIntent);
                            }

                            String dateStr1 = sdf.format(cal.getTime());

//                            toastMessage = getString(R.string.alarm_on_toast) + objCalendar.getTime().toLocaleString();
                            toastMessage = getString(R.string.alarm_on_toast) + dateStr1;
                            Log.d("PocActivity", toastMessage);
                            Log.d("PocActivity", String.valueOf(alarmUp));

                        } else {
                            // Cancel notification if the alarm is turned off.
                            mNotificationManager.cancelAll();
                            Log.d("PocActivity", String.valueOf(alarmUp));

                            if (alarmManager != null) {
                                alarmManager.cancel(notifyPendingIntent);
                            }
                            // Set the toast message for the "off" case.
                            toastMessage = getString(R.string.alarm_off_toast);
                            Log.d("PocActivity", String.valueOf(alarmUp));

                        }

                        // Show a toast to say the alarm is turned on or off.
                        Toast.makeText(getApplicationContext(), toastMessage,
                                Toast.LENGTH_SHORT).show();
                    }
                });


    }


    /**
     * Creates a Notification channel, for OREO and higher.
     */
    public void createNotificationChannel() {

        // Create a notification manager object.
//        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mNotificationManager = NotificationManagerCompat.from(this);

        // Notification channels are only available in OREO and higher.
        // So, add a check on SDK version.
        if (android.os.Build.VERSION.SDK_INT >=
                android.os.Build.VERSION_CODES.O) {

            // Create the NotificationChannel with all the parameters.
            NotificationChannel notificationChannel = new NotificationChannel
                    (PRIMARY_CHANNEL_ID,
                            "No se por que chucha no vale",
                            NotificationManager.IMPORTANCE_HIGH);

            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setDescription("Notifies every aaaa minutes to ");
            mNotificationManager.createNotificationChannel(notificationChannel);
        }
    }

//    private void createNotificationChannel() {
//        // Create the NotificationChannel, but only on API 26+ because
//        // the NotificationChannel class is new and not in the support library
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            CharSequence name = "PoC Channel";
//            String description = "PoC notification";
////            int importance = NotificationManager.IMPORTANCE_DEFAULT;
//            int importance = NotificationManager.IMPORTANCE_HIGH;
//            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
//            channel.setDescription(description);
//            //channel.setShowBadge(true);
//            channel.enableVibration(true);
//            channel.setLockscreenVisibility(NotificationCompat.VISIBILITY_PUBLIC);
//            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//            channel.setSound(defaultSoundUri, null);
//
//            // Register the channel with the system; you can't change the importance
//            // or other notification behaviors after this
////            NotificationManager notificationManager = getSystemService(NotificationManager.class);
//            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//            notificationManager.createNotificationChannel(channel);
//        }
//    }


    public void showNormalNotification(View view) {
        int notificationId = 10;
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_oficios)
                .setContentTitle("NotificationCompat.PRIORITY_DEFAULT")
                .setContentText("textContent")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setCategory(NotificationCompat.CATEGORY_CALL);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        notificationManagerCompat.notify(notificationId, builder.build());
//
//        // the NotificationChannel class is new and not in the support library
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            notificationManager.notify(notificationId, builder.build());
//        } else {
//            // notificationId is a unique int for each notification that you must define
//            notificationManagerCompat.notify(notificationId, builder.build());
//        }

    }

    public void showAlertNotification(View view) {
        int notificationId = 11;
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_oficios)
                .setContentTitle(String.format(Locale.US, "Priority: %d", NotificationCompat.PRIORITY_MAX))
                .setContentText(String.format(Locale.US, "Category: %s", NotificationCompat.CATEGORY_MESSAGE))
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setVibrate(new long[]{100, 250, 100, 500});
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        notificationManagerCompat.notify(notificationId, builder.build());

//        // the NotificationChannel class is new and not in the support library
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            notificationManager.notify(notificationId, builder.build());
//        } else {
//            // notificationId is a unique int for each notification that you must define
//            notificationManagerCompat.notify(notificationId, builder.build());
//        }

    }

    public void showBubbleScreenNotification(View view) {
        int notificationId = 12;

        // Create bubble intent
        Intent target = new Intent(getApplicationContext(), IndividualChatActivity.class);
        PendingIntent bubbleIntent =
                PendingIntent.getActivity(getApplicationContext(), 0, target, 0 /* flags */);

        String CATEGORY_TEXT_SHARE_TARGET =
                "com.example.category.IMG_SHARE_TARGET";

        Person chatPartner = new Person.Builder()
                .setName("Chat partner")
                .setImportant(true)
                .build();

// Create sharing shortcut
        String shortcutId = "asdased3";
//        ShortcutInfoCompat shortcut = new ShortcutInfoCompat.Builder(getApplicationContext(), shortcutId)
//                .setCategories(Collections.singleton(CATEGORY_TEXT_SHARE_TARGET))
////                        .setIntent(Intent(Intent.ACTION_DEFAULT))
//                .setLongLived(true)
//                .setShortLabel(chatPartner.getName()).build();

// Create bubble metadata
        NotificationCompat.BubbleMetadata bubbleData =
                new NotificationCompat.BubbleMetadata.Builder(bubbleIntent, IconCompat.createWithResource(getApplicationContext(), R.drawable.ic_oficios))
                        .setDesiredHeight(600)
                        .build();

// Create notification, referencing the sharing shortcut
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
//                        .setContentIntent(contentIntent)
                        .setContentTitle("Marlon Apolo")
                        .setContentText("Buenas tardes me podría ayudar?...")
                        .setSmallIcon(R.drawable.ic_oficios)
                        .setBubbleMetadata(bubbleData)
                        .setShortcutId(shortcutId)
                        .addPerson(chatPartner);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        notificationManagerCompat.notify(notificationId, builder.build());

//        // the NotificationChannel class is new and not in the support library
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            notificationManager.notify(notificationId, builder.build());
//        } else {
//            // notificationId is a unique int for each notification that you must define
//           // notificationManagerCompat.notify(notificationId, builder.build());
//        }

    }

    private static Bitmap drawableToBitmap(final Drawable drawable, final Context context) {

        final float screenDensity = context.getResources().getDisplayMetrics().density;
        float ADAPTIVE_ICON_SIZE_DP = 10;
        float ADAPTIVE_ICON_OUTER_SIDES_DP = 10;

        final int adaptiveIconSize = Math.round(ADAPTIVE_ICON_SIZE_DP * screenDensity);
        final int adaptiveIconOuterSides = Math.round(ADAPTIVE_ICON_OUTER_SIDES_DP * screenDensity);

        final Bitmap bitmap = Bitmap.createBitmap(adaptiveIconSize, adaptiveIconSize, Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(adaptiveIconOuterSides, adaptiveIconOuterSides, adaptiveIconSize - adaptiveIconOuterSides,
                adaptiveIconSize - adaptiveIconOuterSides);
        drawable.draw(canvas);
        return bitmap;
    }

    public void showFullScreenNotification(View view) {
        int notificationId = 12;
        IconCompat icon = IconCompat.createWithBitmap(drawableToBitmap(getApplicationContext().getResources().getDrawable(R.drawable.ic_oficios), getApplicationContext()));
        Person user = new Person.Builder().setName("Marlon Apolo").build();
        Person person = new Person.Builder().setName("Marlon Apolo").setIcon(icon).build();
        //val contentUri = "https://android.example.com/chat/${chat.contact.id}".toUri();

        Intent snoozeIntent = new Intent(this, AcceptCallReceiver.class);
        snoozeIntent.setAction(ACTION_ACCEPT_CALL);
        snoozeIntent.putExtra("EXTRA_NOTIFICATION_ID", notificationId);
        PendingIntent pendingIntent =
                PendingIntent.getBroadcast(this, notificationId, snoozeIntent, 0);

//        val pendingIntent = PendingIntent.getActivity(
//                context,
//                REQUEST_BUBBLE,
//                // Launch BubbleActivity as the expanded bubble.
//                Intent(context, BubbleActivity::class.java)
//                .setAction(Intent.ACTION_VIEW)
//                .setData(contentUri),
//                flagUpdateCurrent(mutable = true)
//        )

        // Let's add some more content to the notification in case it falls back to a normal
        // notification.
        NotificationCompat.MessagingStyle messagingStyle = new NotificationCompat.MessagingStyle(user);
        long timeStamp = System.currentTimeMillis();
        NotificationCompat.MessagingStyle.Message message = new NotificationCompat.MessagingStyle.Message("Contenido", timeStamp, user);
        messagingStyle.addMessage(message);

        //        val lastId = chat.messages.last().id
//        for (message in chat.messages) {
//            val m = NotificationCompat.MessagingStyle.Message(
//                    message.text,
//                    message.timestamp,
//            if (message.isIncoming) person else null
//            ).apply {
//                if (message.photoUri != null) {
//                    setData(message.photoMimeType, message.photoUri)
//                }
//            }
//            if (message.id < lastId) {
//                messagingStyle.addHistoricMessage(m)
//            } else {
//                messagingStyle.addMessage(m)
//            }
//        }

        Intent target = new Intent(getApplicationContext(), VideoLlamadaActivity.class);
        PendingIntent bubbleIntent =
                PendingIntent.getActivity(getApplicationContext(), 0, target, 0 /* flags */);

        NotificationCompat.BubbleMetadata bubbleData =
                new NotificationCompat.BubbleMetadata.Builder(bubbleIntent,
                        IconCompat.createWithResource(getApplicationContext(),
                                R.drawable.ic_oficios))
                        .setDesiredHeight(600)
                        .build();

        String shortcutId = "asd43r4tsdf45twsf43rtfszdcv";
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_oficios)
                .setContentTitle(String.format("%s %s", "Marlon", "Apolo"))
//                .setContentText(mensajeNubeArrayList.toString())
                .setAutoCancel(true)// Quita la notification cunado el usuario la presiona
                //.addAction(action)
                //.setContentIntent(pendingIntentChat)
                //.setDeleteIntent(deletePendingIntent)
                .setCategory(Notification.CATEGORY_MESSAGE)
                .setShortcutId(shortcutId)
                .setLocusId(new LocusIdCompat(shortcutId))
//                .setLocusId(LocusIdCompat(chat.contact.shortcutId))
                .addPerson(person)
                .setShowWhen(true)
                .setBubbleMetadata(bubbleData)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_ALL);


//        NotificationCompat builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
//                // A notification can be shown as a bubble by calling setBubbleMetadata()
//                .setBubbleMetadata(
//                        NotificationCompat.BubbleMetadata.Builder(pendingIntent, icon)
//                                // The height of the expanded bubble.
//                                .setDesiredHeight(context.resources.getDimensionPixelSize(R.dimen.bubble_height))
//                                .apply {
//            // When the bubble is explicitly opened by the user, we can show the bubble
//            // automatically in the expanded state. This works only when the app is in
//            // the foreground.
//            if (fromUser) {
//                setAutoExpandBubble(true)
//            }
//            if (fromUser || update) {
//                setSuppressNotification(true)
//            }
//        }
//                    .build()
//            )
//        // The user can turn off the bubble in system settings. In that case, this notification
//        // is shown as a normal notification instead of a bubble. Make sure that this
//        // notification works as a normal notification as well.
//            .setContentTitle(chat.contact.name)
//                .setSmallIcon(R.drawable.ic_message)
//                .setCategory(Notification.CATEGORY_MESSAGE)
//                .setShortcutId(chat.contact.shortcutId)
//                // This ID helps the intelligence services of the device to correlate this notification
//                // with the corresponding dynamic shortcut.
//                .setLocusId(LocusIdCompat(chat.contact.shortcutId))
//                .addPerson(person)
//                .setShowWhen(true)
//                // The content Intent is used when the user clicks on the "Open Content" icon button on
//                // the expanded bubble, as well as when the fall-back notification is clicked.
//                .setContentIntent(
//                        PendingIntent.getActivity(
//                                context,
//                                REQUEST_CONTENT,
//                                Intent(context, MainActivity:: class.java)
//                        .setAction(Intent.ACTION_VIEW)
//                .setData(contentUri),
//                flagUpdateCurrent(mutable = false)
//                )
//            )
//        // Direct Reply
//            .addAction(
//                NotificationCompat.Action
//                        .Builder(
//                                IconCompat.createWithResource(context, R.drawable.ic_send),
//                                context.getString(R.string.label_reply),
//                                PendingIntent.getBroadcast(
//                                        context,
//                                        REQUEST_CONTENT,
//                                        Intent(context, ReplyReceiver:: class.java).
//        setData(contentUri),
//                flagUpdateCurrent(mutable = true)
//                        )
//                    )
//                    .addRemoteInput(
//                RemoteInput.Builder(ReplyReceiver.KEY_TEXT_REPLY)
//                        .setLabel(context.getString(R.string.hint_input))
//                        .build()
//        )
//                .setAllowGeneratedReplies(true)
//                .build()
//            )
//        // Let's add some more content to the notification in case it falls back to a normal
//        // notification.
//            .setStyle(messagingStyle)
//                .setWhen(chat.messages.last().timestamp)
//        // Don't sound/vibrate if an update to an existing notification.
//        if (update) {
//            builder.setOnlyAlertOnce(true)
//        }
//        notificationManager.notify(chat.contact.id.toInt(), builder.build())

        builder.setStyle(messagingStyle);
        builder.setOnlyAlertOnce(true);


//        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
//        notificationManagerCompat.notify(notificationId, builder.build());

        createNotif();
        //createHighNotification();
    }

    private void createNotif() {
        String id = "my_channel_id_01";

        Intent notificationIntent = new Intent(this, VideoLlamadaActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, id)
                .setSmallIcon(R.drawable.ic_oficios)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_oficios))
                .setStyle(new NotificationCompat.BigPictureStyle()
                        .bigPicture(BitmapFactory.decodeResource(getResources(), R.drawable.ic_oficios))
                        .bigLargeIcon(null))
                .setContentTitle("Title")
                .setContentText("Your text description")
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setVibrate(new long[]{100, 1000, 200, 340})
                .setAutoCancel(false)//true touch on notificaiton menu dismissed, but swipe to dismiss
                .setTicker("Nofiication");
        builder.setContentIntent(contentIntent);
        NotificationManagerCompat m = NotificationManagerCompat.from(getApplicationContext());
        //id to generate new notification in list notifications menu
        m.notify(new Random().nextInt(), builder.build());

    }


    private void createHighNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Channel High";
            String description = "Notificaciones de alta importancia";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            notificationChannelHigh = new NotificationChannel("CHANNEL_HIGH", name, importance);
            notificationChannelHigh.setDescription(description);
            //channel.setShowBadge(true);
            notificationChannelHigh.enableVibration(true);
            notificationChannelHigh.setLockscreenVisibility(NotificationCompat.VISIBILITY_PUBLIC);
            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            notificationChannelHigh.setSound(defaultSoundUri, null);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(notificationChannelHigh);
        }
    }

    private void createHighNotification() {
        int notificationId = 13;
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "CHANNEL_HIGH")
//                .setSmallIcon(R.drawable.ic_oficios)
//                .setContentTitle("textTitle")
//                .setContentText("textContent")
//                .setPriority(NotificationCompat.PRIORITY_MAX)
//                .setCategory(NotificationCompat.CATEGORY_MESSAGE);


        NotificationCompat.Builder publicBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_oficios)
                .setContentTitle("Alternative notification")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);


        String shortcutId = "asd43r4tsdf45twsf43rtfszdcv";
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "CHANNEL_HIGH")
                .setSmallIcon(R.drawable.ic_oficios)
                .setContentTitle(String.format("%s %s", "Marlon", "Apolo"))
                .setContentText("Hola...")
                .setAutoCancel(true)// Quita la notification cunado el usuario la presiona
                //.addAction(action)
                //.setContentIntent(pendingIntentChat)
                //.setDeleteIntent(deletePendingIntent)
                .setCategory(Notification.CATEGORY_MESSAGE)
                .setShortcutId(shortcutId)
                .setLocusId(new LocusIdCompat(shortcutId))
//                .setLocusId(LocusIdCompat(chat.contact.shortcutId))
//                .addPerson(person)
//                .setShowWhen(true)
//                .setBubbleMetadata(bubbleData)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setPublicVersion(publicBuilder.build());


        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(notificationId, builder.build());


    }

//    private void createMessageNotificationChannel() {
//        // Create the NotificationChannel, but only on API 26+ because
//        // the NotificationChannel class is new and not in the support library
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            CharSequence name = "Mensajes";
//            String description = "Notificaciones de mensajes";
//            int importance = NotificationManager.IMPORTANCE_DEFAULT;
//            NotificationChannel channel = new NotificationChannel(MESSAGE_CHANNEL_ID, name, importance);
//            channel.setDescription(description);
//            // Register the channel with the system; you can't change the importance
//            // or other notification behaviors after this
//            NotificationManager notificationManager = getSystemService(NotificationManager.class);
//            notificationManager.createNotificationChannel(channel);
//        }
//    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public void showConfigChannel(View view) {

        Intent intent = new Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS);
        intent.putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
        intent.putExtra(Settings.EXTRA_CHANNEL_ID, notificationChannelHigh.getId());
        startActivity(intent);

    }

    public void showHighNotification(View view) {
        createHighNotification();
    }

    private void createMessageNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Notificaciones de mensajes";
            String description = "Notificaciones de mensajes desde Firebase";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            notificationChannelMessages = new NotificationChannel(MESSAGE_CHANNEL_ID, name, importance);
            notificationChannelMessages.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(notificationChannelMessages);
        }
    }

    public void showNotificationMessage(View view) {
        int notificationId = 1501;
        // Create an explicit intent for an Activity in your app
        Intent intent = new Intent(this, PoCActivity2.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, notificationId, intent, 0);

        String ACTION_REPLY_NOTIFICATION = "ACTION_REPLY_NOTIFICATION";
        ReplyBroadcastReceiver replyBroadcastReceiver = new ReplyBroadcastReceiver();
        registerReceiver(replyBroadcastReceiver, new IntentFilter(ACTION_REPLY_NOTIFICATION));


        // Key for the string that's delivered in the action's intent.
        String KEY_TEXT_REPLY = "key_text_reply";

        String replyLabel = getResources().getString(R.string.reply_label);
        RemoteInput remoteInput = new RemoteInput.Builder(KEY_TEXT_REPLY)
                .setLabel(replyLabel)
                .build();


//        Intent snoozeIntent = new Intent(this, ReplyBroadcastReceiver.class);
        Intent replyIntent = new Intent(ACTION_REPLY_NOTIFICATION);
        replyIntent.setAction(ACTION_REPLY_NOTIFICATION);
        replyIntent.putExtra("EXTRA_NOTIFICATION_ID", notificationId);
//        PendingIntent snoozePendingIntent = PendingIntent.getBroadcast(this, notificationId, snoozeIntent, 0);

        int conversationId = notificationId;
        // Build a PendingIntent for the reply action to trigger.
        PendingIntent replyPendingIntent =
                PendingIntent.getBroadcast(getApplicationContext(),
                        conversationId,
                        replyIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);

        // Create the reply action and add the remote input.
        NotificationCompat.Action action =
                new NotificationCompat.Action.Builder(R.drawable.ic_baseline_send_24, "Responder", replyPendingIntent)
                        .addRemoteInput(remoteInput)
                        .build();


//        Notification notification = new NotificationCompat.Builder(this, MESSAGE_CHANNEL_ID)
//                .setContentTitle("2 new messages with " + "Mayté Báez")
//                .setContentText("subject")
//                .setSmallIcon(R.drawable.ic_oficios)
////                .setLargeIcon(aBitmap)
//                .setStyle(new NotificationCompat.MessagingStyle("rEPLY NAME")
//                        .addMessage("messages[0].getText()", System.currentTimeMillis(), "Mayté Báez")
//                        .addMessage("messages[1].getText()", System.currentTimeMillis(), "Melisa Castro"))
//                .build();

//        Bitmap bitmapImage = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.usuario);
        Bitmap bitmapImage = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.usuario);
        iconCompat = IconCompat.createWithBitmap(bitmapImage);
        Person user1 = new Person.Builder().setName(String.format("%s %s", "Mayté", "Báez")).setIcon(iconCompat).build();
        Person user2 = new Person.Builder().setName(String.format("%s %s", "Melisa", "Castro")).setIcon(iconCompat).build();
        NotificationCompat.MessagingStyle messagingStyle1 = new NotificationCompat.MessagingStyle(user1).setGroupConversation(true);

        ArrayList<String> strings = new ArrayList<>();
        strings.add("Holi Marlon como estás");
        strings.add("Me porias ayudar con una consulta");
        strings.add("Es acerca de Android");

        for (String not : strings) {
            long timeStamp = System.currentTimeMillis();
            NotificationCompat.MessagingStyle.Message message = new NotificationCompat.MessagingStyle.Message(not, timeStamp, user1);
            message.setData("image/*", Uri.parse("https://firebasestorage.googleapis.com/v0/b/tfinal2022-afc91.appspot.com/o/empleadores%2FAQzZI7X4KUUkzRfcaAPX4j6Iu3m2%2FfotoPerfil.jpg?alt=media&token=7064cffd-2aa6-4c42-84bb-6f106c1d85c8"));
            messagingStyle1.addMessage(message);
        }


        NotificationCompat.Builder notificationMessage1 = new NotificationCompat.Builder(this, MESSAGE_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_oficios)
                .setContentTitle(String.format(Locale.US, "%d mensajes con %s ", strings.size(), "Mayté Báez"))
//                .setContentText("Hola Marlon muchas gracias por tus palabras...")
                .setLargeIcon(bitmapImage)
                .setContentText("Subject")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                // Set the intent that will fire when the user taps the notification
                .setContentIntent(pendingIntent)
//                .addAction(R.drawable.ic_baseline_send_24, "ResponderZ", snoozePendingIntent)
                .addAction(action)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setAutoCancel(true)
                .setStyle(messagingStyle1)
                .setGroup(GROUP_KEY_MESSAGES);

        NotificationCompat.MessagingStyle messagingStyle2 = new NotificationCompat.MessagingStyle(user1).setGroupConversation(true);

        ArrayList<String> strings2 = new ArrayList<>();
        strings2.add("Holi Marlon como estás");
        strings2.add("Me porias ayudar con una consulta");
        strings2.add("Es acerca de Android");

        for (String not : strings2) {
            long timeStamp = System.currentTimeMillis();
            NotificationCompat.MessagingStyle.Message message = new NotificationCompat.MessagingStyle.Message(not, timeStamp, user2);
            message.setData("image/*", Uri.parse("https://firebasestorage.googleapis.com/v0/b/tfinal2022-afc91.appspot.com/o/empleadores%2FAQzZI7X4KUUkzRfcaAPX4j6Iu3m2%2FfotoPerfil.jpg?alt=media&token=7064cffd-2aa6-4c42-84bb-6f106c1d85c8"));
            messagingStyle2.addMessage(message);
        }

        NotificationCompat.Builder notificationMessage2 = new NotificationCompat.Builder(this, MESSAGE_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_oficios)
                .setContentTitle(String.format(Locale.US, "%d mensajes con %s ", strings.size(), "Melisa Castro"))
//                .setContentText("Hola Marlon muchas gracias por tus palabras...")
                .setLargeIcon(bitmapImage)
                .setContentText("Subject")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                // Set the intent that will fire when the user taps the notification
                .setContentIntent(pendingIntent)
//                .addAction(R.drawable.ic_baseline_send_24, "ResponderZ", snoozePendingIntent)
                .addAction(action)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setAutoCancel(true)
                .setStyle(messagingStyle2)
                .setGroup(GROUP_KEY_MESSAGES);


//        Notification newMessageNotification1 =
//                new NotificationCompat.Builder(this, MESSAGE_CHANNEL_ID)
//                        .setSmallIcon(R.drawable.ic_oficios)
//                        .setContentTitle("emailObject1.getSummary()")
//                        .setContentText("You will not believe...")
//                        .setGroup(GROUP_KEY_MESSAGES)
//                        .build();

//        Notification newMessageNotification2 =
//                new NotificationCompat.Builder(this, MESSAGE_CHANNEL_ID)
//                        .setSmallIcon(R.drawable.ic_oficios)
//                        .setContentTitle("emailObject2.getSummary()")
//                        .setContentText("Please join us to celebrate the...")
//                        .setGroup(GROUP_KEY_MESSAGES)
//                        .build();

        Notification summaryNotification =
                new NotificationCompat.Builder(this, MESSAGE_CHANNEL_ID)
                        .setContentTitle("emailObject.getSummary()")
                        //set content text to support devices running API level < 24
                        .setContentText("Two new messages")
                        .setSmallIcon(R.drawable.ic_oficios)
                        //build summary info into InboxStyle template
                        .setStyle(new NotificationCompat.InboxStyle()
                                .addLine("Mayté Báez Check this out")
                                .addLine("Melisa Castro    Launch Party")
                                .setBigContentTitle("2 new messages")
                                .setSummaryText("janedoe@example.com"))
                        //specify which group this notification belongs to
                        .setGroup(GROUP_KEY_MESSAGES)
                        //set this notification as the summary for the group
                        .setGroupSummary(true)
                        .build();

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        notificationManager.notify(1502, notificationMessage1.build());
        notificationManager.notify(1501, notificationMessage2.build());
        notificationManager.notify(SUMMARY_MESSAGES, summaryNotification);

        // notificationId is a unique int for each notification that you must define
        //notificationManager.notify(notificationId, builder.build());

    }


    private void createCallNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Notificaciones de llamadas";
            String description = "Notificaciones de llamadas desde Firebase";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            notificationChannelCalls = new NotificationChannel(CALL_CHANNEL_ID, name, importance);
            notificationChannelCalls.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(notificationChannelCalls);
        }
    }

    private void createAppointmentNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Notificaciones de citas";
            String description = "Notificaciones de citas desde Firebase";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            notificationChannelAppointments = new NotificationChannel(APPOINTMENT_CHANNEL_ID, name, importance);
            notificationChannelAppointments.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(notificationChannelAppointments);
        }
    }


    public void showNotificationCall(View view) {

    }

    public void showNotificationCita(View view) {
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void showConfigNotificationMessage(View view) {
        Intent intent = new Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS);
        intent.putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
        intent.putExtra(Settings.EXTRA_CHANNEL_ID, notificationChannelMessages.getId());
        startActivity(intent);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void showConfigNotificationCall(View view) {
        Intent intent = new Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS);
        intent.putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
        intent.putExtra(Settings.EXTRA_CHANNEL_ID, notificationChannelCalls.getId());
        startActivity(intent);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void showConfigNotificationAppointment(View view) {
        Intent intent = new Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS);
        intent.putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
        intent.putExtra(Settings.EXTRA_CHANNEL_ID, notificationChannelAppointments.getId());
        startActivity(intent);
    }
}