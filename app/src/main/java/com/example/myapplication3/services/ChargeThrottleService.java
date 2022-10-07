package com.example.myapplication3.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.example.myapplication3.MainActivity;
import com.example.myapplication3.R;
import com.example.myapplication3.fragment_data_models.Power;
import com.example.myapplication3.tools.Utils;

import java.util.Objects;

public class ChargeThrottleService extends Service {
    private final String TAG = "ChargeThrottleService";
    private boolean serviceRunState = false;
    private float throttleTemps;
    private float fastChargeThrottleTemps;
    public static float curTemps = 0;
    private boolean isCharging = false;
    private boolean isClamped = false;
    private boolean isFastChgClamped = false;
    private final IBinder binder = new LocalBinder();

    @RequiresApi(api = Build.VERSION_CODES.S)
    @Override
    public void onCreate() {
        super.onCreate();
        if(serviceRunState)
            startForeground(1, getMyActivityNotification(""));
    }

    @RequiresApi(api = Build.VERSION_CODES.S)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(Objects.equals(intent.getAction(), "START.FOREGROUND")){
            throttleTemps = Power.Params.defaultChargeTemps;
            fastChargeThrottleTemps = Power.Params.defaultFastChargeTemps;
            serviceRunState = true;

            startForeground(1, getMyActivityNotification(""));
            IntentFilter filter = new IntentFilter();
            filter.addAction(Intent.ACTION_BATTERY_CHANGED);
            getApplicationContext().registerReceiver(this.mBroadcastReceiver, filter);
            updateNotification();
        }
        else {
            stopSelfResult(startId);
        }
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getApplicationContext().unregisterReceiver(this.mBroadcastReceiver);
        stopForeground(true);
    }

    private void clampCharging(){
        Utils.serviceWrite("0", Power.PATH.CHG_ENABLE);
        isClamped = true;
    }

    private void clampFastCharge(){
        Utils.serviceWrite("0", Power.PATH.FAST_CHG_ENABLE);
        isFastChgClamped = true;
    }

    private void unClampCharging(){
        Utils.serviceWrite("1", Power.PATH.CHG_ENABLE);
        isClamped = false;
    }

    private void unclampFastCharge(){
        Utils.serviceWrite("1", Power.PATH.FAST_CHG_ENABLE);
        isFastChgClamped = false;
    }

    public BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Intent.ACTION_BATTERY_CHANGED)) {
                int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, 0);
                Bundle bundle = intent.getExtras();
                curTemps = (float)(bundle.getInt("temperature")/10);
                switch (status) {
                    case BatteryManager.BATTERY_STATUS_CHARGING:
                        isCharging = true;
                        break;
                    case BatteryManager.BATTERY_STATUS_NOT_CHARGING:
                        isCharging = false;
                        break;
                }
                makeThrottleDecision();
            }
        }
    };

    private void makeThrottleDecision()
    {
        if(isCharging) {
            if(curTemps >= fastChargeThrottleTemps){
                if(!isFastChgClamped)
                    clampFastCharge();
            } else if(isFastChgClamped)
                unclampFastCharge();
            if (curTemps >= throttleTemps) {
                if (curTemps >= throttleTemps)
                    clampCharging();
            } else if (isClamped)
                unClampCharging();
        }
    }

    public class LocalBinder extends Binder {
        ChargeThrottleService getService() {
            //Log.d(TAG, "Return service");
            return ChargeThrottleService.this;
        }
    }

    public boolean isRunning(){
        //Log.d(TAG,"status:" + serviceRunState);
        return serviceRunState;
    }

    public float getThrottleTemps(){
        return throttleTemps;
    }

    public void setThrottletemps(float throttleTemps){
        this.throttleTemps = throttleTemps;
    }

    public float getFastChargeThrottleTemps() {
        return fastChargeThrottleTemps;
    }

    public void setFastChargeThrottleTemps(float fastChargeThrottleTemps) {
        this.fastChargeThrottleTemps = fastChargeThrottleTemps;
    }

    @RequiresApi(api = Build.VERSION_CODES.S)
    private Notification getMyActivityNotification(String text)
    {
        String CHANNEL_ID = "my_channel_01";
        ((NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE))
                .createNotificationChannel(
                new NotificationChannel(CHANNEL_ID, "Channel human readable title",
                        NotificationManager.IMPORTANCE_DEFAULT));

        // The PendingIntent to launch our activity if the user selects
        // this notification
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("page", 5);
        PendingIntent contentIntent = PendingIntent.getActivity(this,
                0,intent,PendingIntent.FLAG_MUTABLE);

        return new NotificationCompat.Builder(this,CHANNEL_ID)
                .setContentTitle("Charge Control")
                .setContentText(text)
                .setOnlyAlertOnce(true) // so when data is updated don't make sound and alert in android 8.0+
                .setOngoing(true)
                .setSmallIcon(R.drawable.zyro_image)
                .setContentIntent(contentIntent)
                .build();
    }

    @RequiresApi(api = Build.VERSION_CODES.S)
    private void updateNotification(){
        String text="charge throttle service is running";

        Notification notification = getMyActivityNotification(text);
        NotificationManager mNotificationManager=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(1 ,notification);
    }
}
