package me.fernandodominguez.zenmap.models.network;

import java.util.ArrayList;
import java.util.List;

import me.fernandodominguez.zenmap.models.ScanResult;

/**
 * Created by fernando on 30/12/15.
 */
public class NetworkScan extends ScanResult {

    private List<Host> hosts;

    public NetworkScan() {
    }

    public NetworkScan(List<Host> hosts) {
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

    public List<Host> getHosts() { return hosts; }

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
}
