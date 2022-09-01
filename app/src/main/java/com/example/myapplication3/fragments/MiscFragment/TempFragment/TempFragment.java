package com.example.myapplication3.fragments.MiscFragment.TempFragment;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.myapplication3.R;
import com.example.myapplication3.fragments.MiscFragment.Misc;
import com.example.myapplication3.tools.UtilException;
import com.example.myapplication3.tools.Utils;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class TempFragment extends Fragment {
    final private static String TAG = "TempFragment";

    final public static int numZones = 27;
    ConstraintLayout layout;
    String[][] tempValues;
    RecyclerView recyclerView;
    TempAdapter adapter;
    ScheduledThreadPoolExecutor executor;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_temp, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        AsyncInitTask initTask = new AsyncInitTask(view);
        initTask.start();
    }

    @SuppressLint("NotifyDataSetChanged")
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

    private void initDataSets(){
        tempValues = new String[numZones][2];
    }

    public void getTempZones(){
        String temp, name;
        for(int i = 0; i < numZones; i++) {
            temp = getTemp(i);
            name = getZoneType(i);
            tempValues[i][0] = temp;
            tempValues[i][1] = name;
        }
    }

    private String getTemp(int index){
        String out;
        try {
            out = Utils.read(0, Misc.PATH.THERMAL_ZONE + index + "/temp");
            out = String.valueOf(Double.parseDouble(out)/1000);
        } catch (UtilException e) {
            out = "0";
        }
        return out;
    }

    private String getZoneType(int index){
        String out;
        try{
            out = Utils.read(0, Misc.PATH.THERMAL_ZONE + index + "/type");
        }catch (UtilException e){
            out = "";
        }
        return out;
    }

    private void initRecyclerView(View view){
        recyclerView = view.findViewById(R.id.temp_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView. setOverScrollMode(View. OVER_SCROLL_NEVER);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new TempAdapter(tempValues, this);
        recyclerView.setHasFixedSize(true);
    }

    class UpdateTemps implements Runnable {
        Handler handler = new Handler(Looper.getMainLooper());
        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void run() {
            getTempZones();
            handler.post(() -> {
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            });
        }
    }

    class AsyncInitTask extends Thread {
        private final View view;
        public AsyncInitTask(View view) {
            this.view = view;
        }

        @Override
        public void run() {
            Handler handler = new Handler(Looper.getMainLooper());
            initDataSets();
            handler.post(() -> {
                layout = view.findViewById(R.id.temperature_layout);
                layout.setVisibility(View.INVISIBLE);
                initRecyclerView(view);
            });
        }
    }

    class AsyncResumeTask extends Thread {
        @Override
        public void run() {
            Handler handler = new Handler(Looper.getMainLooper());
            UpdateTemps updateTemps = new UpdateTemps();
            executor = (ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(1);
            executor.scheduleWithFixedDelay(updateTemps, 0, 2, TimeUnit.SECONDS);
            handler.post(() -> {
                recyclerView.setAdapter(adapter);
                layout.setVisibility(View.VISIBLE);
            });
        }
    }
}
