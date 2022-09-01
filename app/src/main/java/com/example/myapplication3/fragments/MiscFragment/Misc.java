package com.example.myapplication3.fragments.MiscFragment;

public class Misc {

    public static class PATH{
        public final static String WIREGUARD_VER =  "/sys/module/wireguard/version";
        public final static String TCP_AVI = "/proc/sys/net/ipv4/tcp_available_congestion_control";
        public final static String TCP_CUR = "/proc/sys/net/ipv4/tcp_congestion_control";
        public final static String THERMAL_ZONE = "/sys/class/thermal/thermal_zone";
    }

    static class Cmd{
        final static String TCP_CHANGE = "sysctl -w net.ipv4.tcp_congestion_control=";
    }

    static class Params{

        private String[] availTcpAlgo;
        private String curTcp;
        private String WireguardVer;

        private Params params_instance = null;

        public Params getInstance(){
            if(params_instance == null)
                params_instance = new Params();
            return params_instance;
        }

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
    }
}
