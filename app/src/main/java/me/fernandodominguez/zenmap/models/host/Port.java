package me.fernandodominguez.zenmap.models.host;

/**
 * Created by fernando on 30/12/15.
 */
public class Port {
    private String protocol;
    private String port;
    private String service;
    private PortStatus status;

    public Port(String protocol, String port, String service, PortStatus status) {
        this.protocol = protocol;
        this.port = port;
        this.service = service;
        this.status = status;
    }
}
