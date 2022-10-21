package com.example.myapplication3.fragments.MemoryFragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.os.Handler;
import android.os.Looper;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.myapplication3.R;
import com.example.myapplication3.fragment_data_models.Memory;
import com.example.myapplication3.FragmentPersistObject;
import com.example.myapplication3.fragments.InfoPopupWindow;
import com.example.myapplication3.tools.UtilException;
import com.example.myapplication3.tools.Utils;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class MemoryFragment extends Fragment {
    TextView textView1, textView2, textView3, textView4;
    RelativeLayout zramLayout1, zramLayout2, zramLayout3, zramLayout4;
    TextView textView5, textView6, textView7, textView8, textView9, textView10;
    RelativeLayout vmLayout1, vmLayout2, vmLayout3, vmLayout4, vmLayout5, vmLayout6;
    RelativeLayout[] relativeLayoutArr;
    TextView[] textViewArr;
    ZRam zramObject;
    Memory.Params memoryParams;
    Utils utils;
    ImageView zramInfo, vmInfo;

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
        AsyncInitTasks asyncTasks = new AsyncInitTasks(this, view);
        asyncTasks.start();
    }

    @Override
    public void onResume() {
        super.onResume();
        AsyncResumeTasks resumeTasks = new AsyncResumeTasks(this);
        resumeTasks.start();
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
        zramInfo = view.findViewById(R.id.zram_info);
        vmInfo = view.findViewById(R.id.vm_info);

        textViewArr = new TextView[]{textView5, textView6, textView7, textView8, textView9,
                textView10};
        relativeLayoutArr = new RelativeLayout[]{ vmLayout1, vmLayout2, vmLayout3, vmLayout4,
                vmLayout5, vmLayout6};
    }

    private void initViewModel(){
        FragmentPersistObject viewModel = new ViewModelProvider(requireActivity())
                .get(FragmentPersistObject.class);
        memoryParams = viewModel.getMemoryParams();
        utils = new Utils(memoryParams);
        utils.initActivityLogger();
    }

    void setOnClickListeners(){
        Handler handler = new Handler(Looper.getMainLooper());
        for(int i = 0; i < relativeLayoutArr.length; i++){
            String out;
            try {
                out = Utils.read(0,Memory.PATH.VM + Memory.PATH.getVMPaths(i));
            } catch (UtilException e) {
                out = "read error";
            }
            memoryParams.setVmValues(i, out);
            int finalI1 = i;
            String finalOut = out;
            handler.post(() -> {
                textViewArr[finalI1].setText(finalOut);
                relativeLayoutArr[finalI1].setOnClickListener(v -> createDialouge(finalI1));
            });

        }
    }

    public void createDialouge(int i){
        MaterialAlertDialogBuilder builder = new
                MaterialAlertDialogBuilder(requireActivity());
        final EditText weightInput = new EditText(getActivity());

        builder.setTitle("Set VM parameter");
        weightInput.setInputType(InputType.TYPE_CLASS_TEXT);
        weightInput.setHint(textViewArr[i].getText().toString());
        builder.setView(weightInput);

        builder.setPositiveButton("OK", (dialog, which) -> {
            String out = weightInput.getText().toString();
            writeVM(out, Memory.PATH.VM + Memory.PATH.getVMPaths(i));
            textViewArr[i].setText(out);
            memoryParams.setVmValues(i, out);
        });builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    public void writeVM(String out, String Path){
        utils.write(out, Path);
    }

    private void setInfoView(){
        InfoPopupWindow popupWindow = new InfoPopupWindow(this, R.id.activity_main);
        popupWindow.setInfoWindow(zramInfo, requireActivity().getResources()
                .getString(R.string.mem_info_zram));
        popupWindow.setInfoWindow(vmInfo, requireActivity().getResources()
                .getString(R.string.mem_info_vm));
    }

    class AsyncInitTasks extends Thread {
        private final MemoryFragment fragment;
        private final View view;
        public AsyncInitTasks(MemoryFragment fragment, View view) {
            this.fragment = fragment;
            this.view = view;
        }

        @Override
        public void run() {
            initViewModel();
            zramObject = new ZRam(fragment);
            initViews(view);
            setOnClickListeners();
            setInfoView();
        }
    }

    static class AsyncResumeTasks extends Thread {
        private final MemoryFragment fragment;
        public AsyncResumeTasks(MemoryFragment fragment) {
            this.fragment = fragment;
        }

        @Override
        public void run() {
            fragment.zramObject.ZramInit();
        }
    }
}