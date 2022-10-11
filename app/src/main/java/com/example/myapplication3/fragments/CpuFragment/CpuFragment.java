package com.example.myapplication3.fragments.CpuFragment;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.myapplication3.R;
import com.example.myapplication3.fragments.CpuFragment.CoreControl.CoreControlFragment;
import com.example.myapplication3.fragments.CpuFragment.CpuSets.CpuSetFragment;
import com.example.myapplication3.fragments.CpuFragment.Stune.StuneFragment;
import com.example.myapplication3.fragment_data_models.Cpu;
import com.example.myapplication3.fragments.HomeFragment.FragmentPersistObject;
import com.example.myapplication3.tools.UtilException;
import com.example.myapplication3.tools.Utils;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CpuFragment extends Fragment {
    static final String TAG = "Cpustats";
    ArrayList<CpuDataModel> cpuArrayList;
    static String[] clusterNames = {"Little Cluster", "Big Cluster", "Prime Cluster"};


    RVAdapter adapter;
    RecyclerView recyclerView;
    ExecutorService service;
    LinearLayout linearLayout1, linearLayout2, linearLayout3;
    NestedScrollView scrollView;
    Cpu.Params cpuParams;
    Utils utils;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_cpu, container, false);
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
        AsyncResumeTask task = new AsyncResumeTask();
        task.start();
    }

    /* Initialise the cluster count and policy paths for the respective clusters */
    public void initData(){
        FragmentPersistObject viewModel = new ViewModelProvider(requireActivity()).get(FragmentPersistObject.class);
        cpuParams = viewModel.getCpuParams();
        utils = new Utils(cpuParams);
        utils.initActivityLogger();
        cpuParams.setGovArr(Utils.splitStrings(Cpu.PATH.POLICY_PATH +
                cpuParams.getPolicyArr()[0] +
                Cpu.PATH.AVI_SCALING_GOVERNOR, "\\s+"));
    }

    /* Initialise cpuArrayList and append 'Mhz' to cpuArrayList list with variables */
    private void initList(){
        cpuArrayList = new ArrayList<>();
        String[] policyArr = cpuParams.getPolicyArr();
        for(int i = 0; i < policyArr.length; i++){
            try {
                String maxFreq = Utils.read(0, Cpu.PATH.POLICY_PATH + policyArr[i] +
                        Cpu.PATH.MAX_FREQ);
                String minFreq = Utils.read(0, Cpu.PATH.POLICY_PATH + policyArr[i] +
                        Cpu.PATH.MIN_FREQ);
                String govName = Utils.read(0, Cpu.PATH.POLICY_PATH + policyArr[i] +
                        Cpu.PATH.SCALING_GOVERNOR);
                CpuDataModel cpuList = new CpuDataModel(maxFreq, minFreq, clusterNames[i], govName);
                cpuArrayList.add(cpuList);
            }catch (UtilException e){
                Log.e(TAG, "Failed to get min/max cpufreq");
            }
        }
    }

    public void initListeners(){
        linearLayout1.setOnClickListener(v -> {
            setCpuSetFragment();
        });
        linearLayout2.setOnClickListener(v -> {
            setCoreControlFragment();
        });
        linearLayout3.setOnClickListener(v -> {
            setStuneFragment();
        });
    }

    private void setCpuSetFragment(){
        CpuSetFragment frag = new CpuSetFragment();
        FragmentManager fragmentManager= requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.activity_main,frag,"tag #1");
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void setCoreControlFragment(){
        CoreControlFragment frag = new CoreControlFragment();
        FragmentManager fragmentManager= requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.activity_main,frag,"tag #2");
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void setStuneFragment(){
        StuneFragment frag = new StuneFragment();
        FragmentManager fragmentManager= requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.activity_main,frag,"tag #3");
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    /* Initialise the recyclerView and layout manager */
    public void initRecyclerView(View view){
        recyclerView = view.findViewById(R.id.cpu_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView. setOverScrollMode(View. OVER_SCROLL_NEVER);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        adapter = new RVAdapter( this);
    }

    // ---------Set of helper functions-----------

    public String getFreq(int cluster, String Path){
        try {
            return Utils.read(0, Cpu.PATH.POLICY_PATH +
                    cpuParams.getPolicyArr()[cluster] + Path);
        } catch (UtilException e) {
            return "0";
        }
    }

    public String getGov(int cluster){
        try {
            return Utils.read(0, Cpu.PATH.POLICY_PATH + cpuParams.getPolicyArr()[cluster] +
                    Cpu.PATH.SCALING_GOVERNOR);
        } catch (UtilException e) {
            return "Null";
        }
    }

    public void setMaxFreq(CpuDataModel list, int curCluster){
        service.execute(() -> {
            int Freq = Integer.parseInt(list.MaxFreq);
            utils.write(String.valueOf(Freq), Cpu.PATH.POLICY_PATH +
                    cpuParams.getPolicyArr()[curCluster] + Cpu.PATH.MAX_FREQ);
            updateData(curCluster);
        });
    }

    public void setMinFreq(CpuDataModel list, int curCluster){
        service.execute(() -> {
            int Freq = Integer.parseInt(list.MinFreq);
            utils.write(String.valueOf(Freq), Cpu.PATH.POLICY_PATH +
                    cpuParams.getPolicyArr()[curCluster]
                    + Cpu.PATH.MIN_FREQ);
            updateData(curCluster);
        });
    }

    public void setGov(CpuDataModel list, int curCluster){
        service.execute(() -> {
            utils.write(list.Governor, Cpu.PATH.POLICY_PATH +
                    cpuParams.getPolicyArr()[curCluster]
                    + Cpu.PATH.SCALING_GOVERNOR);
            updateData(curCluster);
        });
    }

    public void updateData(int curCluster){
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> {
            recyclerView.setAdapter(adapter);
            adapter.notifyItemChanged(curCluster);
        });
    }

    class AsyncInitTask extends Thread {
        final View view;
        public AsyncInitTask(View view) {
            this.view = view;
        }

        @Override
        public void run() {
            scrollView = view.findViewById(R.id.cpu_nestedScrollView);
            linearLayout1 = view.findViewById(R.id.cpusets_launch);
            linearLayout2 = view.findViewById(R.id.core_ctrl_launch);
            linearLayout3 = view.findViewById(R.id.stune_launch);
            service = Executors.newSingleThreadExecutor();
            FragmentPersistObject viewModel = new ViewModelProvider(requireActivity())
                    .get(FragmentPersistObject.class);
            cpuParams = viewModel.getCpuParams();
            utils = new Utils(cpuParams);
            utils.initActivityLogger();
            initRecyclerView(view);
            initListeners();
        }
    }

    class AsyncResumeTask extends Thread {
        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void run() {
            Handler handler = new Handler(Looper.getMainLooper());
            initData();
            initList();
            handler.post(() -> {
                adapter.setAdapter(cpuArrayList);
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            });
        }
    }
}