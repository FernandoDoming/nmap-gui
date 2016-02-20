package me.fernandodominguez.zenmap.models.network;

import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import java.util.ArrayList;
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
    public String getResult() {
        List<Host> upHosts = getUpHosts();
        if (upHosts.size() == 1) {
            return upHosts.size() + " detected host up.";
        } else {
            return upHosts.size() + " detected hosts up.";
        }
    }

    public List<Host> getHosts() {
        if (hosts == null) {
            hosts = getMany(Host.class, "NetworkScan");
            for (Host host : hosts) {
                host.getStatus();
            }
        }
        return hosts;
    }

    public void setHosts(List<Host> hosts) {
        this.hosts = hosts;
    }

    public List<Host> getUpHosts() {
        List<Host> allHosts = getHosts();
        List<Host> upHosts = new ArrayList<>();
        for (Host host : allHosts) {
            if (host.getStatus().getState().equals(HostStatus.UP)) {
                upHosts.add(host);
            }
        }
        return upHosts;
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

    @Override
    public ScanResult populate() {
        this.hosts = getHosts();
        return this;
    }

    public static List<NetworkScan> all() {
        List<NetworkScan> scans = new Select().all().from(NetworkScan.class).execute();
        for (NetworkScan scan : scans) {
            scan.getHosts();
        }
        return scans;
    }
}
