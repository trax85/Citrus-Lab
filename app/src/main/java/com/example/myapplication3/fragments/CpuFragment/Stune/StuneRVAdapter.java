package com.example.myapplication3.fragments.CpuFragment.Stune;

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
import com.topjohnwu.superuser.Shell;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class StuneRVAdapter extends RecyclerView.Adapter<StuneRVAdapter.ViewHolder> {
    StuneFragment fragment;
    String[] stuneItems;
    String[] subItems;
    private TextView textView1;
    View view;
    RecyclerView[] recyclerViewArr;
    StuneSubRVAdapter adapter;
    ExecutorService executor = Executors.newSingleThreadExecutor();
    Handler handler = new Handler(Looper.getMainLooper());
    final static String TAG = "RV Parent";

    public StuneRVAdapter(String[] string, StuneFragment fragment, View view){
        stuneItems = string;
        this.fragment = fragment;
        this.view = view;
        recyclerViewArr = new RecyclerView[stuneItems.length];
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "Create Main Layout");
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.stune_list_items,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.d(TAG,"OnBIndVH");
        String itemName = stuneItems[position];
        executor.execute(() -> {
            Shell.Result result;
            List<String> out;
            result = Shell.cmd("ls " + StuneFragment.stunePath + "/" + itemName +
                    "/sched* | cut -d'/' -f5").exec();
            out = result.getOut();
            subItems = out.toArray(new String[0]);
            adapter = new StuneSubRVAdapter(subItems, fragment, itemName, this);
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

    public void setItem(StuneSubRVAdapter adapter, String itemName, String value){
        int pos = Arrays.asList(stuneItems).indexOf(adapter.headerName);
        Shell.cmd(" echo " + value + " > " + StuneFragment.stunePath
                + "/" + adapter.headerName + "/" + itemName).exec();
        recyclerViewArr[pos].setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }
}