package me.fernandodominguez.zenmap.models.network;

import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import java.util.List;

import me.fernandodominguez.zenmap.models.ScanResult;

/**
 * Created by fernando on 30/12/15.
 */
@Table(name = "NetworkScans")
public class NetworkScan extends ScanResult {

    @Column(name = "Hosts")
    private List<Host> hosts;

    public NetworkScan() {
        super();
    }

    public NetworkScan(List<Host> hosts) {
        super();
        this.hosts = hosts;
    }

    @Override
    public String getTitle() {
        return super.getTarget();
    }

    @Override
    public String getResult() {
        if (hosts.size() == 1) {
            return hosts.size() + " detected host up.";
        } else {
            return hosts.size() + " detected hosts up.";
        }
    }

    public List<Host> getHosts() {
        if (hosts == null) hosts = getMany(Host.class, "NetworkScan");
        for (Host host : hosts) {
            host.getStatus();
        }
        return hosts;
    }

    @Override
    public void saveWithChildren() {
        this.save();
        if (hosts != null) {
            for (Host host : hosts) {
                host.networkScan = this;
                host.save();

                HostStatus status = host.getStatus();
                if (status != null) {
                    status.host = host;
                    status.save();
                }
            }
        }
    }

    public static List<NetworkScan> all() {
        List<NetworkScan> scans = new Select().all().from(NetworkScan.class).execute();
        for (NetworkScan scan : scans) {
            scan.getHosts();
        }
        return scans;
    }
}
