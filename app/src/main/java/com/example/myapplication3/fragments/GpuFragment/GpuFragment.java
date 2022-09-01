package com.example.myapplication3.fragments.GpuFragment;

import static com.example.myapplication3.fragments.GpuFragment.Gpu.PATH.DVFS_STATE;

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
    final static String TAG = "GpuFragment";

    private TextView[] textViews;
    LinearLayout[] linearLayouts;
    CircularProgressIndicator cProgress;
    RelativeLayout relativeLayout;
    private TextView textViewMhz, textViewLoad, textViewInfo, textViewVoltage;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private Switch switchDVFS, switchBoost;

    ScheduledThreadPoolExecutor executor;
    StatsLoop statsLoop;
    private Gpu.Params gpuParams;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_gpu, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        AsyncInitTask initTask = new AsyncInitTask(view);
        initTask.start();
    }

    @Override
    public void onResume() {
        super.onResume();
        AsyncResumeTask resumeTask = new AsyncResumeTask();
        resumeTask.start();
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
        gpuParams.setGpuInfo(getGpuInfo());
        gpuParams.setDvfsState(getDvfsState());
        gpuParams.setBoostState(getBoostState());
    }

    public void initText(){
        textViewInfo.setText(gpuParams.getGpuInfo());
        switchDVFS.setChecked(gpuParams.getDvfsState());
        switchBoost.setChecked(gpuParams.getBoostState());
    }

    private void InitGpuData(){
        String[] gpuFreqData, gpuVoltData, gpuFreqDataApp;
        try {
            gpuFreqData = Utils.readGetArr("cut -d' ' -f4 " + Gpu.PATH.GPU_OPP_DUMP);
            gpuVoltData = Utils.readGetArr("cut -d' ' -f7 " + Gpu.PATH.GPU_OPP_DUMP);
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

        gpuParams.setGpuFreqData(gpuFreqData);
        gpuParams.setGpuVoltData(gpuVoltData);
        gpuParams.setGpuFreqDataApp(gpuFreqDataApp);
    }

    @SuppressLint("SetTextI18n")
    public void initLinearLayouts(){
        Handler handler = new Handler(Looper.getMainLooper());
        String out, outText;
        boolean isErr = false;

        for(int i = 0; i < linearLayouts.length; i++){
            try {
                out = Utils.read(0, Gpu.PATH.getGpuBoostPaths(i));
                if(Objects.equals(out, "0"))
                    outText = "Not set";
                else
                    outText = out + " Khz";
            } catch (UtilException e) {
                outText = "Read error";
                isErr = true;
            }

            int finalI = i;
            boolean finalIsErr = isErr;
            String finalOutText = outText;
            handler.post(() -> {
                textViews[finalI].setText(finalOutText);
                if(!finalIsErr)
                    linearLayouts[finalI].setOnClickListener(v -> showBoostSetDialogue(finalI));
            });
        }
    }

    public void initSwitch(){
        switchDVFS.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked){
                writeDvfsState("1");
            }else {
                writeDvfsState("0");
            }
        });
        switchBoost.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Log.d(TAG, "Write Boost");
            if(isChecked) {
                writeGpuBoost("1");
                gpuParams.setBoostState(true);
            }else {
                writeGpuBoost("0");
                gpuParams.setBoostState(false);
            }
        });
    }

    public void setListener(){
        relativeLayout.setOnClickListener(v -> showDialogue());
    }

    public void showDialogue()
    {
        String[] gpuFreqData = gpuParams.getGpuFreqData();
        String out = getCurGpuFreq();
        int checkedItem = 0;

        if(!Objects.equals(out, "0"))
            checkedItem = Arrays.asList(gpuFreqData).indexOf(out) + 1;
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireActivity());
        builder.setTitle("Choose Fixed GPU OPP");
        builder.setSingleChoiceItems(gpuParams.getGpuFreqDataApp(), checkedItem, (dialog, which) -> {
            if(which == 0)
                Utils.write("0", Gpu.PATH.GPU_OPP_FREQ);
            else
                Utils.write(gpuFreqData[which - 1], Gpu.PATH.GPU_OPP_FREQ);
            dialog.dismiss();
        });
        builder.show();
    }

    private void showBoostSetDialogue(int pos)
    {
        String out = getGpuBoostFreq(pos);
        String[] gpuFreqDataApp = gpuParams.getGpuFreqDataApp();
        String[] gpuFreqData = gpuParams.getGpuFreqData();
        int checkedItem = -1;

        if(!Objects.equals(out, "0"))
            checkedItem = Arrays.asList(gpuFreqData).indexOf(out) + 1;
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireActivity());
        builder.setTitle("Choose Frequency");
        builder.setSingleChoiceItems(gpuFreqDataApp, checkedItem, (dialog, which) -> {
            if(which == 0)
                Utils.write("0", Gpu.PATH.getGpuBoostPaths(pos));
            else
                Utils.write(gpuFreqData[which - 1], Gpu.PATH.getGpuBoostPaths(pos));
            textViews[pos].setText(gpuFreqDataApp[which]);
            dialog.dismiss();
        });
        builder.show();
    }

    private String getGpuInfo(){
        String out;
        try {
            out = Utils.read(0, Gpu.PATH.GPU_INFO);
        } catch (UtilException e) {
            out = "Unavailable";
        }
        return out;
    }

    private Boolean getDvfsState(){
        String out;
        try {
            out = Utils.execCmdRead(0, "cut -d':' -f2 " + DVFS_STATE);
            if(out.contains("1"))
                return true;
        } catch (UtilException ignored) {}
        return false;
    }

    private Boolean getBoostState(){
        String out, out1;
        try {
            out = Utils.read(0, Gpu.PATH.getGpuBoostStatePaths(0));
            out1 = Utils.read(0, Gpu.PATH.getGpuBoostStatePaths(1));
            if(out.contains("1") && out1.contains("1")) {
                Log.d(TAG,"Enabled");
                return true;
            }
            else
                Log.d(TAG,"Boost Disabled");
        } catch (UtilException ignored) {}
        return false;
    }

    private String getCurGpuFreq(){
        List<String> out;
        String freq;
        try {
            out = Utils.readGetList("cut -d' ' -f2 " + Gpu.PATH.GPU_CUR_FREQ);
            freq = out.get(0);
        } catch (UtilException e) {
            Log.d(TAG, "Read list error");
            freq = "0";
        }
        return freq;
    }

    private String getGpuBoostFreq(int pos){
        List<String> out;
        String freq;
        try {
            out = Utils.readGetList("cat " + Gpu.PATH.getGpuBoostPaths(pos));
            freq = out.get(0);
        } catch (UtilException e) {
            e.printStackTrace();
            freq = "0";
        }
        return freq;
    }

    private void writeGpuBoost(String out){
        Utils.write(out, Gpu.PATH.getGpuBoostStatePaths(0));
        Utils.write(out, Gpu.PATH.getGpuBoostStatePaths(1));
    }

    private void writeDvfsState(String out){
        Utils.write(out, DVFS_STATE);
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
                String[] gpuFreqData = gpuParams.getGpuFreqData();
                curFreq = Utils.execCmdRead(0,"cut -d' ' -f2 " + Gpu.PATH.GPU_CUR_FREQ);
                curLoad = Utils.read(0, Gpu.PATH.GPU_LOADING);
                idx = Arrays.asList(gpuFreqData).indexOf(curFreq);
                curVoltage = gpuParams.getGpuVoltData(idx);
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

    class AsyncInitTask extends Thread {
        View view;
        public AsyncInitTask(View view) {
            this.view = view;
        }

        @Override
        public void run() {
            statsLoop = new StatsLoop();
            gpuParams = new Gpu.Params().getInstance();
            initView(view);
            InitGpuData();
            initData();
        }
    }

    class AsyncResumeTask extends Thread {
        @Override
        public void run() {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(() -> {
                initText();
                initSwitch();
                setListener();
            });
            initLinearLayouts();
            executor = (ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(1);
            executor.scheduleWithFixedDelay(statsLoop, 0, 2000, TimeUnit.MILLISECONDS);
        }
    }
}