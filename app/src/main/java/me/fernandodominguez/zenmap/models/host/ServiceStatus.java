package me.fernandodominguez.zenmap.models.host;

import java.io.Serializable;

/**
 * Created by fernando on 30/12/15.
 */
public class ServiceStatus implements Serializable {

    private String state;
    private String reason;

    public ServiceStatus(String state, String reason) {
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
