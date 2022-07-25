package com.example.myapplication3.fragments.HomeFragment;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.example.myapplication3.tools.UtilException;
import com.example.myapplication3.tools.Utils;

public class BatteryStats implements Runnable{
    private static final String TAG = "BattstatsAct";
    HomeFragment homeFragment;

    String chargeRes, volt;
    float temps;
    int currNow, capacity, totalCap;

    public void setBattClass(HomeFragment fragment){
        homeFragment = fragment;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void run(){
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> {
            try {
                volt = Utils.read(0, "/sys/class/power_supply/battery/voltage_now");
            } catch (UtilException e) {
                Log.d(TAG, "Voltage data read error");
                volt = "0";
            }
            // Update BatteryStats UI elements
            homeFragment.textView4.setText(capacity + "%");
            homeFragment.textView5.setText(chargeRes + " "+ volt + "mV");
            homeFragment.textView6.setText(temps + " C" + "\u00B0");
            homeFragment.textView7.setText("4500"+ "Mah"); //Hard code for now
        });
    }

    public BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Intent.ACTION_BATTERY_CHANGED)) {
                String statusString = "";
                String acString;
                int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, 0);
                int plugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED,
                        0);
                int state = 0;
                totalCap = BatteryManager.BATTERY_PROPERTY_CAPACITY;
                capacity = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
                Bundle bundle = intent.getExtras();
                currNow = bundle.getInt("current_avg");
                temps = (float)(bundle.getInt("temperature")/10);
                switch (status) {
                    case BatteryManager.BATTERY_STATUS_CHARGING:
                        statusString = "Charging";
                        state = 0;
                        break;
                    case BatteryManager.BATTERY_STATUS_DISCHARGING:
                        statusString = "Discharging";
                        state = 1;
                        break;
                    case BatteryManager.BATTERY_STATUS_FULL:
                        statusString = "Full";
                        state = 3;
                        break;
                    case BatteryManager.BATTERY_STATUS_NOT_CHARGING:
                        statusString = "Not charging";
                        state = 2;
                        break;
                }
                chargeRes = statusString;
                // Charge type
                if(state == 0) {
                    switch (plugged) {
                        case BatteryManager.BATTERY_PLUGGED_AC:
                            acString = "AC ";
                            break;
                        case BatteryManager.BATTERY_PLUGGED_USB:
                            acString = "USB ";
                            break;
                        default:
                            acString = "";
                    }
                    chargeRes = acString + chargeRes;
                }
            }
        }
    };
}
