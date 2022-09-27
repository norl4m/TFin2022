package com.marlon.apolo.tfinal2022.individualChat;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.RemoteInput;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.marlon.apolo.tfinal2022.R;

public class DirectReplyActivity extends AppCompatActivity {


    private static final String KEY_TEXT_REPLY = "KEY_TEXT_REPLY";
    private static final String NOTIFICATIONS_CHANNEL_ID = "NOTIFICATIONS_CHANNEL_ID";
    private NotificationManager notificationManager;

    private void handleIntent() {

        Intent intent = this.getIntent();

        Bundle remoteInput = RemoteInput.getResultsFromIntent(intent);

        if (remoteInput != null) {

            TextView myTextView = (TextView) findViewById(R.id.textView);
            String inputString = remoteInput.getCharSequence(
                    KEY_TEXT_REPLY).toString();

            myTextView.setText(inputString);

            Notification repliedNotification =
                    null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                repliedNotification = new Notification.Builder(this, NOTIFICATIONS_CHANNEL_ID)
                        .setSmallIcon(
                                android.R.drawable.ic_dialog_info)
                        .setContentText("Reply received")
                        .build();
                notificationManager.notify(7000, repliedNotification);
            } else {
                NotificationCompat.Builder repliedNotificationBuilder = new NotificationCompat.Builder(this, NOTIFICATIONS_CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_oficios)
                        .setContentTitle("textTitle")
                        .setContentText("textContent")
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);
                notificationManager.notify(7000, repliedNotificationBuilder.build());
            }

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_direct_reply);

        notificationManager =
                (NotificationManager)
                        getSystemService(Context.NOTIFICATION_SERVICE);
        handleIntent();
    }


}