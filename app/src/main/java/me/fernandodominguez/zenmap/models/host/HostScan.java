package me.fernandodominguez.zenmap.models.host;

import java.util.List;

import me.fernandodominguez.zenmap.models.ScanResult;

/**
 * Created by fernando on 30/12/15.
 */
public class HostScan extends ScanResult {
    private List<Port> ports;
    private String address;
    private String hostname;

    public HostScan(List<Port> ports, String address, String hostname) {
        this.ports = ports;
        this.address = address;
        this.hostname = hostname;
    }

    @Override
    public String getTitle() {
        if (hostname != null && !hostname.equals("")) {
            return hostname;
        } else {
            return address;
        }
    }
}
