package com.example.myapplication3.fragments.DisplayFragment;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;

import com.example.myapplication3.R;
import com.example.myapplication3.tools.UtilException;
import com.example.myapplication3.tools.Utils;

import java.util.Objects;

public class DisplayFragment extends Fragment {
    private static final String TAG = "HomeActivity";
    Button ref60HzBut, ref120HzBut, refAutoBut;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    Switch dcSwitch, hbmSwitch, d2wSwitch, touchBoostSwitch;

    private String[] stateAction;
    private String[] curState;
    Switch[] switches;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_display, container, false);
    }

    @SuppressLint("StaticFieldLeak")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        AsyncInitTask initTask = new AsyncInitTask(view);
        initTask.start();
    }

    private void initViews(View view){
        ref60HzBut = view.findViewById(R.id.buttonrerrate1);
        ref120HzBut = view.findViewById(R.id.buttonrerrate2);
        refAutoBut = view.findViewById(R.id.buttonrerrate3);
        dcSwitch = view.findViewById(R.id.dcSwitch);
        hbmSwitch = view.findViewById(R.id.hbmSwitch);
        d2wSwitch = view.findViewById(R.id.d2wSwitch);
        touchBoostSwitch = view.findViewById(R.id.touchSwitch);
        switches = new Switch[]{dcSwitch, hbmSwitch, d2wSwitch, touchBoostSwitch};
    }

    private void initData() {
        int count = 0;
        String[] strings;
        for(int i = 0;; i++){
            strings = Display.PATH.getDisplayPaths(i);
            if(strings == null)
                break;

            for(String string : strings){
                try {
                    Log.d(TAG, "Init Data:" + count);
                    curState[count++] = Utils.read(0, string);
                } catch (UtilException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void setViews(){
        int count = 0;
        String[] strings;
        for (int i = 0;; i++) {
            strings = Display.PATH.getDisplayPaths(i);
            if(strings == null)
                break;
            for(int j = 0; j < strings.length; j++){
                Log.d(TAG,"setChecked:" + count + ":" + Objects.equals(curState[count], stateAction[count]));
                switches[i].setChecked(Objects.equals(curState[count], stateAction[count]));
                count++;
            }
        }
    }

    private void initListeners() {
        ref60HzBut.setOnClickListener(v -> {
            Utils.execCmdWrite("settings put system peak_refresh_rate 120");
            Utils.execCmdWrite("settings put system min_refresh_rate 120");
            Toast.makeText(getActivity(),
                    "Set 120Hz",
                    Toast.LENGTH_SHORT).show();

        });
        ref120HzBut.setOnClickListener(v -> {
            Utils.execCmdWrite("settings put system peak_refresh_rate 60");
            Utils.execCmdWrite("settings put system min_refresh_rate 60");
            Toast.makeText(getActivity(),
                    "Set 60Hz",
                    Toast.LENGTH_SHORT).show();
        });
        refAutoBut.setOnClickListener(v -> {
            Utils.execCmdWrite("settings put system peak_refresh_rate 120");
            Utils.execCmdWrite("settings put system min_refresh_rate 60");
            Toast.makeText(getActivity(),
                    "Set to Auto",
                    Toast.LENGTH_SHORT).show();
        });

        int count = 0;
        for (int i = 0;; i++) {
            String[] strings = Display.PATH.getDisplayPaths(i);
            if(strings == null)
                break;

            int finalCount = count;
            switches[i].setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    for (int j = 0; j < strings.length; j++)
                        Utils.write(stateAction[finalCount + j], strings[j]);
                } else {
                    for (String string : strings) Utils.write("0", string);
                }
            });
            count++;
        }
    }


    class AsyncInitTask extends Thread {
        final View view;
        public AsyncInitTask(View view) {
            this.view = view;
        }

        @Override
        public void run() {
            Handler handler = new Handler(Looper.getMainLooper());
            stateAction = new String[]{"1","1","1","1","5","1"};
            curState = new String[stateAction.length];
            initViews(view);
            initData();
            try {
                Utils.execCmdRead(0,"ls /sys/kernel/oppo_display");
            } catch (UtilException e) {
                Log.d(TAG, "Oppo feature set not present");
                return;
            }
            handler.post(() -> {
                setViews();
                initListeners();
            });
        }
    }
}