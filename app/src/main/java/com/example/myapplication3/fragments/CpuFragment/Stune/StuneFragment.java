package com.example.myapplication3.fragments.CpuFragment.Stune;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.myapplication3.R;
import com.example.myapplication3.fragments.CpuFragment.Cpu;
import com.example.myapplication3.tools.UtilException;
import com.example.myapplication3.tools.Utils;

import java.util.Arrays;
import java.util.Objects;

public class StuneFragment extends Fragment {
    ImageView imageView;
    StuneRVAdapter adapter;
    RecyclerView recyclerView;
    boolean stuneInited = true;
    Cpu.Params cpuParams;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_stune, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        imageView = view.findViewById(R.id.ic_core_ctl_back);
        imageView.setOnClickListener(v -> requireActivity().onBackPressed());
        cpuParams = new Cpu.Params().getInstance();
        initData();
        initRecyclerView(view);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onResume() {
        super.onResume();
        if(!stuneInited)
            return;
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    public void initData(){
        try {
            cpuParams.setStuneItems(Utils.readGetArr("ls -d " + Cpu.PATH.STUNE_PATH +
                    "/*/ | cut -d'/' -f4"));
        } catch (UtilException e) {
            stuneInited = false;
        }
    }

    private void initRecyclerView(View view){
        recyclerView = view.findViewById(R.id.stune_items);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView. setOverScrollMode(View. OVER_SCROLL_NEVER);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new StuneRVAdapter(cpuParams.getStuneItems(), this, view);
        recyclerView.setHasFixedSize(true);
    }
}
