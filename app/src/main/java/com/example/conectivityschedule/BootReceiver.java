package com.example.conectivityschedule;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootReceiver extends BroadcastReceiver {
    private static final int JOBID = 0;
    private static final String TAG = "BootReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        if("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())){
            Log.d(TAG,"Starting jobScheduler");
            scheduleJob(context);
        }
        else{
            Log.d(TAG,"Received unexpected Intent");
        }
    }

    public void scheduleJob(Context context){
        ComponentName componentName = new ComponentName(context, RescheduleAlarmsJobService.class);
        JobInfo info = new JobInfo.Builder(JOBID, componentName)
                .setOverrideDeadline(0)
                .setPersisted(true)
                .build();

        JobScheduler scheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        int resultCode = scheduler.schedule(info);
        if(resultCode == JobScheduler.RESULT_SUCCESS){
            Log.d(TAG, "RescheduleAlarmsJob scheduled");
        }else{
            Log.d(TAG, "RescheduleAlarmsJob not scheduled");
        }

    }

    public void cancelJob(Context context){
        JobScheduler scheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        scheduler.cancel(JOBID);
        Log.d(TAG, "Job Cancelled");
    }
}
