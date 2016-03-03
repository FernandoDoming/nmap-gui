package me.fernandodominguez.zenmap.models.network;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import java.io.Serializable;
import java.util.List;

import me.fernandodominguez.zenmap.models.ScanResult;
import me.fernandodominguez.zenmap.models.host.HostScan;
import me.fernandodominguez.zenmap.models.host.Service;
import me.fernandodominguez.zenmap.models.host.ServiceStatus;

/**
 * Created by fernando on 30/12/15.
 */
@Table(name = "Hosts")
public class Host extends Model implements Serializable {

    @Column(name = "Address")
    private String address;

    @Column(name = "Services")
    private List<Service> services;

    @Column(name = "Hostname")
    private String hostname;

    @Column(name = "Os")
    private String os;

    @Column(name = "Mac")
    private String mac;

    @Column(name = "MacVendor")
    private String macVendor;

    @Column(name = "Status")
    private HostStatus status;

    // Belongs to a HostScan
    @Column(name = "HostScan", onDelete = Column.ForeignKeyAction.CASCADE)
    public HostScan hostScan;

    // Or to a NetworkScan
    @Column(name = "NetworkScan", onDelete = Column.ForeignKeyAction.CASCADE)
    public NetworkScan networkScan;

    public Host() {
        super();
    }

    public Host(String address, HostStatus status) {
        super();
        this.address = address;
        this.status = status;
    }

    public Host(String address, List<Service> services, HostStatus status) {
        this.address = address;
        this.services = services;
        this.status = status;
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

    public HostStatus getStatus() {
        if (status == null) status = getMany(HostStatus.class, "Host").get(0);
        return status;
    }

    public void setStatus(HostStatus status) {
        this.status = status;
    }

    public List<Service> getServices() {
        if (services == null) services = getMany(Service.class, "Host");
        for (Service service : services) {
            service.getStatus();
        }
        return services;
    }

    public void setServices(List<Service> services) {
        this.services = services;
    }

    public void saveWithChildren() {
        this.save();
        if (this.services != null) {
            for (Service service : services) {
                service.host = this;
                service.save();

                ServiceStatus status = service.getStatus();
                if (status != null) {
                    status.service = service;
                    status.save();
                }
            }
        }
        if (status != null) {
            status.host = this;
            status.save();
        }
    }

    public String getTarget() {
        if (hostname != null && !hostname.equals("")) {
            return hostname;
        } else {
            return address;
        }
    }

    public static List<Host> all() {
        List<Host> scans = new Select().all().from(Host.class).execute();
        return scans;
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
