package me.fernandodominguez.zenmap.models.host;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.io.Serializable;

/**
 * Created by fernando on 30/12/15.
 */
@Table(name = "Ports")
public class Port extends Model implements Serializable {

    @Column(name = "Protocol")
    private String protocol;

    @Column(name = "Port")
    private String port;

    @Column(name = "Service")
    private String service;

    @Column(name = "Status")
    private PortStatus status;

    // A port belongs in a HostScan
    @Column(name = "HostScan", onDelete = Column.ForeignKeyAction.CASCADE)
    protected HostScan hostScan;

    public Port() {
        super();
    }

    public Port(String protocol, String port, String service, PortStatus status) {
        super();
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

    public PortStatus getStatus() {
        if (status == null) status = getMany(PortStatus.class, "Port").get(0);
        return status;
    }
}
