package com.example.conectivityalarm.activities;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

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



    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            removeAlarm(viewHolder.getAdapterPosition());
            saveAlarmList(activityTitle);
        }
    };

    void buildRecyclerView(){

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        rvAdapter = new RecyclerViewAdapter(currentAlarmList, new RecyclerViewAdapter.MyRecyclerViewActionListener() {
            @Override
            public void onTimeClickListener(long itemId, int itemPosition) {
                setTime(itemId, itemPosition);
            }

            @Override
            public void onSwitchChanged(long itemId, final int itemPosition, boolean isChecked) {
                currentAlarmList.get(itemPosition).setState(isChecked);
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
                rvAdapter.notifyDataSetChanged();
            }
        }, selectedHour, selectedMinute, true);

        timePicker.show();

    }

    public void sendToast(String toastMessage, int length){
        Toast toast = Toast.makeText(getApplicationContext(), toastMessage, length);
        toast.show();
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

}
