package com.example.myapplication3.fragments.HomeFragment;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;

import com.example.myapplication3.tools.UtilException;
import com.example.myapplication3.tools.Utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class SystemInfo {
    HomeFragment homeFragment;
    private static final String TAG = "SystemStatsAct";
    String Uptime, DeepSleep, uname;

    @SuppressLint("SetTextI18n")
    public void StartSysStats(HomeFragment fragment) {
        ExecutorService service = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        service.execute(() -> {
            homeFragment = fragment;
            // Get the whole uptime
            try {
                uname = Utils.execCmdRead(0, "uname -a");
            } catch (UtilException e) {
                Log.d(TAG,"Version info not available");
                uname = "Unavailable";
            }
            long uptimeMillis = SystemClock.elapsedRealtime();
            // Get the uptime without deep sleep
            long elapsedMillis = SystemClock.uptimeMillis();
            long deepsleepMills = uptimeMillis - elapsedMillis;
            //Get percentage of deep sleep
            int percentDeepSleep = (int)(((float)deepsleepMills / (float)uptimeMillis) * 100);
            Uptime = FormatTime(uptimeMillis);
            DeepSleep = FormatTime(deepsleepMills);

            handler.post(() -> {
                homeFragment.textView11.setText(Uptime);
                homeFragment.textView12.setText(DeepSleep + " (" + percentDeepSleep + "%)");
                homeFragment.textView13.setText(uname);
            });
            try {
                TimeUnit.MILLISECONDS.sleep(2500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }
    private static String FormatTime(long millis){
        long seconds = millis / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;
        return days + "d, " + hours % 24 + "h, " + minutes % 60 + "m, " + seconds % 60 +
                "s";
    }

}
