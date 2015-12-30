package me.fernandodominguez.zenmap.models.network;

/**
 * Created by fernando on 30/12/15.
 */
public class Host {
    private String address;
    private HostStatus status;

    public Host(String address, HostStatus status) {
        this.address = address;
        this.status = status;
    }

    public String getAddress() {
        return address;
    }

    public HostStatus getStatus() {
        return status;
    }
}
