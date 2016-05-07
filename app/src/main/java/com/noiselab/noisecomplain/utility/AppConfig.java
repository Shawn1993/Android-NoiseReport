package com.noiselab.noisecomplain.utility;

import android.content.Context;
import android.net.wifi.WifiManager;

/**
 * Created by shawn on 30/3/2016.
 */
public class AppConfig {

    public static final double DB_OFFSET = 4;

    public static final int DB_INTERVAL = 100;

    public static final String DATE_FORMAT = "yyyy-MM-dd-HH-mm-ss";

    public static final String REQUEST_URL = "http://222.200.176.92:58080/ncpserver/api/complain";

    public static String getMacAddress(Context context) {
        WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        return wm.getConnectionInfo().getMacAddress();
    }

}
