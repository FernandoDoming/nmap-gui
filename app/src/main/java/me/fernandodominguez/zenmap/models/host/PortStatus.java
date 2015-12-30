package me.fernandodominguez.zenmap.models.host;

/**
 * Created by fernando on 30/12/15.
 */
public class PortStatus {
    private String state;
    private String reason;

    public PortStatus(String state, String reason) {
        this.state = state;
        this.reason = reason;
    }
}
