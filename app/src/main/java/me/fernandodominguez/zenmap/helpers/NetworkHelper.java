package me.fernandodominguez.zenmap.helpers;

import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static android.content.Context.WIFI_SERVICE;

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

                for (byte aMac : mac) buf.append(String.format("%02X:", aMac));

                if (buf.length() > 0) buf.deleteCharAt(buf.length()-1);
                return buf.toString();
            }
        } catch (Exception e) { e.printStackTrace(); } // for now eat exceptions
        return null;
    }

    public static String getIfaceByIp(byte[] ip) {
        NetworkInterface netInterface = null;
        try {
            InetAddress addr = InetAddress.getByAddress(ip);
            netInterface = NetworkInterface.getByInetAddress(addr);
        } catch (SocketException | UnknownHostException e) {
            e.printStackTrace();
        }

        if (netInterface == null) return null;
        else return netInterface.getDisplayName();
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

    /**
     * Get the private network address from first non-localhost interface with it's subnet mask
     * @return subnet address (String) or null
     */
    public static String getNetworkAddress(){
        try {
            List<NetworkInterface> interfaces = new ArrayList<NetworkInterface>();
            // It is usually named wlan0
            if (NetworkInterface.getByName("wlan0") != null) {
                interfaces.add( NetworkInterface.getByName("wlan0") );
            }
            // If not get all interfaces
            else {
                interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            }


            for (NetworkInterface iface : interfaces) {
                List<InterfaceAddress> addrs = iface.getInterfaceAddresses();
                for (InterfaceAddress interfaceAddress : addrs) {
                    InetAddress inetAddress = interfaceAddress.getAddress();
                    if (!inetAddress.isLoopbackAddress() &&
                            inetAddress.isSiteLocalAddress()) {

                        String sAddr = inetAddress.getHostAddress();
                        boolean isIPv4 = sAddr.indexOf(':') < 0;

                        if (isIPv4) {
                            short prefix = interfaceAddress.getNetworkPrefixLength();
                            SubnetHelper helper = new SubnetHelper(sAddr + "/" + prefix);
                            return helper.getInfo().getNetworkAddress() + "/" + prefix;
                        }
                        // TODO: IPv6
                    }
                }
            }
        } catch (Exception e) { e.printStackTrace(); } // for now eat exceptions
        return null;
    }

    /**
    * Checks wether an IP address is within the private range or not
     * @return a boolean indicating if the IP addr is within a private range
    * */
    public static boolean isPrivateAddress(String ip) throws UnknownHostException {
        InetAddress address = InetAddress.getByName(ip);
        return address.isSiteLocalAddress();
    }

    /**
    * Gets the default gateway if connected to a WIFI network
     * @param context A context to call system services on
     * @return the default gateway (String) or null
    * */
    public static String getDefaultGw(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(WIFI_SERVICE);
        WifiInfo wifiInfo       = wifiManager.getConnectionInfo();

        if (wifiInfo.getSupplicantState().equals(SupplicantState.COMPLETED)) {
            DhcpInfo dhcp     = wifiManager.getDhcpInfo();
            return intToIp(dhcp.gateway);
        } else {
            return null;
        }
    }

    /* Private methods */

    private static String intToIp(int i) {

        return ( i & 0xFF) + "." +
               ((i >> 8 ) & 0xFF) + "." +
               ((i >> 16 ) & 0xFF) + "." +
               ((i >> 24 ) & 0xFF );
    }
}
