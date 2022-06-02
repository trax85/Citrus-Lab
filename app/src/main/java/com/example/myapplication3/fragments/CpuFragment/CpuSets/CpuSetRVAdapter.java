package com.example.myapplication3.fragments.CpuFragment.CpuSets;

import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication3.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.List;

public class CpuSetRVAdapter extends RecyclerView.Adapter<CpuSetRVAdapter.ViewHolder> {

    private List<CpuSetDataModel> list;
    CpuSetFragment fragment;
    TextView textView1, textView2;
    LinearLayout layout;
    final static String TAG = "CpusetRVA";
    public CpuSetRVAdapter(List<CpuSetDataModel> list, CpuSetFragment fragment) {
        this.list = list;
        this.fragment = fragment;
    }

    @NonNull
    @Override
    public CpuSetRVAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cpuset_list_items,
                parent,false);
        return new CpuSetRVAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CpuSetRVAdapter.ViewHolder holder, int position) {
        String headerName = list.get(position).getHeaderName();
        String cpuSetAttr = list.get(position).getCpuSetAttr();
        holder.setData(headerName, cpuSetAttr, position);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            Log.d(TAG,"ViewHolder init");
            textView1 = itemView.findViewById(R.id.cpuset_header);
            textView2 = itemView.findViewById(R.id.cpuset_item_val);
            layout = itemView.findViewById(R.id.cpuset_layout);
        }

        public void setData(String headerName, String cpuSetAttr, int position) {
            textView1.setText(headerName);
            textView2.setText(cpuSetAttr);
            layout.setOnClickListener(v -> {
                MaterialAlertDialogBuilder builder = new
                        MaterialAlertDialogBuilder(fragment.getActivity());
                final EditText weightInput = new EditText(fragment.getActivity());

                builder.setTitle("Edit" + list.get(position).getHeaderName());
                weightInput.setInputType(InputType.TYPE_CLASS_TEXT);
                weightInput.setHint(cpuSetAttr);
                builder.setView(weightInput);

                builder.setPositiveButton("OK", (dialog, which) -> {
                    list.get(position).setCpuSetAttr(weightInput.getText().toString());
                    fragment.setCpuSetAttr(list.get(position),position);
                });builder.setNegativeButton("Cancle", (dialog, which) -> dialog.cancel());
                builder.show();
            });
        }
    }
}
