package me.fernandodominguez.zenmap.models.host;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.io.Serializable;

import me.fernandodominguez.zenmap.models.network.Host;

/**
 * Created by fernando on 30/12/15.
 */
@Table(name = "Services")
public class Service extends Model implements Serializable {

    @Column(name = "Protocol")
    private String protocol;

    @Column(name = "Port")
    private String port;

    @Column(name = "Service")
    private String service;

    @Column(name = "Version")
    private String version;

    @Column(name = "Status")
    private ServiceStatus status;

    // A service belongs to a Host
    @Column(name = "Host", onDelete = Column.ForeignKeyAction.CASCADE)
    public Host host;

    public Service() {
        super();
    }

    public Service(String protocol, String port, String service, ServiceStatus status) {
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

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public ServiceStatus getStatus() {
        if (status == null) status = getMany(ServiceStatus.class, "Service").get(0);
        return status;
    }
}
