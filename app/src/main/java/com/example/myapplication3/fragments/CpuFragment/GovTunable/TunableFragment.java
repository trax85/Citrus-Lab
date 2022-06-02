package com.example.myapplication3.fragments.CpuFragment.GovTunable;

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
import com.example.myapplication3.fragments.CpuFragment.CpuFragment;
import com.topjohnwu.superuser.Shell;

import java.util.ArrayList;
import java.util.List;

public class TunableFragment extends Fragment {
    String policyPath, curGov, policy;
    public String[] itemsArr, appItemArr;
    List<TunableDataModel> ItemList;
    TunableRVAdapter adapter;
    RecyclerView recyclerView;
    ImageView imageView;
    static final String TAG = "TunableFrag";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tunable, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        imageView = view.findViewById(R.id.ic_core_ctl_back);
        imageView.setOnClickListener(v -> getActivity().onBackPressed());
        Bundle bundle = getArguments();
        policyPath = bundle.getString("path");
        curGov = "/" + bundle.getString("gov");
        policy = CpuFragment.policyPath;
        ItemList = new ArrayList<>();
        initItems();
        initItemList();
        initTRVAdapter(view);
    }

    @Override
    public void onResume() {
        super.onResume();
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    public void initItems(){
        Shell.Result result = Shell.cmd("ls "+ policy + policyPath +curGov).exec();
        List<String> out = result.getOut();
        itemsArr = out.toArray(new String[out.size()]);
    }

    public void initItemList(){
        for (String s : itemsArr) {
            String ItemVal = getStr(policy + policyPath + curGov + "/" + s);
            TunableDataModel tunablesList = new TunableDataModel(s, ItemVal);
            ItemList.add(tunablesList);
        }
    }

    public void initTRVAdapter(View view){
        recyclerView = view.findViewById(R.id.tunable_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new TunableRVAdapter(ItemList, this);
    }

    private String getStr(String path){
        Shell.Result result = Shell.cmd("cat "+path).exec();
        List<String> out = result.getOut();
        return out.get(0);
    }
    public void setItemVal(TunableDataModel list, int index){
        Log.d(TAG, "Set gov value");
        Shell.cmd("echo " + list.tunableAttr + " > " + policy +policyPath + curGov
                + "/" + list.tunableName).exec();
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> {
            recyclerView.setAdapter(adapter);
            adapter.notifyItemChanged(index);
        });
    }
}