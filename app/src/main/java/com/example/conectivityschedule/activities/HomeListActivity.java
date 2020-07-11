package com.example.conectivityschedule.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.conectivityschedule.R;

import java.util.ArrayList;

public class HomeListActivity extends AppCompatActivity {

    private  final String WIFI = "Wi-Fi";
    private  final String BLUE = "Bluetooth";
    ListView optionsList;
    ArrayList<String> optionsArr;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_list);

        optionsList = findViewById(R.id.list_view);
        optionsArr = new ArrayList<>();
        optionsArr.add(WIFI);
        optionsArr.add(BLUE);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                optionsArr
        );

        optionsList.setAdapter(adapter);

        optionsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String optionSelected = (String)parent.getItemAtPosition(position);
                if(optionSelected.equals(WIFI)) {
                    intent = new Intent(getApplicationContext(), AlarmActivity.class);
                    intent.putExtra("ALARM_TYPE",WIFI);
                }
                if(optionSelected.equals(BLUE)) {
                    intent = new Intent(getApplicationContext(), AlarmActivity.class);
                    intent.putExtra("ALARM_TYPE",BLUE);
                }
                startActivity(intent);
            }
        });
    }
}
