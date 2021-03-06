package me.fernandodominguez.zenmap.helpers;

import android.content.Context;

import me.fernandodominguez.zenmap.R;
import me.fernandodominguez.zenmap.constants.ScanTypes;

/**
 * Created by fernando on 29/12/15.
 */
public class ScanHelper {

    public static String intensityKeyFromValue (Context context, String value) {

        String key = null;
        if ( value.equals(context.getResources().getString(R.string.intense_scan)) ) {
            key = ScanTypes.INTENSE_SCAN;
        } else if ( value.equals(context.getResources().getString(R.string.intense_scan_all_tcp_ports)) ) {
            key = ScanTypes.INTENSE_SCAN_ALL_TCP_PORTS;
        } else if ( value.equals(context.getResources().getString(R.string.host_discovery)) ) {
            key = ScanTypes.HOST_DISCOVERY;
        } else if ( value.equals(context.getResources().getString(R.string.regular_scan)) ) {
            key = ScanTypes.REGULAR_SCAN;
        } else if ( value.equals(context.getResources().getString(R.string.os_scan)) ) {
            key = ScanTypes.OS_SCAN;
        } else if ( value.equals(context.getString(R.string.os_service_scan))) {
            key = ScanTypes.OS_SERVICE_SCAN;
        } else if ( value.equals(context.getString(R.string.host_ext_discovery))) {
            key = ScanTypes.HOST_EXT_DISCOVERY;
        } else if ( value.equals(context.getString(R.string.host_service_discovery))) {
            key = ScanTypes.HOST_SERVICE_DISCOVERY;
        } else if ( value.equals(context.getString(R.string.host_os_discovery))) {
            key = ScanTypes.HOST_OS_DISCOVERY;
        } else if ( value.equals(context.getString(R.string.host_os_service_discovery))) {
            key = ScanTypes.HOST_OS_SERVICE_DISCOVERY;
        }

        return key;
    }

    public static int getDrawableIcon(String os) {
        if (os == null) return R.drawable.desktop;

        if (os.startsWith("Apple")) {
            return R.drawable.apple;
        } else if (os.startsWith("Microsoft")) {
            return R.drawable.microsoft;
        } else if (os.startsWith("Linux")) {
            return R.drawable.tux;
        } else {
            return R.drawable.desktop;
        }
    }
}
