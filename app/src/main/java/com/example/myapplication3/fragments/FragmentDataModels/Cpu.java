package com.example.myapplication3.fragments.CpuFragment;

import android.util.Log;

public class Cpu {
    public static class PATH{
        public static String POLICY_PATH = "/sys/devices/system/cpu/cpufreq";
        public static String MAX_FREQ = "/scaling_max_freq";
        public static String MIN_FREQ = "/scaling_min_freq";
        public static String SCALING_GOVERNOR = "/scaling_governor";
        public static String AVI_SCALING_GOVERNOR = "/scaling_available_governors";
        public static String STUNE_PATH = "/dev/stune";
        public static String CPUSET = "/dev/cpuset";
        public static String CORE_CONTROL = "/sys/devices/system/cpu";
        public static String SCALING_AVI_FREQ = "/scaling_available_frequencies";
        public static String CUR_SCALING_FREQ = "/scaling_cur_freq";
    }

    public static class Params{
        private String[] policyArr;
        private String[] GovArr;
        private String[][] FreqArr;
        private String[][] AppendedFreqArr;
        private boolean[] cpuOnline;
        private String[] stuneItems;

        private Params params_instance = null;

        public Params getInstance(){
            if(params_instance == null){
                params_instance = new Params();
            }
            return params_instance;
        }

        public String[] getPolicyArr() {
            return policyArr;
        }

        public String[] getGovArr() {
            return GovArr;
        }

        public String[][] getFreqArr() {
            return FreqArr;
        }

        public String[][] getAppendedFreqArr() {
            return AppendedFreqArr;
        }

        public boolean[] getCpuOnline() {
            return cpuOnline;
        }

        public String[] getStuneItems() {
            return stuneItems;
        }

        public void setPolicyArr(String[] policyArr) {
            this.policyArr = policyArr;
        }

        public void setGovArr(String[] govArr) {
            GovArr = govArr;
        }

        public void setFreqArr(String[][] freqArr) {
            FreqArr = freqArr;
        }

        public void setAppendedFreqArr(String[][] appendedFreqArr) {
            AppendedFreqArr = appendedFreqArr;
        }

        public void setCpuOnline(boolean[] cpuOnline) {
            this.cpuOnline = cpuOnline;
        }

        public void setStuneItems(String[] stuneItems) {
            this.stuneItems = stuneItems;
        }
    }
}
