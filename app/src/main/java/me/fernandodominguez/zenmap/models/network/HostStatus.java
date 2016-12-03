package me.fernandodominguez.zenmap.models.network;

import java.io.Serializable;

/**
 * Created by fernando on 30/12/15.
 */
public class HostStatus implements Serializable {

    private String state;
    private String reason;

    public static final String UP = "up";
    public static final String DOWN = "down";

    public HostStatus(String state) {
        this.state = state;
    }

    public HostStatus(String state, String reason) {
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
