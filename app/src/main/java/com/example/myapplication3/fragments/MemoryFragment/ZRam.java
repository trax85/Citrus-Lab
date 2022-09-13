package com.example.myapplication3.fragments.MemoryFragment;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Looper;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.myapplication3.FragmentDataModels.Memory;
import com.example.myapplication3.tools.UtilException;
import com.example.myapplication3.tools.Utils;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.Arrays;

public class ZRam {
    static String TAG = "MemoryFragment";
    private final MemoryFragment fragment;
    private static boolean algoAvailable = false;
    private static boolean isRunning = false;
    private final Memory.Params zRamParams;
    Utils utils;

    public ZRam(MemoryFragment fragment){
        this.fragment = fragment;
        zRamParams = fragment.memoryParams;
        utils = new Utils(zRamParams);
        utils.initActivityLogger();
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

    public int getZramState(){
        try {
            String out = Utils.read(1, Memory.PATH.SWAP);
            if (out.contains(Memory.PATH.DISK) || out.contains(Memory.PATH.DISK.substring(4,15)))
                zRamParams.setZramState("swapon");
        } catch (UtilException e) {
            Log.d(TAG, ":"+ e);
            zRamParams.setZramState("swapoff");
            return 1;
        }
        return 0;
    }

    public void getZramAlgo(){
        String[] outArr = Utils.splitStrings(Memory.PATH.AVI_ALGO, "\\s+");
        if(outArr != null){
            zRamParams.setZramAlgo(getSanitizedString(outArr));
            for (int i = 0; i < outArr.length; i++)
                if (outArr[i].contains("["))
                    outArr[i] = rmBackets(outArr[i]);
            zRamParams.setZramAviAlgo(outArr);
            algoAvailable = true;
        }
    }

    public void getZramDiskSize(){
        try{
            String out = Utils.read(0, Memory.PATH.DISKSIZE);
            double size = Double.parseDouble(out);
            zRamParams.setZramDisk((int)(size / (1024 * 1024)) + "Mb");
        }catch (UtilException e){
            zRamParams.setZramDisk("0");
        }
    }

    public void getZramSwappiness(){
        try {
            String out = Utils.read(0, Memory.PATH.SWAPPINESS);
            zRamParams.setZramSwap(out + "%");
        } catch (UtilException e) {
            zRamParams.setZramSwap("0");
        }
    }

    @SuppressLint("SetTextI18n")
    void setTextViews()
    {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> {
            if(zRamParams.getZramState().contains("swapoff"))
                fragment.textView1.setText("Disabled");
            else
                fragment.textView1.setText("Enabled");
            fragment.textView2.setText(zRamParams.getZramAlgo());
            fragment.textView3.setText(zRamParams.getZramDisk());
            fragment.textView4.setText(zRamParams.getZramSwap());
        });
    }

    void setOnClickListeners()
    {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> {
            fragment.zramLayout1.setOnClickListener(this::setZramState);
            if(algoAvailable)
                fragment.zramLayout2.setOnClickListener(v -> showAviAlgoDialouge());
            fragment.zramLayout3.setOnClickListener(v -> setDiskSize());
            fragment.zramLayout4.setOnClickListener(v -> setSwappiness());
        });
    }

    public void setZramState(View v)
    {
        Handler handler = new Handler(Looper.getMainLooper());
        String setText;
        int state = 0;

        if(isRunning)
            return;

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
        ZramBgTask asyncTask = new ZramBgTask(state, v, fragment);
        asyncTask.start();
    }

    public void showAviAlgoDialouge(){
        String str = fragment.textView2.getText().toString();
        String[] aviAlgo = zRamParams.getZramAviAlgo();

        int checkedItem = Arrays.asList(aviAlgo).indexOf(str);
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(fragment.requireActivity());
        builder.setTitle("Choose Option");
        builder.setSingleChoiceItems(aviAlgo, checkedItem, (dialog, which) -> {
                utils.write(aviAlgo[which], Memory.PATH.AVI_ALGO);
                fragment.textView2.setText(aviAlgo[which]);
                zRamParams.setZramCurAlgo(aviAlgo[which]);
                dialog.dismiss();
        });
        builder.show();
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
            ZramSetDisksizeTask setSize = new ZramSetDisksizeTask(size);
            setSize.start();
            fragment.textView3.setText(weightInput.getText().toString() + "Mb");
        });builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    @SuppressLint("SetTextI18n")
    public void setSwappiness()
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
            String out = weightInput.getText().toString();
            fragment.writeVM(out, Memory.PATH.SWAPPINESS);
            fragment.textView4.setText(out+ "%");
            zRamParams.setZramSwap(out);
        });builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    public static class ZramBgTask extends Thread {
        private final int state;
        Handler handle;
        View v;
        MemoryFragment fragment;

        public ZramBgTask(int state, View v, MemoryFragment fragment){
            this.state = state;
            this.v = v;
            this.fragment = fragment;
            handle = new Handler(Looper.getMainLooper());
        }
        
        @Override
        public void run() {
            String str;
            String cmd;
            isRunning = true;
            if(state == 1){
                cmd = "swapon";
                str = "Enabled";
            } else {
                cmd = "swapoff";
                str = "Disabled";
            }
            fragment.zramObject.writeZramState(cmd);
            fragment.zramObject.zRamParams.setZramState(cmd);
            String finalStr = str;
            handle.post(() -> {
                fragment.textView1.setText(finalStr);
                Toast.makeText(v.getContext(), finalStr, Toast.LENGTH_SHORT).show();
            });
            isRunning = false;
        }
    }

    class ZramSetDisksizeTask extends Thread {
        private int size;
        public ZramSetDisksizeTask(int size) {
            this.size = size;
            this.size = this.size * 1024 * 1024;
        }

        @Override
        public void run() {
            writeZramDiskSize(size);
            fragment.zramObject.zRamParams.setZramDisk(String.valueOf(size));
        }
    }

    private void writeZramDiskSize(int size){
        writeZramState("swapoff");
        utils.write("1", Memory.PATH.RESET);
        utils.write("0", Memory.PATH.DISKSIZE);
        utils.write("8", Memory.PATH.COMP_STREAMS);
        utils.write(String.valueOf(size), Memory.PATH.DISKSIZE);
        writeZramState("mkswap");
        writeZramState("swapon");
    }

    private void writeZramState(String cmd){
        utils.execWrite(cmd, " /dev/block/zram0 > /dev/null 2>&1");
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
