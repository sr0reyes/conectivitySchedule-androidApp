package com.example.conectivityalarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.conectivityalarm.myclasses.Alarm;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;

public class RescheduleAlarmsJobService extends JobService {

    private static final String TAG = "RescheduleAlarmService";
    private boolean jobCancelled = true;
    private ArrayList<Alarm> wifiAlarmsList;
    private ArrayList<Alarm> blueAlarmsList;

    private final String WIFI = getString(R.string.activity_wifi);
    private final String BLUE = getString(R.string.activity_bluetooth);

    @Override
    public boolean onStartJob(JobParameters params) {
        loadAlarms();
        rescheduleAlarms(params);
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.d(TAG, "Job cancelled before completition");
        jobCancelled = true;
        return true;
    }


    void rescheduleAlarms(final JobParameters params){
        new Thread(new Runnable() {
            @Override
            public void run() {

                for(int i = 0; i < wifiAlarmsList.size(); i++){

                    if(wifiAlarmsList.get(i).isActive()){
                        scheduleAlarm(wifiAlarmsList.get(i));
                    }

                    if(jobCancelled)
                        return;
                }


                for(int i = 0; i < blueAlarmsList.size(); i++){

                    if(blueAlarmsList.get(i).isActive()){
                        scheduleAlarm(blueAlarmsList.get(i));
                    }

                    if(jobCancelled)
                        return;
                }

                jobFinished(params, false);
            }
        }).start();
    }

    private void scheduleAlarm(Alarm alarm){
        int alarmID = (int) alarm.getAlarmID();
        int hour = alarm.getHour();
        String alarmType = alarm.getAlarmType();
        int minute = alarm.getMinute();
        int action = alarm.getAction();

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR_OF_DAY, hour);
        calendar.add(Calendar.MINUTE, minute);
        calendar.add(Calendar.SECOND, 0);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getApplicationContext(), AlarmReceiver.class);
        intent.putExtra("ACTIVITY_TITLE", alarmType);
        intent.putExtra("ACTION", action);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), alarmID, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        if(calendar.before(Calendar.getInstance())){
            calendar.add(Calendar.DATE, 1);
        }

        alarmManager.setInexactRepeating(AlarmManager.RTC, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);


    }

    private void loadAlarms(){
        SharedPreferences sharedPreferences = getSharedPreferences("USER",MODE_PRIVATE);
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<Alarm>>() {}.getType();
        String json;

        //get Wifi Alarms
        json = sharedPreferences.getString(WIFI, null);
        wifiAlarmsList = gson.fromJson(json,type);

        //get Wifi Alarms
        json = sharedPreferences.getString(BLUE, null);
        blueAlarmsList = gson.fromJson(json,type);
    }
}
