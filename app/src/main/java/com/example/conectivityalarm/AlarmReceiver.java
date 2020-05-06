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

public class AlarmReceiver extends BroadcastReceiver {

    private NotificationManagerCompat notificationManager;

    @Override
    public void onReceive(Context context, Intent intent) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        boolean action = intent.getBooleanExtra("ACTION", true);

        wifiManager.setWifiEnabled(action);
        notificationManager = NotificationManagerCompat.from(context);

        String title = context.getString(R.string.app_name);
        String message = "";
        if(action)
            message = context.getString(R.string.notificationWifiOn);
        if(!action)
            message = context.getString(R.string.notificationWifiOff);

        Notification notification = new NotificationCompat.Builder(context, CHANNEL_1_ID)
                .setSmallIcon(R.drawable.ic_signal_wifi_4_bar_black_24dp)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .build();

        notificationManager.notify(1, notification);
    }


}

