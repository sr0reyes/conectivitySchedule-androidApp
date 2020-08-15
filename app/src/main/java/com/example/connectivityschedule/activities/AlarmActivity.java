package com.example.connectivityschedule.activities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.example.connectivityschedule.AlarmReceiver;
import com.example.connectivityschedule.R;
import com.example.connectivityschedule.adapters.RecyclerViewAdapter;
import com.example.connectivityschedule.myclasses.Alarm;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.View;
import android.widget.TimePicker;
import android.widget.Toast;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;

public class AlarmActivity extends AppCompatActivity {

    private final  static String TAG = "AlarmActivity";
    String alarmListType;
    ArrayList<Alarm> currentAlarmList;
    RecyclerView recyclerView;
    RecyclerViewAdapter rvAdapter;
    ItemTouchHelper itemTouchHelper;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);
        Intent intent = getIntent();
        alarmListType = intent.getStringExtra("ALARM_TYPE");

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(alarmListType);

        loadAlarmList(alarmListType);
        buildRecyclerView();

        itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createAlarm(rvAdapter.getItemCount());
            }
        });
    }

    @Override
    public void onStop(){
        super.onStop();
        saveAlarmList(alarmListType);
    }


    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            cancelAlarm(viewHolder.getAdapterPosition());
            removeAlarm(viewHolder.getAdapterPosition());
        }
    };


    void buildRecyclerView(){

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        rvAdapter = new RecyclerViewAdapter(currentAlarmList, new RecyclerViewAdapter.MyRecyclerViewActionListener() {
            @Override
            public void onTimeClickListener(int itemPosition) {
                setTime(itemPosition);
            }

            @Override
            public void onSpinnerOptionSelected(int itemPosition, int option) {
                currentAlarmList.get(itemPosition).setAction(option);
            }

            @Override
            public void onSwitchChanged(int itemPosition, boolean isChecked) {
                if(isChecked)
                    scheduleAlarm(itemPosition);
                if(!isChecked)
                    cancelAlarm(itemPosition);
            }
        });
        rvAdapter.setHasStableIds(true);
        recyclerView.setAdapter(rvAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        RecyclerView.ItemDecoration divider = new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(divider);

    }



    void createAlarm(int position){
        final int generatedId =  (int) System.currentTimeMillis();
        Alarm newAlarm = new Alarm(generatedId, alarmListType);
        currentAlarmList.add(newAlarm);
        rvAdapter.notifyItemInserted(position);
        Log.d(TAG, "Alarm created whith id: " + generatedId);
    }

    void removeAlarm(int position){
        currentAlarmList.remove(position);
        rvAdapter.notifyItemRemoved(position);
        Log.d(TAG, "Alarm removed");
    }

    void setTime(final int alarmObjectPosition){
        int selectedHour = currentAlarmList.get(alarmObjectPosition).getHour();
        int selectedMinute = currentAlarmList.get(alarmObjectPosition).getMinute();
        TimePickerDialog timePicker = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                currentAlarmList.get(alarmObjectPosition).setTime(hourOfDay, minute);
                currentAlarmList.get(alarmObjectPosition).setActive(true);
                rvAdapter.notifyDataSetChanged();
                scheduleAlarm(alarmObjectPosition);
            }
        }, selectedHour, selectedMinute, true);

        timePicker.show();

    }

    public void sendToast(String toastMessage, int length){
        Toast toast = Toast.makeText(getApplicationContext(), toastMessage, length);
        toast.show();
    }

    public void scheduleAlarm(int position){
        Alarm alarm = currentAlarmList.get(position);
        alarm.setActive(true);
        int alarmID = (int) alarm.getAlarmID();
        String alarmType = alarm.getAlarmType();
        int hour = alarm.getHour();
        int minute = alarm.getMinute();
        int action = alarm.getAction();
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getApplicationContext(), AlarmReceiver.class);
        intent.putExtra("ALARM_TYPE", alarmType);
        intent.putExtra("ACTION", action);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), alarmID, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        if(calendar.before(Calendar.getInstance())){
            calendar.add(Calendar.DATE,1);
        }

        alarmManager.setInexactRepeating(AlarmManager.RTC, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);

        String message= alarmListType + " ";
        switch(action){
            case 0:
                message = message + getString(R.string.toastOn);
                break;
            case 1:
                message = message + getString(R.string.toastOff);
                break;
        }
        message = message + " " + alarm.getTime();
        sendToast(message, Toast.LENGTH_SHORT);

        Log.d(TAG,"Alarm: "+ alarmID + " scheduled at " + alarm.getTime());

    }

    public void cancelAlarm(int position){
        Alarm alarm = currentAlarmList.get(position);
        alarm.setActive(false);
        int alarmID = (int) alarm.getAlarmID();
        int hour = alarm.getHour();
        int minute = alarm.getMinute();
        int action = alarm.getAction();
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getApplicationContext(), AlarmReceiver.class);
        intent.putExtra("ACTIVITY_TITLE", alarmListType);
        intent.putExtra("ACTION", action);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), alarmID, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        alarmManager.cancel(pendingIntent);

        Log.d(TAG, "Alarm "+ alarmID + " is no longer active");
    }

    private void saveAlarmList(String alarmType){
        SharedPreferences sharedPreferences = getSharedPreferences("USER",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(currentAlarmList);
        editor.putString(alarmType, json);
        editor.apply();

        Log.d(TAG,alarmType + " Alarms saved on shared preferences" );
    }

    private void loadAlarmList(String option){
        SharedPreferences sharedPreferences = getSharedPreferences("USER",MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString(option, null);
        Type type = new TypeToken<ArrayList<Alarm>>() {}.getType();
        currentAlarmList = gson.fromJson(json,type);

        if(currentAlarmList == null){
            currentAlarmList = new ArrayList<>();
        }
        Log.d(TAG, option + " Alarms Retrieved from shared prefernces");
    }



}
