package com.example.myapplication3.fragments.CpuFragment.Stune;

import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication3.R;
import com.example.myapplication3.fragment_data_models.Cpu;
import com.example.myapplication3.tools.UtilException;
import com.example.myapplication3.tools.Utils;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class StuneSubRVAdapter extends RecyclerView.Adapter<StuneSubRVAdapter.ViewHolder>{
    final static String TAG = "RV Child";
    StuneFragment fragment;
    String headerName;
    String[] subItemNames, subItemValue;
    ConstraintLayout constraintLayout;
    StuneRVAdapter stuneRVAdapter;
    private TextView textView1, textView2;

    public StuneSubRVAdapter(String[] string, StuneFragment fragment, String headerName,
                             StuneRVAdapter adapter){
        subItemNames = string;
        this.fragment = fragment;
        this.headerName = headerName;
        stuneRVAdapter = adapter;
        initData();
    }

    public void initData(){
        subItemValue = new String[subItemNames.length];
        for(int i = 0; i < subItemNames.length; i++){
            try {
                subItemValue[i] = Utils.read(0, Cpu.PATH.STUNE_PATH + "/" +
                        headerName + "/" + subItemNames[i]);
            } catch (UtilException e) {
                subItemValue[i] = "read error";
            }
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.stune_sublist,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String itemName = subItemNames[position];
        String itemValue = subItemValue[position];
        holder.setData(itemName, itemValue, position,this);
    }

    @Override
    public int getItemCount() {
        return subItemNames.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView1 = itemView.findViewById(R.id.stune_item_name);
            textView2 = itemView.findViewById(R.id.stune_itemval);
            constraintLayout = itemView.findViewById(R.id.stune_itemlayout);
        }

        public void setData(String itemName, String itemVal,int pos,StuneSubRVAdapter adapter) {
            textView1.setText(itemName);
            textView2.setText(itemVal);
            constraintLayout.setOnClickListener(v -> {
                MaterialAlertDialogBuilder builder = new
                        MaterialAlertDialogBuilder(fragment.requireActivity());
                final EditText weightInput = new EditText(fragment.getActivity());

                builder.setTitle("Edit " + itemName);
                weightInput.setInputType(InputType.TYPE_CLASS_TEXT);
                weightInput.setHint(itemVal);
                builder.setView(weightInput);

                builder.setPositiveButton("OK", (dialog, which) -> {
                    String val = weightInput.getText().toString();
                    subItemValue[pos] = val;
                    stuneRVAdapter.setItem(adapter, itemName, val);
                });builder.setNegativeButton("Cancle", (dialog, which) -> dialog.cancel());
                builder.show();
            });
        }
    }
}
