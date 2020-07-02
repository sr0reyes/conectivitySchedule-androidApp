package com.example.conectivityalarm.activities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.example.conectivityalarm.AlarmReceiver;
import com.example.conectivityalarm.R;
import com.example.conectivityalarm.adapters.RecyclerViewAdapter;
import com.example.conectivityalarm.myclasses.Alarm;
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

    String activityTitle;
    ArrayList<Alarm> currentAlarmList;
    RecyclerView recyclerView;
    RecyclerViewAdapter rvAdapter;
    ItemTouchHelper itemTouchHelper;
    int selectedHour;
    int selectedMinute;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        Intent intent = getIntent();
        activityTitle = intent.getStringExtra("OPTION");
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(activityTitle);

        loadAlarmList(activityTitle);
        buildRecyclerView();

        itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createAlarm(rvAdapter.getItemCount());
                sendToast("No de Alarmas" + String.valueOf(currentAlarmList.size()), Toast.LENGTH_SHORT);
            }
        });
    }

    @Override
    public void onStop(){
        super.onStop();
        saveAlarmList(activityTitle);
    }





    void buildRecyclerView(){

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        rvAdapter = new RecyclerViewAdapter(currentAlarmList, new RecyclerViewAdapter.MyRecyclerViewActionListener() {
            @Override
            public void onTimeClickListener(long itemId, int itemPosition) {
                setTime(itemId, itemPosition);
            }

            @Override
            public void onSpinnerOptionSelected(int itemPosition, int option) {
                currentAlarmList.get(itemPosition).setAction(option);
            }

            @Override
            public void onSwitchChanged(int itemPosition, boolean isChecked) {
                currentAlarmList.get(itemPosition).setState(isChecked);
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
        final long generatedId =  System.currentTimeMillis();
        Alarm newAlarm = new Alarm(generatedId);
        currentAlarmList.add(newAlarm);
        rvAdapter.notifyItemInserted(position);

    }

    void removeAlarm(int id){
        currentAlarmList.remove(id);
        rvAdapter.notifyItemRemoved(id);
    }

    void setTime(long alarmObjectId, final int alarmObjectPosition){
        int selectedHour = currentAlarmList.get(alarmObjectPosition).getHour();
        int selectedMinute = currentAlarmList.get(alarmObjectPosition).getMinute();
        TimePickerDialog timePicker = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                currentAlarmList.get(alarmObjectPosition).setTime(hourOfDay, minute);
                currentAlarmList.get(alarmObjectPosition).setState(true);
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
        int alarmID = (int) alarm.getAlarmID();
        int hour = alarm.getHour();
        int minute = alarm.getMinute();
        int action = alarm.getAction();
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        AlarmManager alarmManager =(AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getApplicationContext(), AlarmReceiver.class);
        intent.putExtra("ACTIVITY_TITLE", activityTitle);
        intent.putExtra("ACTION", action);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), alarmID, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        if(calendar.before(Calendar.getInstance())){
            calendar.add(Calendar.DATE,1);
        }

        alarmManager.setInexactRepeating(AlarmManager.RTC, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
        String message= activityTitle + " ";
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

    }

    public void cancelAlarm(int position){
        Alarm alarm = currentAlarmList.get(position);
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
        intent.putExtra("ACTIVITY_TITLE", activityTitle);
        intent.putExtra("ACTION", action);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), alarmID, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        alarmManager.cancel(pendingIntent);
    }

    private void saveAlarmList(String option){
        SharedPreferences sharedPreferences = getSharedPreferences("USER",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(currentAlarmList);
        editor.putString(option, json);
        editor.apply();

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

}
