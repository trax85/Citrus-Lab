package com.example.myapplication3.fragments.CpuFragment.GovTunable;

public class TunableDataModel {
    public String tunableName, tunableAttr;

    public TunableDataModel(String tunableName, String tunableAttr) {
        this.tunableName = tunableName;
        this.tunableAttr = tunableAttr;
    }

    public String getTunableName() {
        return tunableName;
    }

    public String getTunableAttr() {
        return tunableAttr;
    }

    public void setTunableAttr(String tunableAttr) {
        this.tunableAttr = tunableAttr;
    }
}
