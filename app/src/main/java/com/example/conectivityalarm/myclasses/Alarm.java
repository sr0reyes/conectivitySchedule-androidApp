package com.example.conectivityalarm.myclasses;


public class Alarm {

    private long alarmID;
    private boolean state;
    private int action;
    private String time;


    public Alarm(){
    }

    public boolean getState() {
        return state;
    }

    public String getTime() {
        return time;
    }

    public int getAction() {
        return action;
    }

    public long getAlarmID() {
        return alarmID;
    }

    public void setAlarmID(long alarmID) {
        this.alarmID = alarmID;
    }
}
