package com.example.myapplication3.fragments.HomeFragment;

import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.example.myapplication3.tools.UtilException;
import com.example.myapplication3.tools.Utils;

import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class CpuStats {
    private static final String TAG = "CPUstatsAct";
    public static String policyPath = "/sys/devices/system/cpu/cpufreq";
    String scalingAviFreqPath = "/scaling_available_frequencies";
    String curScalingFreqPath = "/scaling_cur_freq";
    public String[] policyArr;
    private HomeFragment homeFragment;
    String[][] cpuFreqArr, appCpuFreqArr;
    int clusterCount;
    boolean[] cpuOnline;
    AviFreqData viewModel;
    ScheduledThreadPoolExecutor executor;

    public void setCpuClass(HomeFragment fragment){
        homeFragment = fragment;
        viewModel = new ViewModelProvider(homeFragment.requireActivity()).get(AviFreqData.class);
        init();
    }

    /* Initialise the cluster count and policy paths for the respective clusters */
    public void init(){
        try {
            policyArr = Utils.readGetArr(" ls " + policyPath);
            Log.d(TAG, "Getpolicy");
            clusterCount = policyArr.length;
            //Add '/' so it can be used as a path variable
            for(int i = 0; i < policyArr.length; i++){
                policyArr[i] = "/" + policyArr[i];
            }
        }catch (UtilException e){
            policyArr = null;
        }
        viewModel.setPolicyAttr(policyArr);
    }

    public void startThread(){
        executor = (ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(3);
        for(int i = 0; i <= clusterCount; i++){
                CpuStatsLooper statsLooper = new CpuStatsLooper(i);
                executor.scheduleWithFixedDelay(statsLooper, 0, 1800, TimeUnit.MILLISECONDS);
        }
    }

    public void stopThread(){
        executor.shutdown();
    }

    public void initCpuArr()
    {
        cpuFreqArr = new String[clusterCount][];
        appCpuFreqArr = new String[clusterCount][];
        cpuOnline = new boolean[clusterCount];
        String[] str;

        for(int i = 0;i < clusterCount; i++) {
            Log.d(TAG, "Getavifreq");
            str = Utils.splitStrings(policyPath + policyArr[i] + scalingAviFreqPath,
                    "\\s+");
            /* Check if cluster is online if not we skip to avoid crashing app while
             * converting string to int. if we encounter any alphabet in the string then
             * there was an error in fetching values aka offline */
            if(str == null){
                cpuOnline[i] = false;
                continue;
            }

            cpuFreqArr[i] = str;
            String[] temp = new String[str.length];
            for(int j = 0; j < str.length; j++){
                temp[j] = Integer.parseInt(str[j]) / 1000 + " Mhz";
            }
            appCpuFreqArr[i] = temp;
            cpuOnline[i] = true;
        }
        viewModel.setCpuOnline(cpuOnline);
        viewModel.setCpuFreqArr(cpuFreqArr);
        viewModel.setAppCpuFreqArr(appCpuFreqArr);
    }

    // ---------Set of helper functions-----------
    public static String getFreq(String string){
        try {
            String out = Utils.read(0, string);
            return (Integer.parseInt(out) / 1000) + "";
        } catch (UtilException e) {
            return null;
        }
    }

    private int getProgress(String[] string, String val, int len){
        float index = Arrays.asList(string).indexOf(val);
        //The values are stored in reverse order inside array
        float out = ((len - index) / len) * 100;
        return (int)out;
    }

    class CpuStatsLooper implements Runnable{
        int cluster;
        int progress;
        String curFreq;

        public CpuStatsLooper(int cluster){
            this.cluster = cluster;
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void run() {
            Handler handler = new Handler(Looper.getMainLooper());
                if(!cpuOnline[cluster]) {
                    curFreq = "Offline";
                    progress = 0;
                }
                else{
                    curFreq = getFreq(policyPath + policyArr[cluster] + curScalingFreqPath); //convert to Mhz
                    progress = getProgress(cpuFreqArr[cluster], curFreq + "000",
                            cpuFreqArr[cluster].length);
                    //Log.d(TAG, "Progress:" + progress + " cpu:" + curFreq + " cpufreq:" + cpuFreqArr[i][0]);
                }
                handler.post(() -> {
                    homeFragment.cProg[cluster].setCurrentProgress(progress);
                    homeFragment.textViews[cluster].setText(curFreq +" Mhz");
                });
        }
    }
}
