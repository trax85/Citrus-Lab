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
import com.example.myapplication3.tools.UtilException;
import com.example.myapplication3.tools.Utils;

import java.util.List;

public class RVAdapter extends RecyclerView.Adapter<RVAdapter.ViewHolder> {
    final static String TAG = "Recycler view";
    private final List<DisplayDataModel> list;
    private TextView textView1;
    private TextView textView2;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private Switch aSwitch;

    public RVAdapter(List<DisplayDataModel> list){
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.display_list_items,
                parent,false);
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

        public void setData(String primaryContent, String secondaryContent, String[] filePath,
                            int[] actionSet) {
            //Hardcoded as disable usually is 0
            int actionUnset = 0;
            textView1.setText(primaryContent);
            textView2.setText(secondaryContent);
            for (String value : filePath) {
                try {
                    String out = Utils.read(0, value);
                    if(!out.equals("0"))
                        aSwitch.setChecked(true);
                } catch (UtilException e) {
                    Log.d(TAG, "Error encountered");
                    return;
                }
            }

            aSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked){
                    for (int i = 0; i < filePath.length; i++)
                        Utils.write(String.valueOf(actionSet[i]), filePath[i]);
                } else {
                    for (String path : filePath) Utils.write(String.valueOf(actionUnset), path);
                }
            });
        }
    }
}
