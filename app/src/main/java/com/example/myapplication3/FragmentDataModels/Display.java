package com.example.myapplication3.FragmentDataModels;

import java.util.ArrayList;

public class Display {
    public static class PATH {
        public static final String OPPO_DISPLAY = "/sys/kernel/oppo_display";
        private static final String DCDPath = "/sys/kernel/oppo_display/dimlayer_bl_en";
        private static final String HBMPath = "/sys/kernel/oppo_display/hbm";
        private static final String SLB = "/proc/sys/kernel/slide_boost_enabled";
        private static final String D2W = "/proc/touchpanel/double_tap_enable";
        private static final String SEN_LVL = "/proc/touchpanel/sensitive_level";
        private static final String SMOOTH_LVL = "/proc/touchpanel/smooth_level";
        private static final String[][] packagedPATHS = new String[][]{ {DCDPath}, {HBMPath}, {SLB, D2W},
                {SEN_LVL, SMOOTH_LVL} };
        public static String[] getDisplayPaths(int index){
            if(index < packagedPATHS.length)
                return packagedPATHS[index];
            else return null;
        }
    }

    public class Cmd {
        public final static String MAX_REFRESH  = "settings put system peak_refresh_rate";
        public final static String MIN_REFRESH = "settings put system min_refresh_rate";
    }

    public static class Params extends FragmentParameter {
        private String DimLayer;
        private String hbm;
        private String slideBoost;
        private String doubleTapToWake;
        private String sensitiveLevel;
        private String smoothLevel;
        private String[] featureSet = new String[]{DimLayer, hbm, slideBoost, doubleTapToWake,
                sensitiveLevel, smoothLevel};
        private ArrayList<String> activityLog;

        public void initFeatureSet(String[] featureSet){
            this.featureSet = featureSet;
        }

        public void setFeatureSet(int index, String state) {
            if(index < featureSet.length){
                featureSet[index] = state;
            }
        }

        public ArrayList<String> getActivityLog() {
            if(activityLog == null)
                activityLog = new ArrayList<>();
            return activityLog;
        }

        public void setActivityLog(ArrayList<String> activityLog) {
            this.activityLog = activityLog;
        }

        public String[] getFeatureSet() {
            return featureSet;
        }
    }
}
