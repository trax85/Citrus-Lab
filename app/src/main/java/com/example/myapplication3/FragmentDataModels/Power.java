package com.example.myapplication3.fragments.FragmentDataModels;

public class Power {
    static class PATH{
        final static String PS = "/sys/kernel/power_suspend/power_suspend_mode";
        final static String PSVER = "/sys/kernel/power_suspend/power_suspend_version";
        final static String CHG_ENABLE= "/sys/class/power_supply/battery/mmi_charging_enable";
        final static String FAST_CHG_ENABLE = "/sys/class/power_supply/battery/fastcharger";
        private static final String[] PS_STATE = {"Autosleep", "Userspace", "LCD Panel", "Hybrid"};
        final static String POWERLMT_STATE = "/sys/kernel/power_limiter/enable";
        final static String POWERLMT_PROFILE = "/sys/kernel/power_limiter/power_profile";

        public static String getPsState(int index){
            if(index < PS_STATE.length)
                return PS_STATE[index];
            return null;
        }

        public static String[] getPsStateArr() {
            return PS_STATE;
        }
    }

    static class Params{
        private String psType;
        private String psVer;
        private String pwrLmtState;
        private String PwrProfile;
        public final static float defaultChargeTemps = 40;
        public final static float defaultFastChargeTemps = 38;
        private final static String[] aviProfile = {"High", "Medium", "Low"};

        private Params params_instance = null;

        public Params getInstance(){
            if(params_instance == null)
                params_instance = new Params();
            return params_instance;
        }

        public String getPsType() {
            return psType;
        }

        public String getPsVer() {
            return psVer;
        }

        public String getPwrLmtState() {
            return pwrLmtState;
        }

        public String getPwrProfile() {
            return PwrProfile;
        }

        public String getAviProfile(int index){
            if(index < aviProfile.length){
                return aviProfile[index];
            }
            return "Unknown";
        }

        public String[] getAviProfileArr(){
            return aviProfile;
        }

        public void setPsType(String psType) {
            this.psType = psType;
        }

        public void setPsVer(String psVer) {
            this.psVer = psVer;
        }

        public void setPwrLmtState(String pwrLmtState) {
            this.pwrLmtState = pwrLmtState;
        }

        public void setPwrProfile(String pwrProfile) {
            PwrProfile = pwrProfile;
        }
    }
}
