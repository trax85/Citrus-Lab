package com.example.myapplication3.fragments.CpuFragment.CpuSets;

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
import com.topjohnwu.superuser.Shell;

import java.util.ArrayList;
import java.util.List;


public class CpuSetFragment extends Fragment {

    String[] cpusetArr;
    String cpusetPath = "/dev/cpuset";

    List<CpuSetDataModel> cpuSetDataModel;
    RecyclerView recyclerView;
    CpuSetRVAdapter adapter;
    ImageView imageView;
    final static String TAG = "Cpusets Fragment";

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
        initList();
        initRecyclerView(view);
    }

    @Override
    public void onResume() {
        super.onResume();
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    public void initCpusetPath(){
        Shell.Result result = Shell.cmd("ls -d " + cpusetPath + "/*/ | cut -d'/' -f 4").exec();
        List<String> out = result.getOut();
        cpusetArr = out.toArray(new String[out.size()]);
        Log.d(TAG, "Init cpusets dir array");
    }

    public void initList(){
        for(int i = 0; i < cpusetArr.length; i++){
            String FileAttr = getStr(cpusetPath + "/" +cpusetArr[i] + "/cpus");
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
    private String getStr(String path){
        Shell.Result result = Shell.cmd("cat "+path).exec();
        List<String> out = result.getOut();
        return out.get(0);
    }
    public void setCpuSetAttr(CpuSetDataModel list, int position){
        Shell.cmd("echo " + list.cpuSetAttr + " > " +
                cpusetPath + "/" +cpusetArr[position] + "/cpus").exec();
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