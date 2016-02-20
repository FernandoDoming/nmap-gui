package me.fernandodominguez.zenmap.models.network;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.io.Serializable;

/**
 * Created by fernando on 30/12/15.
 */
@Table(name = "HostStatus")
public class HostStatus extends Model implements Serializable {

    @Column(name = "State")
    private String state;

    @Column(name = "Reason")
    private String reason;

    // Belongs to a Host
    @Column(name = "Host", onDelete = Column.ForeignKeyAction.CASCADE)
    protected Host host;

    public static final String UP = "up";
    public static final String DOWN = "down";

    public HostStatus() {
        super();
    }

    public HostStatus(String state) {
        super();
        this.state = state;
    }

    public HostStatus(String state, String reason) {
        super();
        this.state = state;
        this.reason = reason;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    @Override
    public String toString() {
        return state;
    }
}
