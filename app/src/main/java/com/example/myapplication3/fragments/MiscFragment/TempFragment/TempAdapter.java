package com.example.myapplication3.fragments.MiscFragment.TempFragment;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication3.R;

public class TempAdapter extends RecyclerView.Adapter<TempAdapter.ViewHolder> {
    String[][] tempValues;
    TextView textView1, textView2;
    private static int numZones;
    TempFragment fragment;

    public TempAdapter(String[][] values, TempFragment fragment) {
        tempValues = values;
        this.fragment = fragment;
        numZones = fragment.numZones;
    }

    @NonNull
    @Override
    public TempAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.temp_list_items, parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TempAdapter.ViewHolder holder, int position) {
        holder.setData(position);
    }

    @Override
    public int getItemCount() {
        return numZones;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView1 = itemView.findViewById(R.id.temp_header);
            textView2 = itemView.findViewById(R.id.temp_value);
        }

        @SuppressLint("SetTextI18n")
        public void setData(int position) {
            textView1.setText("thermal zone" + position + " - " + tempValues[position][1]);
            textView2.setText(tempValues[position][0] + "\u2103");
        }
    }
}
