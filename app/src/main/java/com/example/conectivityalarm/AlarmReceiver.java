package com.example.conectivityalarm;

import android.app.Notification;
import android.app.NotificationManager;
import android.bluetooth.BluetoothAdapter;
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

    private static final String TAG = "AlarmReceiver";
    private static final String WIFI = "Wi-Fi";
    private static final String BLUE = "Bluetooth";

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.d(TAG, "onReceive: ejecutado");

        int action = intent.getIntExtra("ACTION", 0);
        String alarmType = intent.getStringExtra("ALARM_TYPE");
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        int notificationId = 0;
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        BluetoothAdapter blueAdp = BluetoothAdapter.getDefaultAdapter();

        switch(alarmType){
            case WIFI:
                notificationId = 0;
                if(action == 0) {

                        try {
                            wifiManager.setWifiEnabled(true);
                            Log.d(TAG, "Wifi is on");
                        } catch (Exception e) {
                            Log.d(TAG, e.toString());
                        }
                        String actionString = context.getString(R.string.notificationOn);
                        sendNotification(context, alarmType, actionString, notificationManager, notificationId);

                }
                if(action == 1){

                        try {
                            wifiManager.setWifiEnabled(false);
                            Log.d(TAG, "Wifi is off");
                        } catch (Exception e) {
                            Log.d(TAG, e.toString());
                        }
                        String actionString = context.getString(R.string.notificationOff);
                        sendNotification(context, alarmType, actionString, notificationManager, notificationId);

                }

                break;

            case BLUE:
                notificationId = 1;
                if(action == 0) {
                    if (!blueAdp.isEnabled()) {
                        try {
                            blueAdp.enable();
                            Log.d(TAG,"Bluetooth is: on");
                        }
                        catch(Exception e){
                            Log.d(TAG, e.toString());
                        }
                        String actionString = context.getString(R.string.notificationOn);
                        sendNotification(context, alarmType, actionString, notificationManager, notificationId);
                    }
                }
                if(action == 1) {
                    if (blueAdp.isEnabled()) {
                        try {
                            blueAdp.disable();
                            Log.d("Bluetooth is: ", "off");
                        }
                        catch(Exception e){
                            Log.d(TAG, e.toString());
                        }
                        String actionString = context.getString(R.string.notificationOff);
                        sendNotification(context, alarmType, actionString, notificationManager, notificationId);
                    }
                }
                break;

        }
    }

    private void sendNotification(Context context, String alarmType, String action, NotificationManagerCompat notificationManager, int id){
        String title = context.getString(R.string.app_name);
        Notification notification = new Notification();
        String message = alarmType + " " + action;
        switch(alarmType){
            case  WIFI:
                notification = new NotificationCompat.Builder(context, CHANNEL_1_ID)
                        .setSmallIcon(R.drawable.ic_signal_wifi_4_bar_black_24dp)
                        .setContentTitle(title)
                        .setContentText(message)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                        .build();
                break;
            case BLUE:
                notification = new NotificationCompat.Builder(context, CHANNEL_1_ID)
                        .setSmallIcon(R.drawable.ic_bluetooth_disabled_black_24dp)
                        .setContentTitle(title)
                        .setContentText(message)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                        .build();
                break;
        }


        notificationManager.notify(id, notification);
        Log.d(TAG, "Notification sent");

    }


}

