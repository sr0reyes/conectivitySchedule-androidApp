package com.example.conectivityalarm.myclasses;


public class Alarm {

    private long alarmID;
    private boolean state;
    private int action;
    private int hour;
    private int minute;
    private String time;


    public Alarm(long id){
        this.alarmID = id;
    }

    private void setTime(){
        String formatHour = (this.hour < 10)? String.valueOf("0" + this.hour) : String.valueOf(this.hour);
        String formatMinute =  (this.minute < 10)? String.valueOf("0" + this.minute) : String.valueOf(this.minute);
        this.time = formatHour + ":" + formatMinute;
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
