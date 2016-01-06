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
    private List<Port> ports;

    @Column(name = "Address")
    private String address;

    @Column(name = "Hostname")
    private String hostname;

    public HostScan() {
        super();
    }

    public HostScan(List<Port> ports, String address, String hostname) {
        super();
        this.ports = ports;
        this.address = address;
        this.hostname = hostname;
    }

    public List<Port> getPorts() {
        if (ports == null) ports = getMany(Port.class, "HostScan");
        for (Port port : ports) {
            port.getStatus();
        }
        return ports;
    }

    public void setPorts(List<Port> ports) {
        this.ports = ports;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    @Override
    public String getResult() {
        if (ports.size() == 1) {
            return ports.size() + " detected port open.";
        } else {
            return ports.size() + " detected ports open.";
        }
    }

    @Override
    public void saveWithChildren() {
        this.save();
        if (this.ports != null) {
            for (Port port : ports) {
                port.hostScan = this;
                port.save();

                PortStatus status = port.getStatus();
                if (status != null) {
                    status.port = port;
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
            scan.getPorts();
        }
        return scans;
    }
}
