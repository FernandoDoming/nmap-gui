package me.fernandodominguez.zenmap.models.host;

import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import java.util.List;

import me.fernandodominguez.zenmap.models.ScanResult;

/**
 * Created by fernando on 30/12/15.
 */

@Table(name = "HostScans")
public class HostScan extends ScanResult {

    @Column(name = "Ports")
    private List<Service> services;

    @Column(name = "Address")
    private String address;

    @Column(name = "Hostname")
    private String hostname;

    @Column(name = "Os")
    private String os;

    @Column(name = "Mac")
    private String mac;

    @Column(name = "MacVendor")
    private String macVendor;

    @Column(name = "isUp")
    private boolean up;

    public HostScan() {
        super();
    }

    public HostScan(List<Service> services, String address, String hostname) {
        super();
        this.services = services;
        this.address = address;
        this.hostname = hostname;
    }

    public List<Service> getServices() {
        if (services == null) services = getMany(Service.class, "HostScan");
        for (Service service : services) {
            service.getStatus();
        }
        return services;
    }

    public void setServices(List<Service> services) {
        this.services = services;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    @Override
    public String getResult() {
        if (isUp()) {
            if (getServices().size() == 1) {
                return getServices().size() + " detected service open.";
            } else {
                return getServices().size() + " detected services open.";
            }
        } else {
            return "Host is offline";
        }
    }

    @Override
    public void saveWithChildren() {
        this.save();
        if (this.services != null) {
            for (Service service : services) {
                service.hostScan = this;
                service.save();

                ServiceStatus status = service.getStatus();
                if (status != null) {
                    status.service = service;
                    status.save();
                }
            }
        }
    }

    @Override
    public String getTarget() {
        if (hostname != null && !hostname.equals("")) {
            return hostname;
        } else {
            return address;
        }
    }

    public static List<HostScan> all() {
        List<HostScan> scans = new Select().all().from(HostScan.class).execute();
        for (HostScan scan : scans) {
            scan.getServices();
        }
        return scans;
    }

    public boolean isUp() {
        return up;
    }

    public void setUp(boolean up) {
        this.up = up;
    }

    public String getOs() {
        return os;
    }

    public void setOs(String os) {
        this.os = os;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getMacVendor() {
        return macVendor;
    }

    public void setMacVendor(String macVendor) {
        this.macVendor = macVendor;
    }
}
