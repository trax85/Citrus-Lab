package com.example.myapplication3.fragments.GpuFragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.example.myapplication3.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.topjohnwu.superuser.Shell;

import org.w3c.dom.Text;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import antonkozyriatskyi.circularprogressindicator.CircularProgressIndicator;

public class GpuFragment extends Fragment {

    private String GpuOppPath = "/proc/gpufreq/gpufreq_opp_dump";
    private String GpuCurFreqPath = "/sys/kernel/ged/hal/current_freqency";
    private String GpuLoadPath = "/sys/module/ged/parameters/gpu_loading";
    private String GpuInfoPath = "/sys/devices/platform/13000000.mali/gpuinfo";
    private String DvfsPath = "/proc/mali/dvfs_enable";
    private String GpuFreqOppPath = "/proc/gpufreq/gpufreq_opp_freq";
    private String[] GpuBoostPath = {"/sys/module/ged/parameters/boost_gpu_enable",
            "/sys/module/ged/parameters/enable_gpu_boost"};
    private String[] GpuBoostBoundPath = {"/sys/module/ged/parameters/gpu_bottom_freq",
            "/sys/module/ged/parameters/gpu_cust_upbound_freq",
            "/sys/module/ged/parameters/gpu_cust_boost_freq" };
    private TextView[] textViews;
    LinearLayout[] linearLayouts;
    CircularProgressIndicator cProgress;
    RelativeLayout relativeLayout;
    private TextView textViewMhz, textViewLoad, textViewInfo, textViewVoltage;
    private Switch switchDVFS, switchBoost;
    String[] gpuFreqData, gpuFreqDataApp, gpuVoltData;
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

    public void initData(){
        List<String> out;
        Shell.Result result;

        result = Shell.cmd("cut -d' ' -f4 " + GpuOppPath).exec();
        out = result.getOut();
        gpuFreqData = out.toArray(new String[out.size()]);
        result = Shell.cmd("cut -d' ' -f7 " + GpuOppPath).exec();
        out = result.getOut();
        gpuVoltData = out.toArray(new String[out.size()]);
        gpuFreqDataApp = new String[gpuFreqData.length];
        for(int i = 0; i < gpuFreqData.length; i++){
            gpuFreqData[i] = gpuFreqData[i].replaceFirst(",","");
            gpuVoltData[i] = gpuVoltData[i].replaceFirst(",","");
            gpuFreqDataApp[i] = gpuFreqData[i] + " Mhz";
        }

        String[] str = new String[gpuFreqDataApp.length + 1];
        str[0] = "Not fixed";
        System.arraycopy(gpuFreqDataApp, 0, str, 1, gpuFreqDataApp.length);
        gpuFreqDataApp = str;

        result = Shell.cmd("cat " + GpuInfoPath).exec();
        out = result.getOut();
        textViewInfo.setText(out.get(0));
        cProgress.setMaxProgress(gpuFreqData.length);

        result = Shell.cmd("cut -d':' -f2 " + DvfsPath).exec();
        out = result.getOut();
        if(out.get(0).contains("1"))
            switchDVFS.setChecked(true);
        result = Shell.cmd("cat " + GpuBoostPath[0]).exec();
        out = result.getOut();
        if(out.get(0).contains("1"))
            switchBoost.setChecked(true);
    }

    public void initLinearLayouts(){
        Shell.Result result;
        List<String> out;
        for(int i = 0; i < linearLayouts.length; i++){
            result = Shell.cmd("cat " + GpuBoostBoundPath[i]).exec();
            out = result.getOut();
            if(out.get(0) == "0")
                textViews[i].setText("Not set");
            else
                textViews[i].setText(out.get(0) + "Mhz");
            int finalI = i;
            linearLayouts[i].setOnClickListener(v -> showBoostSetDialogue(finalI));
        }
    }

    public void initSwitch(){
        switchDVFS.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked){
                Shell.cmd("echo 1 > " + DvfsPath).exec();
            } else {
                Shell.cmd("echo 0 > " + DvfsPath).exec();
            }
        });
        switchBoost.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked) {
                Shell.cmd("echo 1 > " + GpuBoostPath[0]).exec();
                Shell.cmd("echo 1 > " + GpuBoostPath[1]).exec();
            }
            else{
                Shell.cmd("echo 0 > " + GpuBoostPath[0]).exec();
                Shell.cmd("echo 1 > " + GpuBoostPath[1]).exec();
            }
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
            Shell.Result result = Shell.cmd("cut -d' ' -f2 " + GpuCurFreqPath).exec();
            List<String> out = result.getOut();
            curFreq = out.get(0);
            result = Shell.cmd("cat " + GpuLoadPath).exec();
            out = result.getOut();
            curLoad = out.get(0);
            idx = Arrays.asList(gpuFreqData).indexOf(curFreq);
            curVoltage = gpuVoltData[idx];
            idx = gpuFreqData.length - idx;
            curFreq = Integer.parseInt(curFreq) / 1000 + " Mhz";
        }

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
        Shell.Result result = Shell.cmd("cut -d' ' -f2 " + GpuCurFreqPath).exec();
        List<String> out = result.getOut();
        int checkedItem;
        if(out.get(0) == "0")
            checkedItem = 0;
        else
         checkedItem = Arrays.asList(gpuFreqData).indexOf(out.get(0)) + 1;
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getActivity());
        builder.setTitle("Choose Fixed GPU OPP");
        builder.setSingleChoiceItems(gpuFreqDataApp, checkedItem, (dialog, which) -> {
            if(which == 0)
                Shell.cmd("echo 0 "  + "> " + GpuFreqOppPath).exec();
            else
                Shell.cmd("echo " + gpuFreqData[which - 1] + " > " + GpuFreqOppPath).exec();
            dialog.dismiss();
        });
        builder.show();
    }

    private void showBoostSetDialogue(int pos) {
        Shell.Result result = Shell.cmd("cat " + GpuBoostBoundPath[pos]).exec();
        List<String> out = result.getOut();
        int checkedItem;
        if(out.get(0) == "0")
            checkedItem = -1;
        else
            checkedItem = Arrays.asList(gpuFreqData).indexOf(out.get(0)) + 1;
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getActivity());
        builder.setTitle("Choose Frequency");
        builder.setSingleChoiceItems(gpuFreqDataApp, checkedItem, (dialog, which) -> {
            if(which == 0)
                Shell.cmd("echo  0 > " + GpuBoostBoundPath[pos]).exec();
            else
                Shell.cmd("echo " + gpuFreqData[which - 1] + " > " + GpuBoostBoundPath[pos]).exec();
            textViews[pos].setText(gpuFreqDataApp[which]);
            dialog.dismiss();
        });
        builder.show();
    }
}