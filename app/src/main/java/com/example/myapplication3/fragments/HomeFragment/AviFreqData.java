package com.example.myapplication3.fragments.HomeFragment;

import androidx.lifecycle.ViewModel;

public class AviFreqData extends ViewModel {
    String[][] cpuFreqArr, appCpuFreqArr;
    String[] policyAttr;
    boolean[] cpuOnline;

    public void setCpuFreqArr(String[][] cpuFreqArr) {
        this.cpuFreqArr = cpuFreqArr;
    }

    public void setAppCpuFreqArr(String[][] appCpuFreqArr) {
        this.appCpuFreqArr = appCpuFreqArr;
    }

    public void setPolicyAttr(String[] policyAttr) {
        this.policyAttr = policyAttr;
    }

    public void setCpuOnline(boolean[] cpuOnline) {
        this.cpuOnline = cpuOnline;
    }

    public String[] getPolicyAttr() {
        return policyAttr;
    }

    public boolean[] getCpuOnline() {
        return cpuOnline;
    }

    public String[][] getCpuFreqArr() {
        return cpuFreqArr;
    }

    public String[][] getAppCpuFreqArr() {
        return appCpuFreqArr;
    }
}
