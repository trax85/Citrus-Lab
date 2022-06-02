package com.example.myapplication3.fragments.CpuFragment.GovTunable;

import android.content.DialogInterface;
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

import java.util.Arrays;
import java.util.List;

public class TunableRVAdapter extends RecyclerView.Adapter<TunableRVAdapter.ViewHolder>{
    private List<TunableDataModel> list;
    TextView textView1, textView2;
    LinearLayout layout;
    TunableFragment fragment;
    final static String TAG = "TRVAdapter";
    public TunableRVAdapter(List<TunableDataModel> list, TunableFragment activity) {
            this.list = list;
            this.fragment =activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tunable_list_items,
                parent,false);
        return new TunableRVAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TunableRVAdapter.ViewHolder holder, int position) {
        String tunableNames = list.get(position).getTunableName();
        String tunableAttr = list.get(position).getTunableAttr();
        Log.d(TAG, "View Builder attr" + tunableAttr);
        holder.setData(tunableNames, tunableAttr);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView1 = itemView.findViewById(R.id.nameOfTunable);
            textView2 = itemView.findViewById(R.id.valOfAttr);
            layout = itemView.findViewById(R.id.layoutTunable);
        }

        public void setData(String tunableNames, String tunableAttr) {
            int itemNum = Arrays.asList(fragment.itemsArr).indexOf(tunableNames);
            textView1.setText(tunableNames);
            textView2.setText(tunableAttr);
            layout.setOnClickListener(v -> {
                MaterialAlertDialogBuilder builder = new
                        MaterialAlertDialogBuilder(fragment.getActivity());
                final EditText weightInput = new EditText(fragment.getActivity());

                builder.setTitle("Edit Tunable");
                weightInput.setInputType(InputType.TYPE_CLASS_TEXT);
                weightInput.setHint(tunableAttr);
                builder.setView(weightInput);

                builder.setPositiveButton("OK", (dialog, which) -> {
                    list.get(itemNum).setTunableAttr(weightInput.getText().toString());
                    fragment.setItemVal(list.get(itemNum), itemNum);
                });builder.setNegativeButton("Cancle", (dialog, which) -> dialog.cancel());
                builder.show();
            });
        }
    }
}
