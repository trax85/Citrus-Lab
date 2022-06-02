package com.example.myapplication3.fragments.CpuFragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication3.R;
import com.example.myapplication3.fragments.CpuFragment.GovTunable.TunableFragment;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RVAdapter extends RecyclerView.Adapter<RVAdapter.ViewHolder> {
    private List<CpuDataModel> list;
    private TextView textView1, textView2, textView3, textView4;
    private LinearLayout linearLayout1, linearLayout2, linearLayout3, linearLayout4;
    private CpuFragment fragment;
    final static String TAG = "RVAdapter";

    public RVAdapter(CpuFragment fragment){
        this.fragment = fragment;
    }
    public void setAdapter(List<CpuDataModel> list){
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cpu_list_items,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String maxFreq = list.get(position).getMaxFreq();
        String minFreq = list.get(position).getMinFreq();
        String govName = list.get(position).getGovernor();
        String clusterName = list.get(position).getClusterName();
        Log.d(TAG,"Max:" + maxFreq + " min:" + minFreq + "clusterName" + clusterName);
        holder.setData(maxFreq, minFreq, clusterName, govName);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView1 = itemView.findViewById(R.id.clusterName);
            textView2 = itemView.findViewById(R.id.textviewMaxFreq);
            textView3 = itemView.findViewById(R.id.textviewMinFreq);
            textView4 = itemView.findViewById(R.id.textviewGov);
            linearLayout1 = itemView.findViewById(R.id.layoutMaxFreq);
            linearLayout2 = itemView.findViewById(R.id.layoutMinFreq);
            linearLayout3 = itemView.findViewById(R.id.layoutGov);
            linearLayout4 = itemView.findViewById(R.id.layoutTune);
        }

        public void setData(String maxFreq, String minFreq, String clusterName, String govName) {
            ExecutorService service = Executors.newSingleThreadExecutor();
            int curCluster = Arrays.asList(fragment.clusterNames).indexOf(clusterName);

            maxFreq = Integer.parseInt(maxFreq) / 1000 + " Mhz";
            minFreq = Integer.parseInt(minFreq) / 1000 + " Mhz";

            textView1.setText(clusterName);
            textView2.setText(maxFreq);
            textView3.setText(minFreq);
            textView4.setText(govName);

            //Heavy job get it done on a separate thread
            service.execute(() -> {
                linearLayout1.setOnClickListener(v -> showMaxFreqDialog(curCluster));
                linearLayout2.setOnClickListener(v -> showMinFreqDialog(curCluster));
                linearLayout3.setOnClickListener(v -> showGovDialog(curCluster));
                linearLayout4.setOnClickListener(v -> {
                    TunableFragment fragment2 = new TunableFragment();
                    Bundle bundle = new Bundle();
                    String curGov = fragment.getGov(curCluster);
                    bundle.putString("path", fragment.policyArr[curCluster]);
                    bundle.putString("gov" , curGov);
                    fragment2.setArguments(bundle);

                    FragmentManager fragmentManager= fragment.getActivity().getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.activity_main,fragment2,"tag");
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                });
            });
        }
    }

    public void showMaxFreqDialog(int curCluster){
        String[] AppFreqArr = fragment.AppendedFreqList.get(curCluster); //Mhz
        String[] FreqList = fragment.FreqList.get(curCluster);  //Khz
        String Freq = fragment.getFreq(curCluster, fragment.maxFreqPath);
        int checkedItem = Arrays.asList(FreqList).indexOf(Freq);
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(fragment.getActivity());
        builder.setTitle("Choose Max Frequency");
        builder.setSingleChoiceItems(AppFreqArr, checkedItem, (dialog, which) -> {
            list.get(curCluster).setMaxFreq(FreqList[which]);
            list.get(curCluster).setAppMaxFreq(AppFreqArr[which]);
            //Reduce stress on single thread
            fragment.setMaxFreq(list.get(curCluster), curCluster);
            dialog.dismiss();
        });
        builder.show();
    }

    public void showMinFreqDialog(int curCluster){
        String[] AppFreqArr = fragment.AppendedFreqList.get(curCluster);    //Mhz
        String[] FreqList = fragment.FreqList.get(curCluster);  //Khz
        String Freq = fragment.getFreq(curCluster, fragment.minFreqPath);
        int checkedItem = Arrays.asList(FreqList).indexOf(Freq);
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(fragment.getActivity());
        builder.setTitle("Choose Max Frequency");
        builder.setSingleChoiceItems(AppFreqArr, checkedItem, (dialog, which) -> {
            list.get(curCluster).setMinFreq(FreqList[which]);
            list.get(curCluster).setAppMinFreq(AppFreqArr[which]);
            fragment.setMinFreq(list.get(curCluster), curCluster);
            dialog.dismiss();
        });
        builder.show();
    }

    public void showGovDialog(int curCluster){
        String[] aviGov = fragment.GovArr;
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(fragment.getActivity());
        builder.setTitle("Choose Governor");
        String curGov = fragment.getGov(curCluster);
        int checkedItem = Arrays.asList(aviGov).indexOf(curGov);
        Log.d(TAG,"Checked:" + checkedItem);
        builder.setSingleChoiceItems(aviGov, checkedItem, (dialog, which) -> {
            list.get(curCluster).Governor = aviGov[which];
            fragment.setGov(list.get(curCluster), curCluster);
            dialog.dismiss();
        });
        builder.show();
    }

}
