package com.example.myapplication3.fragments.DisplayFragment;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.myapplication3.R;
import com.example.myapplication3.tools.UtilException;
import com.example.myapplication3.tools.Utils;

import java.util.ArrayList;
import java.util.List;

public class DisplayFragment extends Fragment {
    private static final String TAG = "HomeActivity";
    String[] DCDPath = {"/sys/kernel/oppo_display/dimlayer_bl_en"};
    String[] HBMPath = {"/sys/kernel/oppo_display/hbm"};
    String[] D2W = {"/proc/sys/kernel/slide_boost_enabled",
            "/proc/touchpanel/double_tap_enable"};
    String[] TB = {"/proc/touchpanel/sensitive_level",
            "/proc/touchpanel/smooth_level"};
    String[] PrimaryDesc = {"DC Dimming","High Brightness Mode","Double-Tap to Wake", "Touch Boost"};
    String[] SecondaryDesc = {"Stops screen flicker", "Pushes Display to max brightness level",
            "Double tap on screen wakes up device", "Increases Touch sensitivity"};

    Button button1, button2, button3;
    ArrayList<DisplayDataModel> displayArrayLists;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_display, container, false);
    }

    @SuppressLint("StaticFieldLeak")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        button1 = view.findViewById(R.id.buttonrerrate1);
        button2 = view.findViewById(R.id.buttonrerrate2);
        button3 = view.findViewById(R.id.buttonrerrate3);
        try {
            Utils.execCmdRead(0,"ls /sys/kernel/oppo_display");
            initList();
            initRecyclerView(view);
        } catch (UtilException e) {
            Log.d(TAG, "Oppo feature set not present");
        }
        initListeners();
    }

    private void initList(){
        //Everything must be added in correct order/sequence
        int[][] ActionSet = {{1},{1}, {1,1}, {5,1}};
        List<String[]> FilePath = setList();
        displayArrayLists = new ArrayList<>();
        for(int i = 0; i < PrimaryDesc.length; i++){
            DisplayDataModel displayList = new DisplayDataModel(PrimaryDesc[i], SecondaryDesc[i],
                    FilePath.get(i), ActionSet[i]);
            displayArrayLists.add(displayList);
        }
    }

    private List<String[]> setList(){

        List<String[]> Filepath = new ArrayList<>();
        Filepath.add(DCDPath);
        Filepath.add(HBMPath);
        Filepath.add(D2W);
        Filepath.add(TB);
        return Filepath;
    }

    private void initListeners(){
        button1.setOnClickListener(v -> {
            Utils.execCmdWrite("settings put system peak_refresh_rate 120");
            Utils.execCmdWrite("settings put system min_refresh_rate 120");
            Toast.makeText(getActivity(),
                    "Set 120Hz",
                    Toast.LENGTH_SHORT).show();

        });
        button2.setOnClickListener(v -> {
            Utils.execCmdWrite("settings put system peak_refresh_rate 60");
            Utils.execCmdWrite("settings put system min_refresh_rate 60");
            Toast.makeText(getActivity(),
                    "Set 60Hz",
                    Toast.LENGTH_SHORT).show();
        });
        button3.setOnClickListener(v -> {
            Utils.execCmdWrite("settings put system peak_refresh_rate 120");
            Utils.execCmdWrite("settings put system min_refresh_rate 60");
            Toast.makeText(getActivity(),
                    "Set to Auto",
                    Toast.LENGTH_SHORT).show();
        });
    }

    private void initRecyclerView(View view){
        RecyclerView recyclerView = view.findViewById(R.id.display_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView. setOverScrollMode(View. OVER_SCROLL_NEVER);
        recyclerView.setLayoutManager(layoutManager);
        RVAdapter adapter = new RVAdapter(displayArrayLists);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }
}