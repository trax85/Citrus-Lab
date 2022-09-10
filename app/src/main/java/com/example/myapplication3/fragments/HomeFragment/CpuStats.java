package com.example.myapplication3.fragments.HomeFragment;

import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Looper;

import com.example.myapplication3.FragmentDataModels.Cpu;
import com.example.myapplication3.tools.UtilException;
import com.example.myapplication3.tools.Utils;

import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class CpuStats {
    private static final String TAG = "CPUstatsAct";
    public String[] policyArr;
    private final HomeFragment homeFragment;
    private String[][] cpuFreqArr;
    int clusterCount;
    boolean[] cpuOnline;
    ScheduledThreadPoolExecutor executor;
    private Cpu.Params cpuParams;

    public CpuStats(HomeFragment fragment){
        homeFragment = fragment;
    }

    public void setViewModel(){
        FragmentPersistObject viewModel = new ViewModelProvider(homeFragment.requireActivity()).get(FragmentPersistObject.class);
        cpuParams = viewModel.getCpuParams();
        initCpuData();
    }

    public void startCpuStats(){
        initCpuArr();
        startThread();
    }

    /* Initialise the cluster count and policy paths for the respective clusters */
    public void initCpuData(){
        try {
            policyArr = Utils.readGetArr(" ls " + Cpu.PATH.POLICY_PATH);
            clusterCount = policyArr.length;
            //Add '/' so it can be used as a path variable
            for(int i = 0; i < policyArr.length; i++){
                policyArr[i] = "/" + policyArr[i];
            }
        }catch (UtilException e){
            policyArr = null;
        }
        cpuParams.setPolicyArr(policyArr);
    }

    public void startThread(){
        executor = (ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(3);
        for(int i = 0; i <= clusterCount; i++){
                CpuStatsLooper statsLooper = new CpuStatsLooper(i);
                executor.scheduleWithFixedDelay(statsLooper, 0, 1800,
                        TimeUnit.MILLISECONDS);
        }
    }

    public void stopThread(){
        executor.shutdown();
    }

    public void initCpuArr()
    {
        cpuFreqArr = new String[clusterCount][];
        String[][] appCpuFreqArr = new String[clusterCount][];
        cpuOnline = new boolean[clusterCount];
        String[] str;

        for(int i = 0;i < clusterCount; i++) {
            str = Utils.splitStrings(Cpu.PATH.POLICY_PATH + policyArr[i] + Cpu.PATH.SCALING_AVI_FREQ,
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
        cpuParams.setCpuOnline(cpuOnline);
        cpuParams.setFreqArr(cpuFreqArr);
        cpuParams.setAppendedFreqArr(appCpuFreqArr);
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
                    curFreq = getFreq(Cpu.PATH.POLICY_PATH + policyArr[cluster] + Cpu.PATH.CUR_SCALING_FREQ);
                    progress = getProgress(cpuFreqArr[cluster], curFreq + "000",
                            cpuFreqArr[cluster].length);
                }
                handler.post(() -> {
                    homeFragment.cProg[cluster].setCurrentProgress(progress);
                    homeFragment.textViews[cluster].setText(curFreq +" Mhz");
                });
        }
    }
}
