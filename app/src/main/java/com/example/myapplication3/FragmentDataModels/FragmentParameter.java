package com.example.myapplication3.FragmentDataModels;

import java.util.ArrayList;

public abstract class FragmentParameter {
    private ArrayList<String> activityLog;

    abstract public ArrayList<String> getActivityLog();

    abstract public void setActivityLog(ArrayList<String> activityLog);
}
