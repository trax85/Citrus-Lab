package com.example.myapplication3.fragments.CpuFragment.CpuSets;

public class CpuSetDataModel {
    String headerName;
    String cpuSetAttr;

    public CpuSetDataModel(String headerName, String cpuSetAttr) {
        this.headerName = headerName;
        this.cpuSetAttr = cpuSetAttr;
    }

    public String getHeaderName() {
        return headerName;
    }

    public String getCpuSetAttr() {
        return cpuSetAttr;
    }

    public void setCpuSetAttr(String cpuSetAttr) {
        this.cpuSetAttr = cpuSetAttr;
    }
}
