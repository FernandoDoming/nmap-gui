package me.fernandodominguez.zenmap.models.host;

import java.io.Serializable;

/**
 * Created by fernando on 30/12/15.
 */
public class Service implements Serializable {

    private String protocol;
    private String port;
    private String service;
    private String version;
    private ServiceStatus status;

    public Service(String protocol, String port, String service, ServiceStatus status) {
        this.protocol = protocol;
        this.port = port;
        this.service = service;
        this.status = status;
    }

    public String getProtocol() {
        return protocol;
    }

    public String getPort() {
        return port;
    }

    public String getService() {
        return service;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public ServiceStatus getStatus() { return status; }
}
