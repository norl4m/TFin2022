package com.marlon.apolo.tfinal2022.PoC;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.marlon.apolo.tfinal2022.R;
import com.marlon.apolo.tfinal2022.puntoEntrada.MainActivity;

import org.checkerframework.checker.units.qual.A;
import org.checkerframework.checker.units.qual.C;

import java.util.ArrayList;
import java.util.Random;

import kotlin.jvm.internal.ClassReference;

public class PocActivity6 extends AppCompatActivity {

    // Constants for the notification actions buttons.
    private static final String ACTION_UPDATE_NOTIFICATION =
            "com.marlon.apolo.tfinal2022.PoC.ACTION_UPDATE_NOTIFICATION";
    // Notification channel ID.
    private static final String PRIMARY_CHANNEL_ID =
            "poc_notification_channel";
    // Notification ID.
//    private static final int NOTIFICATION_ID = 0;
    private static final CharSequence CHANNEL_NAME = "poc_channel";
    private static final String CHANNEL_DESCRIPTION = "canal de pruebas locas";
    private static final String ACTION_CANCEL_NOTIFICATION = "com.marlon.apolo.tfinal2022.PoC.ACTION_CANCEL_NOTIFICATION";

    private Button button_notify;
    private Button button_cancel;
    private Button button_update;

    private NotificationManager mNotifyManager;

    private NotificationReceiver mReceiver = new NotificationReceiver();
    private CancelNotificationReceiver cancelNotificationReceiver = new CancelNotificationReceiver();
    private ArrayList<String> notificaciones = new ArrayList<>();

    public class CustomNotification {
        int idNotification;
        ArrayList<String> content;

        public CustomNotification(int idNotification) {
            this.idNotification = idNotification;
        }

        public int getIdNotification() {
            return idNotification;
        }

        public void setIdNotification(int idNotification) {
            this.idNotification = idNotification;
        }

        public ArrayList<String> getContent() {
            return content;
        }

        public void setContent(ArrayList<String> content) {
            this.content = content;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poc6);

        notificaciones.add("Mensaje de prueba 1");
        notificaciones.add("Mensaje de prueba 2");
        notificaciones.add("Mensaje de prueba 3");
        notificaciones.add("Mensaje de prueba 4");
        notificaciones.add("Mensaje de prueba 5");
        notificaciones.add("Mensaje de prueba 6");
        notificaciones.add("Mensaje de prueba 7");
        // Create the notification channel.
        createNotificationChannel();

        // Register the broadcast receiver to receive the update action from
        // the notification.
        registerReceiver(mReceiver,
                new IntentFilter(ACTION_UPDATE_NOTIFICATION));
        registerReceiver(cancelNotificationReceiver,
                new IntentFilter(ACTION_CANCEL_NOTIFICATION));

        // Add onClick handlers to all the buttons.
        button_notify = findViewById(R.id.notify);
        button_notify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Send the notification
                sendNotification();
            }
        });

        button_update = (Button) findViewById(R.id.update);
        button_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Update the notification.
//                updateNotification();
            }
        });

        button_cancel = (Button) findViewById(R.id.cancel);
        button_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Cancel the notification.
//                cancelNotification();
            }
        });

        // Reset the button states. Enable only Notify button and disable
        // update and cancel buttons.
        setNotificationButtonState(true, false, false);
    }

    /**
     * Unregisters the receiver when the app is being destroyed.
     */
    @Override
    protected void onDestroy() {
        unregisterReceiver(mReceiver);
        unregisterReceiver(cancelNotificationReceiver);
        super.onDestroy();
    }

    /**
     * Creates a Notification channel, for OREO and higher.
     */
    public void createNotificationChannel() {

        // Create a notification manager object.
        mNotifyManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        // Notification channels are only available in OREO and higher.
        // So, add a check on SDK version.
        if (android.os.Build.VERSION.SDK_INT >=
                android.os.Build.VERSION_CODES.O) {

            // Create the NotificationChannel with all the parameters.
            NotificationChannel notificationChannel = new NotificationChannel
                    (PRIMARY_CHANNEL_ID,
                            CHANNEL_NAME,
                            NotificationManager.IMPORTANCE_HIGH);

            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setDescription(CHANNEL_DESCRIPTION);

            mNotifyManager.createNotificationChannel(notificationChannel);
        }
    }

    /**
     * OnClick method for the "Notify Me!" button.
     * Creates and delivers a simple notification.
     */
    public void sendNotification() {
        final int min = 0;
        final int max = 6;
        int idNotification = new Random().nextInt((max - min) + 1) + min;
        // Sets up the pending intent to update the notification.
        // Corresponds to a press of the Update Me! button.
        Intent updateIntent = new Intent(ACTION_UPDATE_NOTIFICATION);
        updateIntent.putExtra("idUpdateNotification", idNotification);
        PendingIntent updatePendingIntent = PendingIntent.getBroadcast(this,
                idNotification, updateIntent, PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_MUTABLE);

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//            updatePendingIntent = PendingIntent.getBroadcast(this,
//                    NOTIFICATION_ID, updateIntent, PendingIntent.FLAG_ONE_SHOT);
//        } else {
//            updatePendingIntent = PendingIntent.getBroadcast(this,
//                    NOTIFICATION_ID, updateIntent, PendingIntent.FLAG_ONE_SHOT);
//        }

        // Build the notification with all of the parameters using helper
        // method.

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent notificationPendingIntent = PendingIntent.getActivity
                (this, idNotification, notificationIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);

        NotificationCompat.Builder notifyBuilder = getNotificationBuilder();

        notifyBuilder.setContentText(notificaciones.get(idNotification));
        notifyBuilder.setContentIntent(notificationPendingIntent);
        // Add the action button using the pending intent.
        notifyBuilder.addAction(R.drawable.ic_baseline_send_24, "Update", updatePendingIntent);


        Intent cancelIntent = new Intent(ACTION_CANCEL_NOTIFICATION);
        cancelIntent.putExtra("idUpdateNotification", idNotification);
        PendingIntent cancelPendingIntent = PendingIntent.getBroadcast(this, idNotification, cancelIntent, PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_MUTABLE);
        notifyBuilder.addAction(R.drawable.ic_baseline_send_24, "Cancel", cancelPendingIntent);

        CustomNotification customNotification = new CustomNotification(idNotification);
        customNotification.setContent(new ArrayList<>());
        customNotification.getContent().add(notificaciones.get(idNotification));

        // Deliver the notification.
        mNotifyManager.notify(idNotification, notifyBuilder.build());


        // Enable the update and cancel buttons but disables the "Notify
        // Me!" button.
        setNotificationButtonState(false, true, true);
    }

    /**
     * Helper method that builds the notification.
     *
     * @return NotificationCompat.Builder: notification build with all the
     * parameters.
     */
    private NotificationCompat.Builder getNotificationBuilder() {

        // Set up the pending intent that is delivered when the notification
        // is clicked.


        // Build the notification with all of the parameters.
        NotificationCompat.Builder notifyBuilder = new NotificationCompat
                .Builder(this, PRIMARY_CHANNEL_ID)
                .setContentTitle("Title")
                .setSmallIcon(R.drawable.ic_oficios)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_ALL);
        return notifyBuilder;
    }

    /**
     * OnClick method for the "Update Me!" button. Updates the existing
     * notification to show a picture.
     */
    public void updateNotification(int idUpdateNotification) {

        // Load the drawable resource into the a bitmap image.
        Bitmap androidImage = BitmapFactory
                .decodeResource(getResources(), R.drawable.ic_baseline_mic_24);

        // Build the notification with all of the parameters using helper
        // method.
        NotificationCompat.Builder notifyBuilder = getNotificationBuilder();

        notifyBuilder.setContentTitle("Title");
        notifyBuilder.setContentText("Notification update: " + String.valueOf(idUpdateNotification));

        // Update the notification style to BigPictureStyle.
//        notifyBuilder.setStyle(new NotificationCompat.BigPictureStyle()
//                .bigPicture(androidImage)
//                .setBigContentTitle("Notification update: " + String.valueOf(idUpdateNotification)));

        // Deliver the notification.
        mNotifyManager.notify(idUpdateNotification, notifyBuilder.build());

        // Disable the update button, leaving only the cancel button enabled.
        setNotificationButtonState(false, false, true);
    }

    /**
     * OnClick method for the "Cancel Me!" button. Cancels the notification.
     */
    public void cancelNotification(int idNotification) {
        // Cancel the notification.
        mNotifyManager.cancel(idNotification);

        // Reset the buttons.
        setNotificationButtonState(true, false, false);
    }

    /**
     * Helper method to enable/disable the buttons.
     *
     * @param isNotifyEnabled, boolean: true if notify button enabled
     * @param isUpdateEnabled, boolean: true if update button enabled
     * @param isCancelEnabled, boolean: true if cancel button enabled
     */
    void setNotificationButtonState(Boolean isNotifyEnabled, Boolean
            isUpdateEnabled, Boolean isCancelEnabled) {
        button_notify.setEnabled(isNotifyEnabled);
        button_update.setEnabled(isUpdateEnabled);
        button_cancel.setEnabled(isCancelEnabled);
    }

    /**
     * The broadcast receiver class for notifications.
     * Responds to the update notification pending intent action.
     */
    public class NotificationReceiver extends BroadcastReceiver {

        public NotificationReceiver() {
        }

        /**
         * Receives the incoming broadcasts and responds accordingly.
         *
         * @param context Context of the app when the broadcast is received.
         * @param intent  The broadcast intent containing the action.
         */
        @Override
        public void onReceive(Context context, Intent intent) {
            // Update the notification.
            int idUpdateNotification = intent.getIntExtra("idUpdateNotification", -1);
            updateNotification(idUpdateNotification);
        }
    }

    /**
     * The broadcast receiver class for notifications.
     * Responds to the update notification pending intent action.
     */
    public class CancelNotificationReceiver extends BroadcastReceiver {

        public CancelNotificationReceiver() {
        }

        /**
         * Receives the incoming broadcasts and responds accordingly.
         *
         * @param context Context of the app when the broadcast is received.
         * @param intent  The broadcast intent containing the action.
         */
        @Override
        public void onReceive(Context context, Intent intent) {
            // Update the notification.
            int idUpdateNotification = intent.getIntExtra("idUpdateNotification", -1);
            cancelNotification(idUpdateNotification);
        }
    }
}