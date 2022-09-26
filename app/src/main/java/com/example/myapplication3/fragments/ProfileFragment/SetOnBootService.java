package com.example.myapplication3.fragments.ProfileFragment;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.example.myapplication3.MainActivity;
import com.example.myapplication3.R;
import com.example.myapplication3.tools.Utils;

public class SetOnBootService extends Service {
    final static String TAG = "CustomProfile";
    private final String [] labelTags = {"CustProf1", "CustProf2", "CustProf3"};
    private final String[] listItems = {"Profile 1", "Profile 2", "Profile 3", "Not Selected"};
    private SharedPreferences pref;
    private int bootProfile;
    private boolean serviceRunning = false;

    @RequiresApi(api = Build.VERSION_CODES.S)
    @Override
    public void onCreate() {
        super.onCreate();
        if(!serviceRunning)
            startForeground(1, getMyActivityNotification(""));
    }

    @RequiresApi(api = Build.VERSION_CODES.S)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        serviceRunning = true;
        initData();
        //Exit if 'Not selected' option was chosen by user or was not set
        if(bootProfile != 3) {
            startForeground(1, getMyActivityNotification(""));
            updateNotification();
            startOp();
            Toast.makeText(getApplicationContext(), listItems[bootProfile]
                            + " Applied successfully", Toast.LENGTH_SHORT).show();
            stopSelfResult(startId);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopForeground(true);
    }

    private void initData(){
        pref = getApplicationContext().getSharedPreferences(TAG, Context.MODE_PRIVATE);
        bootProfile = pref.getInt("setOnBoot", 3);
    }

    private void startOp(){
        String out = pref.getString(labelTags[bootProfile], "");
        Utils.execCmdString(out);
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
                .setContentTitle("Set On Boot")
                .setContentText(text)
                .setOnlyAlertOnce(true) //so when data is updated don't make sound and alert in android 8.0+
                .setOngoing(true)
                .setSmallIcon(R.drawable.zyro_image)
                .setContentIntent(contentIntent)
                .build();
    }

    @RequiresApi(api = Build.VERSION_CODES.S)
    private void updateNotification(){
        String text="executing script";

        Notification notification = getMyActivityNotification(text);
        NotificationManager mNotificationManager=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(1 ,notification);
    }

}
