package com.example.myapplication3.fragments.DisplayFragment;

public class Display {
    static class PATH{
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
}
