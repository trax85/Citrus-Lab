package com.example.myapplication3.fragments.GpuFragment;

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

    public static class Params{
        private String[] gpuFreqData;
        private String[] gpuFreqDataApp;
        private String[] gpuVoltData;
        private String gpuInfo;
        private boolean dvfsState = false;
        private boolean boostState = false;

        private Params params_instance = null;

        public Params getInstance(){
            if(params_instance == null)
                params_instance = new Params();
            return params_instance;
        }

        public String getGpuInfo() {
            return gpuInfo;
        }

        public boolean getDvfsState() {
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

        public String getGpuVoltData(int index) {
            if(index < gpuVoltData.length)
                return gpuVoltData[index];
            return "0";
        }

        public void setGpuInfo(String gpuInfo) {
            this.gpuInfo = gpuInfo;
        }

        public void setDvfsState(boolean dvfsState) {
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

        public void setGpuVoltData(String[] gpuVoltData) {
            this.gpuVoltData = gpuVoltData;
        }
    }
}
