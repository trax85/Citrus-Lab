package com.example.myapplication3.fragments.CpuFragment;

public class CpuDataModel {
    String MaxFreq, MinFreq;
    String ClusterName, Governor;
    String AppMaxFreq, AppMinFreq;

    public CpuDataModel(String maxFreq, String minFreq, String clusterName, String governor) {
        MaxFreq = maxFreq;
        MinFreq = minFreq;
        ClusterName = clusterName;
        Governor = governor;
    }

    public String getMaxFreq() {
        return MaxFreq;
    }

    public String getMinFreq() {
        return MinFreq;
    }

    public String getClusterName() {
        return ClusterName;
    }

    public String getGovernor() {
        return Governor;
    }

    public void setGovernor(String governor) {
        Governor = governor;
    }

    public void setAppMaxFreq(String appMaxFreq) {
        AppMaxFreq = appMaxFreq;
    }

    public void setAppMinFreq(String appMinFreq) {
        AppMinFreq = appMinFreq;
    }

    public void setMaxFreq(String maxFreq) {
        MaxFreq = maxFreq;
    }

    public void setMinFreq(String minFreq) {
        MinFreq = minFreq;
    }
}
