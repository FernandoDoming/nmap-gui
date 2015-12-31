package me.fernandodominguez.zenmap.models;

/**
 * Created by fernando on 30/12/15.
 */
public abstract class ScanResult implements IScanResult {

    protected String target;
    protected long startTime;
    protected long endTime;
    protected String scanStatus;
    protected String output;

    public long getStartTime() {
        return startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public String getScanStatus() {
        return scanStatus;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }
}
