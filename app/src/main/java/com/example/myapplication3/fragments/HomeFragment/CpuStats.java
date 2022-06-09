package com.example.myapplication3.fragments.HomeFragment;

import android.annotation.SuppressLint;

import androidx.lifecycle.ViewModelProvider;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.topjohnwu.superuser.Shell;

import java.util.Arrays;
import java.util.List;

public class CpuStats implements Runnable{
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

    public void setCpuClass(HomeFragment fragment){
        homeFragment = fragment;
        viewModel = new ViewModelProvider(homeFragment.requireActivity()).get(AviFreqData.class);
        init();
    }

    /* Initialise the cluster count and policy paths for the respective clusters */
    public void init(){
        Shell.Result results;
        List<String> out;
        results = Shell.cmd(" ls " + policyPath).exec();
        out = results.getOut();
        policyArr = out.toArray(new String[out.size()]);//policy list
        clusterCount = policyArr.length;
        //Add '/' so it can be used as a path variable
        for(int i = 0; i < policyArr.length; i++){
            policyArr[i] = "/" + policyArr[i];
        }
        viewModel.setPolicyAttr(policyArr);
    }

    public void initCpuArr()
    {
        cpuFreqArr = new String[clusterCount][];
        appCpuFreqArr = new String[clusterCount][];
        cpuOnline = new boolean[clusterCount];
        String[] str;

        for(int i = 0;i < clusterCount; i++) {
            str = splitStrings(policyPath + policyArr[i] + scalingAviFreqPath);
            /* Check if cluster is online if not we skip to avoid crashing app while
             * converting string to int. if we encounter any alphabet in the string then
             * there was an error in fetching values aka offline */
            if(!isNumber(str[0])){
                cpuOnline[i] = false;
                continue;
            }

            cpuFreqArr[i] = str;
            String temp[] = new String[str.length];
            for(int j = 0; j < str.length; j++){
                temp[j] = Integer.parseInt(str[j]) / 1000 + " Mhz";
            }
            appCpuFreqArr[i] = temp;
            cpuOnline[i] = true;
        }
        //Log.d(TAG, "cpufreq" + cpuFreqList[1][1] + "appcpu" + appCpuFreqList[1][1]);
        viewModel.setCpuOnline(cpuOnline);
        viewModel.setCpuFreqArr(cpuFreqArr);
        viewModel.setAppCpuFreqArr(appCpuFreqArr);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void run(){
        int progress;
        String curFreq;

        Handler handler = new Handler(Looper.getMainLooper());
        for(int i = 0; i < clusterCount; i++){
            if(!cpuOnline[i]) {
                curFreq = "Offline";
                progress = 0;
            }
            else{
                curFreq = getFreq(policyPath + policyArr[i] + curScalingFreqPath); //convert to Mhz
                progress = getProgress(cpuFreqArr[i], curFreq + "000", cpuFreqArr[i].length);
                Log.d(TAG, "Progress:" + progress + " cpu:" + curFreq + " cpufreq:" + cpuFreqArr[i][0]);
                curFreq = curFreq + " Mhz";
            }
            int finalI = i;
            int finalProgress = progress;
            String finalCurFreq = curFreq;

            handler.post(() -> {
              homeFragment.cProg[finalI].setCurrentProgress(finalProgress);
              homeFragment.textViews[finalI].setText(finalCurFreq);
            });
        }
    }

    // ---------Set of helper functions-----------
    public static String[] splitStrings(String string){
        Shell.Result result = Shell.cmd("cat " + string).exec();
        List<String> out = result.getOut();
        String[] arrStr = out.toArray(new String[out.size()]);
        // Some workarounds to get in form of strings
        return arrStr[0].split("\\s+");
    }

    public static String getFreq(String string){
        Shell.Result result = Shell.cmd("cat " + string).exec();
        List<String> out = result.getOut();
        return (Integer.parseInt(out.get(0)) / 1000) + "";
    }

    private int getProgress(String[] string, String val, int len){
        float index = Arrays.asList(string).indexOf(val);
        //The values are stored in reverse order inside array
        float out = ((len - index) / len) * 100;
        return (int)out;
    }

    public static boolean isNumber(String str){
        try{
            Integer.parseInt(str);
            return true;
        }catch(NumberFormatException ex){}
        return false;
    }
}
