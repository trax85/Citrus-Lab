package com.example.myapplication3.fragments.CpuFragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.myapplication3.R;
import com.example.myapplication3.fragments.CpuFragment.CoreControl.CoreControlFragment;
import com.example.myapplication3.fragments.CpuFragment.CpuSets.CpuSetFragment;
import com.example.myapplication3.fragments.CpuFragment.Stune.StuneFragment;
import com.example.myapplication3.fragments.HomeFragment.CpuStats;
import com.example.myapplication3.fragments.HomeFragment.AviFreqData;
import com.topjohnwu.superuser.Shell;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CpuFragment extends Fragment {
    ArrayList<CpuDataModel> cpuArrayList;
    public static String policyPath = "/sys/devices/system/cpu/cpufreq";
    String maxFreqPath = "/scaling_max_freq";
    String minFreqPath = "/scaling_min_freq";
    String governorPath = "/scaling_governor";
    String aviGovPath = "/scaling_available_governors";
    String[] policyArr, GovArr;
    static final String TAG = "Cpustats";
    String[][] FreqArr, AppendedFreqArr;
    boolean[] cpuOnline;
    static String[] clusterNames = {"Little Cluster", "Big Cluster", "Prime Cluster"};
    RVAdapter adapter;
    RecyclerView recyclerView;
    ExecutorService service;
    LinearLayout linearLayout1, linearLayout2, linearLayout3;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_cpu, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        linearLayout1 = view.findViewById(R.id.cpusets_launch);
        linearLayout2 = view.findViewById(R.id.core_ctrl_launch);
        linearLayout3 = view.findViewById(R.id.stune_launch);
        service = Executors.newSingleThreadExecutor();
        initRecyclerView(view);
        initListeners();
    }

    @Override
    public void onResume() {
        super.onResume();

        init();
        initList();
        adapter.setAdapter(cpuArrayList);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    /* Initialise the cluster count and policy paths for the respective clusters */
    public void init(){
        AviFreqData viewModel = new ViewModelProvider(requireActivity()).get(AviFreqData.class);
        FreqArr = viewModel.getCpuFreqArr();
        AppendedFreqArr = viewModel.getAppCpuFreqArr();
        policyArr = viewModel.getPolicyAttr();
        cpuOnline = viewModel.getCpuOnline();
        GovArr =  CpuStats.splitStrings(policyPath + policyArr[0] + aviGovPath);
    }

    /* Initialise cpuArrayList and append 'Mhz' to cpuArrayList list with variables */
    private void initList(){
        cpuArrayList = new ArrayList<>();
        for(int i = 0; i < policyArr.length; i++){
            String maxFreq = getStr(policyPath + policyArr[i] + maxFreqPath);
            String minFreq = getStr(policyPath + policyArr[i] + minFreqPath);
            String govName = getStr(policyPath + policyArr[i] + governorPath);
            CpuDataModel cpuList = new CpuDataModel(maxFreq, minFreq, clusterNames[i], govName);
            cpuArrayList.add(cpuList);
        }
    }

    public void initListeners(){
        linearLayout1.setOnClickListener(v -> {
            CpuSetFragment frag = new CpuSetFragment();
            FragmentManager fragmentManager= getActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.activity_main,frag,"tag #2");
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        });
        linearLayout2.setOnClickListener(v -> {
            CoreControlFragment frag = new CoreControlFragment();
            FragmentManager fragmentManager= getActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.activity_main,frag,"tag #3");
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        });
        linearLayout3.setOnClickListener(v -> {
            StuneFragment frag = new StuneFragment();
            FragmentManager fragmentManager= getActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.activity_main,frag,"tag #3");
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        });
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
    private String getStr(String path){
        Shell.Result result = Shell.cmd("cat "+path).exec();
        List<String> out = result.getOut();
        return out.get(0);
    }

    public String getFreq(int cluster, String Path){
        return getStr(policyPath + policyArr[cluster] + Path);
    }

    public String getGov(int cluster){
        return getStr(policyPath + policyArr[cluster] + governorPath);
    }

    public void setMaxFreq(CpuDataModel list, int curCluster){
        service.execute(() -> {
            int Freq = Integer.parseInt(list.MaxFreq);
            Shell.cmd("echo " + Freq + " > " + policyPath + policyArr[curCluster]
                    + maxFreqPath).exec();
            updateData(curCluster);
        });
    }

    public void setMinFreq(CpuDataModel list, int curCluster){
        service.execute(() -> {
            int Freq = Integer.parseInt(list.MinFreq);
            Shell.cmd("echo " + Freq + " > " + policyPath + policyArr[curCluster]
                    + minFreqPath).exec();
            updateData(curCluster);
        });
    }

    public void setGov(CpuDataModel list, int curCluster){
        service.execute(() -> {
            Shell.cmd("echo " + list.Governor + " > " +
                    policyPath + policyArr[curCluster] + governorPath).exec();
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
}