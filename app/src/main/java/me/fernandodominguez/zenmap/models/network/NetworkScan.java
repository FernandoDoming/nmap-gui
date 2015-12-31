package me.fernandodominguez.zenmap.models.network;

import java.util.List;

import me.fernandodominguez.zenmap.models.ScanResult;

/**
 * Created by fernando on 30/12/15.
 */
public class NetworkScan extends ScanResult {
    private List<Host> hosts;

    public NetworkScan(List<Host> hosts) {
        this.hosts = hosts;
    }

    @Override
    public String getTitle() {
        return super.getTarget();
    }
}
