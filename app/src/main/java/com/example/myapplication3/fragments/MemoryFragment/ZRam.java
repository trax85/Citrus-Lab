package com.example.myapplication3.fragments.MemoryFragment;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Looper;
import android.text.InputType;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.example.myapplication3.tools.UtilException;
import com.example.myapplication3.tools.Utils;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.Arrays;

public class ZRamInit {
    static String TAG = "MemoryFragment";
    private static final String DISKSIZE = "/sys/devices/virtual/block/zram0/disksize";
    private static final String DISK = "/dev/block/zram0";
    private static final String AVI_ALGO = "/sys/block/zram0/comp_algorithm";
    private static final String SWAPPINESS = "/proc/sys/vm/swappiness";
    private static final String SWAP = "/proc/swaps";
    private static String[] compAlgo;
    private final MemoryFragment fragment;
    private static Boolean algoAvailable = false;

    public ZRamInit(MemoryFragment fragment){
        this.fragment = fragment;
        setTextViews();
        setOnClickListeners();
    }

    @SuppressLint("SetTextI18n")
    void setTextViews()
    {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> {
            String out;
            String[] outArr;
            double size;

            //read Zram status
            try {
                out = Utils.read(1, SWAP);
                if (out.contains(DISK))
                    fragment.textView1.setText("Enabled");
            } catch (UtilException e) {
                Log.d(TAG, "error:"+ e);
                fragment.textView1.setText("Disabled");
            }

            //get available algorithms
            outArr = Utils.splitStrings(AVI_ALGO, "\\s+");
            if(outArr != null){
                algoAvailable = true;
                fragment.textView2.setText(getSantizedString(outArr));
                for (int i = 0; i < outArr.length; i++)
                    if (outArr[i].contains("["))
                        outArr[i] = rmBackets(outArr[i]);
                compAlgo = outArr;
                Log.d(TAG, "also:" + algoAvailable);
            }

            //get disksize
            try{
                out = Utils.read(0, DISKSIZE);
                size = Double.parseDouble(out);
            }catch (UtilException e){
                size = 0;
            }
            fragment.textView3.setText((int)(size / (1024 * 1024)) + "Mb");

            //get zram swappiness
            try {
                out = Utils.read(0, SWAPPINESS);
            } catch (UtilException e) {
                out = "0";
            }
            fragment.textView4.setText(out + "%");
        });
    }

    @SuppressLint("SetTextI18n")
    void setOnClickListeners()
    {
        Log.d(TAG,"algo"+ algoAvailable);
        if(algoAvailable) {
            fragment.zramLayout2.setOnClickListener(v -> showDialogue());
        }

        fragment.zramLayout1.setOnClickListener(v -> {
                try {
                    String out = Utils.read(1, SWAP);
                    if (out.contains(DISK)){
                        fragment.textView1.setText("Disabled");
                        Utils.execCmdWrite("swapoff /dev/block/zram0 > /dev/null 2>&1");
                        Utils.write("0", DISKSIZE);
                        Toast.makeText(v.getContext(), "Disabling Zram...",Toast.LENGTH_SHORT).show();
                    }
                } catch (UtilException e) {
                    fragment.textView1.setText("Enabled");
                    Utils.execCmdWrite("swapon /dev/block/zram0 > /dev/null 2>&1");
                    Toast.makeText(v.getContext(), "Enabling Zram",Toast.LENGTH_SHORT).show();
                }
        });



        fragment.zramLayout3.setOnClickListener(v -> {
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
        });

        fragment.zramLayout4.setOnClickListener(v -> {
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
        });
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

    String getSantizedString(String[] arr){
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
