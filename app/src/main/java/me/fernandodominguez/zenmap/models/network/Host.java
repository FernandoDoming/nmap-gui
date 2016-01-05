package me.fernandodominguez.zenmap.models.network;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.io.Serializable;

/**
 * Created by fernando on 30/12/15.
 */
@Table(name = "Hosts")
public class Host extends Model implements Serializable {

    @Column(name = "Address")
    private String address;

    @Column(name = "Status")
    private HostStatus status;

    // Belongs to a NetworkScan
    @Column(name = "NetworkScan", onDelete = Column.ForeignKeyAction.CASCADE)
    protected NetworkScan networkScan;

    public Host() {
        super();
    }

    public Host(String address, HostStatus status) {
        super();
        this.address = address;
        this.status = status;
    }

    public String getAddress() {
        return address;
    }

    public HostStatus getStatus() {
        if (status == null) status = getMany(HostStatus.class, "Host").get(0);
        return status;
    }
}
