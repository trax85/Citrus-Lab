package com.example.myapplication3.fragments.MiscFragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.myapplication3.R;
import com.example.myapplication3.fragment_data_models.Misc;
import com.example.myapplication3.fragments.HomeFragment.FragmentPersistObject;
import com.example.myapplication3.tools.UtilException;
import com.example.myapplication3.tools.Utils;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.Arrays;

public class NetworkFragment extends Fragment {
    TextView textViewTcp, textViewWireGuard;
    RelativeLayout relativeLayoutTcp, relativeLayoutWg;
    private Misc.Params miscParams;
    private Utils utils;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_network, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FragmentPersistObject viewModel = new ViewModelProvider(requireActivity())
                .get(FragmentPersistObject.class);
        miscParams = viewModel.getMiscParams();
        utils = new Utils(miscParams);
        utils.initActivityLogger();
        initView(view);
        initData();
        setViews();
    }

    void initView(View view){
        textViewTcp = view.findViewById(R.id.tcp);
        textViewWireGuard = view.findViewById(R.id.wireguard);
        relativeLayoutTcp = view.findViewById(R.id.tcp_layout);
        relativeLayoutWg = view.findViewById(R.id.wireguard_layout);
    }

    void initData(){
        miscParams.setWireguardVer(getWireGuardVer());
        miscParams.setCurTcp(getTcpCurrent());
        miscParams.setAvailTcpAlgo(getAviTcpAlgo());
    }

    void setViews(){
        textViewWireGuard.setText(miscParams.getWireguardVer());
        textViewTcp.setText(miscParams.getCurTcp());
        relativeLayoutTcp.setOnClickListener(v -> showDialog());
    }

    public String getWireGuardVer(){
        String str;
        try {
            str = Utils.read(0,Misc.PATH.WIREGUARD_VER);
            str = "v" + str;
        } catch (UtilException e) {
            str = "Not present";
        }
        return str;
    }

    public String getTcpCurrent(){
        String str;
        try {
            str = Utils.read(0, Misc.PATH.TCP_CUR);
            miscParams.setCurTcp(str);
        } catch (UtilException e) {
            str = "Read Error";
        }
        return str;
    }

    public String[] getAviTcpAlgo(){
        String[] str;
        str = Utils.splitStrings(Misc.PATH.TCP_AVI, "\\s+");
        return str;
    }
    public void showDialog(){
        MaterialAlertDialogBuilder builder =
                new MaterialAlertDialogBuilder(requireActivity());
        builder.setTitle("Choose Algorithm");
        String[] aviAlgo = miscParams.getAviAlgo();
        int checkedItem = Arrays.asList(aviAlgo).indexOf(textViewTcp.getText());
        builder.setSingleChoiceItems(aviAlgo, checkedItem, (dialog, which) -> {
            textViewTcp.setText(aviAlgo[which]);
            utils.execWrite(Misc.Cmd.TCP_CHANGE + aviAlgo[which]);
            miscParams.setCurTcp(aviAlgo[which]);
            dialog.dismiss();
        });
        builder.show();
    }
}