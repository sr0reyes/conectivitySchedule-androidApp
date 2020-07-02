package com.example.conectivityalarm;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import static com.example.conectivityalarm.App.CHANNEL_1_ID;
import static com.example.conectivityalarm.App.CHANNEL_2_ID;

public class AlarmReceiver extends BroadcastReceiver {

    private NotificationManagerCompat notificationManager;

    @Override
    public void onReceive(Context context, Intent intent) {

        int action = intent.getIntExtra("ACTION", 0);
        String activityTitle = intent.getStringExtra("ACTIVITY_TITLE");
        notificationManager = NotificationManagerCompat.from(context);

        String title = context.getString(R.string.app_name);
        String message = activityTitle + " ";
        switch (action){
            case 0:
                message = message + context.getString(R.string.notificationOn);
                break;
            case 1:
                message = message + context.getString(R.string.notificationOff);
                break;
        }

        Notification notification = new Notification();
        int notificationID = 0;

        switch (activityTitle){
            case "Wi-Fi":
                notificationID = 0;
                        notification = new NotificationCompat.Builder(context, CHANNEL_1_ID)
                        .setSmallIcon(R.drawable.ic_signal_wifi_4_bar_black_24dp)
                        .setContentTitle(title)
                        .setContentText(message)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                        .build();
                break;

            case "Bluetooth":
                notificationID = 1;
                        notification = new NotificationCompat.Builder(context, CHANNEL_2_ID)
                        .setSmallIcon(R.drawable.ic_bluetooth_disabled_black_24dp)
                        .setContentTitle(title)
                        .setContentText(message)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                        .build();
                break;

        }

        notificationManager.notify(notificationID, notification);
        Log.d("TAG", message);
    }


}

