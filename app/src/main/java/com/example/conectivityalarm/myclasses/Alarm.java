package com.example.conectivityalarm.myclasses;


public class Alarm {

    static private int alarmCount = 0;
    private long alarmID;
    private boolean state;
    private int action;
    private String time;


    public Alarm(){

        alarmCount += 1;
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


    public static int getAlarmCount() {
        return alarmCount;
    }
    public static void decreaseAlarmCount(){ alarmCount = alarmCount - 1;}

    public long getAlarmID() {
        return alarmID;
    }

    public void setAlarmID(long alarmID) {
        this.alarmID = alarmID;
    }
}
