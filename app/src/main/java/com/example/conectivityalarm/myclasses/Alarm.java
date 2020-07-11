package com.example.conectivityalarm.myclasses;


public class Alarm {

    private int alarmID;
    private String alarmType;
    private boolean active;
    private int action;
    private int hour;
    private int minute;
    private String time;


    public Alarm(int id, String type){
        this.alarmID = id;
        this.alarmType = type;
        this.active = false;
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

    public String getAlarmType() {
        return alarmType;
    }


    public void setActive(boolean state) {
        this.active = state;
    }

    public void setAction(int action) {
        this.action = action;
    }

    public boolean isActive() {
        return active;
    }

    public String getTime() {
        return time;
    }

    public int getAction() {
        return action;
    }

    public int getAlarmID() {
        return alarmID;
    }

    public int getHour() {
        return hour;
    }

    public int getMinute() {
        return minute;
    }
}
