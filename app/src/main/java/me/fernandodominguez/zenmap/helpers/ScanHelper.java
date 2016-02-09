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
        }

        return key;
    }
}
