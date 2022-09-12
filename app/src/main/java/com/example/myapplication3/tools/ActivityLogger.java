package com.example.myapplication3.tools;

import android.util.Log;

import java.util.ArrayList;

public class ActivityLogger {
    private final static String TAG = "ShellActivityLogger";
    public ArrayList<String> activityLog;

    public void logWriteActivity(String cmd, String path){
        String finalCmd = cmd + path + ";";
        int index = checkForDuplicate(path);
        if(index > -1){
            activityLog.set(index, finalCmd);
        } else {
            activityLog.add(finalCmd);
        }
    }

    public void logExecWriteActivity(String cmd, String finalCmd){
        int index = checkForDuplicate(cmd);
        if(index > -1){
            activityLog.set(index, finalCmd);
        } else {
            activityLog.add(finalCmd);
        }
    }

    private int checkForDuplicate(String matchedString){
        for(int i = 0; i < activityLog.size(); i++) {
            if(activityLog.get(i).contains(matchedString)) {
                return i;
            }
        }
        return -1;
    }
}
