package com.example.myapplication3.fragments.DisplayFragment;

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

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    private List<DisplayList> list;
    private TextView textView1;
    private TextView textView2;
    private Switch aSwitch;

    public RecyclerViewAdapter(List<DisplayList> list){
        this.list = list;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_items,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String primaryContent = list.get(position).getPrimaryContent();
        String secondaryContent = list.get(position).getSecondaryContent();
        String filePath = list.get(position).getFilePath();
        int actionSet = list.get(position).getActionSet();
        int actionUnset = list.get(position).getActionUnset();
        holder.setData(primaryContent, secondaryContent, filePath, actionSet, actionUnset);
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

        public void setData(String primaryContent, String secondaryContent, String filePath, int actionSet, int actionUnset) {
            Shell.Result res;
            List<String> out;
            textView1.setText(primaryContent);
            textView2.setText(secondaryContent);
            res = Shell.cmd("cat "+filePath).exec();
            out = res.getOut();
            if(Integer.parseInt(out.get(0)) == actionSet){
                aSwitch.setChecked(true);
            }
            aSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked == true)
                    Shell.cmd("echo "+actionSet+"  > "+filePath).exec();
                else
                    Shell.cmd("echo "+actionUnset+" > "+filePath).exec();
            });
        }
    }
}
