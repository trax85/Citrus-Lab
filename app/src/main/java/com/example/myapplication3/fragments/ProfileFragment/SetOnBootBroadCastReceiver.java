package com.example.myapplication3.fragments.ProfileFragment;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.myapplication3.services.SetOnBootService;

public class SetOnBootBroadCastReceiver extends BroadcastReceiver {
    private final String TAG = "setOnBootBroadCast";
    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent serviceIntent = new Intent(context, SetOnBootService.class);
        context.startForegroundService(serviceIntent);
    }
}
