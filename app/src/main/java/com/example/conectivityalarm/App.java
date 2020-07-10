package com.example.conectivityalarm;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.net.Uri;
import android.os.Build;

public class App extends Application {

    public static final String CHANNEL_1_ID = "channel1";
    public static final String CHANNEL_2_ID = "channel1";


    @Override
    public void onCreate() {
        super.onCreate();
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        createChannels(notificationManager);
    }

    private void createChannels(NotificationManager notificationManager){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel notificationChannel1 = new NotificationChannel(CHANNEL_1_ID, "chanel 1", NotificationManager.IMPORTANCE_HIGH);
            notificationChannel1.setDescription("This is the notification chanel 1");

            NotificationChannel notificationChannel2 = new NotificationChannel(CHANNEL_2_ID, "chanel 2", NotificationManager.IMPORTANCE_HIGH);
            notificationChannel1.setDescription("This is the notification chanel 2");


            notificationManager.createNotificationChannel(notificationChannel1);
            notificationManager.createNotificationChannel(notificationChannel2);
        }
    }
}
