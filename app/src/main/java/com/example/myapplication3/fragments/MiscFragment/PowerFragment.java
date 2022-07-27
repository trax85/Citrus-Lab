package com.example.myapplication3.fragments.MiscFragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.myapplication3.R;
import com.example.myapplication3.tools.UtilException;
import com.example.myapplication3.tools.Utils;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.Arrays;

public class PowerFragment extends Fragment {
    final static String PS = "/sys/kernel/power_suspend/power_suspend_mode";
    final static String PSver = "/sys/kernel/power_suspend/power_suspend_version";
    final static String[] PSstates  = {"Autosleep", "Userspace", " LCD Panel", "Hybrid"};
    TextView powerSuspend, powerSuspendVer;
    RelativeLayout relativeLayout;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_power, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        initPaths();
    }

    void initViews(View view){
        powerSuspend = view.findViewById(R.id.powerSus);
        powerSuspendVer = view.findViewById(R.id.psver);
        relativeLayout = view.findViewById(R.id.ps_layout);
    }

    void initPaths(){
        String str;
        try {
            str = Utils.read(0, PS);
            powerSuspend.setText(PSstates[Integer.parseInt(str)]);
            relativeLayout.setOnClickListener(v -> showDialog());
        } catch (UtilException e) {
            powerSuspend.setText("Not present");
        }
        try {
            str = Utils.read(0,PSver);
            powerSuspendVer.setText(str);
        } catch (UtilException e) {
            powerSuspendVer.setText("Unknown");
        }
    }
    public void showDialog(){
        MaterialAlertDialogBuilder builder =
                new MaterialAlertDialogBuilder(requireActivity());
        builder.setTitle("Choose Type");
        int checkedItem = Arrays.asList(PSstates).indexOf(powerSuspend.getText().toString());
        builder.setSingleChoiceItems(PSstates, checkedItem, (dialog, which) -> {
            powerSuspend.setText(PSstates[which]);
            Utils.write(String.valueOf(which), PS);
            dialog.dismiss();
        });
        builder.show();
    }
}