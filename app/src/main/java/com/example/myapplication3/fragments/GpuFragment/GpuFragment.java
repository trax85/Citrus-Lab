package com.example.myapplication3.fragments.GpuFragment;

import static com.example.myapplication3.fragment_data_models.Gpu.PATH.DVFS_STATE;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.example.myapplication3.R;
import com.example.myapplication3.fragment_data_models.Gpu;
import com.example.myapplication3.FragmentPersistObject;
import com.example.myapplication3.fragments.InfoPopupWindow;
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
    private ImageView setFixedGpuFreq, boostParams;

    ScheduledThreadPoolExecutor executor;
    StatsLoop statsLoop;
    private Gpu.Params gpuParams;
    Utils utils;

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

    private void initViewModel(){
        FragmentPersistObject viewModel = new ViewModelProvider(requireActivity())
                .get(FragmentPersistObject.class);
        gpuParams = viewModel.getGpuParams();
        utils = new Utils(gpuParams);
        utils.initActivityLogger();
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
        setFixedGpuFreq = v.findViewById(R.id.set_fixed_freq_info);
        boostParams = v.findViewById(R.id.boost_params_help);
    }

    @SuppressLint("SetTextI18n")
    public void initData(){
        gpuParams.setGpuInfo(getGpuInfo());
        gpuParams.setDvfsState(getDvfsState());
        gpuParams.setBoostState(getBoostState());
    }

    public void initText(){
        textViewInfo.setText(gpuParams.getGpuInfo());
        if(gpuParams.getDvfsState().contains("1"))
            switchDVFS.setChecked(true);
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
    public void initBoostLayouts(){
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
                out = "0";
                isErr = true;
            }
            gpuParams.setGpuBoostFreq(i, out);
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
                gpuParams.setDvfsState("1");
            }else {
                writeDvfsState("0");
                gpuParams.setDvfsState("0");
            }
        });
        switchBoost.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Log.d(TAG, "Write Boost");
            if(isChecked) {
                writeGpuBoost("1");
                gpuParams.setBoostState(true);
                gpuParams.setGpuBoost(new String[]{"1", "1"});
            }else {
                writeGpuBoost("0");
                gpuParams.setBoostState(false);
                gpuParams.setGpuBoost(new String[]{"0", "0"});
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
            String output;
            if(which == 0)
                output = "0";
            else
                output = gpuFreqData[which - 1];
            writeFreq(output, Gpu.PATH.GPU_OPP_FREQ);
            gpuParams.setGpuCurFreq(output);
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
            String output;
            if(which == 0)
                output = "0";
            else
                output = gpuFreqData[which - 1];
            writeFreq(output, Gpu.PATH.getGpuBoostPaths(pos));
            gpuParams.setGpuBoostFreq(pos, output);
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

    private String getDvfsState(){
        String out;
        try {
            out = Utils.execCmdRead(0, "cut -d':' -f2 " + DVFS_STATE);
        } catch (UtilException ignored) { out = "0"; }
        gpuParams.setDvfsState(out);
        return out;
    }

    private boolean getBoostState(){
        String out, out1;
        try {
            out = Utils.read(0, Gpu.PATH.getGpuBoostStatePaths(0));
            out1 = Utils.read(0, Gpu.PATH.getGpuBoostStatePaths(1));
            gpuParams.setGpuBoost(new String[]{out, out1});
            if(out.contains("1") && out1.contains("1"))
                return true;
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
        gpuParams.setGpuCurFreq(freq);
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
        gpuParams.setGpuBoostFreq(pos, freq);
        return freq;
    }

    private void writeGpuBoost(String out){
        utils.write(out, Gpu.PATH.getGpuBoostStatePaths(0));
        utils.write(out, Gpu.PATH.getGpuBoostStatePaths(1));
    }

    private void writeDvfsState(String out){
        utils.write(out, DVFS_STATE);
    }

    private void writeFreq(String out, String Path){
        utils.write(out, Path);
    }

    private void setInfoView(){
        InfoPopupWindow popupWindow = new InfoPopupWindow(this, R.id.gpu_fragment);
        popupWindow.setInfoWindow(setFixedGpuFreq, requireActivity().getResources()
                .getString(R.string.gpu_info_setfixedfreq));
        popupWindow.setInfoWindow(boostParams, requireActivity().getResources()
                .getString(R.string.gpu_info_boost_info));
    }

    class StatsLoop implements Runnable {
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
                curFreq = String.valueOf(Integer.parseInt(curFreq) / 1000);
            } catch (Exception e) {
                curFreq = "0";
                curLoad = "0";
                curVoltage = "0";
            }
        }

        @SuppressLint("SetTextI18n")
        public void setUI(){
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(() -> {
                textViewMhz.setText(curFreq + " Mhz");
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
            initView(view);
            initViewModel();
            initData();
            InitGpuData();
            setInfoView();
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
            initBoostLayouts();
            executor = (ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(1);
            executor.scheduleWithFixedDelay(statsLoop, 0, 2000, TimeUnit.MILLISECONDS);
        }
    }
}