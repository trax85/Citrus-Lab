package com.example.myapplication3.fragments.MemoryFragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.myapplication3.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.topjohnwu.superuser.Shell;

import java.util.List;

public class MemoryFragment extends Fragment {
    TextView textView1, textView2, textView3, textView4;
    RelativeLayout zramLayout1, zramLayout2, zramLayout3, zramLayout4;
    TextView textView5, textView6, textView7, textView8, textView9, textView10;
    RelativeLayout vmLayout1, vmLayout2, vmLayout3, vmLayout4, vmLayout5, vmLayout6;
    RelativeLayout relativeLayoutArr[];
    TextView textViewArr[];
    ZRamInit ramInit;
    String PATH = "/proc/sys/vm/";
    String[] VMPaths = {"min_free_kbytes", "extra_free_kbytes", "dirty_ratio",
            "dirty_background_ratio", "overcommit_ratio", "vfs_cache_pressure"};
    Shell.Result result;
    List<String> out;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_memory, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        setOnClickListners();
    }

    @Override
    public void onResume() {
        super.onResume();
        ramInit = new ZRamInit(this);
    }

    void initViews(View view){
        //zRam Views
        textView1 = view.findViewById(R.id.zram_enable);
        textView2 = view.findViewById(R.id.zram_algo);
        textView3 = view.findViewById(R.id.zram_size);
        textView4 = view.findViewById(R.id.zram_swap);
        zramLayout1 = view.findViewById(R.id.zram_state_layout);
        zramLayout2 = view.findViewById(R.id.zram_algo_layout);
        zramLayout3 = view.findViewById(R.id.zram_size_layout);
        zramLayout4 = view.findViewById(R.id.zram_swap_layout);
        //virtual memory views
        textView5 = view.findViewById(R.id.vm_minfree);
        textView6 = view.findViewById(R.id.vm_extrafree);
        textView7 = view.findViewById(R.id.vm_dirtyratio);
        textView8 = view.findViewById(R.id.vm_bgdirtyratio);
        textView9 = view.findViewById(R.id.vm_overcom);
        textView10 = view.findViewById(R.id.vm_vfs);
        vmLayout1 = view.findViewById(R.id.vm_minfree_layout);
        vmLayout2 = view.findViewById(R.id.vm_extrafree_layout);
        vmLayout3 = view.findViewById(R.id.vm_dirtratio_layout);
        vmLayout4 = view.findViewById(R.id.vm_bgdirtratio_layout);
        vmLayout5 = view.findViewById(R.id.vm_overcom_layout);
        vmLayout6 = view.findViewById(R.id.vm_vfs_layout);

        textViewArr = new TextView[]{textView5, textView6, textView7, textView8, textView9,
                textView10};
        relativeLayoutArr = new RelativeLayout[]{ vmLayout1, vmLayout2, vmLayout3, vmLayout4,
                vmLayout5, vmLayout6};
    }

    void setOnClickListners(){
        for(int i = 0; i < relativeLayoutArr.length; i++){
            String out = read("cat " + PATH + VMPaths[i]);
            textViewArr[i].setText(out);
            int finalI = i;
            relativeLayoutArr[i].setOnClickListener(v -> createDialouge(finalI));
        }
    }

    public void createDialouge(int i){
        MaterialAlertDialogBuilder builder = new
                MaterialAlertDialogBuilder(getActivity());
        final EditText weightInput = new EditText(getActivity());

        builder.setTitle("Set VM parameter");
        weightInput.setInputType(InputType.TYPE_CLASS_TEXT);
        weightInput.setHint(textViewArr[i].getText().toString());
        builder.setView(weightInput);

        builder.setPositiveButton("OK", (dialog, which) -> {
            String out = weightInput.getText().toString();
            write("echo " + out + " > " + PATH + VMPaths[i]);
            textViewArr[i].setText(out);
        });builder.setNegativeButton("Cancle", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    public String read(String cmd){
        result = Shell.cmd(cmd).exec();
        out = result.getOut();
        return out.get(0);
    }

    public void write(String cmd){
        Shell.cmd(cmd).exec();
    }

    public String[] readArr(String cmd){
        result = Shell.cmd(cmd).exec();
        out = result.getOut();
        String str = out.get(0);
        // Some workarounds to get in form of strings
        return str.split("\\s+");
    }
}