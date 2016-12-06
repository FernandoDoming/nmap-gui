package me.fernandodominguez.zenmap.models.network;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import me.fernandodominguez.zenmap.models.host.Service;

/**
 * Created by fernando on 30/12/15.
 */
public class Host implements Serializable {

    private String address;
    private List<Service> services;
    private String hostname;
    private String os;
    private String mac;
    private String macVendor;
    private HostStatus status;

    public static List<String> HOST_DETAILS = Arrays.asList("os", "mac", "mac_vendor");

    public Host() {
    }

    public Host(String address, HostStatus status) {
        this.address = address;
        this.status = status;
    }

    public Host(String address, List<Service> services, HostStatus status) {
        this.address  = address;
        this.services = services;
        this.status   = status;
    }

    public String getTitle() {
        if (hostname  == null && mac == null) return address;
        if (hostname != null) return hostname;
        return mac;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public HostStatus getStatus() { return status; }

    public void setStatus(HostStatus status) {
        this.status = status;
    }

    public List<Service> getServices() {
        return services;
    }

    public void setServices(List<Service> services) {
        this.services = services;
    }

    public String getTarget() {
        if (hostname != null && !hostname.equals("")) {
            return hostname;
        } else {
            return address;
        }
    }

    public boolean isUp() {
        return status.getState().equals(HostStatus.UP);
    }

    public String getMacVendor() {
        return macVendor;
    }

    public void setMacVendor(String macVendor) {
        this.macVendor = macVendor;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getOs() {
        return os;
    }

    public void setOs(String os) {
        this.os = os;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }
}
