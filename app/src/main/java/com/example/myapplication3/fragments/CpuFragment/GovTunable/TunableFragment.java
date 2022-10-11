package com.example.myapplication3.fragments.CpuFragment.GovTunable;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.myapplication3.R;
import com.example.myapplication3.fragment_data_models.Cpu;
import com.example.myapplication3.fragments.HomeFragment.FragmentPersistObject;
import com.example.myapplication3.tools.UtilException;
import com.example.myapplication3.tools.Utils;

import java.util.ArrayList;
import java.util.List;

public class TunableFragment extends Fragment {
    final static String TAG = "TunableFrag";
    String policyPath, curGov, policy;
    public String[] itemsArr;
    TextView textView;
    List<TunableDataModel> ItemList;
    TunableRVAdapter adapter;
    RecyclerView recyclerView;
    ImageView imageView;
    boolean dataSetReceived = true;
    Cpu.Params cpuParams;
    Utils utils;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tunable, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViewModel();
        initViews(view);
        Bundle bundle = getArguments();
        if (bundle != null) {
            policyPath = bundle.getString("path");
            curGov = "/" + bundle.getString("gov");
        }
        policy = Cpu.PATH.POLICY_PATH;
        ItemList = new ArrayList<>();
        initItems();
        if(dataSetReceived){
            textView.setVisibility(View.INVISIBLE);
            initItemList();
            initTunableRVAdapter(view);
        }
    }

    public void initViewModel(){
        FragmentPersistObject viewModel = new ViewModelProvider(requireActivity())
                .get(FragmentPersistObject.class);
        cpuParams = viewModel.getCpuParams();
        utils = new Utils(cpuParams);
        utils.initActivityLogger();
    }

    public void initViews(View view){
        textView = view.findViewById(R.id.no_items);
        imageView = view.findViewById(R.id.ic_core_ctl_back);
        imageView.setOnClickListener(v -> requireActivity().onBackPressed());
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onResume() {
        super.onResume();
        if(!dataSetReceived)
            return;
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    public void initItems(){
        try {
            itemsArr = Utils.readGetArr("ls "+ policy + policyPath +curGov);
        } catch (UtilException e) {
            dataSetReceived = false;
        }
    }

    public void initItemList(){
        for (String s : itemsArr) {
            String ItemVal;
            try {
                ItemVal = Utils.read(0,policy + policyPath + curGov + "/" + s);
            } catch (UtilException e) {
                ItemVal = "0";
            }
            TunableDataModel tunablesList = new TunableDataModel(s, ItemVal);
            ItemList.add(tunablesList);
        }
    }

    public void initTunableRVAdapter(View view){
        recyclerView = view.findViewById(R.id.tunable_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new TunableRVAdapter(ItemList, this);
    }

    public void setItemVal(TunableDataModel list, int index){
        Log.d(TAG, "Set gov value");
        utils.write(list.tunableAttr,policy + policyPath + curGov
                + "/" + list.tunableName);
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> {
            recyclerView.setAdapter(adapter);
            adapter.notifyItemChanged(index);
        });
    }
}