package com.example.myapplication3.fragments.MiscFragment.TempFragment;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.myapplication3.R;
import com.example.myapplication3.tools.UtilException;
import com.example.myapplication3.tools.Utils;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class TempFragment extends Fragment {
    final private static String TAG = "TempFragment";
    final static String Path = "/sys/class/thermal/thermal_zone";
    final public static int numZones = 27;
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
        initDataSets();
        initRecyclerView(view);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onResume() {
        super.onResume();
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        UpdateTemps updateTemps = new UpdateTemps();
        executor = (ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(1);
        executor.scheduleWithFixedDelay(updateTemps, 2, 2, TimeUnit.SECONDS);
    }

    @Override
    public void onPause() {
        super.onPause();
        executor.shutdown();
    }

    private void initDataSets(){
        tempValues = new String[numZones][2];
        getTemps();
    }

    public void getTemps(){
        String temp, name;
        for(int i = 0; i < numZones; i++) {
            try {
                temp = Utils.read(0, Path + i + "/temp");
                temp = String.valueOf(Double.parseDouble(temp)/1000);
            } catch (UtilException e) {
                temp = "0";
            }
            try{
                name = Utils.read(0, Path + i + "/type");
            }catch (UtilException e){
                name = "";
            }
            tempValues[i][0] = temp;
            tempValues[i][1] = name;
        }
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
            getTemps();
            handler.post(() -> {
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            });
        }
    }
}