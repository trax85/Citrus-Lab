package com.example.myapplication3.fragment_data_models;

import java.util.ArrayList;

public class Gpu {
    public static class PATH{
        public static final String GPU_CUR_FREQ = "/sys/kernel/ged/hal/current_freqency";
        public static final String DVFS_STATE = "/proc/mali/dvfs_enable";
        public static final String GPU_OPP_FREQ = "/proc/gpufreq/gpufreq_opp_freq";
        public static final String GPU_INFO = "/sys/devices/platform/13000000.mali/gpuinfo";
        public static final String GPU_OPP_DUMP = "/proc/gpufreq/gpufreq_opp_dump";
        public static final String GPU_LOADING = "/sys/module/ged/parameters/gpu_loading";
        private static final String[] GPU_BOOST_STATE = {"/sys/module/ged/parameters/boost_gpu_enable",
                "/sys/module/ged/parameters/enable_gpu_boost"};
        private static final String[] GPU_BOOST = {"/sys/module/ged/parameters/gpu_bottom_freq",
                "/sys/module/ged/parameters/gpu_cust_upbound_freq",
                "/sys/module/ged/parameters/gpu_cust_boost_freq" };

        public static String getGpuBoostStatePaths(int index){
            if(index < GPU_BOOST_STATE.length)
                return GPU_BOOST_STATE[index];
            return null;
        }

        public static String getGpuBoostPaths(int index){
            if(index < GPU_BOOST.length)
                return GPU_BOOST[index];
            return null;
        }
    }

    public static class Params extends FragmentParameter {
        private String[] gpuFreqData;
        private String[] gpuFreqDataApp;
        private String[] gpuVoltData;
        private String gpuCurFreq;
        private String dvfsState;
        private String[] gpuBoost;
        private String[] gpuBoostFreq = new String[]{"0", "0", "0"};
        private String gpuInfo;
        private boolean boostState;
        private ArrayList<String> activityLog;

        public String getGpuInfo() {
            return gpuInfo;
        }

        public String getDvfsState() {
            return dvfsState;
        }

        public boolean getBoostState() {
            return boostState;
        }

        public String[] getGpuFreqData() {
            return gpuFreqData;
        }

        public String[] getGpuFreqDataApp() {
            return gpuFreqDataApp;
        }

        public String getGpuCurFreq() {
            return gpuCurFreq;
        }

        public String[] getGpuBoostFreq() {
            return gpuBoostFreq;
        }

        public String getGpuVoltData(int index) {
            if(index < gpuVoltData.length)
                return gpuVoltData[index];
            return "0";
        }

        public String[] getGpuBoost(){
            return gpuBoost;
        }

        public void setGpuInfo(String gpuInfo) {
            this.gpuInfo = gpuInfo;
        }

        public void setDvfsState(String dvfsState) {
            this.dvfsState = dvfsState;
        }

        public void setBoostState(boolean boostState) {
            this.boostState = boostState;
        }

        public void setGpuFreqData(String[] gpuFreqData) {
            this.gpuFreqData = gpuFreqData;
        }

        public void setGpuFreqDataApp(String[] gpuFreqDataApp) {
            this.gpuFreqDataApp = gpuFreqDataApp;
        }

        public void setGpuCurFreq(String gpuCurFreq) {
            this.gpuCurFreq = gpuCurFreq;
        }

        public void setGpuBoost(String[] gpuBoost){
            this.gpuBoost = gpuBoost;
        }

        public void setGpuBoostFreq(int index, String gpuBoostFreq) {
            if(index < gpuBoostFreq.length())
                this.gpuBoostFreq[index] = gpuBoostFreq;
        }

        public void setGpuVoltData(String[] gpuVoltData) {
            this.gpuVoltData = gpuVoltData;
        }

        @Override
        public ArrayList<String> getActivityLog() {
            if(activityLog == null)
                activityLog = new ArrayList<>();
            return activityLog;
        }

        @Override
        public void setActivityLog(ArrayList<String> activityLog) {
            this.activityLog = activityLog;
        }
    }
}
