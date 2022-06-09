package com.example.myapplication3.fragments.HomeFragment;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;

import com.topjohnwu.superuser.Shell;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class SystemInfo {
    HomeFragment homeFragment;
    private static final String TAG = "SystemStatsAct";
    String Uptime, DeepSleep, uname;
    Shell.Result res;
    List<String> out;
    @SuppressLint("SetTextI18n")
    public void StartSysStats(HomeFragment fragment) {
        ExecutorService service = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        service.execute(() -> {
            homeFragment = fragment;
            // Get the whole uptime
            res = Shell.cmd("uname -o -r").exec();
            out = res.getOut();
            uname = out.get(0);
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
        String time = days + "d, " + hours % 24 + "h, " + minutes % 60 + "m, " + seconds % 60 +
                "s";
        return time;
    }

}
