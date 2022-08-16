package com.example.myapplication3.fragments.MemoryFragment;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Looper;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.myapplication3.tools.UtilException;
import com.example.myapplication3.tools.Utils;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.Arrays;

public class ZRam {
    static String TAG = "MemoryFragment";
    private static final String DISKSIZE = "/sys/devices/virtual/block/zram0/disksize";
    private static final String DISK = "/dev/block/zram0";
    private static final String AVI_ALGO = "/sys/block/zram0/comp_algorithm";
    private static final String SWAPPINESS = "/proc/sys/vm/swappiness";
    private static final String SWAP = "/proc/swaps";
    private static String[] compAlgo;
    private final MemoryFragment fragment;
    private static Boolean algoAvailable = false;
    private static String zramState, zramAlgo, zramSwap, zramDisk;

    public ZRam(MemoryFragment fragment){
        this.fragment = fragment;
    }

    public void ZramInit(){
        initParams();
        setOnClickListeners();
        setTextViews();
    }

    public void initParams(){
        getZramState();
        getZramAlgo();
        getZramDiskSize();
        getZramSwappiness();
    }

    @SuppressLint("SetTextI18n")
    void setTextViews()
    {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> {
            fragment.textView1.setText(zramState);
            fragment.textView2.setText(zramAlgo);
            fragment.textView3.setText(zramDisk);
            fragment.textView4.setText(zramSwap);
        });
    }

    void setOnClickListeners()
    {
        fragment.zramLayout1.setOnClickListener(this::setZramState);
        if(algoAvailable)
            fragment.zramLayout2.setOnClickListener(v -> showDialogue());
        fragment.zramLayout3.setOnClickListener(v -> setDiskSize());
        fragment.zramLayout4.setOnClickListener(v -> setSwapiness());
    }

    public void showDialogue(){
        String str = fragment.textView2.getText().toString();
        int checkedItem = Arrays.asList(compAlgo).indexOf(str);
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(fragment.requireActivity());
        builder.setTitle("Choose Option");
        builder.setSingleChoiceItems(compAlgo, checkedItem, (dialog, which) -> {
                Utils.write(compAlgo[which], AVI_ALGO);
                fragment.textView2.setText(compAlgo[which]);
                dialog.dismiss();
        });
        builder.show();
    }

    public void setZramState(View v)
    {
        Handler handler = new Handler(Looper.getMainLooper());
        String setText;
        int state = 0;

        if(getZramState() == 0){
                setText = "Disabling";
        } else {
            setText = "Enabling";
            state = 1;
        }
        String finalSetText = setText;
        handler.post(() -> {
            fragment.textView1.setText(finalSetText);
            Toast.makeText(v.getContext(), setText + " Zram...", Toast.LENGTH_SHORT).show();
        });
        AsyncTask asyncTask = new AsyncTask(state, v, fragment);
        asyncTask.start();
    }

    @SuppressLint("SetTextI18n")
    public void setDiskSize()
    {
        MaterialAlertDialogBuilder builder = new
                MaterialAlertDialogBuilder(fragment.requireActivity());
        final EditText weightInput = new EditText(fragment.getActivity());

        builder.setTitle("Set swap size");
        weightInput.setInputType(InputType.TYPE_CLASS_TEXT);
        weightInput.setHint(fragment.textView3.getText().toString());
        builder.setView(weightInput);

        builder.setPositiveButton("OK", (dialog, which) -> {
            int size = Integer.parseInt(weightInput.getText().toString());
            size = size * 1024 * 1024;
            Utils.write(Integer.toString(size), DISKSIZE);
            fragment.textView3.setText(weightInput.getText().toString() + "Mb");
        });builder.setNegativeButton("Cancle", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    @SuppressLint("SetTextI18n")
    public void setSwapiness()
    {
        MaterialAlertDialogBuilder builder = new
                MaterialAlertDialogBuilder(fragment.requireActivity());
        final EditText weightInput = new EditText(fragment.getActivity());

        builder.setTitle("Set swappiness");
        weightInput.setInputType(InputType.TYPE_CLASS_TEXT);
        weightInput.setHint(
                fragment.textView4.getText().toString().replace("%", ""));
        builder.setView(weightInput);

        builder.setPositiveButton("OK", (dialog, which) -> {
            Utils.write(weightInput.getText().toString(), SWAPPINESS);
            fragment.textView4.setText(weightInput.getText().toString() + "%");
        });builder.setNegativeButton("Cancle", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    public int getZramState(){
        try {
            String out = Utils.read(1, SWAP);
            if (out.contains(DISK) || out.contains(DISK.substring(4,15)))
                zramState = "Enabled";
        } catch (UtilException e) {
            Log.d(TAG, ":"+ e);
            zramState = "Disabled";
            return 1;
        }
        return 0;
    }

    public int getZramAlgo(){
        String[] outArr = Utils.splitStrings(AVI_ALGO, "\\s+");
        if(outArr != null){
            zramAlgo = getSanitizedString(outArr);
            for (int i = 0; i < outArr.length; i++)
                if (outArr[i].contains("["))
                    outArr[i] = rmBackets(outArr[i]);
            compAlgo = outArr;
            algoAvailable = true;
            return 0;
        }
        return 1;
    }

    public int getZramDiskSize(){
        try{
            String out = Utils.read(0, DISKSIZE);
            Double size = Double.parseDouble(out);
            zramDisk = (int)(size / (1024 * 1024)) + "Mb";
        }catch (UtilException e){
            zramDisk = "0";
            return 1;
        }
        return 0;
    }

    public int getZramSwappiness(){
        try {
            String out = Utils.read(0, SWAPPINESS);
            zramSwap = out + "%";
        } catch (UtilException e) {
            zramSwap = "0";
            return 1;
        }
        return 0;
    }

    public static class AsyncTask extends Thread {
        private int state = 0;
        Handler handle;
        View v;
        MemoryFragment fragment;

        public AsyncTask(int state, View v, MemoryFragment fragment){
            this.state = state;
            this.v = v;
            this.fragment = fragment;
            handle = new Handler(Looper.getMainLooper());
        }
        @Override
        public void run() {
            String str;

            if(state == 1){
                Utils.execCmdWrite("swapon /dev/block/zram0 > /dev/null 2>&1");
                str = "Enabled";
            } else {
                Utils.execCmdWrite("swapoff /dev/block/zram0 > /dev/null 2>&1");
                str = "Disabled";
            }
            String finalStr = str;
            handle.post(() -> {
                fragment.textView1.setText(finalStr);
                Toast.makeText(v.getContext(), finalStr, Toast.LENGTH_SHORT).show();
            });
        }
    }

    String getSanitizedString(String[] arr){
        String str = "NULL";
        for (String s : arr) {
            if (s.contains("[")){
                str = rmBackets(s);
            }
        }
        return str;
    }

    String rmBackets(String str){
        str = str.replace("[", "");
        str = str.replace("]", "");
        return str;
    }
}
