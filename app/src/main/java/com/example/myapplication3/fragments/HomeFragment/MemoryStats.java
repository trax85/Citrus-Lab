package com.example.myapplication3.fragments.HomeFragment;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MemoryStats {
    HomeFragment homeFragment;
    private static final String TAG = "MemoryStatsAct";

    @SuppressLint("SetTextI18n")
    public void StartMemStats(HomeFragment fragment){
        ExecutorService service = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        //Log.d(TAG, "Enter Thread");
        service.execute(() -> {
            while(true){
                homeFragment = fragment;
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
                    //Log.d(TAG, "Set");
                });
                try {
                    TimeUnit.MILLISECONDS.sleep(2500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
