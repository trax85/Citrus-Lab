package com.example.myapplication3.fragment_data_models;

import java.util.ArrayList;

public class Memory {
    public final static class PATH {
        public static final String DISKSIZE = "/sys/devices/virtual/block/zram0/disksize";
        public static final String DISK = "/dev/block/zram0";
        public static final String AVI_ALGO = "/sys/block/zram0/comp_algorithm";
        public static final String SWAPPINESS = "/proc/sys/vm/swappiness";
        public static final String SWAP = "/proc/swaps";
        private static final String[] VMPaths = {"min_free_kbytes", "extra_free_kbytes", "dirty_ratio",
                "dirty_background_ratio", "overcommit_ratio", "vfs_cache_pressure"};
        public static final String VM = "/proc/sys/vm/";
        public static final String RESET = "/sys/devices/virtual/block/zram0/reset";
        public static final String COMP_STREAMS = "/sys/block/zram0/max_comp_streams";

        public static String getVMPaths(int index) {
            if(index > VMPaths.length)
                return null;
            return VMPaths[index];
        }
     }

    public static class Params extends FragmentParameter{
        private String[] vmValues = new String[6];
        private String zramState;
        private String zramAlgo;
        private String zramSwap;
        private String zramDisk;
        private String[] zramAviAlgo;
        private String zramCurAlgo;
        private ArrayList<String> activityLog;

        public String getVmValues(int index) {
            if(index < vmValues.length)
                return vmValues[index];
            else
                return null;
        }

        public String getZramState() {
            return zramState;
        }

        public String getZramAlgo() {
            return zramAlgo;
        }

        public String getZramSwap() {
            return zramSwap;
        }

        public String getZramDisk() {
            return zramDisk;
        }

        public String[] getZramAviAlgo() {
            return zramAviAlgo;
        }

        public String getZramCurAlgo() {
            return zramCurAlgo;
        }

        public void setVmValues(int index, String value){
            if(index < vmValues.length)
                vmValues[index] = value;
        }

        public void setZramState(String zramState) {
            this.zramState = zramState;
        }

        public void setZramAlgo(String zramAlgo) {
            this.zramAlgo = zramAlgo;
        }

        public void setZramSwap(String zramSwap) {
            this.zramSwap = zramSwap;
        }

        public void setZramDisk(String zramDisk) {
            this.zramDisk = zramDisk;
        }

        public void setZramAviAlgo(String[] zramAviAlgo) {
            this.zramAviAlgo = zramAviAlgo;
        }

        public void setZramCurAlgo(String zramCurAlgo) {
            this.zramCurAlgo = zramCurAlgo;
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
