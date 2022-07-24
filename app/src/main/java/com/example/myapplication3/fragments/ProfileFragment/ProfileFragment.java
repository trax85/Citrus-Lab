package com.example.myapplication3.fragments.ProfileFragment;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RawRes;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.myapplication3.R;
import com.google.android.material.button.MaterialButton;
import com.topjohnwu.superuser.Shell;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.atomic.AtomicReference;

public class ProfileFragment extends Fragment {
    MaterialButton power_button, balance_button, perf_button;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        setListeners();
    }

    void initViews(View view){
        power_button = view.findViewById(R.id.button_power);
        balance_button = view.findViewById(R.id.button_balance);
        perf_button = view.findViewById(R.id.button_performance);
    }

    void setListeners(){
        power_button.setOnClickListener(v -> {
                String out = readTextFile(getContext(), R.raw.power);
                Toast.makeText(getContext(), "Applying Power Profile...",Toast.LENGTH_SHORT).show();
                Shell.cmd(out).exec();
        });
        balance_button.setOnClickListener(v -> {
            String out = readTextFile(getContext(), R.raw.balance);
            Toast.makeText(getContext(), "Applying Balance Profile...",Toast.LENGTH_SHORT).show();
            Shell.cmd(out).exec();
        });
        perf_button.setOnClickListener(v -> {
            String out = readTextFile(getContext(), R.raw.performance);
            Toast.makeText(getContext(), "Applying Performance Profile...",Toast.LENGTH_SHORT).show();
            Shell.cmd(out).exec();
        });
    }

    public static String readTextFile(Context context, @RawRes int id){
        InputStream inputStream = context.getResources().openRawResource(id);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        try {
            int i = inputStream.read();
            while (i != -1) {
                byteArrayOutputStream.write(i);
                i = inputStream.read();
            }
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return byteArrayOutputStream.toString();
    }
}