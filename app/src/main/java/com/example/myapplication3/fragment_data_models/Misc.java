package com.example.myapplication3.fragment_data_models;

import java.util.ArrayList;

public class Misc {

    public static class PATH{
        public final static String WIREGUARD_VER =  "/sys/module/wireguard/version";
        public final static String TCP_AVI = "/proc/sys/net/ipv4/tcp_available_congestion_control";
        public final static String TCP_CUR = "/proc/sys/net/ipv4/tcp_congestion_control";
        public final static String THERMAL_ZONE = "/sys/class/thermal/thermal_zone";
    }

    public static class Cmd{
        public final static String TCP_CHANGE = "sysctl -w net.ipv4.tcp_congestion_control=";
    }

    public static class Params extends FragmentParameter{

        private String[] availTcpAlgo;
        private String curTcp;
        private String WireguardVer;
        private ArrayList<String> activityLog;

        public String getCurTcp() {
            return curTcp;
        }

        public String getWireguardVer() {
            return WireguardVer;
        }

        public String[] getAviAlgo(){
            return availTcpAlgo;
        }

        public void setCurTcp(String curTcp) {
            this.curTcp = curTcp;
        }

        public void setWireguardVer(String wireguardVer) {
            this.WireguardVer = wireguardVer;
        }

        public void setAvailTcpAlgo(String[] availTcpAlgo){
            this.availTcpAlgo = availTcpAlgo;
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
