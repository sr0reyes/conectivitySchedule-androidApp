package com.example.conectivityalarm.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.conectivityalarm.R;
import com.example.conectivityalarm.myclasses.Alarm;

import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private ArrayList<Alarm> mAlarms;
    private MyRecyclerViewActionListener myRecyclerViewActionListener;

    public RecyclerViewAdapter(ArrayList<Alarm> alarms, MyRecyclerViewActionListener listener) {
        this.mAlarms = alarms;
        this.myRecyclerViewActionListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View alarmView = inflater.inflate(R.layout.recycle_layout_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(alarmView, myRecyclerViewActionListener);

        return viewHolder;

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Alarm alarm = mAlarms.get(position);

        TextView timeTextView = holder.TimeTv;
        timeTextView.setText(alarm.getTime());

        Switch stateSwitch = holder.ActivateSw;
        stateSwitch.setChecked(alarm.getState());
    }

    @Override
    public int getItemCount() {
        return mAlarms.size();
    }

    @Override
    public long getItemId(int position) {
        return mAlarms.get(position).getAlarmID();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder{

        Switch ActivateSw;
        Spinner ActionSpnr;
        TextView PrepTv;
        TextView TimeTv;


        public ViewHolder(View itemView, final MyRecyclerViewActionListener holderListener){
            super(itemView);
            ActivateSw = itemView.findViewById(R.id.alarm_switchR);
            ActionSpnr = itemView.findViewById(R.id.alarm_spinnerR);
            PrepTv = itemView.findViewById(R.id.preposition_tvR);
            TimeTv = itemView.findViewById(R.id.time_tvR);

            TimeTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holderListener.onTimeClickListener(getItemId(), getAdapterPosition());
                }
            });

            ActivateSw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    holderListener.onSwitchChanged(getItemId(), getAdapterPosition(), isChecked);
                }
            });


        }
    }

    public interface MyRecyclerViewActionListener{

        void onTimeClickListener(long itemId, int itemPosition);
        void onSwitchChanged(long itemId, int itemPosition, boolean isChecked);
    }

}
