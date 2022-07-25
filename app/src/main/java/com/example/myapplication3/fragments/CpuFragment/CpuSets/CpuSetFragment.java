package com.example.myapplication3.fragments.CpuFragment.CpuSets;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.myapplication3.R;
import com.example.myapplication3.tools.UtilException;
import com.example.myapplication3.tools.Utils;

import java.util.ArrayList;
import java.util.List;


public class CpuSetFragment extends Fragment {
    final static String TAG = "CpusetsFragment";
    String cpusetPath = "/dev/cpuset";

    String[] cpusetArr;
    List<CpuSetDataModel> cpuSetDataModel;
    RecyclerView recyclerView;
    CpuSetRVAdapter adapter;
    ImageView imageView;
    boolean isCpusetsInited = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_cpuset, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        imageView = view.findViewById(R.id.ic_core_ctl_back);
        imageView.setOnClickListener(v -> getActivity().onBackPressed());
        cpuSetDataModel = new ArrayList<>();
        initCpusetPath();
        if(!isCpusetsInited)
            return;
        initList();
        initRecyclerView(view);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onResume() {
        super.onResume();
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    public void initCpusetPath(){
        try {
            cpusetArr = Utils.readGetArr("ls -d " + cpusetPath + "/*/ | cut -d'/' -f 4");
            isCpusetsInited = true;
        } catch (UtilException e) {}
    }

    public void initList(){
        for(int i = 0; i < cpusetArr.length; i++){
            String FileAttr;
            try {
                FileAttr = Utils.read(0, cpusetPath + "/" +cpusetArr[i] + "/cpus");
            } catch (UtilException e) {
                return;
            }
            CpuSetDataModel parentDataModel = new CpuSetDataModel(cpusetArr[i], FileAttr);
            cpuSetDataModel.add(parentDataModel);
        }
    }

    /* Initialise the recyclerView and layout manager */
    public void initRecyclerView(View view){
        recyclerView = view.findViewById(R.id.cpuset_items);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView. setOverScrollMode(View. OVER_SCROLL_NEVER);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new CpuSetRVAdapter(cpuSetDataModel, this);
        Log.d(TAG, "Init Recycler Viewer");
    }

    // ---------Set of helper functions-----------
    public void setCpuSetAttr(CpuSetDataModel list, int position){
        Utils.write(list.cpuSetAttr,
                cpusetPath + "/" +cpusetArr[position] + "/cpus");
        updateData(position);
    }
    public void updateData(int position){
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> {
            recyclerView.setAdapter(adapter);
            adapter.notifyItemChanged(position);
        });
    }
}