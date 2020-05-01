package com.example.conectivityalarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.util.Log;

public class AlarmReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        boolean activate = intent.getBooleanExtra("ACTION", true);

        if(activate == true){
            wifiManager.setWifiEnabled(true);
            Log.d("WiFi is", "enable");
        }
        else if(activate == false){
            wifiManager.setWifiEnabled(false);
            Log.d("Wifi is", "disabled");
        }
    }
}
