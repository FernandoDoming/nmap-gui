package me.fernandodominguez.zenmap.models.host;

import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import java.util.ArrayList;
import java.util.List;

import me.fernandodominguez.zenmap.models.ScanResult;
import me.fernandodominguez.zenmap.models.network.Host;

/**
 * Created by fernando on 30/12/15.
 */

@Table(name = "HostScans")
public class HostScan extends ScanResult {

    @Column(name = "Host")
    private Host host;

    public HostScan() {
        super();
    }

    public HostScan(Host host) {
        super();
        this.host = host;
    }

    @Override
    public String getResult() {
        if (host.isUp()) {
            if (host.getServices().size() == 1) {
                return host.getServices().size() + " detected service open.";
            } else {
                return host.getServices().size() + " detected services open.";
            }
        } else {
            return "Host is offline";
        }
    }

    @Override
    public void saveWithChildren() {
        this.save();
        host.hostScan = this;
        host.saveWithChildren();
    }

    @Override
    public ScanResult populate() {
        this.host = getHost();
        return this;
    }

    @Override
    public List<Host> getHosts() {
        List<Host> hosts = new ArrayList<>();
        hosts.add(this.getHost());
        return hosts;
    }

    @Override
    public String getTarget() {
        return host.getTarget();
    }

    public static List<HostScan> all() {
        List<HostScan> scans = new Select().all().from(HostScan.class).execute();
        return scans;
    }

    public Host getHost() {
        if (host == null) {
            host = getMany(Host.class, "HostScan").get(0);
            host.getStatus();
        }
        return host;
    }

    public void setHost(Host host) {
        this.host = host;
    }
}
