package me.fernandodominguez.zenmap.models.host;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;

import java.io.Serializable;

/**
 * Created by fernando on 30/12/15.
 */
public class ServiceStatus extends Model implements Serializable {

    @Column(name = "State")
    private String state;

    @Column(name = "Reason")
    private String reason;

    // A service status belongs to a service
    @Column(name = "Service", onDelete = Column.ForeignKeyAction.CASCADE)
    protected Service service;

    public ServiceStatus() {
        super();
    }

    public ServiceStatus(String state, String reason) {
        super();
        this.state = state;
        this.reason = reason;
    }

    public String getState() {
        return state;
    }

    public String getReason() {
        return reason;
    }
}
