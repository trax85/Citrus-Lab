package com.example.myapplication3.fragments.DisplayFragment;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

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
import com.example.myapplication3.fragment_data_models.Display;
import com.example.myapplication3.FragmentPersistObject;
import com.example.myapplication3.tools.UtilException;
import com.example.myapplication3.tools.Utils;

import java.util.Objects;

public class DisplayFragment extends Fragment {
    private static final String TAG = "HomeActivity";
    Button ref60HzBut, ref120HzBut, refAutoBut;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    Switch dcSwitch, hbmSwitch, d2wSwitch, touchBoostSwitch;
    private Display.Params displayParams;
    private String[] stateAction;
    private String[] curState;
    Switch[] switches;
    private Utils utils;

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

    private void initViewModel(){
        FragmentPersistObject viewModel = new ViewModelProvider(requireActivity()).get(FragmentPersistObject.class);
        displayParams = viewModel.getDisplayParams();
        utils = new Utils(displayParams);
        utils.initActivityLogger();
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
                    curState[count++] = Utils.read(0, string);
                } catch (UtilException e) {
                    e.printStackTrace();
                }
            }
        }
        displayParams.initFeatureSet(curState);
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
            write120Hz();
            Toast.makeText(getActivity(), "Set 120Hz", Toast.LENGTH_SHORT).show();

        });
        ref120HzBut.setOnClickListener(v -> {
            write60Hz();
            Toast.makeText(getActivity(), "Set 60Hz", Toast.LENGTH_SHORT).show();
        });
        refAutoBut.setOnClickListener(v -> {
            writeAutoHz();
            Toast.makeText(getActivity(),"Set to Auto", Toast.LENGTH_SHORT).show();
        });

        int count = 0;
        for (int i = 0;; i++) {
            String[] featurePaths = Display.PATH.getDisplayPaths(i);
            if(featurePaths == null)
                break;

            int finalCount = count;
            switches[i].setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    for (int j = 0; j < featurePaths.length; j++) {
                        writePanelFeature(stateAction[finalCount + j], featurePaths[j]);
                        displayParams.setFeatureSet(finalCount + j, stateAction[finalCount + j]);
                    }
                } else {
                    Log.d(TAG, "Not checked");
                    for (int j = 0; j < featurePaths.length; j++) {
                        writePanelFeature("0", featurePaths[j]);
                        displayParams.setFeatureSet(finalCount + j, "0");
                    }
                }
            });
            count++;
        }
    }

    private void write120Hz(){
        utils.execWrite(Display.Cmd.MAX_REFRESH, "120");
        utils.execWrite(Display.Cmd.MIN_REFRESH, "120");
    }
    private void write60Hz(){
        utils.execWrite(Display.Cmd.MAX_REFRESH, "60");
        utils.execWrite(Display.Cmd.MIN_REFRESH, "60");
    }
    private void writeAutoHz(){
        utils.execWrite(Display.Cmd.MAX_REFRESH ,"120");
        utils.execWrite(Display.Cmd.MIN_REFRESH ,"60");
    }
    private void writePanelFeature(String out, String Path){
        Log.d(TAG, "State:" + out);
        utils.write(out, Path);
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
            initViewModel();
            initViews(view);
            initData();
            try {
                Utils.execCmdRead(0,"ls " + Display.PATH.OPPO_DISPLAY);
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