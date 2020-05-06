package com.example.conectivityalarm;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    Switch swActivate;
    Spinner spnAction;
    TextView tvTime;

    int aHour;
    int aMinute;
    boolean activate;

    int toastDuration;
    String strTime;
    String toastMessage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toastDuration =Toast.LENGTH_LONG;
        strTime = getString(R.string.defaultTime);
        toastMessage = getString(R.string.toastWifiOn);

        aHour = 0;
        aMinute = 0;
        activate = true;

        swActivate = findViewById(R.id.alarm_switch);
        spnAction = findViewById(R.id.alarm_spinner);
        tvTime = findViewById(R.id.time_tv);

        tvTime.setOnClickListener(this);

        spnAction.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        activate = true;
                        toastMessage = getString(R.string.toastWifiOn);
                        break;
                    case 1:
                        activate = false;
                        toastMessage = getString(R.string.toastWifiOff);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        swActivate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    setAlarm();
                }
                else{
                    cancelAlarm();
                }
            }
        });

    }

    @Override
    public void onClick(View v){
        TimePickerDialog timePicker = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                aHour = hourOfDay;
                aMinute = minute;
                String formatHour =  (hourOfDay < 10)? String.valueOf("0" + hourOfDay) : String.valueOf(hourOfDay);
                String formatMinute =  (minute < 10)? String.valueOf("0" + minute) : String.valueOf(minute);
                strTime = formatHour + ":" + formatMinute;
                tvTime.setText(strTime);
                if(swActivate.isChecked()){
                    setAlarm();
                }
            }


        }, aHour, aMinute, true);

        timePicker.show();
    }

    public void setAlarm(){
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, aHour);
        c.set(Calendar.MINUTE, aMinute);
        c.set(Calendar.SECOND, 0);


        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReceiver.class);
        intent.putExtra("ACTION", activate);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        if(c.before(Calendar.getInstance())){
            c.add(Calendar.DATE,1);
        }

        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);
        Log.d("Alarm setted at",  String.valueOf(aHour)+":"+String.valueOf(aMinute) + "  action=" + String.valueOf(activate));
        sendToast();
    }


    public void cancelAlarm(){
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReceiver.class);
        intent.putExtra("ACTION", activate);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        alarmManager.cancel(pendingIntent);
        Log.d("Alarm unsetted",  String.valueOf(aHour)+":"+String.valueOf(aMinute));
    }


    public void sendToast(){
        toastMessage += " " + strTime;
        Toast toast = Toast.makeText(getApplicationContext(), toastMessage, toastDuration);
        toast.show();
    }
}
