package com.example.myapplication3.fragments.CpuFragment.Stune;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication3.R;
import com.example.myapplication3.FragmentDataModels.Cpu;
import com.example.myapplication3.tools.UtilException;
import com.example.myapplication3.tools.Utils;

import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class StuneRVAdapter extends RecyclerView.Adapter<StuneRVAdapter.ViewHolder> {
    StuneFragment fragment;
    String[] stuneItems;
    String[] stuneSubItems;
    private TextView textView1;
    View view;
    RecyclerView[] recyclerViewArr;
    StuneSubRVAdapter adapter;
    ExecutorService executor = Executors.newSingleThreadExecutor();
    Handler handler = new Handler(Looper.getMainLooper());
    final static String TAG = "RV Parent";
    Utils utils;

    public StuneRVAdapter(String[] string, StuneFragment fragment, View view){
        stuneItems = string;
        this.fragment = fragment;
        this.view = view;
        recyclerViewArr = new RecyclerView[stuneItems.length];
        utils = fragment.utils;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.stune_list_items,parent,false);
        return new ViewHolder(view);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.d(TAG,"OnBIndVH");
        String itemName = stuneItems[position];
        executor.execute(() -> {
            try {
                stuneSubItems = Utils.readGetArr("ls " + Cpu.PATH.STUNE_PATH + "/" + itemName +
                        "/sched* | cut -d'/' -f5");
            } catch (UtilException e) {
                return;
            }
            adapter = new StuneSubRVAdapter(stuneSubItems, fragment, itemName, this);
            handler.post(() -> {
                holder.recyclerView.setLayoutManager(new LinearLayoutManager(fragment.getContext(), LinearLayoutManager.VERTICAL, false));
                holder.recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                recyclerViewArr[position] = holder.recyclerView;
            });
        });
        holder.setData(itemName);
    }

    @Override
    public int getItemCount() {
        return stuneItems.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        RecyclerView recyclerView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView1 = itemView.findViewById(R.id.stune_header);
            recyclerView = itemView.findViewById(R.id.stune_subitems);
        }

        public void setData(String itemName) {
            textView1.setText(itemName);
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setItem(StuneSubRVAdapter adapter, String itemName, String value){
        int pos = Arrays.asList(stuneItems).indexOf(adapter.headerName);
        utils.write(value, Cpu.PATH.STUNE_PATH
                + "/" + adapter.headerName + "/" + itemName);
        recyclerViewArr[pos].setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }
}