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
        this.state = false;
        this.action = 0;
        this.hour = 0;
        this.minute = 0;
        this.time = "00:00";
    }

    public void setTime(int hour, int minute){
        this.hour = hour;
        this.minute = minute;
        String formatHour = (this.hour < 10)? String.valueOf("0" + this.hour) : String.valueOf(this.hour);
        String formatMinute =  (this.minute < 10)? String.valueOf("0" + this.minute) : String.valueOf(this.minute);
        this.time = formatHour + ":" + formatMinute;
    }

    public void setState(boolean state) {
        this.state = state;
    }

    public void setAction(int action) {
        this.action = action;
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

    public int getHour() {
        return hour;
    }

    public int getMinute() {
        return minute;
    }
}
