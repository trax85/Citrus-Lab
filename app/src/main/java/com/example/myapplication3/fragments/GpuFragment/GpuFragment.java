package com.example.myapplication3.fragments.GpuFragment;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.example.myapplication3.R;
import com.example.myapplication3.tools.UtilException;
import com.example.myapplication3.tools.Utils;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import antonkozyriatskyi.circularprogressindicator.CircularProgressIndicator;

public class GpuFragment extends Fragment {
    private final String GpuCurFreqPath = "/sys/kernel/ged/hal/current_freqency";
    private final String DvfsPath = "/proc/mali/dvfs_enable";
    private final String GpuFreqOppPath = "/proc/gpufreq/gpufreq_opp_freq";
    private final String[] GpuBoostPath = {"/sys/module/ged/parameters/boost_gpu_enable",
            "/sys/module/ged/parameters/enable_gpu_boost"};
    private final String[] GpuBoostBoundPath = {"/sys/module/ged/parameters/gpu_bottom_freq",
            "/sys/module/ged/parameters/gpu_cust_upbound_freq",
            "/sys/module/ged/parameters/gpu_cust_boost_freq" };
    final String gpuInfoPath = "/sys/devices/platform/13000000.mali/gpuinfo";
    final String gpuOppPath = "/proc/gpufreq/gpufreq_opp_dump";
    final static String TAG = "GpuFragment";

    private TextView[] textViews;
    LinearLayout[] linearLayouts;
    CircularProgressIndicator cProgress;
    RelativeLayout relativeLayout;
    private TextView textViewMhz, textViewLoad, textViewInfo, textViewVoltage;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private Switch switchDVFS, switchBoost;
    String[] gpuFreqData, gpuFreqDataApp, gpuVoltData;
    String gpuInfo;
    Boolean dvfsState, boostState;
    ScheduledThreadPoolExecutor executor;
    StatsLoop statsLoop;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_gpu, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        statsLoop = new StatsLoop();
        initView(view);
        initData();
    }

    @Override
    public void onResume() {
        super.onResume();
        executor = (ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(1);
        executor.scheduleWithFixedDelay(statsLoop, 0, 2000, TimeUnit.MILLISECONDS);
        initSwitch();
        setListener();
        initLinearLayouts();
    }

    @Override
    public void onPause() {
        super.onPause();
        executor.shutdown();
    }

    public void initView(View v){
        linearLayouts = new LinearLayout[3];
        textViews = new TextView[3];
        textViewInfo = v.findViewById(R.id.gpu_info);
        textViewMhz = v.findViewById(R.id.textViewGpuMhz);
        textViewLoad = v.findViewById(R.id.gpu_load);
        textViewVoltage = v.findViewById(R.id.gpu_voltage);
        cProgress = v.findViewById(R.id.cprogress_gpu);
        switchDVFS = v.findViewById(R.id.switch_dvfs);
        switchBoost = v.findViewById(R.id.switch_gpuboost);
        relativeLayout = v.findViewById(R.id.gpufreq_layout);
        linearLayouts[0] = v.findViewById(R.id.gpu_btmFreq);
        linearLayouts[1] = v.findViewById(R.id.gpu_topFreq);
        linearLayouts[2] = v.findViewById(R.id.gpu_boostFreq);
        textViews[0] = v.findViewById(R.id.gpu_btmFreqDat);
        textViews[1] = v.findViewById(R.id.gpu_topFreqDat);
        textViews[2] = v.findViewById(R.id.gpu_boostFreqDat);
    }

    @SuppressLint("SetTextI18n")
    public void initData(){
        InitGpuData();
        gpuInfo = getGpuInfo();
        dvfsState = getDvfsState();
        boostState = getBoostState();
    }

    public void initText(){
        textViewInfo.setText(gpuInfo);
        switchDVFS.setChecked(dvfsState);
        switchBoost.setChecked(boostState);
    }

    @SuppressLint("SetTextI18n")
    public void initLinearLayouts(){
        String out;
        for(int i = 0; i < linearLayouts.length; i++){
            try {
                out = Utils.read(0,GpuBoostBoundPath[i]);
                if(Objects.equals(out, "0"))
                    textViews[i].setText("Not set");
                else
                    textViews[i].setText(out + "Mhz");
                int finalI = i;
                linearLayouts[i].setOnClickListener(v -> showBoostSetDialogue(finalI));
            } catch (UtilException e) {
                textViews[i].setText("Read error");
                return;
            }
        }
    }

    public void initSwitch(){
        switchDVFS.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked){
                Utils.write("1", DvfsPath);
            } else {
                Utils.write("0", DvfsPath);
            }
        });
        switchBoost.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked) {
                Utils.write("1", GpuBoostPath[0]);
            }
            else{
                Utils.write("0", GpuBoostPath[0]);
            }
            Utils.write("1", GpuBoostPath[1]);
        });
    }

    class StatsLoop implements Runnable{
        String curFreq, curLoad, curVoltage;
        int idx;
        @Override
        public void run() {
            getStats();
            setUI();
        }

        public void getStats(){
            try {
                curFreq = Utils.execCmdRead(0,"cut -d' ' -f2 " + GpuCurFreqPath);
                String gpuLoadPath = "/sys/module/ged/parameters/gpu_loading";
                curLoad = Utils.read(0, gpuLoadPath);
                idx = Arrays.asList(gpuFreqData).indexOf(curFreq);
                curVoltage = gpuVoltData[idx];
                idx = gpuFreqData.length - idx;
                curFreq = Integer.parseInt(curFreq) / 1000 + " Mhz";
            } catch (UtilException e) {
                curFreq = "0 Mhz";
                curLoad = "0";
                curVoltage = "0";
            }
        }

        @SuppressLint("SetTextI18n")
        public void setUI(){
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(() -> {
                textViewMhz.setText(curFreq);
                cProgress.setCurrentProgress(idx);
                textViewLoad.setText(curLoad + "%");
                textViewVoltage.setText(curVoltage + " uV");
            });
        }
    }

    public void setListener(){
        relativeLayout.setOnClickListener(v -> showDialogue());
    }

    public void showDialogue(){
        List<String> out;
        try {
            out = Utils.readGetList("cut -d' ' -f2 " + GpuCurFreqPath);
        } catch (UtilException e) {
            Log.d(TAG, "Read list error");
            return;
        }
        int checkedItem;
        if(Objects.equals(out.get(0), "0"))
            checkedItem = 0;
        else
         checkedItem = Arrays.asList(gpuFreqData).indexOf(out.get(0)) + 1;
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireActivity());
        builder.setTitle("Choose Fixed GPU OPP");
        builder.setSingleChoiceItems(gpuFreqDataApp, checkedItem, (dialog, which) -> {
            if(which == 0)
                Utils.write("0", GpuFreqOppPath);
            else
                Utils.write(gpuFreqData[which - 1], GpuFreqOppPath);
            dialog.dismiss();
        });
        builder.show();
    }

    private void showBoostSetDialogue(int pos) {
        List<String> out;
        try {
            out = Utils.readGetList("cat " + GpuBoostBoundPath[pos]);
        } catch (UtilException e) {
            e.printStackTrace();
            return;
        }
        int checkedItem;
        if(out.get(0).equals("0"))
            checkedItem = -1;
        else
            checkedItem = Arrays.asList(gpuFreqData).indexOf(out.get(0)) + 1;
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireActivity());
        builder.setTitle("Choose Frequency");
        builder.setSingleChoiceItems(gpuFreqDataApp, checkedItem, (dialog, which) -> {
            if(which == 0)
                Utils.write("0", GpuBoostBoundPath[pos]);
            else
                Utils.write(gpuFreqData[which - 1], GpuBoostBoundPath[pos]);
            textViews[pos].setText(gpuFreqDataApp[which]);
            dialog.dismiss();
        });
        builder.show();
    }

    private void InitGpuData(){
        try {
            gpuFreqData = Utils.readGetArr("cut -d' ' -f4 " + gpuOppPath);
            gpuVoltData = Utils.readGetArr("cut -d' ' -f7 " + gpuOppPath);
        } catch (UtilException e) {
            //To-do
            return;
        }
        gpuFreqDataApp = new String[gpuFreqData.length];
        for(int i = 0; i < gpuFreqData.length; i++){
            gpuFreqData[i] = gpuFreqData[i].replaceFirst(",","");
            gpuVoltData[i] = gpuVoltData[i].replaceFirst(",","");
            gpuFreqDataApp[i] = gpuFreqData[i] + " Mhz";
        }
        cProgress.setMaxProgress(gpuFreqData.length);

        String[] str = new String[gpuFreqDataApp.length + 1];
        str[0] = "Not fixed";
        System.arraycopy(gpuFreqDataApp, 0, str, 1, gpuFreqDataApp.length);
        gpuFreqDataApp = str;
    }

    private String getGpuInfo(){
        String out;
        try {
            out = Utils.read(0, gpuInfoPath);
        } catch (UtilException e) {
            out = "Unavailable";
        }
        return out;
    }

    private Boolean getDvfsState(){
        String out;
        try {
            out = Utils.execCmdRead(0, "cut -d':' -f2 " + DvfsPath);
            if(out.contains("1"))
                return true;
        } catch (UtilException ignored) {}
        return false;
    }

    private Boolean getBoostState(){
        String out;
        try {
            out = Utils.read(0,GpuBoostPath[0]);
            if(out.contains("1"))
                return true;
        } catch (UtilException ignored) {}
        return false;
    }
}