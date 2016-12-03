package me.fernandodominguez.zenmap.models.host;

import java.util.ArrayList;
import java.util.List;

import me.fernandodominguez.zenmap.models.ScanResult;
import me.fernandodominguez.zenmap.models.network.Host;

/**
 * Created by fernando on 30/12/15.
 */

public class HostScan extends ScanResult {

    private Host host;

    public HostScan() {
    }

    public HostScan(Host host) {
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
    public List<Host> getHosts() {
        List<Host> hosts = new ArrayList<>();
        hosts.add(host);
        return hosts;
    }

    @Override
    public String getTarget() {
        return host.getTarget();
    }

    public Host getHost() { return host; }

    public void setHost(Host host) {
        this.host = host;
    }
}
