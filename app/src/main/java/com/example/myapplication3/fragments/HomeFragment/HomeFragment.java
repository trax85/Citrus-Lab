package com.example.myapplication3.fragments.HomeFragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.myapplication3.BuildConfig;
import com.example.myapplication3.R;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import antonkozyriatskyi.circularprogressindicator.CircularProgressIndicator;


public class HomeFragment extends Fragment {
    private static final String TAG = "HomeActivity";
    public CpuStats cpuStats;
    BatteryStats batteryStats;
    MemoryStats memoryStats;
    SystemInfo systemInfo;
    IntentFilter filter;
    ScheduledThreadPoolExecutor executor;
    CircularProgressIndicator cProgressIndicator1, cProgressIndicator2, cProgressIndicator3;
    TextView textView1, textView2, textView3, textView4, textView5, textView6, textView7,
            textView8, textView9, textView10, textView11, textView12, textView13, textView14,
            textView15, textView16;
    CircularProgressIndicator[] cProg;
    TextView[] textViews;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }
    @SuppressLint("StaticFieldLeak")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        cpuStats = new CpuStats();
        batteryStats = new BatteryStats();;
        memoryStats = new MemoryStats();
        systemInfo = new SystemInfo();
        filter = new IntentFilter();

        initViews(view);
        cProgressIndicator1.setMaxProgress(100);
        cProgressIndicator2.setMaxProgress(100);
        cProgressIndicator3.setMaxProgress(100);
        //Register broad cast service
        filter.addAction(Intent.ACTION_BATTERY_CHANGED);
        getActivity().registerReceiver(batteryStats.mBroadcastReceiver, filter);
        
        initDeviceInfo();
        cpuStats.setCpuClass(this);
        batteryStats.setBattClass(this);
        memoryStats.setMemClass(this);
    }

    private void initViews(View view){
        //Cpu stats Ui elements
        cProgressIndicator1 = view.findViewById(R.id.circular_progress1);
        cProgressIndicator2 = view.findViewById(R.id.circular_progress2);
        cProgressIndicator3 = view.findViewById(R.id.circular_progress3);
        textView1 = view.findViewById(R.id.textView1);
        textView2 = view.findViewById(R.id.textView2);
        textView3 = view.findViewById(R.id.textView3);
        cProg = new CircularProgressIndicator[]{cProgressIndicator1, cProgressIndicator2,
                cProgressIndicator3};
        textViews = new TextView[]{textView1, textView2, textView3};

        //Battery stats Ui elements
        textView4 = view.findViewById(R.id.textView8);
        textView5 = view.findViewById(R.id.textView10);
        textView6 = view.findViewById(R.id.textView12);
        textView7 = view.findViewById(R.id.textView14);
        //Memory stats Ui elements
        textView8 = view.findViewById(R.id.textView16);
        textView9 = view.findViewById(R.id.textView18);
        textView10 = view.findViewById(R.id.textView20);
        //System Info Ui elements
        textView11 = view.findViewById(R.id.textView22);
        textView12 = view.findViewById(R.id.textView24);
        textView13 = view.findViewById(R.id.textView26);
        //Home Device/App Info
        textView14 = view.findViewById(R.id.textView32);
        textView15 = view.findViewById(R.id.textView34);
        textView16 = view.findViewById(R.id.textView36);
    }
    @SuppressLint("SetTextI18n")
    public void initDeviceInfo(){
        textView14.setText(Build.MODEL + " (" + Build.DEVICE + ")");
        textView15.setText(Build.BOARD);
        textView16.setText("v" + BuildConfig.VERSION_NAME);
    }

    @Override
    public void onPause() {
        super.onPause();
        //Kill all threads
        executor.shutdown();
        Log.d(TAG,"Paused fragment");
        getActivity().unregisterReceiver(batteryStats.mBroadcastReceiver);
    }

    @Override
    public void onResume() {
        super.onResume();
        //Init and Start threads
        cpuStats.initCpuArr();
        executor = (ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(5);
        executor.scheduleWithFixedDelay(cpuStats,0,1000, TimeUnit.MILLISECONDS);
        executor.scheduleWithFixedDelay(batteryStats,0,1300, TimeUnit.MILLISECONDS);
        executor.scheduleWithFixedDelay(memoryStats,0,1700, TimeUnit.MILLISECONDS);
        systemInfo.StartSysStats(this);
        Log.d(TAG,"Resumed fragment");
        getActivity().registerReceiver(batteryStats.mBroadcastReceiver, filter);
    }
}