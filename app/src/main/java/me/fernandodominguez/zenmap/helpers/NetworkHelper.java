package me.fernandodominguez.zenmap.helpers;

import android.net.DhcpInfo;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;

/**
 * Created by fernando on 07/02/16.
 */
public class NetworkHelper {

    /**
     * Returns MAC address of the given interface name.
     * @param interfaceName eth0, wlan0 or NULL=use first interface
     * @return  mac address or empty string
     */
    public static String getMACAddress(String interfaceName) {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                if (interfaceName != null) {
                    if (!intf.getName().equalsIgnoreCase(interfaceName)) continue;
                }

                byte[] mac = intf.getHardwareAddress();
                if (mac == null) return null;
                StringBuilder buf = new StringBuilder();

                for (int idx=0; idx<mac.length; idx++)
                    buf.append(String.format("%02X:", mac[idx]));

                if (buf.length() > 0) buf.deleteCharAt(buf.length()-1);
                return buf.toString();
            }
        } catch (Exception e) { e.printStackTrace(); } // for now eat exceptions
        return null;
    }

    /**
     * Get IP address from first non-localhost interface
     * @return  address or null
     */
    public static String getIPAddress() {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface iface : interfaces) {
                List<InetAddress> addrs = Collections.list(iface.getInetAddresses());
                for (InetAddress addr : addrs) {
                    if (!addr.isLoopbackAddress()) {
                        String sAddr = addr.getHostAddress();
                        boolean isIPv4 = sAddr.indexOf(':') < 0;

                        if (isIPv4) return sAddr;
                    }
                }
            }
        } catch (Exception e) { e.printStackTrace(); } // for now eat exceptions
        return null;
    }

    public static String getIPv6Address() {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
                for (InetAddress addr : addrs) {
                    if (!addr.isLoopbackAddress()) {
                        String sAddr = addr.getHostAddress();
                        //boolean isIPv4 = InetAddressUtils.isIPv4Address(sAddr);
                        boolean isIPv4 = sAddr.indexOf(':')<0;

                        if (!isIPv4) {
                            int delim = sAddr.indexOf('%'); // drop ip6 zone suffix
                            return delim < 0 ? sAddr.toUpperCase() : sAddr.substring(0, delim).toUpperCase();
                        }
                    }
                }
            }
        } catch (Exception e) { e.printStackTrace(); } // for now eat exceptions
        return null;
    }

    public static String getNetworkAddress(){
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface iface : interfaces) {
                List<InterfaceAddress> addrs = iface.getInterfaceAddresses();
                for (InterfaceAddress interfaceAddress : addrs) {
                    InetAddress inetAddress = interfaceAddress.getAddress();
                    if (!inetAddress.isLoopbackAddress()) {

                        String sAddr = inetAddress.getHostAddress();
                        boolean isIPv4 = sAddr.indexOf(':') < 0;

                        if (isIPv4) {
                            short prefix = interfaceAddress.getNetworkPrefixLength();
                            SubnetHelper helper = new SubnetHelper(sAddr + "/" + prefix);
                            return helper.getInfo().getNetworkAddress() + "/" + prefix;
                        }
                    }
                }
            }
        } catch (Exception e) { e.printStackTrace(); } // for now eat exceptions
        return null;
    }
}
