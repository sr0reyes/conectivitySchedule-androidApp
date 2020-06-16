package com.example.conectivityalarm.activities;

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

import android.view.View;
import android.widget.Toast;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class AlarmActivity extends AppCompatActivity {

    ArrayList<Alarm> alarmsList;
    RecyclerView recyclerView;
    RecyclerViewAdapter rvAdapter;
    ItemTouchHelper itemTouchHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);


        loadAlarmList();
        itemTouchHelper = new ItemTouchHelper(simpleCallback);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        buildRecyclerView();
        itemTouchHelper.attachToRecyclerView(recyclerView);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insertAlarm(rvAdapter.getItemCount());
                saveAlarmList();
                sendToast("No de Alarmas" + String.valueOf(alarmsList.size()), Toast.LENGTH_SHORT);
            }
        });
    }



    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            removeAlarm(viewHolder.getAdapterPosition());
            saveAlarmList();
        }
    };

    void buildRecyclerView(){

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        rvAdapter = new RecyclerViewAdapter(alarmsList, new RecyclerViewAdapter.MyRecyclerViewActionListener() {
            @Override
            public void onItemClickListener(long itemId, int itemPosition) {
                sendToast("itemId: " + itemId + ", itemPosition: " + itemPosition, Toast.LENGTH_LONG);
            }
        });
        rvAdapter.setHasStableIds(true);
        recyclerView.setAdapter(rvAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        RecyclerView.ItemDecoration divider = new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(divider);

    }

    void insertAlarm(int position){
       Alarm newAlarm = new Alarm();
       alarmsList.add(newAlarm);
       rvAdapter.notifyItemInserted(position);

    }

    void removeAlarm(int id){
        alarmsList.remove(id);
        Alarm.decreaseAlarmCount();
        rvAdapter.notifyItemRemoved(id);
    }

    public void sendToast(String toastMessage, int length){
        Toast toast = Toast.makeText(getApplicationContext(), toastMessage, length);
        toast.show();
    }

    private void saveAlarmList(){
        SharedPreferences sharedPreferences = getSharedPreferences("USER",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(alarmsList);
        editor.putString("AlarmList", json);
        editor.apply();

    }

    private void loadAlarmList(){
        SharedPreferences sharedPreferences = getSharedPreferences("USER",MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("AlarmList", null);
        Type type = new TypeToken<ArrayList<Alarm>>() {}.getType();
        alarmsList = gson.fromJson(json,type);

        if(alarmsList == null){
            alarmsList = new ArrayList<>();
        }

    }

}
