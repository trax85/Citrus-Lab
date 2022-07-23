package com.example.myapplication3.fragments.MemoryFragment;

import android.os.Handler;
import android.os.Looper;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.topjohnwu.superuser.Shell;

import java.util.Arrays;
import java.util.List;

public class ZRamInit {
    boolean enabled;
    String compAlgo[];
    String disksize = "/sys/devices/virtual/block/zram0/disksize";
    String algo = "/sys/block/zram0/comp_algorithm";
    String swap = "/proc/sys/vm/swappiness";
    static String TAG = "MemoryFragment";
    MemoryFragment fragment;

    public ZRamInit(MemoryFragment fragment){
        this.fragment = fragment;
        setTextViews();
        setOnClickListeners();
    }

    void setTextViews()
    {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> {
            String out, outArr[];
            int size;
            //state
            out = fragment.read("cat " + disksize);
            if (out != "0")
                fragment.textView1.setText("Enabled");
            else
                fragment.textView1.setText("Disabled");
            //comp algo
            outArr = fragment.readArr("cat " + algo);
            fragment.textView2.setText(getAlgo(outArr));
            for (int i = 0; i < outArr.length; i++)
                if (outArr[i].contains("["))
                    outArr[i] = rmBackets(outArr[i]);
            compAlgo = outArr;

            size = Integer.parseInt(out);
            fragment.textView3.setText(size / (1024 * 1024) + "Mb");
            out = fragment.read("cat " + swap);
            fragment.textView4.setText(out);
        });
    }

    void setOnClickListeners(){

        fragment.zramLayout1.setOnClickListener(v -> {
            String out = fragment.read("cat " + disksize);
            if (out != "0")
                fragment.write("swapoff /dev/block/zram0 > /dev/null 2>&1");
            else
                fragment.write("swapon /dev/block/zram0 > /dev/null 2>&1");
        });

        fragment.zramLayout2.setOnClickListener(v -> showDialogue());

        fragment.zramLayout3.setOnClickListener(v -> {
            MaterialAlertDialogBuilder builder = new
                    MaterialAlertDialogBuilder(fragment.getActivity());
            final EditText weightInput = new EditText(fragment.getActivity());

            builder.setTitle("Set swap size");
            weightInput.setInputType(InputType.TYPE_CLASS_TEXT);
            weightInput.setHint(fragment.textView3.getText().toString());
            builder.setView(weightInput);

            builder.setPositiveButton("OK", (dialog, which) -> {
                int size = Integer.parseInt(weightInput.getText().toString());
                size = size * 1024 * 1024;
                fragment.write("echo " + size + " > " + disksize);
                fragment.textView3.setText(weightInput.getText().toString() + "Mb");
            });builder.setNegativeButton("Cancle", (dialog, which) -> dialog.cancel());
            builder.show();
        });

        fragment.zramLayout4.setOnClickListener(v -> {
            MaterialAlertDialogBuilder builder = new
                    MaterialAlertDialogBuilder(fragment.getActivity());
            final EditText weightInput = new EditText(fragment.getActivity());

            builder.setTitle("Set swappiness");
            weightInput.setInputType(InputType.TYPE_CLASS_TEXT);
            weightInput.setHint(fragment.textView4.getText().toString());
            builder.setView(weightInput);

            builder.setPositiveButton("OK", (dialog, which) -> {
                fragment.write("echo " + weightInput.getText().toString() + " > "
                        + swap);
                fragment.textView3.setText(weightInput.getText().toString() + "Mb");
            });builder.setNegativeButton("Cancle", (dialog, which) -> dialog.cancel());
            builder.show();
        });
    }

    public void showDialogue(){
        String str = fragment.textView2.getText().toString();
        int checkedItem = Arrays.asList(compAlgo).indexOf(str);
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(fragment.getActivity());
        builder.setTitle("Choose Option");
        builder.setSingleChoiceItems(compAlgo, checkedItem, (dialog, which) -> {
                Shell.cmd("echo " + compAlgo[which] + " > " + algo).exec();
                fragment.textView2.setText(compAlgo[which]);
                dialog.dismiss();
        });
        builder.show();
    }

    String getAlgo(String[] arr){
        String str = "NULL";
        for(int i = 0; i < arr.length; i++){
            if(arr[i].contains("["))
                str = rmBackets(arr[i]);
        }
        return str;
    }

    String rmBackets(String str){
        str = str.replace("[", "");
        str = str.replace("]", "");
        return str;
    }
}
