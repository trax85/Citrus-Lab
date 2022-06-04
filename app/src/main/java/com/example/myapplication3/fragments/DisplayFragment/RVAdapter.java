package com.example.myapplication3.fragments.DisplayFragment;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication3.R;
import com.topjohnwu.superuser.Shell;

import java.util.List;

public class RVAdapter extends RecyclerView.Adapter<RVAdapter.ViewHolder> {
    private final List<DisplayDataModel> list;
    private TextView textView1;
    private TextView textView2;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private Switch aSwitch;
    final static String TAG = "Recycler view";
    public RVAdapter(List<DisplayDataModel> list){
        this.list = list;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.display_list_items,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String primaryContent = list.get(position).getPrimaryContent();
        String secondaryContent = list.get(position).getSecondaryContent();
        String[] filePath = list.get(position).getFilePath();
        int[] actionSet = list.get(position).getActionSet();
        holder.setData(primaryContent, secondaryContent, filePath, actionSet);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            textView1 = itemView.findViewById(R.id.primaryDesc);
            textView2 = itemView.findViewById(R.id.contentDesc);
            aSwitch = itemView.findViewById(R.id.contentSwitch);
        }

        public void setData(String primaryContent, String secondaryContent, String[] filePath, int[] actionSet) {
            Shell.Result res;
            List<String> out;
            //Hardcoded as disable usually is 0
            int actionUnset = 0;
            Log.d(TAG, "Recycler view");
            textView1.setText(primaryContent);
            textView2.setText(secondaryContent);
            for (int i = 0; i < filePath.length; i++) {
                res = Shell.cmd("cat " + filePath[i]).exec();
                out = res.getOut();
                if (Integer.parseInt(out.get(0)) == actionSet[i]) {
                    aSwitch.setChecked(true);
                }
            }
            aSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked){
                    for (int i = 0; i < filePath.length; i++)
                        Shell.cmd("echo " + actionSet[i] + "  > " + filePath[i]).exec();
                } else {
                    for (String s : filePath) Shell.cmd("echo " + actionUnset + " > " + s).exec();
                }
            });
        }
    }
}
