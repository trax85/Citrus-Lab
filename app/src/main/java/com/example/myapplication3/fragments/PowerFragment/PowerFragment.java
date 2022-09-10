package com.example.myapplication3.fragments.PowerFragment;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication3.R;
import com.example.myapplication3.FragmentDataModels.Power;
import com.example.myapplication3.fragments.HomeFragment.FragmentPersistObject;
import com.example.myapplication3.fragments.InfoPopupWindow;
import com.example.myapplication3.tools.UtilException;
import com.example.myapplication3.tools.Utils;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.Arrays;
import java.util.Objects;


public class PowerFragment extends Fragment {
    private final static String TAG = "PowerFragment";
    TextView powerSuspend, powerSuspendVer, chargeTemps, fastChargeTemps, powerProfile;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    Switch chargeThrotSwitch, powerLimiterSwitch;
    ImageView pwrspndInfo, chgctlInfo, pwrlmtInfo;
    RelativeLayout psTypeLayout, normalChargeLayout, fastChargeTempsLayout, powerLimiterProfile;
    private Power.Params powerParams;
    private ChargeThrottleService chgService = new ChargeThrottleService();
    Utils utils;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_power, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        AsyncInitTask initTask = new AsyncInitTask(view);
        initTask.start();
    }

    private void getViews(View view){
        powerSuspend = view.findViewById(R.id.powerSus);
        powerSuspendVer = view.findViewById(R.id.psver);
        psTypeLayout = view.findViewById(R.id.ps_layout);
        chargeThrotSwitch = view.findViewById(R.id.switch_chrg_ctl);
        chargeTemps =view.findViewById(R.id.charge_throt);
        normalChargeLayout = view.findViewById(R.id.charge_thrt_layout);
        fastChargeTemps = view.findViewById(R.id.fastcharge_throt);
        fastChargeTempsLayout = view.findViewById(R.id.fastcharge_thrt_layout);
        powerProfile = view.findViewById(R.id.pwr_prof);
        powerLimiterProfile = view.findViewById(R.id.pwr_prof_layout);
        powerLimiterSwitch = view.findViewById(R.id.switch_power_lmt);
        pwrspndInfo = view.findViewById(R.id.power_suspend_info);
        pwrlmtInfo = view.findViewById(R.id.power_limiter_info);
        chgctlInfo = view.findViewById(R.id.chg_ctl_info);
    }

    private void initData(){
        powerParams.setPsType(getPowerSuspendType());
        powerParams.setPsVer(getPSver());
        powerParams.setPwrLmtState(getPwrState());
        powerParams.setPwrProfile(getPwrProfile());
    }

    @SuppressLint("SetTextI18n")
    private void initViews(){
        powerSuspend.setText(powerParams.getPsType());
        powerSuspendVer.setText(powerParams.getPsVer());
        if(chgService.isRunning()){
            chargeThrotSwitch.setChecked(true);
            chargeTemps.setText(chgService.getThrottleTemps() + "\u2103");
            fastChargeTemps.setText(chgService.getFastChargeThrottleTemps() + "\u2103");
        }
        else{
            chargeTemps.setText("Disabled");
            fastChargeTemps.setText("Disabled");
        }
        if(!Objects.equals(powerParams.getPwrLmtState(), "0"))
            powerLimiterSwitch.setChecked(true);
        powerProfile.setText(powerParams.getAviProfile(Integer.parseInt(powerParams.getPwrProfile())));
    }

    private void setListeners()
    {
        if(!powerParams.getPsType().contains("Not Present"))
            psTypeLayout.setOnClickListener(v -> showPowerSusTypeDialog());

        if(chgService.isRunning()) {
            normalChargeLayout.setOnClickListener(v -> initNormalChargeDialouge());
            fastChargeTempsLayout.setOnClickListener(v -> initFastChargeDialouge());
        }

        chargeThrotSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> enableChargeControl(isChecked));
        powerLimiterSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> setProfileStateSwitch(isChecked));
        powerLimiterProfile.setOnClickListener(v -> setOptionsDialouge());
    }

    @SuppressLint("SetTextI18n")
    private void enableChargeControl(boolean isChecked){
        Intent intent = new Intent(getContext(), ChargeThrottleService.class);
        Handler handler = new Handler(Looper.getMainLooper());
        String value;
        if(isChecked){
            value = "START.FOREGROUND";
            normalChargeLayout.setOnClickListener(v -> initNormalChargeDialouge());
            fastChargeTempsLayout.setOnClickListener(v -> initFastChargeDialouge());
            handler.post(() -> {
                chargeTemps.setText(Float.toString(Power.Params.defaultChargeTemps)
                        + (char) 0x00B0+"C");
                fastChargeTemps.setText(Float.toString(Power.Params.defaultFastChargeTemps)
                        + (char) 0x00B0+"C");
            });
        }else{
            value = "STOP.FOREGROUND";
            normalChargeLayout.setOnClickListener(null);
            fastChargeTempsLayout.setOnClickListener(null);
            handler.post(() -> {
                chargeTemps.setText("Disabled");
                fastChargeTemps.setText("Disabled");
            });
        }
        intent.setAction(value);
        requireActivity().startForegroundService(intent);
    }

    @SuppressLint("SetTextI18n")
    public void initNormalChargeDialouge(){
        MaterialAlertDialogBuilder builder = new
                MaterialAlertDialogBuilder(requireActivity());
        final EditText weightInput = new EditText(getActivity());

        builder.setTitle("Set throttle temperature");
        weightInput.setInputType(InputType.TYPE_CLASS_TEXT);
        weightInput.setHint(chargeTemps.getText().toString());
        builder.setView(weightInput);

        builder.setPositiveButton("OK", (dialog, which) -> {
            String out = weightInput.getText().toString();
            if(!isValidFloat(out))
                return;
            if(Float.parseFloat(out) > chgService.getFastChargeThrottleTemps()) {
                chgService.setFastChargeThrottleTemps(100);
                fastChargeTemps.setText("Disabled");
            }
            chgService.setThrottletemps(Integer.parseInt(out));
            chargeTemps.setText(out + (char) 0x00B0+"C");
        });builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void setProfileStateSwitch(boolean isChecked)
    {
        if(isChecked){
            utils.write("1", Power.PATH.POWERLMT_STATE);
            powerParams.setPwrLmtState("1");
        }
        else {
            utils.write("0", Power.PATH.POWERLMT_STATE);
            powerParams.setPwrLmtState("0");
        }
    }

    private void setOptionsDialouge(){
        MaterialAlertDialogBuilder builder =
                new MaterialAlertDialogBuilder(requireActivity());
        builder.setTitle("Choose Profile");
        String[] arr = powerParams.getAviProfileArr();
        int checkedItem = Arrays.asList(arr).indexOf(powerProfile.getText());
        builder.setSingleChoiceItems(powerParams.getAviProfileArr(), checkedItem,
                (dialog, which) -> {
            powerProfile.setText(powerParams.getAviProfile(which));
            utils.write(String.valueOf(which), Power.PATH.POWERLMT_PROFILE);
            powerParams.setPwrProfile(String.valueOf(which));
            dialog.dismiss();
        });
        builder.show();
    }

    public void showPowerSusTypeDialog(){
        MaterialAlertDialogBuilder builder =
                new MaterialAlertDialogBuilder(requireActivity());
        builder.setTitle("Choose Type");
        String[] states = Power.PATH.getPsStateArr();
        int checkedItem = Arrays.asList(states).indexOf(powerSuspend.getText().toString());
        builder.setSingleChoiceItems(states, checkedItem, (dialog, which) -> {
            powerSuspend.setText(Power.PATH.getPsState(which));
            utils.write(String.valueOf(which), Power.PATH.PS);
            powerParams.setPsType(String.valueOf(which));
            dialog.dismiss();
        });
        builder.show();
    }

    private String getPowerSuspendType(){
        String str;
        try {
            str = Utils.read(0, Power.PATH.PS);
            powerParams.setPsType(str);
            return Power.PATH.getPsState(Integer.parseInt(str));
        } catch (UtilException e) {
            str = "Not Present";
            return str;
        }
    }

    public String getPSver(){
        String str;
        try {
            str = Utils.read(0,Power.PATH.PSVER);
        } catch (UtilException e) {
            str = "Unknown";
        }
        return str;
    }

    public String getPwrState(){
        String str;
        try{
            str = Utils.read(0, Power.PATH.POWERLMT_STATE);
            powerParams.setPwrLmtState(str);
        }catch (UtilException e){
            str = "0";
        }
        return str;
    }

    public String getPwrProfile(){
        String str;
        try{
            str = Utils.read(0, Power.PATH.POWERLMT_PROFILE);
            powerParams.setPwrProfile(str);
        }catch (UtilException e){
            str = "0";
        }
        return str;
    }

    private void checkIfEnabled()
    {
        ServiceConnection connection = new ServiceConnection() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onServiceConnected(ComponentName className,
                                           IBinder service) {
                ChargeThrottleService.LocalBinder binder = (ChargeThrottleService.LocalBinder) service;
                chgService = binder.getService();
                // Calling your service public method
                if (chgService.isRunning()) {
                    Log.d(TAG,"service is running");
                    chargeThrotSwitch.setChecked(true);
                    chargeTemps.setText(chgService.getThrottleTemps() + "\u2103");
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName arg0) { }
        };
        Intent intent = new Intent(requireActivity(), ChargeThrottleService.class);
        requireActivity().bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

    @SuppressLint("SetTextI18n")
    public void initFastChargeDialouge(){
        MaterialAlertDialogBuilder builder = new
                MaterialAlertDialogBuilder(requireActivity());
        final EditText weightInput = new EditText(getActivity());

        builder.setTitle("Set throttle temperature");
        weightInput.setInputType(InputType.TYPE_CLASS_TEXT);
        weightInput.setHint(fastChargeTemps.getText().toString());
        builder.setView(weightInput);

        builder.setPositiveButton("OK", (dialog, which) -> {
            String out = weightInput.getText().toString();
            if(!isValidFloat(out))
                return;
            if(chgService.getThrottleTemps() > Float.parseFloat(out)){
                Toast.makeText(requireContext(), "FastCharge disabled", Toast.LENGTH_LONG).show();
                fastChargeTemps.setText("Disabled");
                chgService.setFastChargeThrottleTemps(100);
            } else {
                chgService.setFastChargeThrottleTemps(Integer.parseInt(out));
                fastChargeTemps.setText(out + (char) 0x00B0 + "C");
            }
        });builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    public boolean isValidFloat(String out){
        try{
            Float.parseFloat(out);
            return true;
        }catch (Exception e){
            return false;
        }
    }

    private void setInfoView(){
        InfoPopupWindow popupWindow = new InfoPopupWindow(this, R.id.activity_main);
        popupWindow.setInfoWindow(pwrspndInfo, requireActivity().getResources()
                .getString(R.string.power_info_power_sus));
        popupWindow.setInfoWindow(chgctlInfo, requireActivity().getResources()
                .getString(R.string.power_info_chg_ctl));
        popupWindow.setInfoWindow(pwrlmtInfo, requireActivity().getResources()
                .getString(R.string.power_info_pwr_lmt));
    }

    private void initViewModel(){
        FragmentPersistObject viewModel = new ViewModelProvider(requireActivity())
                .get(FragmentPersistObject.class);
        powerParams = viewModel.getPowerParams();
        utils = new Utils(powerParams);
    }

    class AsyncInitTask extends Thread {
        private final View view;
        private final Handler handler;
        public AsyncInitTask(View view) {
            this.view = view;
            handler = new Handler(Looper.getMainLooper());
        }

        @Override
        public void run() {
            initViewModel();
            getViews(view);
            initData();
            handler.post(() -> {
                initViews();
                setListeners();
            });
            checkIfEnabled();
            setInfoView();
        }
    }
}