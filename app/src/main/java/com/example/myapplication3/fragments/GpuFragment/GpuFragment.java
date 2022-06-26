package com.example.myapplication3.fragments.GpuFragment;

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
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.example.myapplication3.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.topjohnwu.superuser.Shell;

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
    private String DvfsPath = "/sys/module/ged/parameters/gpu_dvfs_enable";
    private String GpuFreqOppPath = "/proc/gpufreq/gpufreq_opp_freq";
    CircularProgressIndicator cProgress;
    RelativeLayout relativeLayout;
    private TextView textViewMhz, textViewLoad, textViewInfo, textViewVoltage;
    private Switch aSwitch;
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
        aSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked){
                Shell.cmd("echo 1 > " + DvfsPath).exec();
            } else {
                Shell.cmd("echo 0 > " + DvfsPath).exec();
            }
        });
        setListener();
    }

    @Override
    public void onPause() {
        super.onPause();
        executor.shutdown();
    }

    public void initData(){
        List<String> out;
        Shell.Result result;
        int idx;

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

        result = Shell.cmd("cat " + GpuInfoPath).exec();
        out = result.getOut();
        textViewInfo.setText(out.get(0));
        cProgress.setMaxProgress(gpuFreqData.length);

        result = Shell.cmd("cat " + DvfsPath).exec();
        out = result.getOut();
        if(Integer.parseInt(out.get(0)) == 1)
            aSwitch.setChecked(true);
    }

    public void initView(View v){
        textViewInfo = v.findViewById(R.id.gpu_info);
        textViewMhz = v.findViewById(R.id.textViewGpuMhz);
        textViewLoad = v.findViewById(R.id.gpu_load);
        textViewVoltage = v.findViewById(R.id.gpu_voltage);
        cProgress = v.findViewById(R.id.cprogress_gpu);
        aSwitch = v.findViewById(R.id.switch_dvfs);
        relativeLayout = v.findViewById(R.id.gpufreq_layout);
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
        int checkedItem = Arrays.asList(gpuFreqData).indexOf(out.get(0));
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getActivity());
        builder.setTitle("Choose Fixed GPU OPP");
        builder.setSingleChoiceItems(gpuFreqDataApp, checkedItem, (dialog, which) -> {
            Shell.cmd("echo " + gpuFreqData[which] + " > " + GpuFreqOppPath).exec();
            dialog.dismiss();
        });
        builder.show();
    }
}