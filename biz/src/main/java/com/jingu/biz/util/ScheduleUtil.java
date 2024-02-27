package com.jingu.biz.util;

import java.net.InetAddress;

public class ScheduleUtil {
    private static String OWN_SIGN_BASE = "BASE";

    public static String getLocalHostName(){
        try {
            return InetAddress.getLocalHost().getHostName();
        }catch (Exception e){
            return "";
        }
    }

    public static String getLocalIp(){
        try {
            return InetAddress.getLocalHost().getHostAddress();
        }catch (Exception e){
            return "";
        }
    }
}
