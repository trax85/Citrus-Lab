package com.example.myapplication3.fragments.MiscFragment;

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
import com.example.myapplication3.tools.UtilException;
import com.example.myapplication3.tools.Utils;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.Arrays;

public class NetworkFragment extends Fragment {
    TextView textViewTcp, textViewWireGuard;
    RelativeLayout relativeLayoutTcp, relativeLayoutWg;
    final static String Wireguard =  "/sys/module/wireguard/version";
    final static String TCPAvail = "/proc/sys/net/ipv4/tcp_available_congestion_control";
    final static String TCPCur = "/proc/sys/net/ipv4/tcp_congestion_control";
    String[] availTcpAlgo;
    String curTcp, getWireguardVer;
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
        initView(view);
        initPath();
    }

    void initView(View view){
        textViewTcp = view.findViewById(R.id.tcp);
        textViewWireGuard = view.findViewById(R.id.wireguard);
        relativeLayoutTcp = view.findViewById(R.id.tcp_layout);
        relativeLayoutWg = view.findViewById(R.id.wireguard_layout);
    }

    void initPath(){
        String str;
        try {
            str = Utils.read(0,Wireguard);
            textViewWireGuard.setText("v" + str);
        } catch (UtilException e) {
            textViewWireGuard.setText("Not present");
        }
        try {
            str = Utils.read(0, TCPCur);
            availTcpAlgo = Utils.splitStrings(TCPAvail, "\\s+");
        } catch (UtilException e) {
            textViewTcp.setText("Read error");
            return;
        }
        textViewTcp.setText(str);
        relativeLayoutTcp.setOnClickListener(v -> showDialog());
    }

    public void showDialog(){
        MaterialAlertDialogBuilder builder =
                new MaterialAlertDialogBuilder(requireActivity());
        builder.setTitle("Choose Algorithm");
        String curAlgo = curTcp;
        int checkedItem = Arrays.asList(availTcpAlgo).indexOf(curAlgo);
        builder.setSingleChoiceItems(availTcpAlgo, checkedItem, (dialog, which) -> {
            textViewTcp.setText(availTcpAlgo[which]);
            Utils.execCmdWrite("sysctl -w net.ipv4.tcp_congestion_control=" + availTcpAlgo[which]);
            dialog.dismiss();
        });
        builder.show();
    }
}