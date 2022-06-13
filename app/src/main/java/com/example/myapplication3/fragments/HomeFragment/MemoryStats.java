package com.example.myapplication3.fragments.HomeFragment;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;


public class MemoryStats implements Runnable {
    HomeFragment homeFragment;
    private static final String TAG = "MemoryStatsAct";

    public void setMemClass(HomeFragment fragment){
        homeFragment = fragment;
    }
    @SuppressLint("SetTextI18n")
    @Override
    public void run(){
        Handler handler = new Handler(Looper.getMainLooper());
        Context context = homeFragment.getActivity().getApplicationContext();
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(memoryInfo);
        long totalMemory = memoryInfo.totalMem / 1048576;
        long availableMemory = memoryInfo.availMem / 1048576;
        long usedMemory = totalMemory - availableMemory;
        //Log.d(TAG,"Total Mem:" + totalMemory + " Avilable Mem:"+ avi+
        //        "Used size" + used );
        handler.post(() -> {
            homeFragment.textView8.setText(totalMemory + " Mb");
            homeFragment.textView9.setText(availableMemory + " Mb");
            homeFragment.textView10.setText(usedMemory + " Mb");
        });
    }
}
