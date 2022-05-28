package com.example.myapplication3.fragments.HomeFragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.topjohnwu.superuser.Shell;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class CpuStats implements Runnable{
    private static final String TAG = "CPUstatsAct";
    ExecutorService service;
    private HomeFragment homeFragment;
    Shell.Result clusterOne, clusterTwo, clusterThree;
    String[] storeClusterOne, storeClusterTwo, storeClusterThree;
    private int lenOne, lenTwo, lenThree;
    private int val1, val2, val3;
    private int mhzval1, mhzval2, mhzval3;
    private int MHz;

    public void setCpuClass(HomeFragment fragment){
        homeFragment = fragment;
    }
    public void initThread(){
        storeClusterOne = splitStrings("cat /sys/devices/system/cpu/cpufreq/policy0/scaling_available_frequencies");
        storeClusterTwo = splitStrings("cat /sys/devices/system/cpu/cpufreq/policy4/scaling_available_frequencies");
        storeClusterThree = splitStrings("cat /sys/devices/system/cpu/cpufreq/policy7/scaling_available_frequencies");
        lenOne = storeClusterOne.length;
        lenTwo = storeClusterTwo.length;
        lenThree = storeClusterThree.length;
        //Log.d(TAG, "Inited Thread with length: "+lenOne+" "+lenTwo+" "+lenThree);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void run(){
        Handler handler = new Handler(Looper.getMainLooper());
        //Log.d(TAG, "Enter Thread");
        initThread();
        clusterOne = Shell.cmd("cat /sys/devices/system/cpu/cpufreq/policy0/scaling_cur_freq").exec();
        val1 = getProgress(storeClusterOne, clusterOne.getOut(), lenOne);
        mhzval1 = MHz/1000;
        clusterTwo = Shell.cmd("cat /sys/devices/system/cpu/cpufreq/policy4/scaling_cur_freq").exec();
        val2 = getProgress(storeClusterTwo, clusterTwo.getOut(), lenTwo);
        mhzval2 = MHz/1000;
        clusterThree = Shell.cmd("cat /sys/devices/system/cpu/cpufreq/policy7/scaling_cur_freq").exec();
        val3 = getProgress(storeClusterThree, clusterThree.getOut(), lenThree);
        mhzval3 = MHz/1000;

        handler.post(() -> {
            // Update Cpustats UI elements
            homeFragment.cProgressIndicator1.setCurrentProgress(val1);
            homeFragment.cProgressIndicator2.setCurrentProgress(val2);
            homeFragment.cProgressIndicator3.setCurrentProgress(val3);
            homeFragment.textView1.setText(mhzval1 + "MHz");
            homeFragment.textView2.setText(mhzval2 + "MHz");
            homeFragment.textView3.setText(mhzval3 + "MHz");
        });
    }

    private String[] splitStrings(String string){
        Shell.Result result = Shell.cmd(string).exec();
        List<String> out = result.getOut();
        String[] arrStr = out.toArray(new String[out.size()]);
        // Some workarounds to get in form of strings
        return arrStr[0].split("\\s+");
    }
    private int getProgress(String[] string, List<String> val, int len){
        float index = Arrays.asList(string).indexOf(val.get(0));
        MHz = Integer.parseInt(val.get(0));
        //The values are stored in reverse order inside array
        float out = ((len - index) / len) * 100;
        return (int)out;
    }
}
