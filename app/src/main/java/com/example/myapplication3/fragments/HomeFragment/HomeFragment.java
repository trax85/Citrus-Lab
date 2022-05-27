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

import com.example.myapplication3.R;

import antonkozyriatskyi.circularprogressindicator.CircularProgressIndicator;


public class HomeFragment extends Fragment {
    CpuStats cpuStats = new CpuStats();
    BatteryStats batteryStats = new BatteryStats();
    MemoryStats memoryStats = new MemoryStats();
    SystemInfo systemInfo = new SystemInfo();
    private static final String TAG = "HomeActivity";
    CircularProgressIndicator cProgressIndicator1, cProgressIndicator2, cProgressIndicator3;
    TextView textView1, textView2, textView3, textView4, textView5, textView6, textView7,
            textView8, textView9, textView10, textView11, textView12, textView13;

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
        IntentFilter filter = new IntentFilter();
        //Cpu stats Ui elements
        cProgressIndicator1 = view.findViewById(R.id.circular_progress1);
        cProgressIndicator2 = view.findViewById(R.id.circular_progress2);
        cProgressIndicator3 = view.findViewById(R.id.circular_progress3);
        textView1 = view.findViewById(R.id.textView1);
        textView2 = view.findViewById(R.id.textView2);
        textView3 = view.findViewById(R.id.textView3);
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
        cProgressIndicator1.setMaxProgress(100);
        cProgressIndicator2.setMaxProgress(100);
        cProgressIndicator3.setMaxProgress(100);
        //Register broad cast service
        filter.addAction(Intent.ACTION_BATTERY_CHANGED);
        Log.d(TAG, "Register battery status receiver.");
        getActivity().registerReceiver(batteryStats.mBroadcastReceiver, filter);
        //Start threads
        cpuStats.StartCPUprogBar(this);
        batteryStats.StartBattStats(this);
        memoryStats.StartMemStats(this);
        systemInfo.StartSysStats(this);
    }
}